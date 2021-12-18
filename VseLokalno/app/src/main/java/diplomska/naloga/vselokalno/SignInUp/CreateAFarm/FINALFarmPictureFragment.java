package diplomska.naloga.vselokalno.SignInUp.CreateAFarm;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.farmData;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.signInUpImageCropper;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;

public class FINALFarmPictureFragment extends Fragment implements ImageCropper.ImageCroppedCallbackListener {

    //    Views
    AppCompatImageView imageView;
    //    Image request code
    private final static int PICK_IMAGE = 100;
    //    Image uri
    Uri imageURI;
    //    Firebase authentication
    private FirebaseAuth mAuth;
    //    TAG
    String TAG = "FINALChoosePhotoFragment";
    //    Other variables
    boolean photo_changed = false;
    //    Firebase firestore DB
    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    } // onStart

    public FINALFarmPictureFragment() {
        // Required empty public constructor
    }

    public static FINALFarmPictureFragment newInstance() {
        return new FINALFarmPictureFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_final_farm_picture, container, false);
        imageView = rootView.findViewById(R.id.image_holder);
//        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
//        Choose photo
        MaterialButton choosePhotoButton = rootView.findViewById(R.id.choose_photo);
        choosePhotoButton.setOnClickListener(v -> openGallery());
//        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.create_a_farm_btn);
        nexBtn.setOnClickListener(v -> createAccount());
        return rootView;
    } // onCreateView

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(userData.getEmail(), userData.getPassword())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        makeLogD("UserPasswordFragment", "(OnCreateView) user created!");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
//                                TODO: userData.setPassword(""); // To not show the password unprotected.
                            updateUI(user);
                        } else
                            makeLogW("UserPasswordFragment", "(OnCreateView) ERROR user is null!");
                    } else {
//                            If sign in fails, display a message to the user.
                        makeLogW("UserPasswordFragment", "(OnCreateView) " + task.getException());
                        Toast.makeText(requireContext(), "Za ta e-poštni naslov že obstaja račun.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    } // createAccount

    public void updateUI(FirebaseUser user) {
        if (!photo_changed) {
//            Skip saving image
            makeUser(user);
        } else {
            try {
//            Save to cloud storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
//            Create a storage reference from the app
                StorageReference storageRef = storage.getReference();
                StorageReference imagesRef = storageRef.child("Profile Images/" + user.getUid());
                Uri file = Uri.fromFile(new File(String.valueOf(imageURI)));
                UploadTask uploadTask = imagesRef.putFile(file);
//            Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(exception -> {
//                Handle unsuccessful uploads
                    makeLogW(TAG, "(updateUI) " + exception);
                    Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    userData.setUse_default_pic(false);
//                Make user
                    makeUser(user);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } // updateUI

    private void makeUser(FirebaseUser user) {
        db.collection("Uporabniki").document(user.getUid()).set(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                makeLogD("UserPasswordFragment", "(makeUser) user created!");
                makeFarm(user);
            } else {
                makeLogW("UserPasswordFragment", "(makeUser) " + task.getException());
            }
        });
    } // makeUser

    private void makeFarm(FirebaseUser user) {
        farmData.setKoordinate_kmetije(getLocationFromAddress(farmData.getNaslov_kmetije()));
        db.collection("Kmetije").document(user.getUid()).set(farmData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                makeLogD("UserPasswordFragment", "(makeFarm) farm created!");
                addTooAllFarmList(user);
            } else {
                makeLogW("UserPasswordFragment", "(makeFarm) " + task.getException());
            }
        });
    } // makeFarm

    private void addTooAllFarmList(FirebaseUser user) {
        DocumentReference allFarmsDocument = db.collection("Kmetije").document("Vse_kmetije");
        Map<String, String> shortDataFarm = farmData.getKoordinate_kmetije();
        shortDataFarm.put("ime_kmetije", farmData.getIme_kmetije());
        shortDataFarm.put("id_kmetije", user.getUid());
        allFarmsDocument.update("seznam_vseh_kmetij", FieldValue.arrayUnion(shortDataFarm))
                .addOnSuccessListener(unused -> {
                    makeLogD(TAG, "(addToAllFarmList) addition successful.");
                    Intent toMainActivity = new Intent(requireContext(), MainActivity.class);
                    startActivity(toMainActivity);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> makeLogW(TAG, "(addToAllFarmList) addition failure.\n" + e.getMessage()));
    } // addTooAllFarmList

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    } // openGallery

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                imageURI = data.getData();
                makeLogD(TAG, "My uri" + imageURI.toString());
                signInUpImageCropper.startCrop(imageURI, this);
            } else makeLogW(TAG, "(onActivityResult) data == null!");
        }
    } // onActivityResult

    public Map<String, String> getLocationFromAddress(String strAddress) {
        Map<String, String> latLon = new HashMap<>();
        latLon.put("lat", "46.056946");
        latLon.put("lon", "14.505751");
        Geocoder coder = new Geocoder(requireContext());
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return latLon;
            }
            Address location = address.get(0);
            latLon.put("lat", String.valueOf(location.getLatitude()));
            latLon.put("lon", String.valueOf(location.getLongitude()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLon;
    } // getLocationFromAddress

    @Override
    public void onImageCroppedCallback(@NonNull String path) {
        imageURI = Uri.parse(new File(path).toString());
        imageView.setImageURI(imageURI);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photo_changed = true;
    } // onImageCroppedCallback
}
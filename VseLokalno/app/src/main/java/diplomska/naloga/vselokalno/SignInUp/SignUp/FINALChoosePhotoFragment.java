package diplomska.naloga.vselokalno.SignInUp.SignUp;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.signInUpImageCropper;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;

public class FINALChoosePhotoFragment extends Fragment implements ImageCropper.ImageCroppedCallbackListener {

    //    Views
    CircleImageView imageView;
    ProgressBar progressBar;
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    } // onStart

    public FINALChoosePhotoFragment() {
        // Required empty public constructor
    }

    public static FINALChoosePhotoFragment newInstance() {
        return new FINALChoosePhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_final_choose_photo, container, false);
        imageView = rootView.findViewById(R.id.image_holder);
        progressBar = rootView.findViewById(R.id.loading_bar_create_user);
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
        MaterialButton nexBtn = rootView.findViewById(R.id.register_me_btn);
        nexBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            createAccount();
        });
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
                            userData.setPassword("");
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
    }

    public void updateUI(FirebaseUser user) {
        if (!photo_changed) {
//            Skip saving image
            makeUser(user);
            return;
        }
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

    private void makeUser(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Uporabniki").document(user.getUid()).set(userData).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                makeLogD("UserPasswordFragment", "(makeUser) user created!");
                Intent toMainActivity = new Intent(requireContext(), MainActivity.class);
                startActivity(toMainActivity);
                requireActivity().finish();
            } else {
                makeLogW("UserPasswordFragment", "(makeUser) " + task.getException());
            }
        });
    }

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

    @Override
    public void onImageCroppedCallback(@NonNull String path) {
        makeLogD(TAG, path);
        imageURI = Uri.parse(new File(path).toString());
        imageView.setImageURI(imageURI);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photo_changed = true;
    } // onImageCroppedCallback
}
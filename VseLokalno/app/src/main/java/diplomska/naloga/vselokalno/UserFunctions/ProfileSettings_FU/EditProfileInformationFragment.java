package diplomska.naloga.vselokalno.UserFunctions.ProfileSettings_FU;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.appImageCropper;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.currentUser;
import static diplomska.naloga.vselokalno.MainActivity.fcmTopic;
import static diplomska.naloga.vselokalno.MainActivity.mNotificationsSharedPrefKey;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.mySharedPreferences;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.R;


public class EditProfileInformationFragment extends Fragment implements ImageCropper.ImageCroppedCallbackListener, EditFarmHoursFragment.EditFarmHoursFragmentCallback {

    // TAG
    private final String TAG = "EditProfileInformationFragment";
    //    Image request code
    private final static int PICK_IMAGE = 200;
    boolean photo_changed = false;
    //    Image uri
    Uri imageURI;
    // Views:
    CircleImageView mProfileImageView;
    EditText editEmailView;
    EditText editPasswordView;
    SwitchCompat notificationSwitch;
    EditText editFarmNameView;
    LinearLayout editFarmNameLin;
    EditText editFarmAddressView;
    LinearLayout editFarmAddressLin;
    EditText editUserName;
    LinearLayout editUserNameLin;
    EditText editUserSurname;
    LinearLayout editUserSurnameLin;
    LinearLayout editFarmWorkingHoursLin;


    boolean sendUserUpdatedInfo = false;
    boolean sendFarmUpdatedInfo = false;

    public EditProfileInformationFragment() {
        // Required empty public constructor
    }

    public static EditProfileInformationFragment newInstance() {
        return new EditProfileInformationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile_information, container, false);
        // Edit and display profile image:
        LinearLayout editProfileImageBtn = rootView.findViewById(R.id.edit_profile_image);
        editProfileImageBtn.setOnClickListener(v -> openGallery());
        mProfileImageView = rootView.findViewById(R.id.profile_image_view_EditProfileInformationFragment);
        if (!appUser.isUse_default_pic()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + userID);
            GlideApp.with(requireContext()).load(imageRef).into(mProfileImageView);
        }
        // Edit and display email:
        editEmailView = rootView.findViewById(R.id.edit_email);
        editEmailView.setText(appUser.getEmail());
        // Edit password:
        editPasswordView = rootView.findViewById(R.id.edit_password);
        editPasswordView.setText(appUser.getPassword());
        // Show notifications:
        TextView notificationExplanation = rootView.findViewById(R.id.notification_explanation);
        notificationSwitch = rootView.findViewById(R.id.notification_switch);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                notificationExplanation.setText("Notifikacije so omogočene.");
            else
                notificationExplanation.setText("Notifikacije so onemogočene.");
        });
        boolean showNotifications = mySharedPreferences.getBoolean(mNotificationsSharedPrefKey, true);
        notificationSwitch.setChecked(showNotifications);
        if (notificationSwitch.isChecked())
            notificationExplanation.setText("Notifikacije so omogočene.");
        else
            notificationExplanation.setText("Notifikacije so onemogočene.");
        editFarmNameView = rootView.findViewById(R.id.edit_farmName);
        editFarmNameLin = rootView.findViewById(R.id.lin_layout_farmName);
        editFarmAddressView = rootView.findViewById(R.id.edit_farmAddress);
        editFarmAddressLin = rootView.findViewById(R.id.lin_layout_farmAddress);
        editUserName = rootView.findViewById(R.id.edit_userName);
        editUserNameLin = rootView.findViewById(R.id.lin_layout_userName);
        editUserSurname = rootView.findViewById(R.id.edit_userSurrname);
        editUserSurnameLin = rootView.findViewById(R.id.lin_layout_userSurrname);
        editFarmWorkingHoursLin = rootView.findViewById(R.id.lin_layout_farmWorkingHours);
        if (appUser.isLastnik_kmetije()) {
            // Farm
            editUserNameLin.setVisibility(View.GONE);
            editUserSurnameLin.setVisibility(View.GONE);
            editFarmNameView.setText(appFarm.getIme_kmetije());
            editFarmAddressView.setText(appFarm.getNaslov_kmetije());
            editFarmWorkingHoursLin.setOnClickListener(v-> {
                EditFarmHoursFragment editFarmHoursFragment= EditFarmHoursFragment.newInstance(this);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, editFarmHoursFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            editFarmNameLin.setVisibility(View.GONE);
            editFarmAddressLin.setVisibility(View.GONE);
            editFarmWorkingHoursLin.setVisibility(View.GONE);
            editUserName.setText(appUser.getIme_uporabnika());
            editUserSurname.setText(appUser.getPriimek_uporabnika());
        }
        // Discard changes:
        TextView discardChanges = rootView.findViewById(R.id.discard_changes);
        discardChanges.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Ste prepričani, da želite zavreči spremembe?")
                    .setTitle("Zavrzi spremembe");
            builder.setPositiveButton("Da", (dialog, id) -> getParentFragmentManager().popBackStack());
            builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        // Save changes:
        AppCompatButton saveChangesBtn = rootView.findViewById(R.id.save_changes);
        saveChangesBtn.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Ste prepričani, da želite shraniti spremembe?")
                    .setTitle("Shrani spremembe");
            // Add the buttons
            builder.setPositiveButton("Da", (dialog, id) -> saveChanges());
            builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return rootView;
    }

    private void saveChanges() {
        boolean change_email = false;
        boolean change_password = false;
        if (photo_changed) { // Upload new photo:
            appUser.setUse_default_pic(true);
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String path = "Profile Images/" + userID;
                StorageReference imagesRef = storageRef.child(path);
                Uri file = Uri.fromFile(new File(String.valueOf(imageURI)));
                UploadTask uploadTask = imagesRef.putFile(file);
                uploadTask.addOnFailureListener(exception -> {
                    makeLogW(TAG, "(onCreateView) " + exception);
                    Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* TODO password and email change
        if (!appUser.getEmail().equals(editEmailView.getText().toString())) {
            appUser.setEmail(editEmailView.getText().toString());
            sendUserUpdatedInfo = true;
            change_email = true;
        }
        if (!appUser.getPassword().equals(editPasswordView.getText().toString())) {
            appUser.setPassword(editPasswordView.getText().toString());
            sendUserUpdatedInfo = true;
            change_password = true;
        }
        if (change_email || change_password) {
            changeEmailOrPassword(change_email, change_password);
            sendUserUpdatedInfo = true;
        }*/
        mySharedPreferences.edit()
                .putBoolean(mNotificationsSharedPrefKey, notificationSwitch.isChecked())
                .apply();
        if (notificationSwitch.isChecked())
            FirebaseMessaging.getInstance().subscribeToTopic(fcmTopic);
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(fcmTopic);
        if (appUser.isLastnik_kmetije()) {
            // Edit farm details:
            if (!appFarm.getIme_kmetije().equals(editFarmNameView.getText().toString().trim())) {
                sendFarmUpdatedInfo = true;
                appFarm.setIme_kmetije(editFarmNameView.getText().toString().trim());
            }
            if (!appFarm.getNaslov_kmetije().equals(editFarmAddressView.getText().toString().trim())) {
                sendFarmUpdatedInfo = true;
                appFarm.setNaslov_kmetije(editFarmAddressView.getText().toString().trim());
                appFarm.setKoordinate_kmetije(getLocationFromAddress(appFarm.getNaslov_kmetije()));
            }
        } else {
            // Edit user details:
            if (!appUser.getIme_uporabnika().equals(editUserName.getText().toString().trim())) {
                sendUserUpdatedInfo = true;
                appUser.setIme_uporabnika(editUserName.getText().toString().trim());
            }
            if (!appUser.getPriimek_uporabnika().equals(editUserSurname.getText().toString().trim())) {
                sendUserUpdatedInfo = true;
                appUser.setPriimek_uporabnika(editUserSurname.getText().toString().trim());
            }
        }
        if (sendUserUpdatedInfo) {
            updateUser();
        }
        if (sendFarmUpdatedInfo) {
            updateFarm();
        }
        sendUserUpdatedInfo = false;
        sendFarmUpdatedInfo = false;
        Toast.makeText(requireContext(), "Spremembe so shranjene.", Toast.LENGTH_SHORT).show();
    } // saveChanges

    private void updateUser() {
        FirebaseFirestore.getInstance()
                .collection("Uporabniki").document(userID)
                .set(appUser);
    } // updateUser

    private void updateFarm() {
        FirebaseFirestore.getInstance()
                .collection("Kmetije").document(userID)
                .set(appFarm);
        Map<String, String> newFarmData = appFarm.getKoordinate_kmetije();
        newFarmData.put("ime_kmetije", appFarm.getIme_kmetije());
        FirebaseFirestore.getInstance()
                .collection("Kmetije").document("Vse_kmetije")
                .update(userID, newFarmData);
    } // updateFarm

    private void changeEmailOrPassword(boolean email, boolean password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(appUser.getEmail(), appUser.getPassword());
        // TODO: Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (email)
                        currentUser.updateEmail(editEmailView.getText().toString())
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful())
                                        makeLogD(TAG, "User email address updated.");
                                    else
                                        makeLogW(TAG, Objects.requireNonNull(task1.getException()).getMessage());
                                });
                    if (password)
                        currentUser.updatePassword(editEmailView.getText().toString())
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful())
                                        makeLogD(TAG, "User password updated.");
                                    else
                                        makeLogW(TAG, Objects.requireNonNull(task1.getException()).getMessage());
                                });
                });
    } // changeEmail

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
                appImageCropper.startCrop(imageURI, this);
            } else makeLogW(TAG, "(onActivityResult) data == null!");
        }
    } // onActivityResult

    @Override
    public void onImageCroppedCallback(@NonNull String path) {
        imageURI = Uri.parse(new File(path).toString());
        mProfileImageView.setImageURI(imageURI);
        photo_changed = true;
    } // onImageCroppedCallback

    @Override
    public void onSaveChanges() {
        sendFarmUpdatedInfo = true;
    }

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
}
package diplomska.naloga.vselokalno.UserFunctions.ProfileSettings_FU;

import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.currentUser;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import diplomska.naloga.vselokalno.R;

public class ReauthenticateFragment extends DialogFragment {

    String TAG = "ReauthenticateFragment";
    String mEmail;
    String mPassword;
    boolean showPassword;

    public ReauthenticateFragment() {
        // Required empty public constructor
    }

    public ReauthenticateFragment(String newEmail, String newPassword) {
        this.mEmail = newEmail;
        this.mPassword = newPassword;
    }

    public static ReauthenticateFragment newInstance(String newEmail, String newPassword) {
        return new ReauthenticateFragment(newEmail, newPassword);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reauthenticate, container, false);
        showPassword = false;
        EditText oldEmail = rootView.findViewById(R.id.old_email);
        EditText oldPassword = rootView.findViewById(R.id.old_password);
        ImageButton hidePasswordBtn = rootView.findViewById(R.id.hide_password);
        hidePasswordBtn.setOnClickListener(v -> {
            showPassword = !showPassword;
            if (showPassword) {
                hidePasswordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                hidePasswordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_crossed));
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        AppCompatButton reAuthenticationBtn = rootView.findViewById(R.id.re_authentication);
        reAuthenticationBtn.setOnClickListener(v -> {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(oldEmail.getText().toString(), oldPassword.getText().toString());
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(unused -> {
                        if (!mEmail.isEmpty())
                            currentUser.updateEmail(mEmail)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            appUser.setEmail(mEmail);
                                            makeLogD(TAG, "User email address updated.");
                                            FirebaseFirestore.getInstance().collection("Uporabniki")
                                                    .document(userID).set(appUser);
                                        }
                                        else
                                            makeLogW(TAG, Objects.requireNonNull(task1.getException()).getMessage());
                                    });
                        if (!mPassword.isEmpty())
                            currentUser.updatePassword(mPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful())
                                            makeLogD(TAG, "User password updated.");
                                        else
                                            makeLogW(TAG, Objects.requireNonNull(task1.getException()).getMessage());
                                    });
                        this.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        makeLogW(TAG, "Got error: " + e.getMessage());
                        Toast.makeText(requireContext(), "Prijava ni uspela, poskusite ponovno.", Toast.LENGTH_SHORT).show();
                    });
        });
        return rootView;
    }
}
package diplomska.naloga.vselokalno.SignInUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;

import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;

import static android.content.Context.MODE_PRIVATE;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class SignInFragment extends Fragment {

    //    Tag:
    final String TAG = "SignInFragment";
    //    Firebase Authentication;
    FirebaseAuth mFirebaseAuth;
    //    Edit texts:
    AppCompatEditText emailET;
    AppCompatEditText passwordET;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Cancel.
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        emailET = rootView.findViewById(R.id.email_et_signIn);
        passwordET = rootView.findViewById(R.id.password_et_signIn);

        // Proceed.
        MaterialButton signInBtn = rootView.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(v -> signIn());

        return rootView;
    } // onCreateView

    public void signIn() {
        final String email = Objects.requireNonNull(emailET.getText()).toString();
        final String password = Objects.requireNonNull(passwordET.getText()).toString();
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Prosimo vnesite e-poštni naslov.", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(getContext(), "Prosimo vnesite geslo.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SignInUpActivity.isValidEmail(email)) {
            try {
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                makeLogD(TAG, "(signIn) Sign in successful.");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                makeLogW(TAG, "(signIn) Sign in unsuccessful.");
                                Toast.makeText(getContext(), "Geslo se ne ujema z e-poštnim naslovom. Preverite e-poštni naslov in geslo.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                makeLogW(TAG, "(signIn) \t" + e.getMessage());
                Toast.makeText(getContext(), "Prišlo je do napake, ponovno poskusite s prijavo!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "E-poštni naslov ni pravilne oblike.", Toast.LENGTH_SHORT).show();
        }
    } // signIn

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            makeLogD(TAG, "(updateUI) currentUser is NOT null!");
//            SharedPreferences mPrefs = requireActivity().getSharedPreferences(getString(R.string.shared_pref_file), MODE_PRIVATE);
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            DocumentReference docRef = db.collection("Uporabniki").document(currentUser.getUid());
//            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    makeLogD(TAG, "Getting user by id: " + currentUser.getUid());
//                    userData = documentSnapshot.toObject(User.class);
//                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                    Gson gson = new Gson();
//                    String json = gson.toJson(userData);
//                    prefsEditor.putString("User data", json);
//                    prefsEditor.apply();
//                    makeLogD(TAG, "(updateUI) User data saved:\n" + json);
//
//                    Intent intent = new Intent(requireContext(), MainActivity.class);
//                    startActivity(intent);
//                    requireActivity().finish();
//                }
//            });
        } else {
            makeLogW(TAG, "(updateUI) currentUser IS null!");
        }
    } // updateUI
}
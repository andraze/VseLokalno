package diplomska.naloga.vselokalno.SignInUp;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        emailET = rootView.findViewById(R.id.email_et_signIn);
        passwordET = rootView.findViewById(R.id.password_et_signIn);

        MaterialButton signInBtn = rootView.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        return rootView;
    }

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
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    makeLogD(TAG, "(signIn) Sign in successful.");
                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    makeLogW(TAG, "(signIn) Sign in unsuccessful.");
                                    Toast.makeText(getContext(), "Geslo se ne ujema z e-poštnim naslovom.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (Exception e) {
                makeLogW(TAG, "(signIn) \t" + e.getMessage());
                Toast.makeText(getContext(), "Prišlo je do napake, ponovno poskusite s prijavo!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "E-poštni naslov ni pravilne oblike.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            makeLogD(TAG, "(updateUI) currentUser is NOT null!");
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            makeLogW(TAG, "(updateUI) currentUser IS null!");
        }
    }
}
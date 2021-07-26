package diplomska.naloga.vselokalno.SignInUp.SignUp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import diplomska.naloga.vselokalno.MainActivity;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class UserPasswordFragment extends Fragment {

    AppCompatEditText passwordET;
    AppCompatEditText repeatPasswordET;

    public UserPasswordFragment() {
        // Required empty public constructor
    }

    public static UserPasswordFragment newInstance() {
        return new UserPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_password, container, false);
        passwordET = rootView.findViewById(R.id.password_et_UserPassword);
        repeatPasswordET = rootView.findViewById(R.id.password_repeat_et_UserPassword);

//        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

//        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_userPassword);
        nexBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(passwordET.getText()).toString().isEmpty() || Objects.requireNonNull(repeatPasswordET.getText()).toString().isEmpty()) {
                Toast.makeText(requireContext(), "Najprej dvakrat vnesite geslo.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordET.getText().toString().length() < 6) {
                Toast.makeText(requireContext(), "Geslo mora biti dolgo vsaj 6 znakov!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Objects.requireNonNull(passwordET.getText()).toString().equals(Objects.requireNonNull(repeatPasswordET.getText()).toString()))
                Toast.makeText(requireContext(), "Gesli se ne ujemata.", Toast.LENGTH_SHORT).show();
            else {
                userData.setPassword(passwordET.getText().toString());
                FINALChoosePhotoFragment finalChoosePhotoFragment = FINALChoosePhotoFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right
                        )
                        .replace(R.id.fragment_container_view_signINUP, finalChoosePhotoFragment)
                        .addToBackStack(null)
                        .commit();


            }

        });
        return rootView;
    } // onCreateView
}
package diplomska.naloga.vselokalno.SignInUp.SignUp;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.SignInUp.CreateAFarm.FarmNameFragment;

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
                if (!userData.isLastnik_kmetije()) {
                    UserNameFragment userNameFragment = UserNameFragment.newInstance();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_from_right, R.anim.exit_to_left,
                                    R.anim.enter_from_left, R.anim.exit_to_right
                            )
                            .replace(R.id.fragment_container_view_signINUP, userNameFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    FarmNameFragment farmNameFragment = FarmNameFragment.newInstance();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_from_right, R.anim.exit_to_left,
                                    R.anim.enter_from_left, R.anim.exit_to_right
                            )
                            .replace(R.id.fragment_container_view_signINUP, farmNameFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

        });
        return rootView;
    } // onCreateView
}
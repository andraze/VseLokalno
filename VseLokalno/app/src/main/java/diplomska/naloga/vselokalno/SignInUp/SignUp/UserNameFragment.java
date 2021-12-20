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

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class UserNameFragment extends Fragment {

    //    Edit text
    AppCompatEditText nameET;
    AppCompatEditText surrnameET;

    public UserNameFragment() {
        // Required empty public constructor
    }

    public static UserNameFragment newInstance() {
        return new UserNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_name, container, false);

        nameET = rootView.findViewById(R.id.name_et_userNameFragment);
        surrnameET = rootView.findViewById(R.id.surrname_et_userNameFragment);

//        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

//        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_userName);
        nexBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(nameET.getText()).toString().isEmpty() || Objects.requireNonNull(surrnameET.getText()).toString().isEmpty()) {
                Toast.makeText(requireContext(), "Najprej vnesite ime in priimek.", Toast.LENGTH_SHORT).show();
                return;
            }
            userData.setIme_uporabnika(Objects.requireNonNull(nameET.getText()).toString());
            userData.setPriimek_uporabnika(Objects.requireNonNull(surrnameET.getText()).toString());
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
        });
        return rootView;
    } // onCreateView
}
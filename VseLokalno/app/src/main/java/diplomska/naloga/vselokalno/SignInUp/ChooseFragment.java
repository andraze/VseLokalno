package diplomska.naloga.vselokalno.SignInUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.SignInUp.SignUp.UserTypeFragment;
import diplomska.naloga.vselokalno.DataObjects.User;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class ChooseFragment extends Fragment {

    public ChooseFragment() {
        // Required empty public constructor
    }

    public static ChooseFragment newInstance() {
        return new ChooseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose, container, false);

        MaterialButton signInBtn = rootView.findViewById(R.id.vpisi_se_btm);
        signInBtn.setOnClickListener(v -> {
            userData = new User();
            SignInFragment signInFragment = SignInFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, signInFragment)
                    .addToBackStack("chooser_stage")
                    .commit();
        });

        MaterialButton signUpBtn = rootView.findViewById(R.id.zacnimo_btn);
        signUpBtn.setOnClickListener(v -> {
            userData = new User();
            UserTypeFragment userTypeFragment = UserTypeFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, userTypeFragment)
                    .addToBackStack("chooser_stage")
                    .commit();
        });

        return rootView;
    } // onCreateView
}
package diplomska.naloga.vselokalno.SignInUp.SignUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class UserTypeFragment extends Fragment {

    boolean tempLastnikKmetije;

    public UserTypeFragment() {
        // Required empty public constructor
    }

    public static UserTypeFragment newInstance() {
        return new UserTypeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_type, container, false);

        MaterialButton proizvajalecBtn = rootView.findViewById(R.id.btn_proizvajalec);
        MaterialButton kupecBtn = rootView.findViewById(R.id.btn_kupec);

//        Chosen farmer
        proizvajalecBtn.setOnClickListener(v -> {
            proizvajalecBtn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            proizvajalecBtn.setTextColor(getResources().getColor(R.color.white, null));
            proizvajalecBtn.setElevation(0);
            kupecBtn.setBackgroundColor(getResources().getColor(R.color.white, null));
            kupecBtn.setTextColor(getResources().getColor(R.color.blue_normal, null));
            kupecBtn.setElevation(2);
            tempLastnikKmetije = true;
        });
//        Chosen buyer
        kupecBtn.setOnClickListener(v -> {
            kupecBtn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            kupecBtn.setTextColor(getResources().getColor(R.color.white, null));
            kupecBtn.setElevation(0);
            proizvajalecBtn.setBackgroundColor(getResources().getColor(R.color.white, null));
            proizvajalecBtn.setTextColor(getResources().getColor(R.color.blue_normal, null));
            proizvajalecBtn.setElevation(2);
            tempLastnikKmetije = false;
        });
//        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
//        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_userType);
        nexBtn.setOnClickListener(v -> {
            userData.setLastnik_kmetije(tempLastnikKmetije);
            UserEmailFragment userEmailFragment = UserEmailFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, userEmailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }
}
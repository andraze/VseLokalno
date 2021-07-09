package diplomska.naloga.vselokalno.SignInUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import diplomska.naloga.vselokalno.R;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose, container, false);

        MaterialButton signInBtn = rootView.findViewById(R.id.vpisi_se_btm);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        MaterialButton signUpBtn = rootView.findViewById(R.id.zacnimo_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = SignUpFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right
                        )
                        .replace(R.id.fragment_container_view_signINUP, signUpFragment)
                        .addToBackStack("chooser_stage")
                        .commit();
            }
        });

        return rootView;
    }
}
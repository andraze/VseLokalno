package diplomska.naloga.vselokalno.SignInUp.CreateAFarm;

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

import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.SignInUp.SignUp.UserPasswordFragment;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.farmData;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class FarmNameFragment extends Fragment {

    //    Edit text
    AppCompatEditText nameET;

    public FarmNameFragment() {
        // Required empty public constructor
    }

    public static FarmNameFragment newInstance() {
        return new FarmNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_farm_name, container, false);
        nameET = rootView.findViewById(R.id.name_et_farmNameFragment);
        //        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            farmData = new Kmetija();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        //        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_farmName);
        nexBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(nameET.getText()).toString().isEmpty()) {
                Toast.makeText(requireContext(), "Najprej vnesite ime kmetije.", Toast.LENGTH_SHORT).show();
                return;
            }
            farmData.setIme_kmetije(Objects.requireNonNull(nameET.getText()).toString());
            FarmDescriptionFragment farmDescriptionFragment = FarmDescriptionFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, farmDescriptionFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    } // onCreateView
}
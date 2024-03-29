package diplomska.naloga.vselokalno.SignInUp.CreateAFarm;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.R;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.farmData;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.farmTimeFragment;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

public class FarmLocationFragment extends Fragment {

    //    Edit text
    AppCompatEditText addressET;
    AppCompatEditText postalNumET;
    AppCompatEditText cityET;

    public FarmLocationFragment() {
        // Required empty public constructor
    }

    public static FarmLocationFragment newInstance() {
        return new FarmLocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_farm_location, container, false);
        addressET = rootView.findViewById(R.id.location_et_farmLocationFragment);
        postalNumET = rootView.findViewById(R.id.postalNum_et_farmLocationFragment);
        cityET = rootView.findViewById(R.id.city_et_farmLocationFragment);
        //        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            farmData = new Farm();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        //        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_farmLocation);
        nexBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(addressET.getText()).toString().isEmpty()
                    || Objects.requireNonNull(postalNumET.getText()).toString().isEmpty()
                    || Objects.requireNonNull(cityET.getText()).toString().isEmpty()) {
                Toast.makeText(requireContext(), "Najprej vnesite naslov vaše kmetije.", Toast.LENGTH_SHORT).show();
                return;
            }
            farmData.setNaslov_kmetije(addressET.getText().toString() + ", " +
                    postalNumET.getText().toString() + ", " +
                    cityET.getText().toString()
            );
            farmTimeFragment = FarmTimeFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, farmTimeFragment)
                    .addToBackStack(null)
                    .commit();
        });
        return rootView;
    }
}
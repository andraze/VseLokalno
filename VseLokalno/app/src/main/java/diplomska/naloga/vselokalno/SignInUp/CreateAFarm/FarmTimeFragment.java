package diplomska.naloga.vselokalno.SignInUp.CreateAFarm;

import static diplomska.naloga.vselokalno.MainActivity.timeIDs;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.farmData;
import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.R;

public class FarmTimeFragment extends Fragment {

    ArrayList<Boolean> selectedTimes = new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false,
            false, false, false, false, false, false, false));

    Map<String, ArrayList<Boolean>> weeklyTimeline = new HashMap<>();

    public FarmTimeFragment() {
        // Required empty public constructor
    }

    public static FarmTimeFragment newInstance() {
        return new FarmTimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Toggle for each of the days:
        View rootView = inflater.inflate(R.layout.fragment_farm_time, container, false);
        CheckedTextView pon = rootView.findViewById(R.id.pon);
        pon.setOnClickListener(v -> {
            pon.toggle();
            if (pon.isChecked())
                weeklyTimeline.put("pon", selectedTimes);
            else
                weeklyTimeline.remove("pon");

        });
        CheckedTextView tor = rootView.findViewById(R.id.tor);
        tor.setOnClickListener(v -> {
            tor.toggle();
            if (tor.isChecked())
                weeklyTimeline.put("tor", selectedTimes);
            else
                weeklyTimeline.remove("tor");
        });
        CheckedTextView sre = rootView.findViewById(R.id.sre);
        sre.setOnClickListener(v -> {
            sre.toggle();
            if (sre.isChecked())
                weeklyTimeline.put("sre", selectedTimes);
            else
                weeklyTimeline.remove("sre");
        });
        CheckedTextView cet = rootView.findViewById(R.id.cet);
        cet.setOnClickListener(v -> {
            cet.toggle();
            if (cet.isChecked())
                weeklyTimeline.put("cet", selectedTimes);
            else
                weeklyTimeline.remove("cet");
        });
        CheckedTextView pet = rootView.findViewById(R.id.pet);
        pet.setOnClickListener(v -> {
            pet.toggle();
            if (pet.isChecked())
                weeklyTimeline.put("pet", selectedTimes);
            else
                weeklyTimeline.remove("pet");
        });
        CheckedTextView sob = rootView.findViewById(R.id.sob);
        sob.setOnClickListener(v -> {
            sob.toggle();
            if (sob.isChecked())
                weeklyTimeline.put("sob", selectedTimes);
            else
                weeklyTimeline.remove("sob");
        });
        CheckedTextView ned = rootView.findViewById(R.id.ned);
        ned.setOnClickListener(v -> {
            ned.toggle();
            if (ned.isChecked())
                weeklyTimeline.put("ned", selectedTimes);
            else
                weeklyTimeline.remove("ned");
        });
        //        Cancel
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.pop_to_choser_btn);
        cancelBtn.setOnClickListener(v -> {
            userData = new User();
            farmData = new Kmetija();
            requireActivity().getSupportFragmentManager().popBackStack("chooser_stage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        //        Next
        MaterialButton nexBtn = rootView.findViewById(R.id.next_btn_farmTime);
        nexBtn.setOnClickListener(v -> {
            boolean atLeastOne = false;
            for (boolean b : selectedTimes)
                if (b) {
                    atLeastOne = true;
                    break;
                }
            if (!atLeastOne) {
                Toast.makeText(requireContext(), "Izberite vsaj en dan in en termin.", Toast.LENGTH_SHORT).show();
                return;
            }
            farmData.setCas_prevzema(weeklyTimeline);
            FINALFarmPictureFragment finalFarmPictureFragment = FINALFarmPictureFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_container_view_signINUP, finalFarmPictureFragment)
                    .addToBackStack(null)
                    .commit();
        });
        return rootView;
    } // onCreateView

    public void selectTime(MaterialButton btn) {
        for (int i = 0; i < timeIDs.length; i++) {
            if (btn.getId() == timeIDs[i]) {
                Boolean temp = selectedTimes.get(i);
                selectedTimes.set(i, !temp);
                if (!temp) {
                    // Selected
                    btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
                    btn.setTextColor(getResources().getColor(R.color.white, null));
                    btn.setElevation(0);
                } else {
                    // Unselected
                    btn.setBackgroundColor(getResources().getColor(R.color.white, null));
                    btn.setTextColor(getResources().getColor(R.color.blue_normal, null));
                    btn.setElevation(2);
                }
                weeklyTimeline.forEach((s, booleans) -> weeklyTimeline.replace(s, selectedTimes));
                break;
            }
        }
    } // selectTime
}
package diplomska.naloga.vselokalno.UserFunctions.ProfileSettings_FU;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.timeIDs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import diplomska.naloga.vselokalno.R;

public class EditFarmHoursFragment extends Fragment {

    public interface EditFarmHoursFragmentCallback {
        void onSaveChanges();
    }

    final String TAG = "EditFarmHoursFragment";
    EditFarmHoursFragmentCallback mCallback;

    ArrayList<Boolean> selectedTimes = new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false,
            false, false, false, false, false, false, false));

    Map<String, ArrayList<Boolean>> weeklyTimeline = new HashMap<>();

    public EditFarmHoursFragment() {
        // Required empty public constructor
    }

    public EditFarmHoursFragment(EditFarmHoursFragmentCallback editFarmHoursFragmentCallback) {
        this.mCallback = editFarmHoursFragmentCallback;
    }

    public static EditFarmHoursFragment newInstance(EditFarmHoursFragmentCallback editFarmHoursFragmentCallback) {
        return new EditFarmHoursFragment(editFarmHoursFragmentCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_farm_hours, container, false);
        weeklyTimeline = appFarm.getCas_prevzema();
        String key = (String) weeklyTimeline.keySet().toArray()[0];
        selectedTimes = weeklyTimeline.get(key);
        setOldTimes(rootView);
        // Toggle for each of the days:
        CheckedTextView pon = rootView.findViewById(R.id.pon);
        if (weeklyTimeline.containsKey("Pon"))
            pon.setChecked(true);
        pon.setOnClickListener(v -> {
            pon.toggle();
            if (pon.isChecked())
                weeklyTimeline.put("Pon", selectedTimes);
            else
                weeklyTimeline.remove("Pon");
        });
        CheckedTextView tor = rootView.findViewById(R.id.tor);
        if (weeklyTimeline.containsKey("Tor"))
            tor.setChecked(true);
        tor.setOnClickListener(v -> {
            tor.toggle();
            if (tor.isChecked())
                weeklyTimeline.put("Tor", selectedTimes);
            else
                weeklyTimeline.remove("Tor");
        });
        CheckedTextView sre = rootView.findViewById(R.id.sre);
        if (weeklyTimeline.containsKey("Sre"))
            sre.setChecked(true);
        sre.setOnClickListener(v -> {
            sre.toggle();
            if (sre.isChecked())
                weeklyTimeline.put("Sre", selectedTimes);
            else
                weeklyTimeline.remove("Sre");
        });
        CheckedTextView cet = rootView.findViewById(R.id.cet);
        if (weeklyTimeline.containsKey("Čet"))
            cet.setChecked(true);
        cet.setOnClickListener(v -> {
            cet.toggle();
            if (cet.isChecked())
                weeklyTimeline.put("Čet", selectedTimes);
            else
                weeklyTimeline.remove("Čet");
        });
        CheckedTextView pet = rootView.findViewById(R.id.pet);
        if (weeklyTimeline.containsKey("Pet"))
            pet.setChecked(true);
        pet.setOnClickListener(v -> {
            pet.toggle();
            if (pet.isChecked())
                weeklyTimeline.put("Pet", selectedTimes);
            else
                weeklyTimeline.remove("Pet");
        });
        CheckedTextView sob = rootView.findViewById(R.id.sob);
        if (weeklyTimeline.containsKey("Sob"))
            sob.setChecked(true);
        sob.setOnClickListener(v -> {
            sob.toggle();
            if (sob.isChecked())
                weeklyTimeline.put("Sob", selectedTimes);
            else
                weeklyTimeline.remove("Sob");
        });
        CheckedTextView ned = rootView.findViewById(R.id.ned);
        if (weeklyTimeline.containsKey("Ned"))
            ned.setChecked(true);
        ned.setOnClickListener(v -> {
            ned.toggle();
            if (ned.isChecked())
                weeklyTimeline.put("Ned", selectedTimes);
            else
                weeklyTimeline.remove("Ned");
        });
        //        Next
        AppCompatButton nexBtn = rootView.findViewById(R.id.save_changes);
        nexBtn.setOnClickListener(v -> {
            boolean atLeastOne = false;
            for (boolean b : selectedTimes)
                if (b) {
                    atLeastOne = true;
                    break;
                }
            if (!atLeastOne || weeklyTimeline.isEmpty()) {
                Toast.makeText(requireContext(), "Izberite vsaj en dan in en termin.", Toast.LENGTH_SHORT).show();
                return;
            }
            makeLogD(TAG, weeklyTimeline.toString());
            appFarm.setCas_prevzema(weeklyTimeline);
            mCallback.onSaveChanges();
            getParentFragmentManager().popBackStack();
        });
        TextView discardChangesBtn = rootView.findViewById(R.id.discard_changes);
        discardChangesBtn.setOnClickListener(v-> getParentFragmentManager().popBackStack());
        return rootView;
    } // onCreateView

    void setOldTimes(View rootView) {
        for (int i = 0; i < timeIDs.length; i++) {
            if (selectedTimes.get(i)) {
                MaterialButton btn = rootView.findViewById(timeIDs[i]);
                btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
                btn.setTextColor(getResources().getColor(R.color.white, null));
                btn.setElevation(0);
            }
        }
    }

    public void selectNewTime(MaterialButton btn) {
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
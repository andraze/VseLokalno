package diplomska.naloga.vselokalno.UserFunctions.Basket.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.timeIDs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import diplomska.naloga.vselokalno.R;

public class OrderingFragment extends Fragment {

    private int farmNumber = 0;
    private int indexTimeSelected = -1;

    public OrderingFragment() {
        // Required empty public constructor
    }

    public static OrderingFragment newInstance() {
        return new OrderingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ordering, container, false);

        return rootView;
    }

    public void selectTimeOrder(MaterialButton btn) {
        if (indexTimeSelected < 0) { // No other time selected.
            indexTimeSelected = btn.getId();
            btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            btn.setTextColor(getResources().getColor(R.color.white, null));
            for (int el : timeIDs) {
                if (el == indexTimeSelected) continue;
                // Make all other buttons inactive. TODO
            }
        }
        /*for (int i = 0; i < timeIDs.length; i++) {
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
        }*/
    } // selectTime
}
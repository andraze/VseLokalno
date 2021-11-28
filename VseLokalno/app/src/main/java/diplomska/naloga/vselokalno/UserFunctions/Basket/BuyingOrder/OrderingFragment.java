package diplomska.naloga.vselokalno.UserFunctions.Basket.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.timeIDs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import diplomska.naloga.vselokalno.R;

public class OrderingFragment extends Fragment {

    private int farmNumber = 0;
    private int indexTimeSelected = -1;
    ArrayList<MaterialButton> allTimeViews;

    public OrderingFragment() {
        // Required empty public constructor
    }

    public static OrderingFragment newInstance() {
        return new OrderingFragment();
    } // newInstance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ordering, container, false);
        allTimeViews = new ArrayList<>();
        allTimeViews.add(rootView.findViewById(R.id.time_7_8));
        allTimeViews.add(rootView.findViewById(R.id.time_8_9));
        allTimeViews.add(rootView.findViewById(R.id.time_9_10));
        allTimeViews.add(rootView.findViewById(R.id.time_10_11));
        allTimeViews.add(rootView.findViewById(R.id.time_11_12));
        allTimeViews.add(rootView.findViewById(R.id.time_12_13));
        allTimeViews.add(rootView.findViewById(R.id.time_13_14));
        allTimeViews.add(rootView.findViewById(R.id.time_14_15));
        allTimeViews.add(rootView.findViewById(R.id.time_15_16));
        allTimeViews.add(rootView.findViewById(R.id.time_16_17));
        allTimeViews.add(rootView.findViewById(R.id.time_17_18));
        allTimeViews.add(rootView.findViewById(R.id.time_18_19));
        allTimeViews.add(rootView.findViewById(R.id.time_19_20));
        allTimeViews.add(rootView.findViewById(R.id.time_20_21));
        return rootView;
    }

    public void selectTimeOrder(MaterialButton btn) {
        if (indexTimeSelected != btn.getId()) {
            indexTimeSelected = btn.getId();
            btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            btn.setTextColor(getResources().getColor(R.color.white, null));
            for (MaterialButton el : allTimeViews) {
                if (el.getId() == indexTimeSelected) continue;
                el.setBackgroundColor(getResources().getColor(R.color.disabled_btn_gray, null));
                el.setElevation(0);
            }
        } else {
            indexTimeSelected = -1;
            for (MaterialButton el : allTimeViews) {
                el.setBackgroundColor(getResources().getColor(R.color.white, null));
                el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                el.setElevation(2);
            }
        }
    } // selectTime
}
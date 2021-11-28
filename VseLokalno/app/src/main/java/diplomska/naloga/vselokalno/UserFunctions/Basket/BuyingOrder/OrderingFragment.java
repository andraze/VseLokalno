package diplomska.naloga.vselokalno.UserFunctions.Basket.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.Basket.BasketRecyclerAdapter;

public class OrderingFragment extends Fragment implements OrderRecyclerAdapter.OrderSelectDateListener {

    private int farmNumber;
    private int indexTimeSelected = -1;
    ArrayList<MaterialButton> allTimeViews;
    TextView farmNameView;
    RecyclerView availableDaysRecyclerView;
    OrderRecyclerAdapter mAdapter;
    OrderRecyclerAdapter.OrderSelectDateListener mOrderSelectDateListener;


    public OrderingFragment() {
        // Required empty public constructor
    }

    public OrderingFragment(int farmIndex) {
        this.farmNumber = farmIndex;
        this.mOrderSelectDateListener = this;
    }


    public static OrderingFragment newInstance(int farmIndex) {
        return new OrderingFragment(farmIndex);
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
        for (MaterialButton btn : allTimeViews) {
            btn.setOnClickListener(this::selectTimeOrder);
        }
        farmNameView = rootView.findViewById(R.id.farm_name_tv_OrderingFragment);
        farmNameView.setText(appBasket.get(farmNumber).getIme_kmetije());
        rootView.findViewById(R.id.cancel_order_fab_OrderingFragment).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack("ProceedToBuying", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        rootView.findViewById(R.id.continue_roder_fab_OrderingFragment).setOnClickListener(v -> {
            if (farmNumber + 1 == appBasket.size()) {
                // TODO: Finish order.
            } else {
                OrderingFragment orderingFragment = OrderingFragment.newInstance(farmNumber + 1);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, orderingFragment)
                        .addToBackStack("ProceedToBuying")
                        .commit();
            }
        });
        availableDaysRecyclerView = rootView.findViewById(R.id.recycler_view_date_OrderingFragment);
        mAdapter = new OrderRecyclerAdapter(requireContext(), farmNumber, mOrderSelectDateListener);
        availableDaysRecyclerView.setAdapter(mAdapter);
        availableDaysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        return rootView;
    }

    public void selectTimeOrder(View view) {
        MaterialButton btn = (MaterialButton) view;
        if (indexTimeSelected != btn.getId()) {
            indexTimeSelected = btn.getId();
            btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            btn.setTextColor(getResources().getColor(R.color.white, null));
            for (MaterialButton el : allTimeViews) {
                if (el.getId() == indexTimeSelected) continue;
                el.setBackgroundColor(getResources().getColor(R.color.disabled_btn_gray, null));
                el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                el.setElevation(0);
            }
        } else {
            indexTimeSelected = -1;
            for (MaterialButton el : allTimeViews) {
                el.setBackgroundColor(getResources().getColor(R.color.white, null));
                el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                el.setElevation(4);
            }
        }
    } // selectTime

    @Override
    public void onOrderSelectDateListener(int numOfDaysFromToday, View view, TextView t1, TextView t2) {
        // TODO
    }
}
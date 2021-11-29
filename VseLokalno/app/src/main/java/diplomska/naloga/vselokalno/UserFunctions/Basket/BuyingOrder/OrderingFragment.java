package diplomska.naloga.vselokalno.UserFunctions.Basket.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.allTimes;
import static diplomska.naloga.vselokalno.MainActivity.appBasket;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.R;

public class OrderingFragment extends Fragment implements OrderRecyclerAdapter.OrderSelectDateListener {

    private final String TAG = "OrderingFragment";
    // Current farm index:
    private int farmNumber;
    // current farm object:
    Kmetija farmOfInterest;
    // Time and day selected:
    private int indexTimeSelected = -1;
    private int indexDaySelected = -1;
    // Views for time selection:
    ArrayList<MaterialButton> allTimeViews;
    ArrayList<Boolean> availableTimes;
    // Views for day selection:
    View dayView;
    TextView dayNumberView;
    TextView dayNameView;
    TextView farmNameView;
    // Firestore:
    private FirebaseFirestore db;
    // Recycler:
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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ordering, container, false);
        availableTimes = new ArrayList<>();
        allTimeViews = new ArrayList<>();
        allTimeViews.add(rootView.findViewById(R.id.time_7_8));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_8_9));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_9_10));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_10_11));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_11_12));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_12_13));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_13_14));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_14_15));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_15_16));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_16_17));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_17_18));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_18_19));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_19_20));
        availableTimes.add(false);
        allTimeViews.add(rootView.findViewById(R.id.time_20_21));
        availableTimes.add(false);
        for (MaterialButton btn : allTimeViews) {
            btn.setOnClickListener(this::selectTimeOrder);
        }
        farmNameView = rootView.findViewById(R.id.farm_name_tv_OrderingFragment);
        farmNameView.setText(appBasket.get(farmNumber).getIme_kmetije());
        rootView.findViewById(R.id.cancel_order_fab_OrderingFragment).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack("ProceedToBuying", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        rootView.findViewById(R.id.continue_roder_fab_OrderingFragment).setOnClickListener(v -> {
            if (indexDaySelected == -1) {
                Toast.makeText(requireContext(), "Najprej izberite možen dan dostave.", Toast.LENGTH_SHORT).show();
            } else if (indexTimeSelected == -1) {
                Toast.makeText(requireContext(), "Najprej izberite možno uro dostave.", Toast.LENGTH_SHORT).show();
            } else {
                if (farmNumber + 1 == appBasket.size()) {
                    // TODO: Finish order.
                    makeLogD(TAG, appBasket.get(0).getDatum_dostave().toString());
                } else {
                    setDateOfPickup();
                    OrderingFragment orderingFragment = OrderingFragment.newInstance(farmNumber + 1);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_fragment_container, orderingFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        availableDaysRecyclerView = rootView.findViewById(R.id.recycler_view_date_OrderingFragment);
        DocumentReference farmDocReference = db.collection("Kmetije").document(appBasket.get(farmNumber).getId_kmetije());
        farmDocReference.get().addOnSuccessListener(documentSnapshot -> {
            farmOfInterest = documentSnapshot.toObject(Kmetija.class);
            if (farmOfInterest != null) {
                makeLogD(TAG, "Got farm: " + farmOfInterest);
                mAdapter = new OrderRecyclerAdapter(requireContext(), farmNumber, mOrderSelectDateListener, farmOfInterest);
                availableDaysRecyclerView.setAdapter(mAdapter);
                availableDaysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        });
        return rootView;
    }

    @SuppressLint("SimpleDateFormat")
    private void setDateOfPickup() {
        Date date = new Date();
        appBasket.get(farmNumber).setDatum_narocila(new Timestamp(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, indexDaySelected);
        date = calendar.getTime();
        String dateCalendar = new SimpleDateFormat("dd-MM-yyyy").format(date);
        String dateTime = "";
        for (int i = 0; i < allTimeViews.size(); i++) {
            if (allTimeViews.get(i).getId() == indexTimeSelected) {
                dateTime = allTimes[i] + ":0.0";
                break;
            }
        }
        DateFormat dateFormatOut = new SimpleDateFormat("dd-MM-yyy mm:hh");
        try {
            Date dateOut = dateFormatOut.parse(dateCalendar + " " + dateTime);
            appBasket.get(farmNumber).setDatum_dostave(new Timestamp(Objects.requireNonNull(dateOut)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void selectTimeOrder(View view) {
        if (view != null && indexTimeSelected != view.getId()) {
            MaterialButton btn = (MaterialButton) view;
            for (int i = 0; i < allTimeViews.size(); i++) {
                if (allTimeViews.get(i).getId() == btn.getId()) {
                    if (!availableTimes.get(i)) {
                        Toast.makeText(requireContext(), "To uro prevzem ni mogoč.", Toast.LENGTH_SHORT).show();
                        return;
                    } else break;
                }
            }
            indexTimeSelected = btn.getId();
            btn.setBackgroundColor(getResources().getColor(R.color.green_light, null));
            btn.setTextColor(getResources().getColor(R.color.white, null));
            btn.setElevation(0);
            for (int i = 0; i < allTimeViews.size(); i++) {
                MaterialButton el = allTimeViews.get(i);
                if (el.getId() == indexTimeSelected) continue;
                if (availableTimes.get(i)) {
                    el.setBackgroundColor(getResources().getColor(R.color.white, null));
                    el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                    el.setElevation(4);
                } else {
                    el.setBackgroundColor(getResources().getColor(R.color.disabled_btn_gray, null));
                    el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                    el.setElevation(0);
                }
            }
        } else {
            indexTimeSelected = -1;
            for (int i = 0; i < allTimeViews.size(); i++) {
                MaterialButton el = allTimeViews.get(i);
                if (availableTimes.get(i)) {
                    el.setBackgroundColor(getResources().getColor(R.color.white, null));
                    el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                    el.setElevation(4);
                } else {
                    el.setBackgroundColor(getResources().getColor(R.color.disabled_btn_gray, null));
                    el.setTextColor(getResources().getColor(R.color.blue_normal, null));
                    el.setElevation(0);
                }
            }
        }
    } // selectTime

    @Override
    public void onOrderSelectDateListener(int numOfDaysFromToday, View view, TextView t1, TextView t2) {
        if (indexDaySelected == numOfDaysFromToday) { // Deselect the day.
            indexDaySelected = -1;
            dayView = null;
            dayNameView = null;
            dayNumberView = null;
            view.setBackgroundColor(getResources().getColor(R.color.white));
            t1.setTextColor(getResources().getColor(R.color.blue_normal));
            t2.setTextColor(getResources().getColor(R.color.blue_normal));
            availableTimes.clear();
            for (int i = 0; i < 14; i++) {
                availableTimes.add(false);
            }
        } else {
            if (indexDaySelected != -1) {
                dayView.setBackgroundColor(getResources().getColor(R.color.white));
                dayNameView.setTextColor(getResources().getColor(R.color.blue_normal));
                dayNumberView.setTextColor(getResources().getColor(R.color.blue_normal));
            }
            indexDaySelected = numOfDaysFromToday;
            availableTimes = new ArrayList<>(Objects.requireNonNull(farmOfInterest.getCas_prevzema().get(t1.getText().toString())));
            dayView = view;
            dayNameView = t1;
            dayNumberView = t2;
            dayView.setBackgroundColor(getResources().getColor(R.color.green_light));
            dayNameView.setTextColor(getResources().getColor(R.color.white));
            dayNumberView.setTextColor(getResources().getColor(R.color.white));
        }
        selectTimeOrder(null);
    }
}
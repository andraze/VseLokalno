package diplomska.naloga.vselokalno.UserFunctions.Basket_U.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.allTimes;
import static diplomska.naloga.vselokalno.MainActivity.appBasket;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.OrderNotifications.MyPostRequestSender;
import diplomska.naloga.vselokalno.R;

public class OrderingFragment extends Fragment implements OrderRecyclerAdapter.OrderSelectDateListener {

    private final String TAG = "OrderingFragment";
    // current farm object:
    Farm farmOfInterest;
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

    public static OrderingFragment newInstance() {
        return new OrderingFragment();
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
        farmNameView.setText(appBasket.get(0).getIme_kmetije());
        rootView.findViewById(R.id.cancel_order_fab_OrderingFragment).setOnClickListener(v -> getParentFragmentManager().popBackStack("ProceedToBuying", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        rootView.findViewById(R.id.continue_roder_fab_OrderingFragment).setOnClickListener(v -> {
            if (indexDaySelected == -1) {
                Toast.makeText(requireContext(), "Najprej izberite možen dan dostave.", Toast.LENGTH_SHORT).show();
            } else if (indexTimeSelected == -1) {
                Toast.makeText(requireContext(), "Najprej izberite možno uro dostave.", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Ste prepričani, da želite poslati naročilo?")
                        .setTitle("Pošlji naročilo");
                // Add the buttons
                builder.setPositiveButton("Da", (dialog, id) -> {
                    if (checkCurrentStorage()) {
                        if (!setDateOfPickup()) {
                            Toast.makeText(requireContext(), "Prišlo je do napake, poskusite ponovno.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        finishOrder();
                        if (appBasket.size() == 1) {
                            appBasket.clear();
                            getParentFragmentManager().popBackStack("ProceedToBuying", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else {
                            appBasket.remove(0);
                            OrderingFragment orderingFragment = OrderingFragment.newInstance();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.main_fragment_container, orderingFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Prišlo je do težave z zalogo nekaterih artiklov, preverite ponudbo in ponovno naročite.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        availableDaysRecyclerView = rootView.findViewById(R.id.recycler_view_date_OrderingFragment);
        DocumentReference farmDocReference = db.collection("Kmetije").document(appBasket.get(0).getId_kmetije());
        farmDocReference.get().addOnSuccessListener(documentSnapshot -> {
            farmOfInterest = documentSnapshot.toObject(Farm.class);
            if (farmOfInterest != null) {
                makeLogD(TAG, "Got farm: " + farmOfInterest);
                mAdapter = new OrderRecyclerAdapter(requireContext(), 0, this, farmOfInterest);
                availableDaysRecyclerView.setAdapter(mAdapter);
                availableDaysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        });
        updateArticles(0, 0);
        makeLogD(TAG, "Started updating articles!");
        return rootView;
    }

    void updateArticles(int orderIndexTemp, int articleIndexTemp) {
        if (orderIndexTemp == appBasket.size()) {
            makeLogW(TAG, "Shouldn't arrive here!");
            return;
        }
        Order order = appBasket.get(orderIndexTemp);
        if (articleIndexTemp == order.getOrdered_articles().size()) {
            makeLogD(TAG, "Stoped updating articles!");
            return;
        }
        Article article = order.getOrdered_articles().get(articleIndexTemp);
        db.collection("Kmetije").document(order.getId_kmetije())
                .collection("Artikli").document(article.getArticle_id())
                .get().addOnSuccessListener(documentSnapshot -> {
            Article a = documentSnapshot.toObject(Article.class);
            if (a != null) {
                a.setArticle_buying_amount(article.getArticle_buying_amount());
                appBasket.get(orderIndexTemp).getOrdered_articles().set(articleIndexTemp, a);
            } else {
                // Article doesn't exist anymore:
                article.setArticle_storage(0.0);
                appBasket.get(orderIndexTemp).getOrdered_articles().set(articleIndexTemp, article);
            }
        });
        updateArticles(orderIndexTemp, articleIndexTemp + 1);
    } // updateArticles

    private boolean checkCurrentStorage() {
        Order order = appBasket.get(0);
        for (Article article : order.getOrdered_articles()) {
            if (article.getArticle_storage() < article.getArticle_buying_amount())
                return false;
        }
        return true;
    } // checkCurrentStorage

    private void finishOrder() {
        // Update order with "Opravljeno", IDs, etc.:
        Order order = appBasket.get(0);
        order.setId_kupca(userID);
        order.setIme_priimek_kupca(appUser.getIme_uporabnika() + " " + appUser.getPriimek_uporabnika());
        order.setOpravljeno(0);
        String uniqueString = String.valueOf(System.currentTimeMillis());
        String orderID = userID + "#" + order.getId_kmetije() + "#" + uniqueString;
        order.setId_order(orderID);
        StringBuilder extraStatus = new StringBuilder();
        boolean out_of_stock = false;
        boolean low_on_stock = false;
        for (Article article : order.getOrdered_articles()) {
            if (out_of_stock && low_on_stock)
                break;
            if (article.getArticle_storage() - article.getArticle_buying_amount() < 1 && !low_on_stock) {
                low_on_stock = true;
                extraStatus.append("#1");
            }
            if (article.getArticle_storage() - article.getArticle_buying_amount() < 0.1 && !out_of_stock) {
                out_of_stock = true;
                extraStatus.append("#4");
            }
        }
        // Update storages for every article ordered.
        syncArticlesInFarm(0);
        // Send "New order" notification:
        MyPostRequestSender myPostRequestSender = new MyPostRequestSender(requireContext());
        String status_message = String.valueOf(order.getOpravljeno()) + extraStatus;
        try {
            myPostRequestSender.sendRequest(order.getId_order(), order.getId_kmetije(), status_message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set order to active order of both users:
        db.collection("Kmetije").document(order.getId_kmetije())
                .collection("Aktivna Naročila").document(orderID)
                .set(order)
                .addOnCompleteListener(farmTask -> makeLogD(TAG, farmTask.toString()));
        // Add active order for buyer:
        db.collection("Uporabniki").document(userID)
                .collection("Aktivna Naročila").document(orderID)
                .set(order)
                .addOnCompleteListener(buyerTask -> makeLogD(TAG, buyerTask.toString()));
    } // finishOrder

    private void syncArticlesInFarm(int articleIndex) {
        Order order = appBasket.get(0);
        if (articleIndex == order.getOrdered_articles().size())
            return;
        Article article = order.getOrdered_articles().get(articleIndex).makeCopy();
        article.setArticle_storage(article.getArticle_storage() - article.getArticle_buying_amount());
        article.setArticle_buying_amount(0.0);
        db.collection("Kmetije").document(article.getFarm_id())
                .collection("Artikli").document(article.getArticle_id())
                .set(article);
        syncArticlesInFarm(articleIndex + 1);
    } // syncArticlesInFarm

    @SuppressLint("SimpleDateFormat")
    private boolean setDateOfPickup() {
        Date date = new Date();
        appBasket.get(0).setDatum_narocila(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, indexDaySelected);
        date = calendar.getTime();
        String dateCalendar = new SimpleDateFormat("E dd-MM-yyyy").format(date);
        String dateTime = "";
        for (int i = 0; i < allTimeViews.size(); i++) {
            if (allTimeViews.get(i).getId() == indexTimeSelected) {
                dateTime = allTimes[i];
                break;
            }
        }
        String dateOfPickupString = dateCalendar + " " + dateTime;
        try {
            Date dateOfPickup = new SimpleDateFormat(Order.DATE_FORMAT).parse(dateOfPickupString);
            appBasket.get(0).setDatum_prevzema(dateOfPickup);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    } // setDateOfPickup

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
    } // onOrderSelectDateListener

    public boolean onBackPressed() {
        getParentFragmentManager().popBackStack("ProceedToBuying", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return true;
    }
}
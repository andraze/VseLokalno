package diplomska.naloga.vselokalno.UserFunctions.OrderHistory_FU.SpecificOrder;

import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.getFullOrderDateSlo;
import static diplomska.naloga.vselokalno.MainActivity.getFullPickupDateSlo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU.SpecificOrder.OrderDetailsRecyclerAdapter;

public class OrderHistoryDetailsFragment extends Fragment {

    final String TAG = "OrderHistoryDetailsFragment";
    // Firestore:
    FirebaseFirestore db;
    // Recycler view and its adapter:
    RecyclerView recyclerView;
    OrderDetailsRecyclerAdapter mAdapter;
    // Current order:
    Order mCurrentOrder;
    // Views:
    TextView orderPriceSum;

    public OrderHistoryDetailsFragment() {
        // Required empty public constructor
    }

    public OrderHistoryDetailsFragment(Order order) {
        this.mCurrentOrder = order;
        db = FirebaseFirestore.getInstance();
    }

    public static OrderHistoryDetailsFragment newInstance(Order order) {
        return new OrderHistoryDetailsFragment(order);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_history_details, container, false);
        orderPriceSum = rootView.findViewById(R.id.price_sum_tv_OrderHistoryDetailsFragment);
        calcPriceSum();
        recyclerView = rootView.findViewById(R.id.recycler_view_articles_of_order_OrderHistoryDetailsFragment);
        if (recyclerView != null) {
            mAdapter = new OrderDetailsRecyclerAdapter(requireContext(), mCurrentOrder);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        TextView nameView = rootView.findViewById(R.id.name_OrderHistoryDetailsFragment);
        ShapeableImageView profileImageView = rootView.findViewById(R.id.profile_image_OrderHistoryDetailsFragment);
        TextView orderDateView = rootView.findViewById(R.id.date_order_OrderHistoryDetailsFragment);
        orderDateView.setText(getFullPickupDateSlo(mCurrentOrder.getDatum_narocila()));
        TextView pickupDateView = rootView.findViewById(R.id.date_pickup_OrderHistoryDetailsFragment);
        pickupDateView.setText(getFullPickupDateSlo(mCurrentOrder.getDatum_prevzema()));
        switch (mCurrentOrder.getOpravljeno()) {
            case 2: // Order is confirmed.
                rootView.findViewById(R.id.lin1).setBackground(getResources().getDrawable(R.drawable.green_background_border));
                break;
            case 3: // Order has been canceled.
                rootView.findViewById(R.id.lin1).setBackground(getResources().getDrawable(R.drawable.error_background_border));
                break;
        }
        if (!appUser.isLastnik_kmetije()) { // We have a user:
            nameView.setText(mCurrentOrder.getIme_kmetije());
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + mCurrentOrder.getId_kmetije());
            GlideApp.with(requireContext()).load(imageRef).error(R.drawable.default_profile_picture).into(profileImageView);
            // Confirm button pressed:
        } else { // We have a farm:
            nameView.setText(mCurrentOrder.getIme_priimek_kupca());
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + mCurrentOrder.getId_kupca());
            GlideApp.with(requireContext()).load(imageRef).error(R.drawable.default_profile_picture).into(profileImageView);
            // Confirm button pressed:
        }
        return rootView;
    }

    @SuppressLint("DefaultLocale")
    void calcPriceSum() {
        double sum = 0;
        for (Article article : mCurrentOrder.getOrdered_articles()) {
            sum += article.getArticle_price() * article.getArticle_buying_amount();
        }
        orderPriceSum.setText(String.format("%.2f", sum));
    }
}
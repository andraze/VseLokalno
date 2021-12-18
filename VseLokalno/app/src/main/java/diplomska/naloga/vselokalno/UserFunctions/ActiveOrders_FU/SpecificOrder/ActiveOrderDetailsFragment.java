package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU.SpecificOrder;

import static diplomska.naloga.vselokalno.MainActivity.appUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.OrderNotifications.MyPostRequestSender;
import diplomska.naloga.vselokalno.R;

public class ActiveOrderDetailsFragment extends Fragment {

    public interface UpdateSpecificOrder {
        void onUpdateOrderCallback(); // Refresh the data.
    }

    final String TAG = "ActiveOrderDetailsFragment";
    // Callback:
    UpdateSpecificOrder mUpdateSpecificOrder;
    // Firestore:
    FirebaseFirestore db;
    // Recycler view and its adapter:
    RecyclerView recyclerView;
    OrderDetailsRecyclerAdapter mAdapter;
    // Current order:
    Order mCurrentOrder;
    // Views:
    TextView orderPriceSum;

    public ActiveOrderDetailsFragment() {
        // Required empty public constructor
    }

    public ActiveOrderDetailsFragment(Order order, UpdateSpecificOrder listener) {
        this.mCurrentOrder = order;
        this.mUpdateSpecificOrder = listener;
        db = FirebaseFirestore.getInstance();
    }

    public static ActiveOrderDetailsFragment newInstance(Order order, UpdateSpecificOrder listener) {
        return new ActiveOrderDetailsFragment(order, listener);
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
        View rootView = inflater.inflate(R.layout.fragment_actve_order_details, container, false);
        orderPriceSum = rootView.findViewById(R.id.price_sum_tv_ActiveOrderDetailsFragment);
        calcPriceSum();
        recyclerView = rootView.findViewById(R.id.recycler_view_articles_of_order_ActiveOrderDetails);
        TextView cancelBtn = rootView.findViewById(R.id.cancel_order_ActiveOrderDetailsFragment);
        AppCompatButton confirmBtn = rootView.findViewById(R.id.proceed_ActiveOrderDetailsFragment);
        if (recyclerView != null) {
            mAdapter = new OrderDetailsRecyclerAdapter(requireContext(), mCurrentOrder);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        TextView nameView = rootView.findViewById(R.id.name_ActiveOrderDetails);
        ShapeableImageView profileImageView = rootView.findViewById(R.id.profile_image_ActiveOrderDetails);
        TextView orderDateView = rootView.findViewById(R.id.date_order_ActiveOrderDetails);
        orderDateView.setText(mCurrentOrder.getDatum_narocila());
        TextView pickupDateView = rootView.findViewById(R.id.date_pickup_ActiveOrderDetails);
        pickupDateView.setText(mCurrentOrder.getDatum_prevzema());
        switch (mCurrentOrder.getOpravljeno()) {
            case 1: // Warning, (order is tomorrow).
                rootView.findViewById(R.id.lin1).setBackground(getResources().getDrawable(R.drawable.warning_background_border));
                break;
            case 2: // Order is confirmed.
                confirmBtn.setVisibility(View.GONE);
                rootView.findViewById(R.id.lin1).setBackground(getResources().getDrawable(R.drawable.green_background_border));
                break;
            case 3: // Order has been canceled.
                rootView.findViewById(R.id.canceled_text_ActiveOrderDetails).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.btn_layout).setVisibility(View.GONE);
                rootView.findViewById(R.id.lin1).setBackground(getResources().getDrawable(R.drawable.error_background_border));
                break;
        }
        cancelBtn.setOnClickListener(v -> { // Cancel the order ...
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Ste prepričani, da želite preklicati naročilo?")
                    .setTitle("Preklic naročila");
            // Add the buttons
            builder.setPositiveButton("Da", (dialog, id) -> {
                updateOrderFun(3); // 3 == order is canceled.
            });
            builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        if (!appUser.isLastnik_kmetije()) { // We have a user:
            nameView.setText(mCurrentOrder.getIme_kmetije());
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + mCurrentOrder.getId_kmetije());
            GlideApp.with(requireContext()).load(imageRef).error(R.drawable.default_profile_picture).into(profileImageView);
            // Confirm button pressed:
            confirmBtn.setVisibility(View.GONE);
        } else { // We have a farm:
            nameView.setText(mCurrentOrder.getIme_priimek_kupca());
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + mCurrentOrder.getId_kupca());
            GlideApp.with(requireContext()).load(imageRef).error(R.drawable.default_profile_picture).into(profileImageView);
            // Confirm button pressed:
            confirmBtn.setOnClickListener(v -> { // Confirm the order ...
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Ste prepričani, da želite potrditi naročilo?")
                        .setTitle("Potrditev naročila");
                // Add the buttons
                builder.setPositiveButton("Da", (dialog, id) -> {
                    // User clicked OK button
                    updateOrderFun(2); // 2 == order is approved.
                });
                builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
        return rootView;
    }

    private void updateOrderFun(int newStatus) {
        // Preparation:
        mCurrentOrder.setOpravljeno(newStatus);
        MyPostRequestSender myPostRequestSender = new MyPostRequestSender(requireContext());
        String status_message = String.valueOf(mCurrentOrder.getOpravljeno());
        String idTo;
        if (appUser.isLastnik_kmetije())
            idTo = mCurrentOrder.getId_kupca();
        else
            idTo = mCurrentOrder.getId_kmetije();
        try {
            myPostRequestSender.sendRequest(mCurrentOrder.getId_order(), idTo, status_message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Buyer update:
        db.collection("Uporabniki").document(mCurrentOrder.getId_kupca())
                .collection("Aktivna Naročila").document(mCurrentOrder.getId_order())
                .set(mCurrentOrder);
        // Farmer update:
        db.collection("Kmetije").document(mCurrentOrder.getId_kmetije())
                .collection("Aktivna Naročila").document(mCurrentOrder.getId_order())
                .set(mCurrentOrder);
        // Start callback:
        mUpdateSpecificOrder.onUpdateOrderCallback();
        getParentFragmentManager().popBackStack();
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
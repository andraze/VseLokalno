package diplomska.naloga.vselokalno.UserFunctions.OrderHistory_FU;

import static diplomska.naloga.vselokalno.MainActivity.appOrderHistory;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.getFullOrderDateSlo;
import static diplomska.naloga.vselokalno.MainActivity.getFullPickupDateSlo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;

public class OrderHistoryRecyclerAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerAdapter.ViewHolder> {

    public interface OrderHistoryAdapterCallback {

        void onSpecificOrderClickCallback(Order order);

        void onFinishCallback();
    }

    Context mContext;
    LayoutInflater mInflater;
    OrderHistoryAdapterCallback mOrderHistoryAdapterCallback;

    public OrderHistoryRecyclerAdapter(Context context, OrderHistoryAdapterCallback activeOrdersAdapterCallback) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOrderHistoryAdapterCallback = activeOrdersAdapterCallback;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_order_history_item, parent, false));
    }

    @SuppressLint({"SimpleDateFormat", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order currentOrder = appOrderHistory.get(position);
        holder.dateOrder_tv.setText(getFullOrderDateSlo(currentOrder.getDatum_narocila()));
        holder.datePickup_tv.setText(getFullPickupDateSlo(currentOrder.getDatum_prevzema()));
        showStatus(currentOrder.getOpravljeno(), holder);
        StorageReference profileImageRef;
        if (!appUser.isLastnik_kmetije()) { // We have a user:
            holder.name_tv.setText(currentOrder.getIme_kmetije());
            profileImageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + currentOrder.getId_kmetije());
        } else { // We have a farm:
            holder.name_tv.setText(currentOrder.getIme_priimek_kupca());
            profileImageRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Images/" + currentOrder.getId_kupca());
        }
        GlideApp.with(mContext).load(profileImageRef)
                .error(mContext.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(holder.profileImage);
        // Specific order click listener:
        holder.entireView.setOnClickListener(v -> mOrderHistoryAdapterCallback.onSpecificOrderClickCallback(currentOrder));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    public void showStatus(int index, ViewHolder holderTemp) {
        switch (index) {
            case 2: // The order is processed and is on schedule (green) / today (blue).
                    holderTemp.entireView.setBackgroundColor(mContext.getResources().getColor(R.color.green_normal));
                break;
            case 3: // There is an issue and the order was canceled (red).
                holderTemp.entireView.setBackgroundColor(mContext.getResources().getColor(R.color.red_normal));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int size = appOrderHistory.size();
        mOrderHistoryAdapterCallback.onFinishCallback();
        return size;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the Views:
        TextView name_tv;
        TextView dateOrder_tv;
        TextView datePickup_tv;
        ShapeableImageView profileImage;
        View entireView;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            name_tv = itemView.findViewById(R.id.name_listOrderHistoryItem);
            dateOrder_tv = itemView.findViewById(R.id.date_order_listOrderHistoryItem);
            datePickup_tv = itemView.findViewById(R.id.date_pickup_listOrderHistoryItem);
            profileImage = itemView.findViewById(R.id.profile_image_listOrderHistoryItem);
            entireView = itemView;
        } // ViewHolder
    }
}

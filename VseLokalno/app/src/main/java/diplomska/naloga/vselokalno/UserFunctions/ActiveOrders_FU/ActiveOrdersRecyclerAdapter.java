package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU;

import static diplomska.naloga.vselokalno.MainActivity.appActiveOrders;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.getFullDateSlo;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;

public class ActiveOrdersRecyclerAdapter extends RecyclerView.Adapter<ActiveOrdersRecyclerAdapter.ViewHolder> {

    public interface ActiveOrdersAdapterCallback {

        void onSpecificOrderClickCallback(Order order);

        void onFinishCallback();
    }

    Context mContext;
    LayoutInflater mInflater;
    ActiveOrdersAdapterCallback mActiveOrdersAdapterCallback;

    public ActiveOrdersRecyclerAdapter(Context context, ActiveOrdersAdapterCallback activeOrdersAdapterCallback) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mActiveOrdersAdapterCallback = activeOrdersAdapterCallback;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_active_orders_item, parent, false));
    }

    @SuppressLint({"SimpleDateFormat", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order currentOrder = appActiveOrders.get(position);
        holder.dateOrder_tv.setText(getFullDateSlo(currentOrder.getDatum_narocila()));
        holder.datePickup_tv.setText(getFullDateSlo(currentOrder.getDatum_prevzema()));
        showStatus(currentOrder.getOpravljeno(), holder, currentOrder.getDatum_prevzema());
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
        if (position == appActiveOrders.size() - 1)
            mActiveOrdersAdapterCallback.onFinishCallback();
        // Specific order click listener:
        holder.entireView.setOnClickListener(v -> mActiveOrdersAdapterCallback.onSpecificOrderClickCallback(currentOrder));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    public void showStatus(int index, ViewHolder holderTemp, String datumDostave) {
        switch (index) {
            case 0: // In process and there is still enough time, eg: more than 1 day (white).
                break;
            case 1: // There is something wrong, probably only 1 day left and not yet processed (yellow).
                holderTemp.warningImage.setVisibility(View.VISIBLE);
                holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.warning_background_border));
                break;
            case 2: // The order is processed and is on schedule (green) / today (blue).
                holderTemp.confirmedImage.setVisibility(View.VISIBLE);
                try {
                    Date datePickup = new SimpleDateFormat("E dd-MM-yyyy HH:mm").parse(datumDostave);
                    Date dateToday = new Date();
                    String datePickupString = new SimpleDateFormat("dd-MM-yyyy").format(Objects.requireNonNull(datePickup));
                    String dateTodayString = new SimpleDateFormat("dd-MM-yyyy").format(dateToday);
                    if (datePickup.after(dateToday)) // Not yet pickup day
                        holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.green_background_border));
                    if (datePickupString.equals(dateTodayString)) { // Today is pickup day
                        holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.blue_background_border));
                        holderTemp.infoImage.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 3: // There is an issue and the order was canceled (red).
                holderTemp.errorImage.setVisibility(View.VISIBLE);
                holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.error_background_border));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int size = appActiveOrders.size();
        if (size == 0)
            mActiveOrdersAdapterCallback.onFinishCallback();
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
        ShapeableImageView warningImage;
        ShapeableImageView errorImage;
        ShapeableImageView infoImage;
        ShapeableImageView confirmedImage;
        View entireView;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            name_tv = itemView.findViewById(R.id.name_listActiveOrdersItem);
            dateOrder_tv = itemView.findViewById(R.id.date_order_listActiveOrdersItem);
            datePickup_tv = itemView.findViewById(R.id.date_pickup_listActiveOrdersItem);
            profileImage = itemView.findViewById(R.id.profile_image_listActiveOrdersItem);
            warningImage = itemView.findViewById(R.id.image_warning_listActiveOrdersItem);
            errorImage = itemView.findViewById(R.id.image_error_listActiveOrdersItem);
            infoImage = itemView.findViewById(R.id.image_info_listActiveOrdersItem);
            confirmedImage = itemView.findViewById(R.id.image_confirmed_listActiveOrdersItem);
            entireView = itemView;
        } // ViewHolder
    }
}

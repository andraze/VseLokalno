package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU;

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

import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKmetijo;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class ActiveOrdersRecyclerAdapter extends RecyclerView.Adapter<ActiveOrdersRecyclerAdapter.ViewHolder> {

    public interface ActiveOrdersAdapterCallback {
        void onFinishCallback();
    }

    Context mContext;
    LayoutInflater mInflater;
    Farm farmOfInterest;
    User userOfInterest;
    ActiveOrdersAdapterCallback mListener;

    public ActiveOrdersRecyclerAdapter(Context context, Object obj, ActiveOrdersAdapterCallback listener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mListener = listener;
        if (obj instanceof User) {
            userOfInterest = (User) obj;
            farmOfInterest = null;
        } else {
            userOfInterest = null;
            farmOfInterest = (Farm) obj;
        }
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
        if (userOfInterest != null) { // We have a user:
            ZaKupca currentOrder = userOfInterest.getAktivnaNarocila().get(position);
            holder.name_tv.setText(currentOrder.getIme_kmetije());
            holder.dateOrder_tv.setText(getFullDateSlo(currentOrder.getDatum_narocila()));
            holder.datePickup_tv.setText(getFullDateSlo(currentOrder.getDatum_dostave()));
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Uporabniške profilke/" + currentOrder.getId_kmetije());
            GlideApp.with(mContext).load(imageRef).error(R.drawable.default_profile_picture).into(holder.profileImage);
            showStatus(currentOrder.getOpravljeno(), holder, currentOrder.getDatum_dostave());
            if (position == userOfInterest.getAktivnaNarocila().size()-1)
                mListener.onFinishCallback();
        } else { // We have a farm:
            ZaKmetijo currentOrder = farmOfInterest.getAktivnaNarocila().get(position);
            holder.name_tv.setText(currentOrder.getIme_narocnika());
            holder.dateOrder_tv.setText(getFullDateSlo(currentOrder.getDatum_narocila()));
            holder.datePickup_tv.setText(getFullDateSlo(currentOrder.getDatum_dostave()));
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Uporabniške profilke/" + currentOrder.getId_narocnika());
            GlideApp.with(mContext).load(imageRef).error(R.drawable.default_profile_picture).into(holder.profileImage);
            showStatus(currentOrder.getOpravljeno(), holder, currentOrder.getDatum_dostave());
            if (position == farmOfInterest.getAktivnaNarocila().size()-1)
                mListener.onFinishCallback();
        }
        holder.entireView.setOnClickListener(v -> {
            // TODO: expand on the order.
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    public void showStatus(int index, ViewHolder holderTemp, String datumDostave) {
        switch (index) {
            case 0: // In process and there is still enough time, eg: more than 1 day (white).
                holderTemp.warningImage.setVisibility(View.GONE);
                holderTemp.errorImage.setVisibility(View.GONE);
                holderTemp.infoImage.setVisibility(View.GONE);
                break;
            case 1: // There is something wrong, probably only 1 day left and not yet processed (yellow).
                holderTemp.warningImage.setVisibility(View.VISIBLE);
                holderTemp.errorImage.setVisibility(View.GONE);
                holderTemp.infoImage.setVisibility(View.GONE);
                holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.warning_background_border));
                break;
            case 2: // The order is processed and is on schedule (green) / today (blue).
                holderTemp.warningImage.setVisibility(View.GONE);
                holderTemp.errorImage.setVisibility(View.GONE);
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
                holderTemp.warningImage.setVisibility(View.GONE);
                holderTemp.infoImage.setVisibility(View.GONE);
                holderTemp.errorImage.setVisibility(View.VISIBLE);
                holderTemp.entireView.setBackground(mContext.getResources().getDrawable(R.drawable.error_background_border));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (userOfInterest != null)
            return userOfInterest.getAktivnaNarocila().size();
        else
            return farmOfInterest.getAktivnaNarocila().size();
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
            entireView = itemView;
        } // ViewHolder
    }
}

package diplomska.naloga.vselokalno.UserFunctions.Basket_U.BuyingOrder;

import static diplomska.naloga.vselokalno.MainActivity.getSloDayName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.R;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder> {

    public interface OrderSelectDateListener {
        void onOrderSelectDateListener(int numOfDaysFromToday, View v, TextView t1, TextView t2);
    }

    OrderSelectDateListener orderSelectDateListener;
    private final String TAG = "OrderRecyclerAdapter";
    LayoutInflater mInflater;
    Context mContext;
    int farmNumber;
    Map<String, ArrayList<Boolean>> cas_prevzemaKmetije;

    public OrderRecyclerAdapter(Context tempContext, int farmNum, OrderSelectDateListener tempListener, Farm k) {
        this.mContext = tempContext;
        this.farmNumber = farmNum;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.orderSelectDateListener = tempListener;
        this.cas_prevzemaKmetije = k.getCas_prevzema();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderRecyclerAdapter.ViewHolder(mInflater.inflate(R.layout.order_item, parent, false));
    } // onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] dateTable = addXDays(position);
        holder.dayName.setText(dateTable[0]);
        holder.dateNum.setText(dateTable[1]);
        if (position == 0) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.blue_light, null));
            holder.itemView.setElevation(1);
            holder.dayName.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.dateNum.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.itemView.setOnClickListener(v -> Toast.makeText(mContext, "Danes prevzem ni mogoč.", Toast.LENGTH_SHORT).show());
        } else if (!cas_prevzemaKmetije.containsKey(dateTable[0])) { // Date not available for pickup.
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.red_light, null));
            holder.itemView.setElevation(1);
            holder.dayName.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.dateNum.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.itemView.setOnClickListener(v -> Toast.makeText(mContext, "Ta dan prevzem ni mogoč.", Toast.LENGTH_SHORT).show());
        } else
            holder.itemView.setOnClickListener(v -> orderSelectDateListener
                    .onOrderSelectDateListener(position, holder.itemView, holder.dayName, holder.dateNum));
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return 30;
    } // getItemCount

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayName;
        TextView dateNum;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views:
            dayName = itemView.findViewById(R.id.day);
            dateNum = itemView.findViewById(R.id.date);
        } // ViewHolder

    } // ViewHolder

    @SuppressLint("SimpleDateFormat")
    public String[] addXDays(int x) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, x);
        String[] returnTable = new String[2];
        date = calendar.getTime();
        String temp = new SimpleDateFormat("E").format(date);
        returnTable[0] = getSloDayName(temp);
        returnTable[1] = new SimpleDateFormat("d").format(date);
        return returnTable;
    }
}

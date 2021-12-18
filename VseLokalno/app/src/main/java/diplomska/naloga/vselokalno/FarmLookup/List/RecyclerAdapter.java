package diplomska.naloga.vselokalno.FarmLookup.List;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

/***
 * The adapter class for the RecyclerView, farm data.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final String TAG = "RecyclerAdapter";

    public interface ItemClickListener {
        void onItemClick(int position, Map<String, String> farm);
    } // ItemClickListener

    ItemClickListener ItemClickListener;

    // Member variables.
    private final ArrayList<Map<String, String>> mFarmData;
    private final Context mContext;
    LayoutInflater mInflater;
    FragmentManager fragmentManager;

    public RecyclerAdapter(Context context, ArrayList<Map<String, String>> farmArrayList,
                           FragmentManager newFragmentmaneger, ItemClickListener itemClickListener) {
        mInflater = LayoutInflater.from(context);
        this.mFarmData = farmArrayList;
        this.mContext = context;
        setHasStableIds(true);
        this.fragmentManager = newFragmentmaneger;
        this.ItemClickListener = itemClickListener;
    } // RecyclerAdapter

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_farm_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        // Get current sport.
        Map<String, String> currentFarm = mFarmData.get(position);
        makeLogD(TAG, "(onBindViewHolder) showing farm: " + currentFarm.get("ime_kmetije"));
        // Populate the textviews with data.
        holder.mNaslovText.setText(currentFarm.get("ime_kmetije"));
        // Reference to an image file in Cloud Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("Profile Images/" + currentFarm.get("id_kmetije"));
//        makeLogD(TAG, "(onBindViewHolder) storage reference: " + imageRef.toString());
        GlideApp.with(mContext).load(imageRef)
                .error(mContext.getResources().getDrawable(R.drawable.default_farm_image))
                .into(holder.mImage);
        holder.itemView.setOnClickListener(v -> ItemClickListener.onItemClick(
                holder.getAdapterPosition(),
                currentFarm
        ));
    } // onBindViewHolder

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mFarmData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mNaslovText;
        private final ImageView mImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mNaslovText = itemView.findViewById(R.id.naslov);
            mImage = itemView.findViewById(R.id.slika);
        } // ViewHolder

    } // ViewHolder
}

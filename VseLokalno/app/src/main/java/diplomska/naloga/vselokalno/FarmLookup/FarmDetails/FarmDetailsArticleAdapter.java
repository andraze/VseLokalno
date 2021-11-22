package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

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
import java.util.Objects;

import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class FarmDetailsArticleAdapter extends RecyclerView.Adapter<FarmDetailsArticleAdapter.ViewHolder> {

    private final String TAG = "FarmDetailsArticleAdapter";
    private final Context mContext;
    private ArrayList<Map<String, String>> mArticles;
    FragmentManager mFragmentManager;
    LayoutInflater mInflater;

    /**
     * When a buyer wishes to buy a specific article of a specific farm.
     */
    public interface OnArticleBuyerClickListener {
        void onArticleClickListener(int position);
    } // OnArticleBuyerClickListener

    OnArticleBuyerClickListener onArticleBuyerClickListener;

    public FarmDetailsArticleAdapter(Context context, ArrayList<Map<String, String>> articles,
                                     FragmentManager fragmentManager, OnArticleBuyerClickListener tempArticleBuyerClickListener) {
        this.mContext = context;
        this.mArticles = articles;
        this.mFragmentManager = fragmentManager;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.onArticleBuyerClickListener = tempArticleBuyerClickListener;
        setHasStableIds(true);
    }

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
    public FarmDetailsArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FarmDetailsArticleAdapter.ViewHolder(mInflater.inflate(R.layout.list_specific_farm_item, parent, false));
    } // onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull FarmDetailsArticleAdapter.ViewHolder holder, int position) {
        Map<String, String> currentArticle = mArticles.get(position);
        makeLogD(TAG, "(onBindViewHolder) showing article: " + currentArticle);
//        Populate text views with data:
        holder.mArticleName.setText(currentArticle.get("ime_artikel"));
        makeLogD(TAG, "Artikel cena: " + currentArticle.get("cena_artikel"));
        holder.mArticlePrice.setText(String.format("%s €", currentArticle.get("cena_artikel")));
        holder.mArticleUnit.setText(String.format("na 1 %s", currentArticle.get("enota_artikel")));
        // Reference to an image file in Cloud Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(currentArticle.get("slika_artikel")));
        GlideApp.with(mContext).load(imageRef).into(holder.mArticleImage);
        holder.itemView.setOnClickListener(v -> onArticleBuyerClickListener.onArticleClickListener(holder.getPosition()));
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mArticleName;
        private final ImageView mArticleImage;
        private final TextView mArticlePrice;
        private final TextView mArticleUnit;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mArticleName = itemView.findViewById(R.id.article_name_FarmDetailsArticleAdapter);
            mArticleImage = itemView.findViewById(R.id.article_image_FarmDetailsArticleAdapter);
            mArticlePrice = itemView.findViewById(R.id.article_price_FarmDetailsArticleAdapter);
            mArticleUnit = itemView.findViewById(R.id.article_unit_FarmDetailsArticleAdapter);
        } // ViewHolder

    } // ViewHolder
}
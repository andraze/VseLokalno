package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

public class FarmDetailsArticleAdapter extends RecyclerView.Adapter<FarmDetailsArticleAdapter.ViewHolder> {

    private final String TAG = "FarmDetailsArticleAdapter";
    private final Context mContext;
    private final ArrayList<Article> mArticles;
    LayoutInflater mInflater;

    /**
     * When a buyer wishes to buy a specific article of a specific farm.
     */
    public interface ArticleClickCallback {
        void onArticleClickListener(Article article);
    } // OnArticleBuyerClickListener

    ArticleClickCallback mArticleBuyerClickCallback;

    public FarmDetailsArticleAdapter(Context context,
                                     ArrayList<Article> articles,
                                     ArticleClickCallback tempArticleBuyerClickListener) {
        this.mContext = context;
        this.mArticles = articles;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mArticleBuyerClickCallback = tempArticleBuyerClickListener;
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
        return new FarmDetailsArticleAdapter.ViewHolder(mInflater.inflate(R.layout.list_article_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull FarmDetailsArticleAdapter.ViewHolder holder, int position) {
        Article currentArticle = mArticles.get(position);
        makeLogD(TAG, "(onBindViewHolder) showing article: " + currentArticle);
//        Populate text views with data:
        holder.mArticleName.setText(currentArticle.getArticle_name());
        holder.mArticlePrice.setText(String.format("%.2f €", currentArticle.getArticle_price()));
        holder.mArticleUnit.setText(currentArticle.getArticle_unit());
        // Reference to an image file in Cloud Storage
        if (currentArticle.isPicture()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Article Images/" + currentArticle.getArticle_id());
            GlideApp.with(mContext).load(imageRef).into(holder.mArticleImage);
        } else {
            holder.mArticleImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> mArticleBuyerClickCallback.onArticleClickListener(currentArticle));
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
            mArticleName = itemView.findViewById(R.id.ime_artikel);
            mArticleImage = itemView.findViewById(R.id.slika_artikla);
            mArticlePrice = itemView.findViewById(R.id.cena_artikel);
            mArticleUnit = itemView.findViewById(R.id.enota_artikel);
        } // ViewHolder

    } // ViewHolder
}
package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

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
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

public class FarmDetailsCategoryAdapter extends RecyclerView.Adapter<FarmDetailsCategoryAdapter.ViewHolder> {

    private final String TAG = "FarmDetailsArticleAdapter";
    private final Context mContext;
    private final ArrayList<Category> mCategories;
    private final ArrayList<Article> mArticles;
    LayoutInflater mInflater;

    /**
     * When a buyer wishes to see a specific category of a specific farm.
     */
    public interface CategoryClickCallback {
        void onCategoryClickListener(Category category);
    } // OnArticleBuyerClickListener

    CategoryClickCallback categoryClickCallback;

    public FarmDetailsCategoryAdapter(Context context,
                                      ArrayList<Category> categories,
                                      ArrayList<Article> articles,
                                      CategoryClickCallback tempCategoryClickCallback) {
        this.mContext = context;
        this.mCategories = categories;
        this.mArticles = articles;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.categoryClickCallback = tempCategoryClickCallback;
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
    public FarmDetailsCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FarmDetailsCategoryAdapter.ViewHolder(mInflater.inflate(R.layout.list_category_item, parent, false));
    } // onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull FarmDetailsCategoryAdapter.ViewHolder holder, int position) {
        Category currentCategory = mCategories.get(position);
        makeLogD(TAG, "(onBindViewHolder) showing category: " + currentCategory.getCategory_id());
//        Populate text views with data:
        holder.mCategoryName.setText(currentCategory.getCategory_name());
        ArrayList<String> articleIDs = new ArrayList<>();
        for (Article article : mArticles) {
            if (articleIDs.size() == 3)
                break;
            if (article.getCategory_id().equals(currentCategory.getCategory_id()) &&
                    article.isPicture())
                articleIDs.add(article.getArticle_id());
        }
        switch (articleIDs.size()) {
            case 3:
                StorageReference imageRef3 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(2)));
                GlideApp.with(mContext).load(imageRef3).into(holder.mArticleImage3);
            case 2:
                StorageReference imageRef2 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(1)));
                GlideApp.with(mContext).load(imageRef2).into(holder.mArticleImage2);
            case 1:
                StorageReference imageRef1 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(0)));
                GlideApp.with(mContext).load(imageRef1).into(holder.mArticleImage1);
                break;
        }
        holder.itemView.setOnClickListener(v -> categoryClickCallback.onCategoryClickListener(currentCategory));
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mCategoryName;
        private final ImageView mArticleImage1;
        private final ImageView mArticleImage2;
        private final ImageView mArticleImage3;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mCategoryName = itemView.findViewById(R.id.category_name);
            mArticleImage1 = itemView.findViewById(R.id.article_image1);
            mArticleImage2 = itemView.findViewById(R.id.article_image2);
            mArticleImage3 = itemView.findViewById(R.id.article_image3);
        } // ViewHolder

    } // ViewHolder
}
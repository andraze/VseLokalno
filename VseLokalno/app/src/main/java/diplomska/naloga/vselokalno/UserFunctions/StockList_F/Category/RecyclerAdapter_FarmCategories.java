package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category;

import static diplomska.naloga.vselokalno.MainActivity.appArticles;
import static diplomska.naloga.vselokalno.MainActivity.appCategories;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

public class RecyclerAdapter_FarmCategories extends RecyclerView.Adapter<RecyclerAdapter_FarmCategories.ViewHolder> {

    public interface CategoryClickListener {
        void onItemClick(Category currentCategoryTemp);
    } // ItemClickListener

    CategoryClickListener categoryClickListener;

    // Member variables.
    private final Context mContext;
    LayoutInflater mInflater;

    public RecyclerAdapter_FarmCategories(Context context, CategoryClickListener categoryClickListenerTemp) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        setHasStableIds(true);
        this.categoryClickListener = categoryClickListenerTemp;
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
    public RecyclerAdapter_FarmCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapter_FarmCategories.ViewHolder(mInflater.inflate(R.layout.list_category_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current sport.
        Category currentCategory = appCategories.get(position);
        String TAG = "RecyclerAdapter_FarmCategories";
        makeLogD(TAG, "(onBindViewHolder) showing category: " + currentCategory.getCategory_name());
        // Populate the textviews with data.
        holder.mCategoryName.setText(currentCategory.getCategory_name());
        ArrayList<String> articleIDs = new ArrayList<>();
        boolean low_on_stock = false;
        boolean out_of_stock = false;
        for (Article article : appArticles) {
            if (article.getCategory_id().equals(currentCategory.getCategory_id()) &&
                    article.isPicture()) {
                if (articleIDs.size() < 3)
                    articleIDs.add(article.getArticle_id());
                if (article.getArticle_storage() <= 0.1)
                    out_of_stock = true;
                else if (article.getArticle_storage() <= 1)
                    low_on_stock = true;
            }
        }
        if (out_of_stock)
            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.error_background_border));
        else if (low_on_stock)
            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.warning_background_border));
        switch (articleIDs.size()) {
            case 3:
                StorageReference imageRef3 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(2)));
                GlideApp.with(mContext).load(imageRef3).into(holder.mImagePreview3);
            case 2:
                StorageReference imageRef2 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(1)));
                GlideApp.with(mContext).load(imageRef2).into(holder.mImagePreview2);
            case 1:
                StorageReference imageRef1 = FirebaseStorage.getInstance().getReference()
                        .child("Article Images/" + Objects.requireNonNull(articleIDs.get(0)));
                GlideApp.with(mContext).load(imageRef1).into(holder.mImagePreview1);
                break;
        }

        holder.itemView.setOnClickListener(v -> categoryClickListener.onItemClick(currentCategory));
    } // onBindViewHolder

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return appCategories.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mCategoryName;
        private final CircleImageView mImagePreview1;
        private final CircleImageView mImagePreview2;
        private final CircleImageView mImagePreview3;
        private final LinearLayout statusLayout;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mCategoryName = itemView.findViewById(R.id.category_name);
            mImagePreview1 = itemView.findViewById(R.id.article_image1);
            mImagePreview2 = itemView.findViewById(R.id.article_image2);
            mImagePreview3 = itemView.findViewById(R.id.article_image3);
            statusLayout = itemView.findViewById(R.id.article_linLayout_recyclerAdapter);
        } // ViewHolder

    } // ViewHolder
}

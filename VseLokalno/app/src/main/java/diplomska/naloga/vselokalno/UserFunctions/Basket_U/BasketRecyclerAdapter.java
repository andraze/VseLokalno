package diplomska.naloga.vselokalno.UserFunctions.Basket_U;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

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
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;

public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

    public interface BasketArticleClickCallback {
        void onBasketItemClickListener(Article article, int position);
    }

    private final String TAG = "BasketRecyclerAdapter";
    LayoutInflater mInflater;
    Context mContext;
    ArrayList<Article> mAllOrderedArticles;
    BasketArticleClickCallback mBasketArticleClickCallback;

    public BasketRecyclerAdapter(Context tempContext, BasketArticleClickCallback basketArticleClickCallback) {
        this.mContext = tempContext;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mBasketArticleClickCallback = basketArticleClickCallback;
        mAllOrderedArticles = new ArrayList<>();
        for (Order order : appBasket) {
            mAllOrderedArticles.addAll(order.getOrdered_articles());
        }
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BasketRecyclerAdapter.ViewHolder(mInflater.inflate(R.layout.basket_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Populate views with content:
        Article currentArticle = mAllOrderedArticles.get(position);
        if (currentArticle.isPicture()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Article Images/" + currentArticle.getArticle_id());
            GlideApp.with(mContext).load(imageRef).into(holder.mArticleImage);
        } else {
            holder.mArticleImage.setVisibility(View.GONE);
        }
        holder.mArticleAmount.setText(String.format("%.2f", currentArticle.getArticle_buying_amount()));
        holder.mArticleUnit.setText(currentArticle.getArticle_unit());
        holder.mArticleCost.setText(String.format("%.2f", currentArticle.getArticle_price() * currentArticle.getArticle_buying_amount()));
        holder.mArticleName.setText(currentArticle.getArticle_name());
        double kolicinaTemp = currentArticle.getArticle_buying_amount();
        double zalogaTemp = currentArticle.getArticle_storage();
        if (zalogaTemp >= 0 && zalogaTemp < kolicinaTemp) {
            holder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.error_background_border));
        }
        // Edit specific article:
        holder.itemView.setOnClickListener(v -> mBasketArticleClickCallback.onBasketItemClickListener(currentArticle, position));
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return mAllOrderedArticles.size();
    } // getITemCount

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mArticleName;
        private final ImageView mArticleImage;
        private final TextView mArticleCost;
        private final TextView mArticleAmount;
        private final TextView mArticleUnit;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views:
            mArticleImage = itemView.findViewById(R.id.slika_artikla);
            mArticleName = itemView.findViewById(R.id.ime_artikel);
            mArticleCost = itemView.findViewById(R.id.cena_artikel);
            mArticleAmount = itemView.findViewById(R.id.kolicina_artikel);
            mArticleUnit = itemView.findViewById(R.id.enota_artikel);
        } // ViewHolder

    } // ViewHolder
}

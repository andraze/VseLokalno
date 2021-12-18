package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU.SpecificOrder;

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

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;

public class OrderDetailsRecyclerAdapter extends RecyclerView.Adapter<OrderDetailsRecyclerAdapter.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;
    Order currentOrder;

    public OrderDetailsRecyclerAdapter(Context context, Order orderTemp) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.currentOrder = orderTemp;
        setHasStableIds(true);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.basket_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article currentArticle = currentOrder.getOrdered_articles().get(position);
        if (currentArticle.isPicture()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Article Images/" + currentArticle.getArticle_id());
            GlideApp.with(mContext).load(imageRef).into(holder.mArticleImage);
        } else
            holder.mArticleImage.setVisibility(View.GONE);
        holder.mArticleName.setText(currentArticle.getArticle_name());
        holder.mArticleCost.setText(String.format("%.2f", currentArticle.getArticle_price() * currentArticle.getArticle_buying_amount()));
        holder.mArticleUnit.setText(currentArticle.getArticle_unit());
        holder.mArticleAmount.setText(String.format("%.2f", currentArticle.getArticle_buying_amount()));
    }

    @Override
    public int getItemCount() {
        return currentOrder.getOrdered_articles().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

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

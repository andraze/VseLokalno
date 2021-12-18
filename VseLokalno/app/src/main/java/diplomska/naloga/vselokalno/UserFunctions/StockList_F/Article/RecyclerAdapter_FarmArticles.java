package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Article;

import static diplomska.naloga.vselokalno.MainActivity.appArticles;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

/***
 * The adapter class for the RecyclerView, articles of specific category for farm.
 */
public class RecyclerAdapter_FarmArticles extends RecyclerView.Adapter<RecyclerAdapter_FarmArticles.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(Article article);
    } // ItemClickListener

    ItemClickListener ItemClickListener;

    // Member variables.
    String mCategory_id;
    private final Context mContext;
    LayoutInflater mInflater;
    FragmentManager fragmentManager;
    ArrayList<Article> categoryArticles;

    public RecyclerAdapter_FarmArticles(Context context, String category_id,
                                        FragmentManager newFragmentmaneger, ItemClickListener itemClickListener) {
        mInflater = LayoutInflater.from(context);
        this.mCategory_id = category_id;
        this.mContext = context;
        setHasStableIds(true);
        this.fragmentManager = newFragmentmaneger;
        this.ItemClickListener = itemClickListener;
        this.categoryArticles = new ArrayList<>();
        for (Article el : appArticles) {
            if (el.getCategory_id().equals(mCategory_id))
                this.categoryArticles.add(el);
        }
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
        return new ViewHolder(mInflater.inflate(R.layout.list_article_item, parent, false));
    } // onCreateViewHolder

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current sport.
        Article currentArtikel = categoryArticles.get(position);
        String TAG = "RecyclerAdapter_FarmArticles";
        makeLogD(TAG, "(onBindViewHolder) showing article: " + currentArtikel.getArticle_id());
        // Populate the textviews with data.
        holder.mImeArtikel.setText(currentArtikel.getArticle_name());
        holder.mCenaArtikel.setText(String.format("%.2f", currentArtikel.getArticle_price()));
        holder.mEnotaArtikel.setText(currentArtikel.getArticle_unit());
        // Reference to an image file in Cloud Storage
        if (currentArtikel.isPicture()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Article Images/" + Objects.requireNonNull(currentArtikel.getArticle_id()));
            GlideApp.with(mContext).load(imageRef)
                    .into(holder.mSlikaArtikel);
        } else {
            holder.mSlikaArtikel.setVisibility(View.GONE);
        }

        if (currentArtikel.getArticle_storage() <= 0.1) {
            holder.mRelLayout.setBackground(mContext.getDrawable(R.drawable.error_background_border));
        }

        if (currentArtikel.getArticle_storage() <= 1 && currentArtikel.getArticle_storage() > 0.1) {
            holder.mRelLayout.setBackground(mContext.getDrawable(R.drawable.warning_background_border));
        }

        holder.itemView.setOnClickListener(v -> ItemClickListener.onItemClick(currentArtikel));
    } // onBindViewHolder

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return categoryArticles.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mImeArtikel;
        private final TextView mCenaArtikel;
        private final TextView mEnotaArtikel;
        private final CircleImageView mSlikaArtikel;
        private final RelativeLayout mRelLayout;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            mImeArtikel = itemView.findViewById(R.id.ime_artikel);
            mCenaArtikel = itemView.findViewById(R.id.cena_artikel);
            mEnotaArtikel = itemView.findViewById(R.id.enota_artikel);
            mSlikaArtikel = itemView.findViewById(R.id.slika_artikla);
            mRelLayout = itemView.findViewById(R.id.article_relLayout_recyclerAdapter);
        } // ViewHolder

    } // ViewHolder
}

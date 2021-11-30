package diplomska.naloga.vselokalno.UserFunctions.ArticleList_F;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

/***
 * The adapter class for the RecyclerView, articles of farm.
 */
public class RecyclerAdapter_FarmArticles extends RecyclerView.Adapter<RecyclerAdapter_FarmArticles.ViewHolder> {

    public interface ArticleDeleteListener {
        void onArticleDeleteListener(int position);
    } // ArticleDeleteListener

    ArticleDeleteListener articleDeleteListener;

    public interface ItemClickListener {
        void onItemClick(int position);
    } // ItemClickListener

    ItemClickListener ItemClickListener;

    // Member variables.
    private final ArrayList<Map<String, String>> mArtikli;
    private final Context mContext;
    LayoutInflater mInflater;
    FragmentManager fragmentManager;

    public RecyclerAdapter_FarmArticles(Context context, ArrayList<Map<String, String>> artikli,
                                        FragmentManager newFragmentmaneger, ItemClickListener itemClickListener,
                                        ArticleDeleteListener deleteListener) {
        mInflater = LayoutInflater.from(context);
        this.mArtikli = artikli;
        this.mContext = context;
        setHasStableIds(true);
        this.fragmentManager = newFragmentmaneger;
        this.ItemClickListener = itemClickListener;
        this.articleDeleteListener = deleteListener;
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
        return new ViewHolder(mInflater.inflate(R.layout.list_article_item_farm_view, parent, false));
    } // onCreateViewHolder

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current sport.
        Map<String, String> currentArtikel = mArtikli.get(position);
        String TAG = "RecyclerAdapter_FarmArticles";
        makeLogD(TAG, "(onBindViewHolder) showing farm: " + currentArtikel.get("ime_artikel"));
        // Populate the textviews with data.
        holder.mImeArtikel.setText(currentArtikel.get("ime_artikel"));
        holder.mCenaArtikel.setText(currentArtikel.get("cena_artikel"));
        holder.mEnotaArtikel.setText(currentArtikel.get("enota_artikel"));
        // Reference to an image file in Cloud Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(currentArtikel.get("slika_artikel")));
        GlideApp.with(mContext).load(imageRef).into(holder.mSlikaArtikel);

        if (Double.parseDouble(Objects.requireNonNull(currentArtikel.get("zaloga_artikel"))) == 0) {
            holder.mRelLayout.setBackground(mContext.getDrawable(R.drawable.stock_run_out_background_article));
        }

        holder.mDeleteFab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Ste prepričani, da želite izbrisati artikel:\n" + currentArtikel.get("ime_artikel"))
                    .setTitle("Izbris artikla");
            // Add the buttons
            builder.setPositiveButton("Ja", (dialog, id) -> {
                // User clicked OK button
                articleDeleteListener.onArticleDeleteListener(position);
            });
            builder.setNegativeButton("ne", (dialog, id) -> {
                // User cancelled the dialog
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.itemView.setOnClickListener(v -> ItemClickListener.onItemClick(holder.getAdapterPosition()));
    } // onBindViewHolder

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mArtikli.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private final TextView mImeArtikel;
        private final TextView mCenaArtikel;
        private final TextView mEnotaArtikel;
        private final ShapeableImageView mSlikaArtikel;
        private final FloatingActionButton mDeleteFab;
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
            mDeleteFab = itemView.findViewById(R.id.delete_article);
            mRelLayout = itemView.findViewById(R.id.article_relLayout_recyclerAdapter);
        } // ViewHolder

    } // ViewHolder
}

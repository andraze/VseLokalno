package diplomska.naloga.vselokalno.UserFunctions.Basket;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsArticleAdapter;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

    private final String TAG = "BasketRecyclerAdapter";
    LayoutInflater mInflater;
    Context mContext;
    ArrayList<Map<String, String>> mArticles;

    public BasketRecyclerAdapter(Context tempContext) {
        this.mContext = tempContext;
        this.mInflater = LayoutInflater.from(this.mContext);
        mArticles = new ArrayList<>();
        for (ZaKupca el : appBasket) {
            Map<String, String> numOfUnitsMap = el.getNarocilo_kolicine();
            Map<String, String> imagePaths = el.getNarocilo_slike();
            Map<String, String> unitMap = el.getNarocilo_enote();
            for (Map.Entry<String, String> entry : el.getNarocilo_cene().entrySet()) {
                Map<String, String> newArticle = new HashMap<>();
                newArticle.put("ime", entry.getKey());
                newArticle.put("kolicina", numOfUnitsMap.get(entry.getKey()));
                double price = Double.parseDouble(Objects.requireNonNull(numOfUnitsMap.get(entry.getKey())));
                price *= Double.parseDouble(entry.getValue());
                newArticle.put("cena", String.valueOf(price));
                newArticle.put("slika", imagePaths.get(entry.getKey()));
                newArticle.put("enota", unitMap.get(entry.getKey()));
                mArticles.add(newArticle);
            }
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Populate views with content:
        Map<String, String> currentArticle = mArticles.get(position);
        StorageReference imaageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(currentArticle.get("slika")));
        GlideApp.with(mContext).load(imaageRef).into(holder.mArticleImage);
        holder.mArticleAmount_Unit.setText(String.format("%s%s", currentArticle.get("kolicina"), currentArticle.get("enota")));
        holder.mArticleCost.setText(String.format("%s€", currentArticle.get("cena")));
        holder.mArticleName.setText(currentArticle.get("ime"));
        holder.mRemoveArticle.setOnClickListener(v -> {
            // TODO: delete one item.
        });
        // TODO : Edit one item.
        // holder.itemView.setOnClickListener(v -> onArticleBuyerClickListener.onArticleClickListener(holder.getPosition()));
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return mArticles.size();
    } // getITemCount

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mArticleName;
        private final ImageView mArticleImage;
        private final TextView mArticleCost;
        private final TextView mArticleAmount_Unit;
        private final FloatingActionButton mRemoveArticle;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views:
            mRemoveArticle = itemView.findViewById(R.id.remove_article_basketItem);
            mArticleImage = itemView.findViewById(R.id.slika_artikla_basketItem);
            mArticleName = itemView.findViewById(R.id.ime_artikel_basketItem);
            mArticleCost = itemView.findViewById(R.id.cena_artikel_basketItem);
            mArticleAmount_Unit = itemView.findViewById(R.id.kolicina_enota_artikel_basketItem);
        } // ViewHolder

    } // ViewHolder
}

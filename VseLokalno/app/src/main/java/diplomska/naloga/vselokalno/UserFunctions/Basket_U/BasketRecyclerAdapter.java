package diplomska.naloga.vselokalno.UserFunctions.Basket_U;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.app.AlertDialog;
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
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

    public interface RemoveItemFromBasketInterface {
        void removeItemFromBasketFun(int position);
    }

    public interface BasketArticleClickListener {
        void onBasketItemClickListener(Map<String, String> article, int position);
    }

    private final String TAG = "BasketRecyclerAdapter";
    LayoutInflater mInflater;
    Context mContext;
    ArrayList<Map<String, String>> mArticles;
    RemoveItemFromBasketInterface removeItemFromBasketInterface;
    BasketArticleClickListener mBasketArticleClickListener;

    public BasketRecyclerAdapter(Context tempContext, RemoveItemFromBasketInterface removeItemListener, BasketArticleClickListener basketArticleClickListener) {
        this.mContext = tempContext;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.removeItemFromBasketInterface = removeItemListener;
        this.mBasketArticleClickListener = basketArticleClickListener;
        mArticles = new ArrayList<>();
        for (ZaKupca el : appBasket) {
            Map<String, String> numOfUnitsMap = el.getNarocilo_kolicine();
            Map<String, String> imagePaths = el.getNarocilo_slike();
            Map<String, String> unitMap = el.getNarocilo_enote();
            Map<String, String> storageMap = el.getNarocilo_zaloge();
            for (Map.Entry<String, String> entry : el.getNarocilo_cene().entrySet()) {
                Map<String, String> newArticle = new HashMap<>();
                newArticle.put("ime", entry.getKey());
                newArticle.put("kolicina", numOfUnitsMap.get(entry.getKey()));
                newArticle.put("cena", entry.getValue());
                newArticle.put("slika", imagePaths.get(entry.getKey()));
                newArticle.put("enota", unitMap.get(entry.getKey()));
                newArticle.put("zaloga", storageMap.get(entry.getKey()));
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
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(currentArticle.get("slika")));
        GlideApp.with(mContext).load(imageRef).into(holder.mArticleImage);
        holder.mArticleAmount_Unit.setText(String.format("%s%s", currentArticle.get("kolicina"), currentArticle.get("enota")));
        String totalPriceofArticle = String.valueOf(Double.parseDouble(Objects.requireNonNull(currentArticle.get("cena"))) * Double.parseDouble(Objects.requireNonNull(currentArticle.get("kolicina"))));
        holder.mArticleCost.setText(String.format("%s€", totalPriceofArticle));
        holder.mArticleName.setText(currentArticle.get("ime"));
        holder.mRemoveArticle.setOnClickListener(v -> { // Delete one item:
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Ste prepričani, da želite izbrisati artikel:\n" + currentArticle.get("ime"))
                    .setTitle("Izbris artikla");
            builder.setPositiveButton("Ja", (dialog, id) -> {
                // User clicked OK button:
                for (int i = 0; i < appBasket.size(); i++) {
                    ZaKupca el = appBasket.get(i);
                    Map<String, String> numOfUnitsMap = el.getNarocilo_kolicine();
                    Map<String, String> priceMap = el.getNarocilo_cene();
                    Map<String, String> unitMap = el.getNarocilo_enote();
                    for (Map.Entry<String, String> entry : el.getNarocilo_slike().entrySet()) {
                        if (entry.getValue().equals(currentArticle.get("slika")) &&
                                entry.getKey().equals(currentArticle.get("ime")) &&
                                Objects.equals(numOfUnitsMap.get(entry.getKey()), currentArticle.get("kolicina")) &&
                                Objects.equals(priceMap.get(entry.getKey()), ((currentArticle.get("cena")))) &&
                                Objects.requireNonNull(unitMap.get(entry.getKey())).equals(currentArticle.get("enota"))
                        ) { // This entry.key() article needs to be deleted:
                            appBasket.get(i).removeNarocilo_slike(currentArticle.get("ime"));
                            appBasket.get(i).removeNarocilo_cene(currentArticle.get("ime"));
                            appBasket.get(i).removeNarocilo_enote(currentArticle.get("ime"));
                            appBasket.get(i).removeNarocilo_kolicine(currentArticle.get("ime"));
                            appBasket.get(i).removeNarocilo_zaloge(currentArticle.get("ime"));
                            break;
                        }
                    }
                }
                mArticles.remove(position);
                removeItemFromBasketInterface.removeItemFromBasketFun(position);
            });
            builder.setNegativeButton("ne", (dialog, id) -> {
                // User cancelled the dialog:
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        // Edit specific article:
        holder.itemView.setOnClickListener(v -> mBasketArticleClickListener.onBasketItemClickListener(currentArticle, position));
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

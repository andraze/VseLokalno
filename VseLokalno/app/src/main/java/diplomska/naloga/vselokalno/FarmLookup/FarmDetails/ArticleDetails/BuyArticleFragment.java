package diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.DecimalDitgitsInputFilter;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyArticleFragment extends DialogFragment {

    public interface BuyArticleCallBack {
        void callbackBuyArticle_fun(Map<String, String> order);
    }

    Map<String, String> newArticleOrder;
    BuyArticleCallBack mInterfaceBuyArticleCallback;
    Map<String, String> mArticle;
    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "BuyArticleFragment";
    // Views:
    EditText articleBuyAmountView;

    public BuyArticleFragment() {
        // Required empty public constructor
    }

    public BuyArticleFragment(Map<String, String> article) {
        this.mArticle = article;
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
    } // onStart

    public static BuyArticleFragment newInstance(Map<String, String> specificArticleToBuy) {
        return new BuyArticleFragment(specificArticleToBuy);
    } // newInstance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_buy_article, container, false);
        makeLogD(TAG, "Article is: " + mArticle);
        // Article image:
        ImageView articleImageView = rootView.findViewById(R.id.article_image_buyArticleFragment);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(mArticle.get("slika_artikel")));
        GlideApp.with(requireContext()).load(imageRef).into(articleImageView);
        // Article name:
        TextView articleNameView = rootView.findViewById(R.id.ime_artikel_buyArticleFragment);
        articleNameView.setText(mArticle.get("ime_artikel"));
        // Article price:
        TextView articlePriceView = rootView.findViewById(R.id.cena_artikel_buyArticleFragment);
        articlePriceView.setText(mArticle.get("cena_artikel"));
        // Article units:
        TextView unit1View = rootView.findViewById(R.id.unit1_buyArticleFragment);
        unit1View.setText(mArticle.get("enota_artikel"));
        TextView unit2View = rootView.findViewById(R.id.unit2_buyArticleFragment);
        unit2View.setText(mArticle.get("enota_artikel"));
        TextView unit3View = rootView.findViewById(R.id.unit3_buyArticleFragment);
        unit3View.setText(mArticle.get("enota_artikel"));
        // Article buying amount:
        articleBuyAmountView = rootView.findViewById(R.id.kolicina_artikel_et_buyArticleFragment);
        articleBuyAmountView.setFilters(new InputFilter[]{new DecimalDitgitsInputFilter(5, 2)});
        // Article stock:
        TextView articleStockView = rootView.findViewById(R.id.zaloga_buyArticleFragment);
        articleStockView.setText(mArticle.get("zaloga_artikel"));
        // Decrease buying amount:
        AppCompatImageButton decreaseAmountBtn = rootView.findViewById(R.id.subtract_artikel_buyArticleFragment);
        decreaseAmountBtn.setOnClickListener(v -> {
            String currentAmountString = articleBuyAmountView.getText().toString();
            if (currentAmountString.isEmpty() || Double.parseDouble(currentAmountString) < 0.01) {
                Toast.makeText(requireContext(), "Količina mora biti pozitivna.", Toast.LENGTH_SHORT).show();
            } else {
                double currentAmountDouble = Double.parseDouble(currentAmountString);
                currentAmountDouble -= 0.1;
                articleBuyAmountView.setText(String.format("%.2f", currentAmountDouble));
            }
        });
        // Increase buying amount:
        AppCompatImageButton increaseAmountBtn = rootView.findViewById(R.id.add_artikel_buyArticleFragment);
        increaseAmountBtn.setOnClickListener(v -> {
            String currentAmountString = articleBuyAmountView.getText().toString();
            if (currentAmountString.isEmpty()) {
                currentAmountString = "0";
            }
            double currentAmountDouble = Double.parseDouble(currentAmountString);
            currentAmountDouble += 0.1;
            articleBuyAmountView.setText(String.format("%.2f", currentAmountDouble));
        });
        // Cancel buying article:
        FloatingActionButton cancel = rootView.findViewById(R.id.cancel_artikel_fab_buyArticleFragment);
        cancel.setOnClickListener(v -> this.dismiss());
        // Accept article:
        FloatingActionButton acceptArticleBtn = rootView.findViewById(R.id.buy_artikel_fab_buyArticleFragment);
        acceptArticleBtn.setOnClickListener(v -> {
            String buyingAmountString = articleBuyAmountView.getText().toString();
            // Check if custemer trying to buy 0:
            if (buyingAmountString.isEmpty() || Double.parseDouble(buyingAmountString) == 0) {
                articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                Toast.makeText(requireContext(), "Količina mora biti večja od 0.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if stock is big enough:
                double buyingAmountDouble = Double.parseDouble(buyingAmountString);
                if (Double.parseDouble(Objects.requireNonNull(mArticle.get("zaloga_artikel"))) < buyingAmountDouble) {
                    articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                    Toast.makeText(requireContext(), "Količina je večja kot je na zalogi.", Toast.LENGTH_SHORT).show();
                } else {
                    newArticleOrder = new HashMap<>();
                    newArticleOrder.put("ime", mArticle.get("ime_artikel"));
                    newArticleOrder.put("kolicina", buyingAmountString);
                    newArticleOrder.put("cena", mArticle.get("cena_artikel"));
                    newArticleOrder.put("enota", mArticle.get("enota_artikel"));
                    mInterfaceBuyArticleCallback.callbackBuyArticle_fun(newArticleOrder);
                    this.dismiss();
                }
            }
        });
        return rootView;
    } // onCreateView

    public void setBuyArticleListener(BuyArticleCallBack listener) {
        this.mInterfaceBuyArticleCallback = listener;
    } // setEditArticleListener

    public BuyArticleCallBack getBuyArticleListener() {
        return this.mInterfaceBuyArticleCallback;
    } // getEditArticleListener
}
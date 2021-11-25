package diplomska.naloga.vselokalno.UserFunctions.Basket;

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

public class BasketEditItemFragment extends DialogFragment {

    public interface EditBasketArticleCallBack {
        void callbackEditBasketArticle_fun(Map<String, String> item, int position);
    }

    EditBasketArticleCallBack mBasketArticleCallBack;
    int mPosition;
    Map<String, String> mArticle;
    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "basketItemFragment";
    // Views:
    EditText articleBuyAmountView;

    public BasketEditItemFragment() {
        // Required empty public constructor
    }

    public BasketEditItemFragment(Map<String, String> article, int position) {
        this.mArticle = article;
        this.mPosition = position;
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
    } // onStart

    public static BasketEditItemFragment newInstance(Map<String, String> specificArticleToBuy, int position) {
        return new BasketEditItemFragment(specificArticleToBuy, position);
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
        View rootView = inflater.inflate(R.layout.fragment_basket_item, container, false);
//        makeLogD(TAG, "Article is: " + mArticle);
        // Article image:
        ImageView articleImageView = rootView.findViewById(R.id.article_image_basketItemFragment);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(mArticle.get("slika")));
        GlideApp.with(requireContext()).load(imageRef).into(articleImageView);
        // Article name:
        TextView articleNameView = rootView.findViewById(R.id.ime_artikel_basketItemFragment);
        articleNameView.setText(mArticle.get("ime"));
        // Article price:
        TextView articlePriceView = rootView.findViewById(R.id.cena_artikel_basketItemFragment);
        articlePriceView.setText(mArticle.get("cena"));
        // Article units:
        TextView unit1View = rootView.findViewById(R.id.unit1_basketItemFragment);
        unit1View.setText(mArticle.get("enota"));
        TextView unit2View = rootView.findViewById(R.id.unit2_basketItemFragment);
        unit2View.setText(mArticle.get("enota"));
        TextView unit3View = rootView.findViewById(R.id.unit3_basketItemFragment);
        unit3View.setText(mArticle.get("enota"));
        // Article buying amount:
        articleBuyAmountView = rootView.findViewById(R.id.kolicina_artikel_et_basketItemFragment);
        articleBuyAmountView.setFilters(new InputFilter[]{new DecimalDitgitsInputFilter(5, 2)});
        articleBuyAmountView.setText(mArticle.get("kolicina"));
        // Article stock:
        TextView articleStockView = rootView.findViewById(R.id.zaloga_basketItemFragment);
        articleStockView.setText(mArticle.get("zaloga"));
        // Decrease buying amount:
        AppCompatImageButton decreaseAmountBtn = rootView.findViewById(R.id.subtract_artikel_basketItemFragment);
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
        AppCompatImageButton increaseAmountBtn = rootView.findViewById(R.id.add_artikel_basketItemFragment);
        increaseAmountBtn.setOnClickListener(v -> {
            String currentAmountString = articleBuyAmountView.getText().toString();
            if (currentAmountString.isEmpty()) {
                currentAmountString = "0";
            }
            double currentAmountDouble = Double.parseDouble(currentAmountString);
            currentAmountDouble += 0.1;
            articleBuyAmountView.setText(String.format("%.2f", currentAmountDouble));
        });
        // Cancel editing article:
        FloatingActionButton cancel = rootView.findViewById(R.id.cancel_artikel_fab_basketItemFragment);
        cancel.setOnClickListener(v -> this.dismiss());
        // Accept article:
        FloatingActionButton acceptArticleBtn = rootView.findViewById(R.id.buy_artikel_fab_basketItemFragment);
        acceptArticleBtn.setOnClickListener(v -> {
            String buyingAmountString = articleBuyAmountView.getText().toString();
            // Check if custemer trying to buy 0:
            if (buyingAmountString.isEmpty() || Double.parseDouble(buyingAmountString) == 0) {
                articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                Toast.makeText(requireContext(), "Količina mora biti večja od 0.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if stock is big enough:
                double buyingAmountDouble = Double.parseDouble(buyingAmountString);
                if (Double.parseDouble(Objects.requireNonNull(mArticle.get("zaloga"))) < buyingAmountDouble) {
                    articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                    Toast.makeText(requireContext(), "Količina je večja kot je na zalogi.", Toast.LENGTH_SHORT).show();
                } else {
                    mArticle.put("nova_kolicina", buyingAmountString);
                    mBasketArticleCallBack.callbackEditBasketArticle_fun(mArticle, mPosition);
                    this.dismiss();
                }
            }
        });
        return rootView;
    } // onCreateView

    public void setEditBasketrticleListener(EditBasketArticleCallBack listener) {
        this.mBasketArticleCallBack = listener;
    } // setEditArticleListener

    public EditBasketArticleCallBack getEditBasketrticleListener() {
        return this.mBasketArticleCallBack;
    } // getEditArticleListener
}
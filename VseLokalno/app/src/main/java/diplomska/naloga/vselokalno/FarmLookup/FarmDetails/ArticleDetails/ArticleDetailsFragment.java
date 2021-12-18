package diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.DecimalDitgitsInputFilter;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;

public class ArticleDetailsFragment extends Fragment {

    public interface BuyArticleCallback {
        void onArticleBuyListener(Article article);
    }

    // Current article:
    Article mArticle;
    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "ArticleDetailsFragment";
    // Views:
    EditText articleBuyAmountView;
    // Callback:
    BuyArticleCallback mBuyArticleCallback;

    public ArticleDetailsFragment() {
        // Required empty public constructor
    }

    public ArticleDetailsFragment(Article article) {
        this.mArticle = article;
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
    } // onStart

    public static ArticleDetailsFragment newInstance(Article specificArticleToBuy) {
        return new ArticleDetailsFragment(specificArticleToBuy);
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
        View imageShadow = rootView.findViewById(R.id.image_shadow);
        ImageView articleImageView = rootView.findViewById(R.id.article_image);
        if (mArticle.isPicture()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Article Images/" + mArticle.getArticle_id());
            GlideApp.with(requireContext()).load(imageRef).into(articleImageView);
        } else {
            articleImageView.setVisibility(View.GONE);
            imageShadow.setVisibility(View.GONE);
        }
        // Article name:
        TextView articleNameView = rootView.findViewById(R.id.article_name);
        articleNameView.setText(mArticle.getArticle_name());
        // Article price:
        TextView articlePriceView = rootView.findViewById(R.id.article_price_unit);
        articlePriceView.setText(String.format("%.2f/%s", mArticle.getArticle_price(), mArticle.getArticle_unit()));
        // Article units:
        TextView unit1View = rootView.findViewById(R.id.article_unit);
        unit1View.setText(mArticle.getArticle_unit());
        TextView unit2View = rootView.findViewById(R.id.article_unit2);
        unit2View.setText(mArticle.getArticle_unit());
        // Article buying amount:
        articleBuyAmountView = rootView.findViewById(R.id.article_amount);
        articleBuyAmountView.setFilters(new InputFilter[]{new DecimalDitgitsInputFilter(9, 2)});
        // Article stock:
        TextView articleStockView = rootView.findViewById(R.id.article_storage);
        articleStockView.setText(String.format("%.2f", mArticle.getArticle_storage()));
        // Decrease buying amount:
        AppCompatImageButton decreaseAmountBtn = rootView.findViewById(R.id.decrease_btn);
        decreaseAmountBtn.setOnClickListener(v -> {
            String currentAmountString = articleBuyAmountView.getText().toString();
            if (currentAmountString.isEmpty() || Double.parseDouble(currentAmountString) < 0.0) {
                Toast.makeText(requireContext(), "Količina mora biti pozitivna.", Toast.LENGTH_SHORT).show();
            } else {
                double currentAmountDouble = Double.parseDouble(currentAmountString);
                currentAmountDouble -= 1;
                articleBuyAmountView.setText(String.format("%.2f", currentAmountDouble));
            }
        });
        // Increase buying amount:
        AppCompatImageButton increaseAmountBtn = rootView.findViewById(R.id.increase_btn);
        increaseAmountBtn.setOnClickListener(v -> {
            String currentAmountString = articleBuyAmountView.getText().toString();
            if (currentAmountString.isEmpty()) {
                currentAmountString = "0";
            }
            double currentAmountDouble = Double.parseDouble(currentAmountString);
            currentAmountDouble += 1;
            articleBuyAmountView.setText(String.format("%.2f", currentAmountDouble));
        });
        // Cancel buying article:
        ImageButton cancel = rootView.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        // Accept article:
        AppCompatButton acceptArticleBtn = rootView.findViewById(R.id.add_article_btn);
        acceptArticleBtn.setOnClickListener(v -> {
            String buyingAmountString = articleBuyAmountView.getText().toString();
            // Check if custemer trying to buy 0:
            if (buyingAmountString.isEmpty() || Double.parseDouble(buyingAmountString) == 0) {
                articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                Toast.makeText(requireContext(), "Količina mora biti večja od 0.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if stock is big enough:
                double buyingAmountDouble = Double.parseDouble(buyingAmountString);
                if (mArticle.getArticle_storage() < buyingAmountDouble) {
                    articleStockView.setTextColor(getResources().getColor(R.color.red_normal));
                    Toast.makeText(requireContext(), "Količina je večja kot je na zalogi.", Toast.LENGTH_SHORT).show();
                } else {
                    mArticle.setArticle_buying_amount(buyingAmountDouble);
                    mBuyArticleCallback.onArticleBuyListener(mArticle);
                    getParentFragmentManager().popBackStack();
                }
            }
        });
        return rootView;
    } // onCreateView

    public void setBuyArticleCallback(BuyArticleCallback buyArticleCallback) {
        this.mBuyArticleCallback = buyArticleCallback;
    }
}
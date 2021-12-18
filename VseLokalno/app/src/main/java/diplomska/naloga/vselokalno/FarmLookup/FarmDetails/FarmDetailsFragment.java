package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails.ArticleDetailsFragment;
import diplomska.naloga.vselokalno.R;

public class FarmDetailsFragment extends Fragment implements FarmDetailsCategoryAdapter.CategoryClickCallback, FarmDetailsArticleAdapter.ArticleClickCallback, ArticleDetailsFragment.BuyArticleCallback {

    private final String TAG = "FarmDetailsFragment";
    // Current farm short details:
    private Map<String, String> mCurrentFarm_short;
    // Categories of this farm:
    ArrayList<Category> currentFarmCategories;
    // Articles of this farm:
    ArrayList<Article> currentFarmArticles;
    // Firestore:
    private FirebaseFirestore db;
    // Views
    public RecyclerView mRecyclerView;
    public TextView mFarmName;
    public ImageView mFarmImage;
    LinearLayout topLinearLayout;
    NestedScrollView nestedScrollView;
    View topBackgroundView;
    // Category recycler adapter:
    public FarmDetailsCategoryAdapter mCategoryAdapter;
    // Article recycler adapter:
    public FarmDetailsArticleAdapter mArticleAdapter;

    public FarmDetailsFragment() {
        // Required empty public constructor
    }

    public FarmDetailsFragment(Map<String, String> farm) {
        this.mCurrentFarm_short = farm;
    }

    public static FarmDetailsFragment newInstance(Map<String, String> farm) {
        return new FarmDetailsFragment(farm);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the firebase firestore:
        db = FirebaseFirestore.getInstance();
    } // onCreate

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_farm_details, container, false);
        // Find views:
        topBackgroundView = rootView.findViewById(R.id.top_background);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView_FarmDetailsFragment);
        topLinearLayout = rootView.findViewById(R.id.lin_top);
        mFarmName = rootView.findViewById(R.id.farm_name_FarmDetailsFragment);
        mFarmName.setText(mCurrentFarm_short.get("ime_kmetije"));
        mFarmImage = rootView.findViewById(R.id.farm_image_FarmDetailsFragment);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_FarmDetailsFragment);
        mRecyclerView.setNestedScrollingEnabled(false);
        // Set images:
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("Profile Images/" + mCurrentFarm_short.get("id_kmetije"));
        GlideApp.with(requireContext()).load(imageRef)
                .error(getResources().getDrawable(R.drawable.default_farm_image))
                .into(mFarmImage);
        // Get data:
        currentFarmCategories = new ArrayList<>();
        db.collection("Kmetije").document(Objects.requireNonNull(mCurrentFarm_short.get("id_kmetije")))
                .collection("Kategorije")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentFarmCategories.add(document.toObject(Category.class));
                        }
                        // Set category adapter:
                        setCategoryAdapter();
                    } else {
                        makeLogW(TAG, "Error getting documents: " + task.getException());
                    }
                });
        currentFarmArticles = new ArrayList<>();
        db.collection("Kmetije").document(Objects.requireNonNull(mCurrentFarm_short.get("id_kmetije")))
                .collection("Artikli")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentFarmArticles.add(document.toObject(Article.class));
                        }
                    } else {
                        makeLogW(TAG, "Error getting documents: " + task.getException());
                    }
                });
        // Back button:
        ImageButton backBtn = rootView.findViewById(R.id.backBtn_FarmDetailsFragmen);
        backBtn.setOnClickListener(v -> {
            if (mRecyclerView.getAdapter() instanceof FarmDetailsArticleAdapter)
                setCategoryAdapter();
            else
                requireActivity().onBackPressed();
        });
        // Parallax effect:
        /* intially hide the view */
        topBackgroundView.setAlpha(0f);
        /* set the scroll change listener on scrollview */
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            /* get the maximum height which we have scroll before performing any action */
            int maxDistance = mFarmImage.getHeight();
            /* how much we have scrolled */
            int movement = nestedScrollView.getScrollY();
            /*finally calculate the alpha factor and set on the view */
            float alphaFactor = ((movement * 1.0f) / (maxDistance - topBackgroundView.getHeight()));
            if (movement >= 0 && movement <= maxDistance) {
                /*for image parallax with scroll */
                int temp = -movement / 2;
                mFarmImage.setTranslationY(temp);
                /* set visibility */
                topBackgroundView.setAlpha(alphaFactor);
            }
        });
        return rootView;
    } // onCreateView

    void setCategoryAdapter() {
        mCategoryAdapter = new FarmDetailsCategoryAdapter(
                requireContext(),
                currentFarmCategories,
                currentFarmArticles,
                this
        );
        // Set category adapter:
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } // setCategoryAdapter

    void setArticleAdapter(ArrayList<Article> articlesForCategory) {
        mArticleAdapter = new FarmDetailsArticleAdapter(
                requireContext(),
                articlesForCategory,
                this
        );
        // Set article adapter:
        mRecyclerView.setAdapter(mArticleAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } // setArticleAdapter

    @Override
    public void onCategoryClickListener(Category category) {
        ArrayList<Article> articlesForCategory = new ArrayList<>();
        for (Article el : currentFarmArticles) {
            if (el.getCategory_id().equals(category.getCategory_id())) {
                articlesForCategory.add(el);
            }
        }
        setArticleAdapter(articlesForCategory);
    } // onCategoryClickListener

    @Override
    public void onArticleClickListener(Article article) {
        // Open article details fragment
        ArticleDetailsFragment articleDetailsFragment = ArticleDetailsFragment.newInstance(article);
        articleDetailsFragment.setBuyArticleCallback(this);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.main_fragment_container, articleDetailsFragment)
                .addToBackStack(null)
                .commit();
    } // onArticleClickListener

    @Override
    public void onArticleBuyListener(Article newArticle) {
        for (int orderIndex = 0; orderIndex < appBasket.size(); orderIndex++) {
            Order order = appBasket.get(orderIndex);
            if (order.getId_kmetije().equals(newArticle.getFarm_id())) {
                // Order is from this farm:
                for (int articleIndex = 0; articleIndex < order.getOrdered_articles().size(); articleIndex++) {
                    Article article = order.getOrdered_articles().get(articleIndex);
                    if (article.getArticle_id().equals(newArticle.getArticle_id())) {
                        // Is same article:
                        appBasket.get(orderIndex).editOrdered_articles(articleIndex, newArticle);
                        return;
                    }
                }
                order.addOrdered_articles(newArticle);
                return;
            }
        }
        Order newOrder = new Order();
        newOrder.setIme_kmetije(mCurrentFarm_short.get("ime_kmetije"));
        newOrder.setId_kupca(userID);
        newOrder.setId_kmetije(newArticle.getFarm_id());
        newOrder.setIme_priimek_kupca(appUser.getIme_uporabnika() + " " + appUser.getPriimek_uporabnika());
        newOrder.addOrdered_articles(newArticle);
        appBasket.add(newOrder);
    } // onArticleBuyListener
}
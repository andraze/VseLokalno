package diplomska.naloga.vselokalno.FarmLookup.List;

import static diplomska.naloga.vselokalno.MainActivity.allFarmsDataShort;
import static diplomska.naloga.vselokalno.MainActivity.appBasket;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails.ArticleDetailsFragment;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsArticleAdapter;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsFragment;
import diplomska.naloga.vselokalno.R;

public class ListFragment extends Fragment implements RecyclerAdapter.ItemClickListener, FarmDetailsArticleAdapter.ArticleClickCallback, ArticleDetailsFragment.ArticleDetailsCallback {

    private final String TAG = "ListFragment";
    RecyclerView mRecyclerView;
    ArrayList<Article> mQueryArticles;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        if (mRecyclerView == null)
            makeLogW(TAG, "RecyclerView is NULL!");
        if (mQueryArticles == null || mQueryArticles.isEmpty()) {
            // Initialize the adapter and set it to the RecyclerView.
            RecyclerAdapter mAdapter = new RecyclerAdapter(requireContext(), allFarmsDataShort,
                    requireActivity().getSupportFragmentManager(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            makeLogD(TAG, "(OnCreateView) recycler adapter ready.");
        } else {
            setArticleAdapter();
        }
        // Search for farm:
        SearchView searchView = rootView.findViewById(R.id.search_FarmListFragment);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setArticleQueryAdapter(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return rootView;
    } // onCreateView

    void setArticleQueryAdapter(String query) {
        query = query.toLowerCase();
        mQueryArticles = new ArrayList<>();
        FirebaseFirestore.getInstance().collectionGroup("Artikli")
                .whereArrayContains("article_name_keywords", query)
                .limit(20).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Article articleTemp = documentSnapshot.toObject(Article.class);
                        if (articleTemp != null) {
                            makeLogD(TAG, articleTemp.getArticle_id());
                            mQueryArticles.add(articleTemp);
                        }
                    }
                    setArticleAdapter();
                })
                .addOnFailureListener(e -> makeLogW(TAG, e.getMessage()));
    } // setArticleQueryAdapter

    void setArticleAdapter() {
        FarmDetailsArticleAdapter adapterTemp = new FarmDetailsArticleAdapter(
                requireContext(),
                mQueryArticles,
                this
        );
        // Set article adapter:
        mRecyclerView.setAdapter(adapterTemp);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

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
    public void onItemClick(int position, Map<String, String> farm) {
        makeLogD(TAG, "Farm: " + farm.toString());
        final FarmDetailsFragment detailFragment = FarmDetailsFragment.newInstance(farm);
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .addToBackStack(null)
                    .replace(R.id.main_fragment_container, detailFragment)
                    .commit();
        } else {
            makeLogW(TAG, "(onItemClick) getFragmentManager == null!");
        }
    } // onItemClick

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
        Map<String, String> farmTemp = allFarmsDataShort.get(newArticle.getFarm_id());
        newOrder.setIme_kmetije(Objects.requireNonNull(farmTemp).get("ime_kmetije"));
        newOrder.setId_kupca(userID);
        newOrder.setId_kmetije(newArticle.getFarm_id());
        newOrder.setIme_priimek_kupca(appUser.getIme_uporabnika() + " " + appUser.getPriimek_uporabnika());
        newOrder.addOrdered_articles(newArticle);
        appBasket.add(newOrder);
        // Open article adapter to where we left it:
        setArticleAdapter();
    } // onArticleBuyListener

    @Override
    public void onArticleCancelListener(Article article) {
        // Open article adapter to where we left it:
        setArticleAdapter();
    }

    public boolean onBackPressed() {
        if (mRecyclerView.getAdapter() instanceof FarmDetailsArticleAdapter) {
            mQueryArticles.clear();
            RecyclerAdapter mAdapter = new RecyclerAdapter(requireContext(), allFarmsDataShort,
                    requireActivity().getSupportFragmentManager(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            return true;
        }
        return false;
    }
}
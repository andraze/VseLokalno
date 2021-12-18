package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Article;

import static diplomska.naloga.vselokalno.MainActivity.appArticles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category.EditCategoryFragment;

public class ArticleListFragment extends Fragment implements EditCategoryFragment.EditCategoryRefreshAdapterInterface, RecyclerAdapter_FarmArticles.ItemClickListener, EditArticleFragment.EditArticleRefreshAdapterInterface, NewArticleFragment.NewArticleRefreshAdapterInterface {

    private final String TAG = "ArticleListFragment";
    public RecyclerView mRecyclerView;
    public FirebaseFirestore db;
    RecyclerAdapter_FarmArticles adapter_farmArticles;
    Category mCurrentCategory;
    EditCategoryFragment.EditCategoryRefreshAdapterInterface categoryRefreshAdapterInterface;
    TextView categoryName_view;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    public ArticleListFragment(Category category) {
        this.mCurrentCategory = category;
    }

    public static ArticleListFragment newInstance(Category currentCategoryTemp) {
        return new ArticleListFragment(currentCategoryTemp);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_articleListFragment);
        if (mRecyclerView != null) {
            refreshAdapter(-1);
        }
        ImageButton backBtn = rootView.findViewById(R.id.backBtn_fragmentArticleList);
        backBtn.setOnClickListener(v -> requireActivity().onBackPressed()); // Go back
        ImageButton editCategoryBtn = rootView.findViewById(R.id.editCategoryBtn_fragmentArticleList);
        editCategoryBtn.setOnClickListener(v -> {
            // Open fragment to edit category
            EditCategoryFragment editCategoryFragment = EditCategoryFragment.newInstance(mCurrentCategory);
            editCategoryFragment.show(getParentFragmentManager(), "Uredi kategorijo");
            editCategoryFragment.setEditCategoryListener(this, categoryRefreshAdapterInterface);
        });
        categoryName_view = rootView.findViewById(R.id.category_name_fragmentArticleList);
        categoryName_view.setText(mCurrentCategory.getCategory_name());
        AppCompatButton addArticleBtn = rootView.findViewById(R.id.add_article);
        addArticleBtn.setOnClickListener(v -> {
            // Open fragment to add new article
            NewArticleFragment newArticleFragment = NewArticleFragment.newInstance(mCurrentCategory);
            newArticleFragment.setNewArticleListener(this);
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                    .replace(R.id.main_fragment_container, newArticleFragment)
                    .addToBackStack(null)
                    .commit();
        });
        return rootView;
    } // onCreateView

    @Override
    public void onItemClick(Article article) {
        EditArticleFragment editArticleFragment = EditArticleFragment.newInstance(article);
        editArticleFragment.setEditArticleListener(this);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.main_fragment_container, editArticleFragment)
                .addToBackStack(null)
                .commit();
    } // onItemClick

    public void refreshAdapter(int position) {
        if (position >= 0)
            adapter_farmArticles.notifyItemChanged(position);
        else {
            adapter_farmArticles = new RecyclerAdapter_FarmArticles(requireContext(), mCurrentCategory.getCategory_id(),
                    requireActivity().getSupportFragmentManager(), this);
            mRecyclerView.setAdapter(adapter_farmArticles);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
    }

    @Override
    public void refreshAdapterEditArticle() {
        refreshAdapter(-1);
    }

    @Override
    public void refreshAdapterNewArticle() {
        refreshAdapter(appArticles.size() - 1);
    }

    public void setOnCategoryEditListener(EditCategoryFragment.EditCategoryRefreshAdapterInterface categoryEditListener) {
        this.categoryRefreshAdapterInterface = categoryEditListener;
    } // setOnCategoryEditListener

    @Override
    public void refreshAdapterEditCategory(String categoryName) {
        if (categoryName.isEmpty())
            getParentFragmentManager().popBackStack();
        else {
            mCurrentCategory.setCategory_name(categoryName);
            categoryName_view.setText(mCurrentCategory.getCategory_name());
        }
    } // refreshAdapterEditCategory
}
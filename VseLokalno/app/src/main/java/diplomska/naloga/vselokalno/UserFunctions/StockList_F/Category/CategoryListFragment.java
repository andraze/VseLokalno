package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category;

import static diplomska.naloga.vselokalno.MainActivity.appCategories;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.StockList_F.Article.ArticleListFragment;

public class CategoryListFragment extends Fragment implements RecyclerAdapter_FarmCategories.CategoryClickListener, NewCategoryFragment.NewCategoryRefreshAdapterInterface, EditCategoryFragment.EditCategoryRefreshAdapterInterface {

    RecyclerView categoryRecyclerView;
    RecyclerAdapter_FarmCategories adapter;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        ImageButton backBtn = rootView.findViewById(R.id.backBtn_fragmentCategoryList);
        backBtn.setOnClickListener(v -> requireActivity().onBackPressed());
        AppCompatButton newCategoryBtn = rootView.findViewById(R.id.add_category);
        newCategoryBtn.setOnClickListener(v -> {
            // Open add new category fragment
            NewCategoryFragment newCategoryFragment = NewCategoryFragment.newInstance();
            newCategoryFragment.show(getParentFragmentManager(), "Uredi kategorijo");
            newCategoryFragment.setNewCategoryListener(this);
        });
        categoryRecyclerView = rootView.findViewById(R.id.recycler_view_categoryListFragment);
        refreshAdapter();
        return rootView;
    }

    @Override
    public void onItemClick(Category currentCategoryTemp) {
        // Open article fragment list
        ArticleListFragment articleListFragment = ArticleListFragment.newInstance(currentCategoryTemp);
        articleListFragment.setOnCategoryEditListener(this);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, articleListFragment)
                .addToBackStack(null)
                .commit();
    }

    public void refreshAdapter() {
        adapter = new RecyclerAdapter_FarmCategories(requireContext(), this);
        categoryRecyclerView.setAdapter(adapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void refreshAdapterNewCategory() {
        refreshAdapter();
    }

    @Override
    public void refreshAdapterEditCategory(String categoryName) {
        adapter = new RecyclerAdapter_FarmCategories(requireContext(), this);
        categoryRecyclerView.setAdapter(adapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
package diplomska.naloga.vselokalno.UserFunctions.ArticleList;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

import diplomska.naloga.vselokalno.R;

public class ArticleListFragment extends Fragment implements RecyclerAdapter_FarmArticles.ItemClickListener {

    private final String TAG = "ArticleListFragment";
    public static RecyclerAdapter_FarmArticles.ItemClickListener mItemClickListener;


    public ArticleListFragment() {
        // Required empty public constructor
    }

    public static ArticleListFragment newInstance() {
        return new ArticleListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
        mItemClickListener = this;
        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view_articleListFragment);
        if (mRecyclerView != null) {
            RecyclerAdapter_FarmArticles mAdapter = new RecyclerAdapter_FarmArticles(requireContext(), appFarm.getArtikli(),
                    requireActivity().getSupportFragmentManager(), mItemClickListener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        FloatingActionButton fab = rootView.findViewById(R.id.add_article_fab);
        fab.setOnClickListener(view -> {
            // TODO: open fragment to add a new article
            EditArticleFragment editArticleFragment = new EditArticleFragment();
            editArticleFragment.show(getParentFragmentManager(), "Nov artikel");
        });
        return rootView;
    } // onCreateView

    @Override
    public void onItemClick(int position, Map<String, String> currentArtikel) {
        // TODO: Open fragment to edit article!
    } // onItemClick
}
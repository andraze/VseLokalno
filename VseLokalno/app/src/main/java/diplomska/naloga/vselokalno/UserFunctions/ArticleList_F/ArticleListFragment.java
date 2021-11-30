package diplomska.naloga.vselokalno.UserFunctions.ArticleList_F;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import diplomska.naloga.vselokalno.R;

public class ArticleListFragment extends Fragment implements RecyclerAdapter_FarmArticles.ItemClickListener, RecyclerAdapter_FarmArticles.ArticleDeleteListener, EditArticleFragment.EditArticleRefreshAdapterInterface, NewArticleFragment.NewArticleRefreshAdapterInterface {

    private final String TAG = "ArticleListFragment";
    public static RecyclerAdapter_FarmArticles.ItemClickListener mItemClickListener;
    public static RecyclerAdapter_FarmArticles.ArticleDeleteListener mArticleDeleteListener;
    public RecyclerView mRecyclerView;
    public RecyclerAdapter_FarmArticles mAdapter;
    public FirebaseFirestore db;


    public ArticleListFragment() {
        // Required empty public constructor
    }

    public static ArticleListFragment newInstance() {
        return new ArticleListFragment();
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
        mItemClickListener = this;
        mArticleDeleteListener = this;
        mRecyclerView = rootView.findViewById(R.id.recycler_view_articleListFragment);
        if (mRecyclerView != null) {
            mAdapter = new RecyclerAdapter_FarmArticles(requireContext(), appFarm.getArtikli(),
                    requireActivity().getSupportFragmentManager(), mItemClickListener, mArticleDeleteListener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        FloatingActionButton fab = rootView.findViewById(R.id.add_article_fab);
        fab.setOnClickListener(view -> {
            NewArticleFragment newArticleFragment = new NewArticleFragment();
            newArticleFragment.show(getParentFragmentManager(), "Nov artikel");
            newArticleFragment.setNewArticleListener(this);
        });
        return rootView;
    } // onCreateView

    @Override
    public void onItemClick(int position) {
        EditArticleFragment editArticleFragment = EditArticleFragment.newInstance(position);
        editArticleFragment.show(getParentFragmentManager(), "Uredi artikel");
        editArticleFragment.setEditArticleListener(this);
    } // onItemClick

    public void refreshAdapter(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void refreshAdapterEditArticle(int position) {
        refreshAdapter(position);
    }

    @Override
    public void refreshAdapterNewArticle() {
        refreshAdapter(appFarm.getArtikli().size() - 1);
    }

    public void deleteArticle(int position) {
        DocumentReference docRef = db.collection("Kmetije").document(userID);
        Map<String, String> articleForRemoval = appFarm.getArtikli().get(position);
        appFarm.removeArtikel(position);
        Map<String, Object> updates = new HashMap<>();
        updates.put("artikli", FieldValue.arrayRemove(articleForRemoval));
        docRef.update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                refreshAdapter(position);
                makeLogD(TAG, "(deleteArticle) Article remove successful!");
            } else
                makeLogW(TAG, "(deleteArticle) ERROR\n" + task.getException());
        });
    }

    @Override
    public void onArticleDeleteListener(int position) {
        deleteArticle(position);
    }
}
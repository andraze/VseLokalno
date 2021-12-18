package diplomska.naloga.vselokalno.UserFunctions.Basket_U;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.Basket_U.BuyingOrder.OrderingFragment;

public class BasketFragment extends Fragment implements BasketRecyclerAdapter.BasketArticleClickCallback, BasketEditItemFragment.EditBasketArticleCallBack {

    BasketRecyclerAdapter mAdapter;
    RecyclerView recyclerView;
    TextView priceSumView;

    public BasketFragment() {
        // Required empty public constructor
    }

    public static BasketFragment newInstance() {
        return new BasketFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_basket, container, false);
        priceSumView = rootView.findViewById(R.id.price_sum_tv_BasketFragment);
        if (!appBasket.isEmpty()) { // Basket_U is not empty:
            calc_sum_price();
        }
        recyclerView = rootView.findViewById(R.id.recycler_view_basketFragment);
        if (recyclerView != null) {
            mAdapter = new BasketRecyclerAdapter(requireContext(), this);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        // Clear the basket:
        TextView cancelBtn = rootView.findViewById(R.id.clear_basketFragment);
        cancelBtn.setOnClickListener(v -> {
            if (appBasket.isEmpty()) {
                Toast.makeText(requireContext(), "Košarica je prazna.", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Ste prepričani, da želite izprazniti košarico?")
                        .setTitle("Izprazni košarico");
                // Add the buttons
                builder.setPositiveButton("Da", (dialog, id) -> {
                    appBasket.clear();
                    priceSumView.setText("0.0");
                    mAdapter = new BasketRecyclerAdapter(requireContext(), this);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                });
                builder.setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // Proceed with buying:
        AppCompatButton continueBtn = rootView.findViewById(R.id.proceed_basketFragment);
        continueBtn.setOnClickListener(v -> {
            if (appBasket.isEmpty()) {
                Toast.makeText(requireContext(), "Košarica je prazna.", Toast.LENGTH_SHORT).show();
            } else {
                OrderingFragment orderingFragment = OrderingFragment.newInstance(0);
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, orderingFragment)
                        .addToBackStack("ProceedToBuying")
                        .commit();
            }
        });
        return rootView;
    } // onCreateView

    @SuppressLint("DefaultLocale")
    private void calc_sum_price() {
        double fullSumPrice = 0.0;
        for (Order order : appBasket) {
            for (Article article : order.getOrdered_articles()) {
                fullSumPrice += article.getArticle_buying_amount() * article.getArticle_price();
            }
        }
        priceSumView.setText(String.format("%.2f", fullSumPrice));
    } // calc_sum_price

    public void refreshAdapter(int position) {
        mAdapter.notifyItemChanged(position);
    } // refreshAdapter

    @Override
    public void onBasketItemClickListener(Article article, int position) {
        // Open fragment for editing specific item in basket:
        BasketEditItemFragment basketEditItemFragment = BasketEditItemFragment.newInstance(article, position);
        basketEditItemFragment.setEditBasketrticleListener(this);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .replace(R.id.main_fragment_container, basketEditItemFragment)
                .addToBackStack(null)
                .commit();
    } // onBasketItemClickListener

    @Override
    public void onEditBasketArticleListener(Article editedArticle, int position) {
        // Find item in appBasket and replace with new values:
        for (int orderIndex = 0; orderIndex < appBasket.size(); orderIndex++) {
            Order order = appBasket.get(orderIndex);
            for (int articleIndex = 0; articleIndex < order.getOrdered_articles().size(); articleIndex++) {
                Article article = order.getOrdered_articles().get(articleIndex);
                if (article.getArticle_id().equals(editedArticle.getArticle_id())) {
                    appBasket.get(orderIndex).editOrdered_articles(articleIndex, editedArticle);
                    refreshAdapter(position);
                    return;
                }
            }
        }
    } // onEditBasketArticleListener

    @Override
    public void onDeleteBasketArticleListener(Article deleteArticle, int position) {
        for (Order order : appBasket) {
            for (Article article : order.getOrdered_articles()) {
                if (article.getArticle_id().equals(deleteArticle.getArticle_id())) {
                    order.removeOrdered_articles(deleteArticle);
                    if (order.getOrdered_articles().isEmpty()) {
                        appBasket.remove(order);
                    }
                    calc_sum_price();
                    refreshAdapter(position);
                    return;
                }
            }
        }
    } // onDeleteBasketArticleListener
}
package diplomska.naloga.vselokalno.UserFunctions.Basket;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.Basket.BuyingOrder.OrderingFragment;

public class BasketFragment extends Fragment implements BasketRecyclerAdapter.RemoveItemFromBasketInterface, BasketRecyclerAdapter.BasketArticleClickListener, BasketEditItemFragment.EditBasketArticleCallBack {

    BasketRecyclerAdapter mAdapter;
    RecyclerView recyclerView;
    BasketRecyclerAdapter.RemoveItemFromBasketInterface removeItemFromBasketListener;
    BasketRecyclerAdapter.BasketArticleClickListener mBasketArticleClickListener;
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
        removeItemFromBasketListener = this;
        mBasketArticleClickListener = this;
        priceSumView = rootView.findViewById(R.id.price_sum_tv_BasketFragment);
        if (!appBasket.isEmpty()) { // Basket is not empty:
            calc_sum_price();
        }
        recyclerView = rootView.findViewById(R.id.recycler_view_basketFragment);
        if (recyclerView != null) {
            mAdapter = new BasketRecyclerAdapter(requireContext(), removeItemFromBasketListener, mBasketArticleClickListener);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        // Clear the basket:
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.cancel_fab_basketFragment);
        cancelBtn.setOnClickListener(v -> {
            appBasket.clear();
            priceSumView.setText("0.0");
            mAdapter = new BasketRecyclerAdapter(requireContext(), removeItemFromBasketListener, mBasketArticleClickListener);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
        // Proceed with buying:
        FloatingActionButton continueBtn = rootView.findViewById(R.id.buy_fab_basketFragment);
        continueBtn.setOnClickListener(v -> {
            OrderingFragment orderingFragment = OrderingFragment.newInstance(0);
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_fragment_container, orderingFragment)
                    .addToBackStack("ProceedToBuying")
                    .commit();
        });
        return rootView;
    }

    @SuppressLint("DefaultLocale")
    private void calc_sum_price() {
        double fullSumPrice = 0;
        for (ZaKupca el : appBasket) {
            Map<String, String> numOfUnitsMap = el.getNarocilo_kolicine();
            for (Map.Entry<String, String> entry : el.getNarocilo_cene().entrySet()) {
                fullSumPrice += Double.parseDouble(entry.getValue()) * Double.parseDouble(Objects.requireNonNull(numOfUnitsMap.get(entry.getKey())));
            }
        }
        priceSumView.setText(String.format("%.2f", fullSumPrice));
    }

    @Override
    public void removeItemFromBasketFun(int position) {
        calc_sum_price();
        refreshAdapter(position);
    } // removeItemFromBasketFun

    public void refreshAdapter(int position) {
        mAdapter.notifyItemChanged(position);
    } // refreshAdapter

    @Override
    public void onBasketItemClickListener(Map<String, String> article, int position) {
        // Open fragment for editing specific item in basket:
        BasketEditItemFragment basketEditItemFragment = BasketEditItemFragment.newInstance(article, position);
        basketEditItemFragment.show(getParentFragmentManager(), "Uredi izdelek");
        basketEditItemFragment.setEditBasketrticleListener(this);
    }

    @Override
    public void callbackEditBasketArticle_fun(Map<String, String> item, int position) {
        // Find item in appBasket and replace with new values:
        for (int i = 0; i < appBasket.size(); i++) {
            ZaKupca el = appBasket.get(i);
            Map<String, String> numOfUnitsMap = el.getNarocilo_kolicine();
            Map<String, String> priceMap = el.getNarocilo_cene();
            Map<String, String> unitMap = el.getNarocilo_enote();
            for (Map.Entry<String, String> entry : el.getNarocilo_slike().entrySet()) {
                if (entry.getValue().equals(item.get("slika")) &&
                        entry.getKey().equals(item.get("ime")) &&
                        Objects.equals(numOfUnitsMap.get(entry.getKey()), item.get("kolicina")) &&
//                        Objects.requireNonNull(priceMap.get(entry.getKey())).equals(String.valueOf(Double.parseDouble(Objects.requireNonNull(item.get("cena"))) / Double.parseDouble(Objects.requireNonNull(numOfUnitsMap.get(entry.getKey()))))) &&
                        Objects.requireNonNull(priceMap.get(entry.getKey())).equals(item.get("cena")) &&
                        Objects.requireNonNull(unitMap.get(entry.getKey())).equals(item.get("enota"))
                ) { // This entry.key() article needs to be updated:
                    appBasket.get(i).addNarocilo_kolicine(item.get("ime"), item.get("nova_kolicina"));
                    mAdapter = new BasketRecyclerAdapter(requireContext(), removeItemFromBasketListener, mBasketArticleClickListener);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    calc_sum_price();
                    return;
                }
            }
        }
    }
}
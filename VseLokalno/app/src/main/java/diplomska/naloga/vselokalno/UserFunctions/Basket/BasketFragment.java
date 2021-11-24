package diplomska.naloga.vselokalno.UserFunctions.Basket;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.R;

public class BasketFragment extends Fragment implements BasketRecyclerAdapter.RemoveItemFromBasketInterface {

    BasketRecyclerAdapter mAdapter;
    RecyclerView recyclerView;
    BasketRecyclerAdapter.RemoveItemFromBasketInterface removeItemFromBasketListener;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_basket, container, false);
        removeItemFromBasketListener = this;
        if (!appBasket.isEmpty()) { // Basket is not empty:
            priceSumView = rootView.findViewById(R.id.price_sum_tv_BasketFragment);
            calc_sum_price();
        }
        recyclerView = rootView.findViewById(R.id.recycler_view_basketFragment);
        if (recyclerView != null) {
            mAdapter = new BasketRecyclerAdapter(requireContext(), removeItemFromBasketListener);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        // Clear the basket:
        FloatingActionButton cancelBtn = rootView.findViewById(R.id.cancel_fab_basketFragment);
        cancelBtn.setOnClickListener(v -> {
            appBasket.clear();
            mAdapter = new BasketRecyclerAdapter(requireContext(), removeItemFromBasketListener);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
        // Proceed with buying:
        FloatingActionButton continueBtn = rootView.findViewById(R.id.buy_fab_basketFragment);
        continueBtn.setOnClickListener(v -> {
            // TODO: Proceed with purchase.
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
}
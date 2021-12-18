package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU;

import static diplomska.naloga.vselokalno.MainActivity.appActiveOrders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU.SpecificOrder.ActiveOrderDetailsFragment;

public class ActiveOrdersListFragment extends Fragment implements ActiveOrdersRecyclerAdapter.ActiveOrdersAdapterCallback, ActiveOrderDetailsFragment.UpdateSpecificOrder {

    private final String TAG = "ActiveOrdersListFragment";
    SwipeRefreshLayout pullToRefresh;
    TextView numOfActiveOrdersView;
    RecyclerView activeOrdersRecyclerView;

    public ActiveOrdersListFragment() {
        // Required empty public constructor
    }

    public static ActiveOrdersListFragment newInstance() {
        return new ActiveOrdersListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_active_orders_list, container, false);
        // Get active orders:
        numOfActiveOrdersView = rootView.findViewById(R.id.num_of_active_orders_activeOrderListFragment);
        activeOrdersRecyclerView = rootView.findViewById(R.id.recycler_view_activeOrderListFragment);
        ActiveOrdersRecyclerAdapter adapter;
        numOfActiveOrdersView.setText(String.valueOf(appActiveOrders.size()));
        adapter = new ActiveOrdersRecyclerAdapter(requireContext(), this);
        activeOrdersRecyclerView.setAdapter(adapter);
        activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pullToRefresh = rootView.findViewById(R.id.swipe_refresh_layout_activeOrderListFragment);
        // Refreshing data...
        pullToRefresh.setOnRefreshListener(this::refreshData);
        return rootView;
    } // onCreateView

    public void refreshData() {
        ActiveOrdersRecyclerAdapter adapter;
        numOfActiveOrdersView.setText(String.valueOf(appActiveOrders.size()));
        adapter = new ActiveOrdersRecyclerAdapter(requireContext(), this);
        activeOrdersRecyclerView.setAdapter(adapter);
        activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } // refreshData

    @Override
    public void onFinishCallback() {
        pullToRefresh.setRefreshing(false);
    } //onFinishCallback

    @Override
    public void onSpecificOrderClickCallback(Order order) {
        ActiveOrderDetailsFragment activeOrderDetails = ActiveOrderDetailsFragment.newInstance(order, this);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, activeOrderDetails)
                .addToBackStack(null)
                .commit();
    } // onSpecificOrderClickCallback

    @Override
    public void onUpdateOrderCallback() {
        refreshData();
    } // onUpdateOrderCallback
}
package diplomska.naloga.vselokalno.UserFunctions.OrderHistory_FU;

import static diplomska.naloga.vselokalno.MainActivity.appOrderHistory;

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
import diplomska.naloga.vselokalno.UserFunctions.OrderHistory_FU.SpecificOrder.OrderHistoryDetailsFragment;

public class OrderHistoryListFragment extends Fragment implements OrderHistoryRecyclerAdapter.OrderHistoryAdapterCallback {

    private final String TAG = "ActiveOrdersListFragment";
    SwipeRefreshLayout pullToRefresh;
    TextView numOfActiveOrdersView;
    RecyclerView ordersRecyclerView;

    public OrderHistoryListFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryListFragment newInstance() {
        return new OrderHistoryListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order_history_list, container, false);
        // Get active orders:
        numOfActiveOrdersView = rootView.findViewById(R.id.num_of_orders_OrderHistoryListFragment);
        ordersRecyclerView = rootView.findViewById(R.id.recycler_view_OrderHistoryListFragment);
        numOfActiveOrdersView.setText(String.valueOf(appOrderHistory.size()));
        OrderHistoryRecyclerAdapter adapter = new OrderHistoryRecyclerAdapter(requireContext(), this);
        ordersRecyclerView.setAdapter(adapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pullToRefresh = rootView.findViewById(R.id.swipe_refresh_layout_OrderHistoryListFragment);
        // Refreshing data...
        pullToRefresh.setOnRefreshListener(this::refreshData);
        return rootView;
    } // onCreateView

    public void refreshData() {
        numOfActiveOrdersView.setText(String.valueOf(appOrderHistory.size()));
        OrderHistoryRecyclerAdapter adapter = new OrderHistoryRecyclerAdapter(requireContext(), this);
        ordersRecyclerView.setAdapter(adapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } // refreshData

    @Override
    public void onFinishCallback() {
        pullToRefresh.setRefreshing(false);
    } //onFinishCallback

    @Override
    public void onSpecificOrderClickCallback(Order order) {
        OrderHistoryDetailsFragment orderHistoryDetailsFragment = OrderHistoryDetailsFragment.newInstance(order);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, orderHistoryDetailsFragment)
                .addToBackStack(null)
                .commit();
    } // onSpecificOrderClickCallback

}
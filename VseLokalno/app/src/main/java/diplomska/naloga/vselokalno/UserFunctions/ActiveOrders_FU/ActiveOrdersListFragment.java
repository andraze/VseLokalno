package diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import diplomska.naloga.vselokalno.R;

public class ActiveOrdersListFragment extends Fragment implements ActiveOrdersRecyclerAdapter.ActiveOrdersAdapterCallback {

    private final String TAG = "ActiveOrdersListFragment";
    SwipeRefreshLayout pullToRefresh;
    TextView numOfActiveOrders;
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
        numOfActiveOrders = rootView.findViewById(R.id.num_of_active_orders_activeOrderListFragment);
        activeOrdersRecyclerView = rootView.findViewById(R.id.recycler_view_activeOrderListFragment);
        ActiveOrdersRecyclerAdapter adapter;
        if (appUser.isLastnik_kmetije()) {
            numOfActiveOrders.setText(String.valueOf(appFarm.getAktivnaNarocila().size()));
            adapter = new ActiveOrdersRecyclerAdapter(requireContext(), appFarm, this);
        } else {
            numOfActiveOrders.setText(String.valueOf(appUser.getAktivnaNarocila().size()));
            adapter = new ActiveOrdersRecyclerAdapter(requireContext(), appUser, this);
        }
        activeOrdersRecyclerView.setAdapter(adapter);
        activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pullToRefresh = rootView.findViewById(R.id.swipe_refresh_layout_activeOrderListFragment);
        pullToRefresh.setOnRefreshListener(() -> {
            makeLogD(TAG, "Data refreshing started ...");
            makeLogD(TAG, "(onCompleteRefresh) Data refreshing complete!");
            refreshData(); // Refreshing data...
        });
        return rootView;
    } // onCreateView

    public void refreshData() {
        ActiveOrdersRecyclerAdapter adapter;
        if (appUser.isLastnik_kmetije()) {
            numOfActiveOrders.setText(String.valueOf(appFarm.getAktivnaNarocila().size()));
            adapter = new ActiveOrdersRecyclerAdapter(requireContext(), appFarm, this);
        } else {
            numOfActiveOrders.setText(String.valueOf(appUser.getAktivnaNarocila().size()));
            adapter = new ActiveOrdersRecyclerAdapter(requireContext(), appUser, this);
        }
        activeOrdersRecyclerView.setAdapter(adapter);
        activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } // refreshData

    @Override
    public void onFinishCallback() {
        pullToRefresh.setRefreshing(false);
    } //onFinishCallback

}
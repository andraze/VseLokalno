package diplomska.naloga.vselokalno.FarmLookup.List;

import static diplomska.naloga.vselokalno.MainActivity.allFarmsDataShort;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsFragment;
import diplomska.naloga.vselokalno.R;

public class ListFragment extends Fragment implements RecyclerAdapter.ItemClickListener {

    private final String TAG = "ListFragment";

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        makeLogI(TAG, "(onCreateView) Working!");
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        if (mRecyclerView != null) {
            // Initialize the adapter and set it to the RecyclerView.
            RecyclerAdapter mAdapter = new RecyclerAdapter(requireContext(), allFarmsDataShort,
                    requireActivity().getSupportFragmentManager(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            makeLogD(TAG, "(OnCreateView) recycler adapter ready.");
        } else {
            makeLogW(TAG, "(onCreateView) RecyclerView is NULL!");
        }
        return rootView;
    } // onCreateView

    @Override
    public void onItemClick(int position, Map<String, String> farm) {
        makeLogD(TAG, "Farm: " + farm.toString());
        final FarmDetailsFragment detailFragment = FarmDetailsFragment.newInstance(farm);
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    .addToBackStack(null)
                    .replace(R.id.main_fragment_container, detailFragment)
                    .commit();
        } else {
            makeLogW(TAG, "(onItemClick) getFragmentManager == null!");
        }
    } // onItemClick
}
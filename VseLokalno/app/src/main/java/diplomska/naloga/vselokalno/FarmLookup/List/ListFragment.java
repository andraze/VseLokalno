package diplomska.naloga.vselokalno.FarmLookup.List;

import static diplomska.naloga.vselokalno.MainActivity.allFarmsDataShort;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.util.Map;

import diplomska.naloga.vselokalno.R;

public class ListFragment extends Fragment implements RecyclerAdapter.ItemClickListener {

    public static RecyclerAdapter.ItemClickListener mItemClickListener;
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
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mItemClickListener = this;
        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        if (mRecyclerView != null) {
            // Initialize the adapter and set it to the RecyclerView.
            RecyclerAdapter mAdapter = new RecyclerAdapter(requireContext(), allFarmsDataShort,
                    requireActivity().getSupportFragmentManager(), mItemClickListener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            makeLogW(TAG, "(onCreateView) RecyclerView is NULL!");
        }
        return rootView;
    } // onCreateView

    @Override
    public void onItemClick(int position, Map<String, String> farm, TextView textView, String transitionName,
                            ImageView imageView, String imageTransitionName, StorageReference imageRef) {
//        TODO: show details and articles the farm has:
//        Gson gson = new Gson();
//        final DetailFragment detailFragment = DetailFragment.newInstance(gson.toJson(activity), drawableResource, transitionName, imageTransitionName);
//        detailFragment.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.do_transaction));
//        if (getFragmentManager() != null) {
//            getFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(
//                            R.anim.enter_from_right, R.anim.exit_to_left,
//                            R.anim.enter_from_left, R.anim.exit_to_right
//                    )
//                    .addSharedElement(imageView, imageTransitionName)
//                    .addSharedElement(textView, transitionName)
//                    .addToBackStack(null)
//                    .replace(R.id.main_fragment_container, detailFragment)
//                    .commit();
//        } else {
//            makeLogW(TAG, "(onItemClick) getFragmentManager == null!");
//        }
    } // onItemClick
}
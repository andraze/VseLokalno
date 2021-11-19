package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogI;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.ArticleList.RecyclerAdapter_FarmArticles;

public class FarmDetailsFragment extends Fragment {

    private String TAG = "FarmDetailsFragment";
    private static final String FARM_ID = "farm_id";
    private String mFarm_id;
    private FirebaseFirestore db;
    Kmetija farmOfInterest;
    public RecyclerView mRecyclerView;
    public FarmDetailsArticleAdapter mAdapter;
    public TextView mFarmName;
    public ImageView mFarmImage;

    public FarmDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * @param param Farm id to look up.
     * @return A new instance of fragment FarmDetailsFragment.
     */
    public static FarmDetailsFragment newInstance(String param) {
        FarmDetailsFragment fragment = new FarmDetailsFragment();
        Bundle args = new Bundle();
        args.putString(FARM_ID, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the firebase firestore:
        db = FirebaseFirestore.getInstance();
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_farm_details, container, false);
        mFarmName = rootView.findViewById(R.id.farm_name_text_view_FarmDetailsFragment);
        mFarmImage = rootView.findViewById(R.id.farm_image_view_FarmDetailsFragment);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_FarmDetailsFragment);
        if (getArguments() != null) {
            mFarm_id = getArguments().getString(FARM_ID);
            DocumentReference farmDocReference = db.collection("Kmetije").document(mFarm_id);
            farmDocReference.get().addOnSuccessListener(documentSnapshot -> {
                farmOfInterest = documentSnapshot.toObject(Kmetija.class);
//                Got specific farm:
                if (farmOfInterest != null) {
                    mFarmName.setText(farmOfInterest.getIme_kmetije());
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                            .child("Uporabniške profilke/" + mFarm_id);
                    GlideApp.with(requireContext()).load(imageRef).into(mFarmImage);
//                    Fill the recycler view with articles:
                    if (mRecyclerView != null) {
                        mAdapter = new FarmDetailsArticleAdapter(requireContext(), farmOfInterest.getArtikli(),
                                requireActivity().getSupportFragmentManager());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                }
            });
        }
        return rootView;
    } // onCreateView
}
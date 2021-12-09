package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import static diplomska.naloga.vselokalno.MainActivity.appBasket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails.BuyArticleFragment;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class FarmDetailsFragment extends Fragment implements FarmDetailsArticleAdapter.OnArticleBuyerClickListener, BuyArticleFragment.BuyArticleCallBack {

    private final String TAG = "FarmDetailsFragment";
    private static final String FARM_ID = "farm_id";
    private String mFarm_id;
    private FirebaseFirestore db;
    Farm farmOfInterest;
    ArrayList<Map<String, String>> mArticlesForBuying;
    public RecyclerView mRecyclerView;
    public FarmDetailsArticleAdapter mAdapter;
    public TextView mFarmName;
    public ImageView mFarmImage;
    public ImageView mUserImage;
    public static FarmDetailsArticleAdapter.OnArticleBuyerClickListener mArticleBuyerClickListener;

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
        mArticleBuyerClickListener = this;
        mFarmName = rootView.findViewById(R.id.farm_name_text_view_FarmDetailsFragment);
        mFarmImage = rootView.findViewById(R.id.farm_image_view_FarmDetailsFragment);
        mUserImage = rootView.findViewById(R.id.profile_image_view_FarmDetailsFragment);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_FarmDetailsFragment);
        if (getArguments() != null) {
            mFarm_id = getArguments().getString(FARM_ID);
            DocumentReference farmDocReference = db.collection("Kmetije").document(mFarm_id);
            farmDocReference.get().addOnSuccessListener(documentSnapshot -> {
                farmOfInterest = documentSnapshot.toObject(Farm.class);
//                Got specific farm:
                if (farmOfInterest != null) {
                    mFarmName.setText(farmOfInterest.getIme_kmetije());
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                            .child("Uporabniške profilke/" + mFarm_id);
                    GlideApp.with(requireContext()).load(imageRef).error(R.drawable.default_profile_picture).into(mUserImage);
                    //                    Fill the recycler view with articles:
                    mArticlesForBuying = farmOfInterest.getArtikli();
                    if (mRecyclerView != null) {
                        mAdapter = new FarmDetailsArticleAdapter(requireContext(), farmOfInterest.getArtikli(),
                                requireActivity().getSupportFragmentManager(), mArticleBuyerClickListener);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                }
            });
        }
        mUserImage.setOnClickListener(v -> {
            ShowFarmDescriptionFragment showFarmDescriptionFragment = ShowFarmDescriptionFragment.newInstance(farmOfInterest);
            showFarmDescriptionFragment.show(getParentFragmentManager(), "Podrobnosti kmetije");
        });
        return rootView;
    } // onCreateView

    @Override
    public void onArticleClickListener(int position) {
        BuyArticleFragment buyArticleFragment = BuyArticleFragment.newInstance(mArticlesForBuying.get(position));
        buyArticleFragment.show(getParentFragmentManager(), "Kupi izdelek");
        buyArticleFragment.setBuyArticleListener(this);
    } // onArticleClickListener

    @Override
    public void callbackBuyArticle_fun(Map<String, String> order) {
        if (appBasket.isEmpty()) {
            // New order:
            newOrder(order);
        } else {
            boolean found = false;
            for (ZaKupca zaKupcaOrderFromSpecificFarm : appBasket) {
                if (zaKupcaOrderFromSpecificFarm.getId_kmetije().equals(mFarm_id)) {
                    // Update order:
                    found = true;
                    zaKupcaOrderFromSpecificFarm.addNarocilo_imena(order.get("id"), order.get("ime"));
                    zaKupcaOrderFromSpecificFarm.addNarocilo_cene(order.get("id"), order.get("cena"));
                    zaKupcaOrderFromSpecificFarm.addNarocilo_enote(order.get("id"), order.get("enota"));
                    zaKupcaOrderFromSpecificFarm.addNarocilo_kolicine(order.get("id"), order.get("kolicina"));
                    zaKupcaOrderFromSpecificFarm.addNarocilo_slike(order.get("id"), order.get("slika"));
                    zaKupcaOrderFromSpecificFarm.addNarocilo_zaloge(order.get("id"), order.get("zaloga"));
                }
            }
            if (!found) {
                // New order:
                newOrder(order);
            }
        }
    } // callbackBuyArticle_fun

    private void newOrder(Map<String, String> order) {
        ZaKupca newOrder = new ZaKupca();
        newOrder.setIme_kmetije(farmOfInterest.getIme_kmetije());
        newOrder.setId_kmetije(mFarm_id);
        newOrder.setNaslov_dostave(farmOfInterest.getNaslov_dostave());
        newOrder.addNarocilo_imena(order.get("id"), order.get("ime"));
        newOrder.addNarocilo_cene(order.get("id"), order.get("cena"));
        newOrder.addNarocilo_enote(order.get("id"), order.get("enota"));
        newOrder.addNarocilo_kolicine(order.get("id"), order.get("kolicina"));
        newOrder.addNarocilo_slike(order.get("id"), order.get("slika"));
        newOrder.addNarocilo_zaloge(order.get("id"), order.get("zaloga"));
        appBasket.add(newOrder);
    } // newOrder
}
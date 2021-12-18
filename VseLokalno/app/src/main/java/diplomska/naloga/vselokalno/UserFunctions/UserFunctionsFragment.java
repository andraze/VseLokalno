package diplomska.naloga.vselokalno.UserFunctions;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.bottomNavigation;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.ActiveOrders_FU.ActiveOrdersListFragment;
import diplomska.naloga.vselokalno.UserFunctions.Basket_U.BasketFragment;
import diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category.CategoryListFragment;


public class UserFunctionsFragment extends Fragment {

    // Linear Layouts:
    LinearLayoutCompat kosarica_narocilaLinearLayout;
    LinearLayoutCompat seznam_artikliLinearLayout;
    LinearLayoutCompat zeljeLinearLayout;
    LinearLayoutCompat zgodovinaLinearLayout;
    // Fragment:
    CategoryListFragment categoryListFragment;
    private final String TAG = "UserFunctionsFragment";


    public UserFunctionsFragment() {
        // Required empty public constructor
    }

    public static UserFunctionsFragment newInstance() {
        return new UserFunctionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public void onResume() {
        if (!appUser.isLastnik_kmetije())
            bottomNavigation.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_functions, container, false);
        // Fill user data:
        TextView name = rootView.findViewById(R.id.name_text_view_userFunctionsFragment);
        String nameToSet;
        if (appUser.isLastnik_kmetije()) nameToSet = appFarm.getIme_kmetije();
        else
            nameToSet = MessageFormat.format("{0} {1}", appUser.getIme_uporabnika(), appUser.getPriimek_uporabnika());
        name.setText(nameToSet);
        TextView email = rootView.findViewById(R.id.email_text_view_userFunctionsFragment);
        email.setText(appUser.getEmail());
        // Go to firebase storage only if they uploaded a picture:
        CircleImageView profilePic = rootView.findViewById(R.id.profile_image_view_userFunctionsFragment);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("Profile Images/" + userID);
        GlideApp.with(requireContext()).load(imageRef)
//                .addListener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        makeLogW(TAG, "Error loading image resource.");
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        return false;
//                    }
//                })
                .error(getResources().getDrawable(R.drawable.default_profile_picture))
                .into(profilePic);
        if (appUser.isLastnik_kmetije()) {
            // Change some text if farm owner:
            TextView kosaricaVNarocila = rootView.findViewById(R.id.tv_kosarica_userFunctions);
            kosaricaVNarocila.setText(R.string.artikli);
//            TextView seznamVArtikli = rootView.findsetText(R.string.narocilaViewById(R.id.tv_seznam_userFunctions);
//            seznamVArtikli.);
        }
        // Košarica / artikli:
        kosarica_narocilaLinearLayout = rootView.findViewById(R.id.narocilo_kosarica_userFunctionsFragment);
        kosarica_narocilaLinearLayout.setOnClickListener(l -> {
            bottomNavigation.setVisibility(View.GONE);
            if (appUser.isLastnik_kmetije()) { // ?
                categoryListFragment = CategoryListFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, categoryListFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                BasketFragment basketFragment = BasketFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, basketFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        // Aktivna naročila / Aktivna naročila:
        seznam_artikliLinearLayout = rootView.findViewById(R.id.artikli_nakupovalniSeznam_userFunctionsFragment);
        seznam_artikliLinearLayout.setOnClickListener(l -> {
            bottomNavigation.setVisibility(View.GONE);
            ActiveOrdersListFragment activeOrdersListFragment = ActiveOrdersListFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_fragment_container, activeOrdersListFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Zelje:
        zeljeLinearLayout = rootView.findViewById(R.id.zelje_userFunctionsFragment);
        zeljeLinearLayout.setOnClickListener(l -> {
            bottomNavigation.setVisibility(View.GONE);
            // TODO: open zelje
        });
        // Zgodovina:
        zgodovinaLinearLayout = rootView.findViewById(R.id.zgodovina_userFunctionsFragment);
        zgodovinaLinearLayout.setOnClickListener(l -> {
            bottomNavigation.setVisibility(View.GONE);
            // TODO: open history of orders
        });
        return rootView;
    } // onCreateView
}
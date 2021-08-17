package diplomska.naloga.vselokalno.UserFunctions;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.appUser;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.UserFunctions.ArticleList.ArticleListFragment;


public class UserFunctionsFragment extends Fragment {

    // Linear Layouts:
    LinearLayoutCompat kosarica_narocilaLinearLayout;
    LinearLayoutCompat seznam_artikliLinearLayout;
    LinearLayoutCompat zeljeLinearLayout;
    LinearLayoutCompat zgodovinaLinearLayout;


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
        if (!appUser.isUse_default_pic()) {
            // Go to firebase storage only if they uploaded a picture:
            CircleImageView profilePic = rootView.findViewById(R.id.profile_image_view_userFunctionsFragment);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("Uporabniške profilke/" + userID);
            GlideApp.with(requireContext()).load(imageRef).into(profilePic);
        }
        if (appUser.isLastnik_kmetije()) {
            // Change some text if farm owner:
            TextView kosaricaVNarocila = rootView.findViewById(R.id.tv_kosarica_userFunctions);
            kosaricaVNarocila.setText(R.string.narocila);
            TextView seznamVArtikli = rootView.findViewById(R.id.tv_seznam_userFunctions);
            seznamVArtikli.setText(R.string.artikli);
        }
        // Košarica / Naročila:
        kosarica_narocilaLinearLayout = rootView.findViewById(R.id.narocilo_kosarica_userFunctionsFragment);
        kosarica_narocilaLinearLayout.setOnClickListener(l -> {
            if (appUser.isLastnik_kmetije()) {
                // TODO open active narocila
            } else {
                // TODO open kosarica
            }
        });
        // Nakupovalni seznam / artikli:
        seznam_artikliLinearLayout = rootView.findViewById(R.id.artikli_nakupovalniSeznam_userFunctionsFragment);
        seznam_artikliLinearLayout.setOnClickListener(l -> {
            if (appUser.isLastnik_kmetije()) {
                // TODO open seznam artiklov
                ArticleListFragment articleListFragment = ArticleListFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, articleListFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // TODO open nakupovalni seznam
            }
        });
        // Zelje:
        zeljeLinearLayout = rootView.findViewById(R.id.zelje_userFunctionsFragment);
        zeljeLinearLayout.setOnClickListener(l -> {
            // TODO: open zelje
        });
        // Zgodovina:
        zgodovinaLinearLayout = rootView.findViewById(R.id.zgodovina_userFunctionsFragment);
        zgodovinaLinearLayout.setOnClickListener(l -> {
            // TODO: open history of orders
        });
        return rootView;
    } // onCreateView
}
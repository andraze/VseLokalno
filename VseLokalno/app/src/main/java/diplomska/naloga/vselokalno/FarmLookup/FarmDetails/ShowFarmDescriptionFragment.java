package diplomska.naloga.vselokalno.FarmLookup.FarmDetails;

import android.annotation.SuppressLint;

import androidx.fragment.app.DialogFragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.R;

public class ShowFarmDescriptionFragment extends DialogFragment {

    Kmetija farmOfInterest;

    public ShowFarmDescriptionFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ShowFarmDescriptionFragment(Kmetija farm) {
        this.farmOfInterest = farm;
    }

    public static ShowFarmDescriptionFragment newInstance(Kmetija farm) {
        return new ShowFarmDescriptionFragment(farm);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_farm_descripton, container, false);
        TextView farmAddressView = rootView.findViewById(R.id.farm_address_view_ShowFarmDescriptionFragment);
        farmAddressView.setText(farmOfInterest.getNaslov_kmetije());
        TextView farmNameView = rootView.findViewById(R.id.farm_name_view_ShowFarmDescriptionFragment);
        farmNameView.setText(farmOfInterest.getIme_kmetije());
        TextView farmDescriptionView = rootView.findViewById(R.id.farm_description_view_ShowFarmDescriptionFragment);
        farmDescriptionView.setText(farmOfInterest.getOpis_kmetije());
        return rootView;
    }
}
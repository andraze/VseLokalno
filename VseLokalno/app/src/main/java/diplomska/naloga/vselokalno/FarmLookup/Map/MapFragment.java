package diplomska.naloga.vselokalno.FarmLookup.Map;

import static diplomska.naloga.vselokalno.MainActivity.allFarmsDataShort;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsFragment;
import diplomska.naloga.vselokalno.R;
import im.delight.android.location.SimpleLocation;

public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {

    // TAG:
    private static final String TAG = "MapFragment";
    // Google map:
    private GoogleMap mMap;
    // Location settings request:
    static final int LOCATION_SETTINGS_REQUEST = 1;
    // Current LAT and LON:
    public static double LAT = 46.0660318;
    public static double LON = 14.3920158;
    // ArrayList of farms:
    ArrayList<Map<String, String>> farmsList = new ArrayList<>();
    // ArrayList of markers for farms:
    public static ArrayList<Marker> farmMarkers = new ArrayList<>();
    // Request permission ID:
    private static final int requestPermissionID = 200;
    // Simple location (for current location):
    private SimpleLocation location;
    // On Info Window Click Listener:
    GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = this;

    // Callback:
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            try {
                // Customise the styling of the base map using a JSON object defined in a raw resource file.
                boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style));
                if (!success) {
                    Log.e("MAPS_ACTIVITY", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("MAPS_ACTIVITY", "Can't find style. Error: ", e);
            }
            if (LAT == 0 || LON == 0) {
                LAT = 46.0660318;
                LON = 14.3920158;
            }
            LatLng MyLocation = new LatLng(LAT, LON);
            MarkerOptions marker = new MarkerOptions().position(MyLocation);

            showFarms();

            // Changing marker icon
            marker.icon(vectorToBitmap(R.drawable.ic_current_location));
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 12));
        } // onMapReady
    };

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    } // onCreateView

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        location = new SimpleLocation(requireContext());
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                makeToastNeedLocation();
                startActivityForResult((new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)), 1);
            }, 0);
        }

        // Get user location:
        try {
            LAT = location.getLatitude();
            LON = location.getLongitude();

        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "We need your location to start the map.", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(3000);                   // Wait for 3 Seconds
            } catch (Exception e2) {
                System.out.println("Error: " + e2);      // Catch the exception
            }
            restartApp();
        }
        // Log the location under 'USER LOCATION':
        Log.d("USER LOCATION", location.toString());

        if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    requestPermissionID
            );
        }
    } // onViewCreated

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            // user is back from location settings - check if location services are now enabled
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestPermissionID);
                return;
            }
            restartApp();
        }
    } // onActivityResult

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        makeLogD(TAG, marker.getPosition().toString());
        Map<String, String> lookingForFarm = null;
        for (Map.Entry<String,Map<String, String>> entry : allFarmsDataShort.entrySet()) {
            Map<String, String> oneFarmDetails = entry.getValue();
            if (Objects.equals(oneFarmDetails.get("ime_kmetije"), marker.getTitle()) &&
                    marker.getPosition().equals(new LatLng(Double.parseDouble(Objects.requireNonNull(oneFarmDetails.get("lat"))), Double.parseDouble(Objects.requireNonNull(oneFarmDetails.get("lon")))))) {
                lookingForFarm = oneFarmDetails;
                lookingForFarm.put("id_kmetije", entry.getKey());
                break;
            }
        }
        if (lookingForFarm != null) {
            final FarmDetailsFragment detailFragment = FarmDetailsFragment.newInstance(lookingForFarm);
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
                makeLogW(TAG, "(onInfoWindowClick) getFragmentManager == null!");
            }
        }
    } // onInfoWindowClick

    private void showFarms() {
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdaper(requireActivity()));
        for (Map.Entry<String,Map<String, String>> entry : allFarmsDataShort.entrySet()) {
            Map<String, String> farm = entry.getValue();
            LatLng latLngOfFarm;
            if (farm.get("lat") == null || farm.get("lon") == null)
                makeLogW(TAG, "(showFarms) lat or lon == null!");
            latLngOfFarm = new LatLng(Double.parseDouble(Objects.requireNonNull(farm.get("lat"))), Double.parseDouble(Objects.requireNonNull(farm.get("lon"))));
//            makeLogD(TAG, "(showFarms) " + latLngOfFarm.toString());

            Marker farmMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(latLngOfFarm)
                            .title(farm.get("ime_kmetije"))
                            .snippet("Obišči kmetijo " + farm.get("ime_kmetije"))
                            .icon(vectorToBitmap(R.drawable.ic_map_farm))
            );
            farmMarkers.add(farmMarker);
        }
    } // showFarms2

    @Override
    public void onResume() {
        super.onResume();
        location.beginUpdates();
    } // onResume

    @Override
    public void onPause() {
        location.endUpdates();
        super.onPause();
    } // onPause

    public void centrirajMe() {
        LAT = location.getLatitude();
        LON = location.getLongitude();
        LatLng MyLocation = new LatLng(LAT, LON);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 12));
    } // centrirajMe

    public void makeToastNeedLocation() {
        Toast.makeText(requireContext(), "Potrebujemo tvojo lokacijo da prikažemo mapo.", Toast.LENGTH_LONG).show();
    } // makeToastNeedLocation

    private void restartApp() {
        Intent i = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
        assert i != null;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        requireActivity().finish();
    } // restartApp

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        // DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    } // vectorToBitmap
}
package diplomska.naloga.vselokalno;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.FarmLookup.Map.MapFragment;
import diplomska.naloga.vselokalno.SignInUp.SignInUpActivity;


public class MainActivity extends AppCompatActivity {

    //    TAG:
    private static final String TAG = "MainActivity";
    //    Firebase AUTH:
    private FirebaseAuth mAuth;
    //    Firebase user:
    public FirebaseUser currentUser;
    //    App user:
    public static User appUser;
    //    Firestore:
    public FirebaseFirestore db;
    //    Firebase storage:
    public FirebaseStorage storage;
    //    Other variables:
    public static final String[] allTimes = {"07:00-08:00", "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00", "18:00-19:00", "19:00-20:00", "20:00-21:00"};
    public static final int[] timeIDs = {R.id.time_7_8, R.id.time_8_9, R.id.time_9_10, R.id.time_10_11, R.id.time_11_12, R.id.time_12_13, R.id.time_13_14,
            R.id.time_14_15, R.id.time_15_16, R.id.time_16_17, R.id.time_17_18, R.id.time_18_19, R.id.time_19_20, R.id.time_20_21};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        appUser = new User();

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
    }// onCreate

    @Override
    protected void onStart() {
        super.onStart();
        makeLogI(TAG, "(onStart) Started.");
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        makeLogD(TAG, "(onStart) current user: " + currentUser);
        updateUI(currentUser);
    }// onStart

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
//            Send to login/sign up page.
            makeLogD(TAG, "(updateUI) sending user to SignInUpActivity.");
            Intent loginIntent = new Intent(this, SignInUpActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
//            Refresh user data.
            DocumentReference docRef = db.collection("Uporabniki").document(currentUser.getUid());
            docRef.get()
                    .addOnFailureListener(e -> makeLogW(TAG, "(updateUI) ERROR getting document user.\n" + e.getMessage()))
                    .addOnSuccessListener(documentSnapshot -> {
                        appUser = documentSnapshot.toObject(User.class);
                        if (appUser != null) {
                            makeLogD(TAG, "(updateUI) success, got user:\n" + appUser.toString());
                        } else {
                            makeLogW(TAG, "User came back NULL!");
                        }
                    });

        }
    }// updateUI

    public static void makeLogD(String TAG, String Message) {
        Log.d(TAG, Message);
    } // makeLogD

    public static void makeLogW(String TAG, String Message) {
        Log.w(TAG, Message);
    } // makeLogW

    public static void makeLogI(String TAG, String Message) {
        Log.i(TAG, Message);
    } // makeLogI

    public void signOut(View view) {
        mAuth.signOut();
        Intent restart = new Intent(this, MainActivity.class);
        startActivity(restart);
        finish();
    } // signOut

    public void findLoc(View view) {
        EditText et = findViewById(R.id.eT);
        getLocationFromAddress(et.getText().toString());
    } //findLoc

    public Map<String, Object> getLocationFromAddress(String strAddress) {
        Map<String, Object> latLon = new HashMap<>();
        latLon.put("lat", 46.056946);
        latLon.put("lon", 14.505751);
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return latLon;
            }
            Address location = address.get(0);
            latLon.put("lat", location.getLatitude());
            latLon.put("lon", location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLon;
    } // getLocationFromAddress

    public void openMap(View view) {
        MapFragment mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right
                )
                .replace(R.id.main_fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
    }
}

// TODO NEXT TIME: start on map
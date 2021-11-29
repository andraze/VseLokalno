package diplomska.naloga.vselokalno;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import diplomska.naloga.vselokalno.DataObjects.AllFarms;
import diplomska.naloga.vselokalno.DataObjects.Kmetija;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.FarmLookup.List.ListFragment;
import diplomska.naloga.vselokalno.FarmLookup.Map.MapFragment;
import diplomska.naloga.vselokalno.SignInUp.SignInUpActivity;
import diplomska.naloga.vselokalno.UserFunctions.UserFunctionsFragment;


public class MainActivity extends AppCompatActivity {

    //    TAG:
    private static final String TAG = "MainActivity";
    //    Firebase AUTH:
    private FirebaseAuth mAuth;
    //    Firebase user:
    public FirebaseUser currentUser;
    //    App user:
    public static String userID = "";
    public static User appUser;
    //    users farm:
    public static Kmetija appFarm;
    //    Firestore:
    public FirebaseFirestore db;
    //    Firebase storage:
    public FirebaseStorage storage;
    //    Bottom navigation:
    public ChipNavigationBar bottomNavigation;
    //    Fragments:
    private MapFragment mapFragment;
    private ListFragment listFragment;
    private UserFunctionsFragment userFunctionsFragment;
    //    Other variables:
    public static final String[] allTimesFull = {"07:00-08:00", "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00", "18:00-19:00", "19:00-20:00", "20:00-21:00"};
    public static final String[] allTimes = {"07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"};
    public static final int[] timeIDs = {R.id.time_7_8, R.id.time_8_9, R.id.time_9_10, R.id.time_10_11, R.id.time_11_12, R.id.time_12_13, R.id.time_13_14,
            R.id.time_14_15, R.id.time_15_16, R.id.time_16_17, R.id.time_17_18, R.id.time_18_19, R.id.time_19_20, R.id.time_20_21};
    public static ArrayList<Map<String, String>> allFarmsDataShort;
    // Orders for buyer:
    public static ArrayList<ZaKupca> appBasket;
    // Shared preferences file:
    private static final String sharedPrefFile = "app_basket_shared_preferences_filename";
    static SharedPreferences mySharedPreferences;
    public static final String mAppBasketSharedPrefKey = "shared_preferences_key";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Initialise the shared preferences:
        mySharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        // Initialise the firebase authentication:
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // makeLogD(TAG, "(onCreate) current user: " + currentUser);
        // Initialise the firebase firestore:
        db = FirebaseFirestore.getInstance();
        // Initialise the firebase storage:
        storage = FirebaseStorage.getInstance();
        // Initialise the app user and app farm and app basket:
        appUser = new User();
        appFarm = new Kmetija();
        appBasket = new ArrayList<>();
        // Use the bottom navigation:
        bottomNavigation = findViewById(R.id.bottom_nav);
        bottomNavigation.setItemSelected(R.id.map_menu, true);
        mapFragment = new MapFragment();
        listFragment = ListFragment.newInstance();
        userFunctionsFragment = UserFunctionsFragment.newInstance();
        bottomNavigation.setOnItemSelectedListener(id -> {
            // id == id number of tab.
            switch (id) {
                case R.id.list_menu:
                    openFragment(1);
                    break;
                case R.id.user_menu:
                    openFragment(2);
                    break;
                default:
                    // Map
                    openFragment(0);
                    break;
            }
        });
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(currentUser);
    } // onCreate

    @Override
    protected void onStop() {
        super.onStop();
        saveAppBasket();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAppBasket();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
//            Send to login/sign up page.
            makeLogD(TAG, "(updateUI) sending user to SignInUpActivity.");
            Intent loginIntent = new Intent(this, SignInUpActivity.class);
            startActivity(loginIntent);
            finish();
        } else
            // Refresh user data.
            getUserData();
    } // updateUI

    private void getUserData() {
        DocumentReference docRef = db.collection("Uporabniki").document(currentUser.getUid());
        docRef.get()
                .addOnFailureListener(e -> makeLogW(TAG, "(getUserData) ERROR getting document user.\n" + e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    appUser = documentSnapshot.toObject(User.class);
                    if (appUser != null) {
                        userID = currentUser.getUid();
                        makeLogD(TAG, "(getUserData) success, got user:\n" + appUser.toString());
                        if (appUser.isLastnik_kmetije())
                            getUserFarm();
                        else
                            getAllFarmData();
                    } else {
                        makeLogW(TAG, "(getUserData) User came back NULL!");
                    }
                });
    } // getUserData

    private void getUserFarm() {
        DocumentReference docRef = db.collection("Kmetije").document(currentUser.getUid());
        docRef.get()
                .addOnFailureListener(e -> makeLogW(TAG, "(getUserFarm) ERROR getting document user.\n" + e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    appFarm = documentSnapshot.toObject(Kmetija.class);
                    if (appFarm != null) {
                        makeLogD(TAG, "(getUserFarm) success, got farm:\n" + appFarm.toString());
                        findViewById(R.id.bottom_nav).setVisibility(View.GONE);
                        openFragment(2);
                    } else {
                        makeLogW(TAG, "(getUserFarm) Farm came back NULL!");
                    }
                });
    } // getUserFarm

    @SuppressLint("NonConstantResourceId")
    private void getAllFarmData() {
        DocumentReference allFarmsDocRef = db.collection("Kmetije").document("Vse_kmetije");
        allFarmsDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Success
                    if (documentSnapshot.exists()) {
                        makeLogD(TAG, "(getAllFarmData) Got document: " + documentSnapshot.getId());
                        AllFarms allFarms = documentSnapshot.toObject(AllFarms.class);
                        if (allFarms != null) {
                            allFarmsDataShort = allFarms.getSeznam_vseh_kmetij();
                            openFragment(0);
                        }
                    } else {
                        makeLogW(TAG, "(getAllFarmData) No such document!");
                    }
                })
                .addOnFailureListener(e -> {
                    // Fail
                    makeLogW(TAG, "(getAllFarmData) got FAIL with:\n" + e.getMessage());
                });
    }

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

    public void openFragment(int id) {
        Fragment fragmentToOpen = null;
        switch (id) {
            case 0:
                fragmentToOpen = mapFragment;
                break;
            case 1:
                fragmentToOpen = listFragment;
                break;
            case 2:
                fragmentToOpen = userFunctionsFragment;
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentToOpen != null) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_fragment_container, fragmentToOpen)
                    .commit();
        } else
            makeLogW(TAG, "(openFragment) ERROR! fragmentToOpen == null!");
    } // openMap

    public static void saveAppBasket() {
        Gson gson = new Gson();
        String appBasketJsonString = gson.toJson(appBasket);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(mAppBasketSharedPrefKey, appBasketJsonString);
        editor.apply();
    } // saveAppBasket

    public static void loadAppBasket() {
        String jsonAppBasketTemp = mySharedPreferences.getString(mAppBasketSharedPrefKey, "");
        if (!jsonAppBasketTemp.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ZaKupca>>() {
            }.getType();
            appBasket = gson.fromJson(jsonAppBasketTemp, type);
        }
    } // loadAppBasket

}
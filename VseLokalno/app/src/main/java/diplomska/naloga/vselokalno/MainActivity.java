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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.DataObjects.AllFarms;
import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.DataObjects.Order;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.ArticleDetails.ArticleDetailsFragment;
import diplomska.naloga.vselokalno.FarmLookup.FarmDetails.FarmDetailsFragment;
import diplomska.naloga.vselokalno.FarmLookup.List.ListFragment;
import diplomska.naloga.vselokalno.FarmLookup.Map.MapFragment;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.SignInUp.SignInUpActivity;
import diplomska.naloga.vselokalno.UserFunctions.UserFunctionsFragment;


public class MainActivity extends AppCompatActivity {

    //    TAG:
    private static final String TAG = "MainActivity";
    //    Firebase AUTH:
    private FirebaseAuth mAuth;
    //    Firebase user:
    public static FirebaseUser currentUser;
    //    App user:
    public static String userID = "";
    public static User appUser;
    //    users farm:
    public static Farm appFarm;
    // Active orders:
    public static ArrayList<Order> appActiveOrders;
    // Order history:
    public static ArrayList<Order> appOrderHistory;
    //    Firestore:
    public FirebaseFirestore db;
    //    Firebase storage:
    public FirebaseStorage storage;
    //    Firebase messaging:
    FirebaseMessaging firebaseMessaging;
    final String fcmTopic = "active_orders_update";
    //    Bottom navigation:
    public static ChipNavigationBar bottomNavigation;
    //    Fragments:
    private MapFragment mapFragment;
    private ListFragment listFragment;
    private UserFunctionsFragment userFunctionsFragment;
    //    private String GoToFragment;
    public static final String[] allTimes = {"07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"};
    public static final int[] timeIDs = {R.id.time_7_8, R.id.time_8_9, R.id.time_9_10, R.id.time_10_11, R.id.time_11_12, R.id.time_12_13, R.id.time_13_14,
            R.id.time_14_15, R.id.time_15_16, R.id.time_16_17, R.id.time_17_18, R.id.time_18_19, R.id.time_19_20, R.id.time_20_21};
    public static ArrayList<Map<String, String>> allFarmsDataShort;
    // Orders for buyer:
    public static ArrayList<Order> appBasket;
    // Articles and category for farm:
    public static ArrayList<Article> appArticles;
    public static ArrayList<Category> appCategories;
    // Shared preferences file:
    private static final String sharedPrefFile = "app_basket_shared_preferences_filename";
    static SharedPreferences mySharedPreferences;
    public static final String mAppBasketSharedPrefKey = "shared_preferences_key";
    // Translate day names:
    public static final String[] dayNamesEng = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public static final String[] dayNamesSlo = {"Pon", "Tor", "Sre", "Čet", "Pet", "Sob", "Ned"};
    ListenerRegistration activeOrdersListener = null;
    static ListenerRegistration orderHistoryListener = null;
    ListenerRegistration farmArticlesListener = null;
    ListenerRegistration farmCategoriesListener = null;
    // For image cropping:
    public static ImageCropper appImageCropper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the apps image cropper:
        appImageCropper = new ImageCropper(this, this);
//        GoToFragment = getIntent().getStringExtra("OpenFragment");
        // Initialise the shared preferences:
        mySharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        // Initialise the firebase authentication:
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Initialise the firebase Firestore:
        db = FirebaseFirestore.getInstance();
        // Initialise the firebase storage:
        storage = FirebaseStorage.getInstance();
        // Firebase messaging subscribe to notification topic:
        firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic(fcmTopic);
        // Initialise the app user and app farm and app basket:
        appUser = new User();
        appFarm = new Farm();
        appBasket = new ArrayList<>();
        appArticles = new ArrayList<>();
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
        stopActiveOrdersListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAppBasket();
        if (currentUser != null)
            startActiveOrdersListeners();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
//            Send to login/sign up page.
            makeLogD(TAG, "(updateUI) sending user to SignInUpActivity.");
            firebaseMessaging.unsubscribeFromTopic(fcmTopic);
            Intent loginIntent = new Intent(this, SignInUpActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            // Refresh user data and navigate to relative fragments.
            getUserData();
        }
    } // updateUI

    public void startActiveOrdersListeners() {
        if (appUser != null && !appUser.getIme_uporabnika().equals("") && activeOrdersListener == null && !userID.isEmpty()) {
            setActiveOrdersListener(appUser.isLastnik_kmetije());
            setOrderHistoryListener();
        }
    } // startActiveOrdersListeners

    public void stopActiveOrdersListeners() {
        if (activeOrdersListener != null) {
            activeOrdersListener.remove();
            activeOrdersListener = null;
        }
        if (farmArticlesListener != null) {
            farmArticlesListener.remove();
            farmArticlesListener = null;
        }
        if (farmCategoriesListener != null) {
            farmCategoriesListener.remove();
            farmCategoriesListener = null;
        }
        if (orderHistoryListener != null) {
            orderHistoryListener.remove();
            orderHistoryListener = null;
        }
    }

    public void setActiveOrdersListener(boolean isFarmer) {
        Query colRef;
        if (isFarmer) { // We have a farmer:
            colRef = FirebaseFirestore.getInstance().collection("Kmetije").document(currentUser.getUid())
                    .collection("Aktivna Naročila").orderBy("datum_prevzema");
            setFarmAppArticlesListener();
            setFarmAppCategoriesListener();
        } else // We have a buyer:
            colRef = FirebaseFirestore.getInstance().collection("Uporabniki").document(currentUser.getUid())
                    .collection("Aktivna Naročila").orderBy("datum_prevzema");
        activeOrdersListener = colRef
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "(setActiveOrdersListener) Listen failed.", e);
                        return;
                    }
                    appActiveOrders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        appActiveOrders.add(doc.toObject(Order.class));
                    }
                    checkStageOfActiveOrders();
                    Log.d(TAG, "(setActiveOrdersListener) Current orders: " + appActiveOrders);
                });
    } // setActiveOrdersListener

    private void checkStageOfActiveOrders() {
        for (Order order : appActiveOrders) {
            if (order.getOpravljeno() == 3)
                sendOrderToHistory(order);
            if (orderPickupPassed(order))
                sendOrderToHistory(order);
        }
    } // checkStageOfActiveOrders

    @SuppressLint("SimpleDateFormat")
    private boolean orderPickupPassed(Order order) {
        try {
            Date dateOfPickup = order.getDatum_prevzema();
            Date todayDate = new Date();
            String datePickupString = new SimpleDateFormat("dd-MM-yyyy").format(Objects.requireNonNull(dateOfPickup));
            String dateTodayString = new SimpleDateFormat("dd-MM-yyyy").format(todayDate);
            dateOfPickup = new SimpleDateFormat("dd-MM-yyyy").parse(datePickupString);
            todayDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateTodayString);
            if (todayDate != null && todayDate.after(dateOfPickup))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendOrderToHistory(Order order) {
        if (appUser.isLastnik_kmetije()) { // We have a farmer:
            db.collection("Kmetije").document(currentUser.getUid())
                    .collection("Aktivna Naročila").document(order.getId_order()).delete();
            db.collection("Kmetije").document(currentUser.getUid())
                    .collection("Zgodovina Naročil").document(order.getId_order()).set(order);
        } else { // We have a buyer:
            db.collection("Uporabniki").document(currentUser.getUid())
                    .collection("Aktivna Naročila").document(order.getId_order()).delete();
            db.collection("Uporabniki").document(currentUser.getUid())
                    .collection("Zgodovina Naročil").document(order.getId_order()).set(order);
        }
    } // sendOrderToHistory

    public void setOrderHistoryListener() {
        Query colRef;
        if (appUser.isLastnik_kmetije()) { // We have a farmer:
            colRef = FirebaseFirestore.getInstance().collection("Kmetije").document(currentUser.getUid())
                    .collection("Zgodovina Naročil").orderBy("datum_narocila");
        } else // We have a buyer:
            colRef = FirebaseFirestore.getInstance().collection("Uporabniki").document(currentUser.getUid())
                    .collection("Zgodovina Naročil").orderBy("datum_narocila");
        orderHistoryListener = colRef
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "(setActiveOrdersListener) Listen failed.", e);
                        return;
                    }
                    appOrderHistory = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        appOrderHistory.add(doc.toObject(Order.class));
                    }
                });
    } // setOrderHistoryListener

    private void getUserData() { // Get user data only once and open relative fragments (for onCreate)
        DocumentReference docRef = db.collection("Uporabniki").document(currentUser.getUid());
        docRef.get()
                .addOnFailureListener(e -> makeLogW(TAG, "(getUserData) ERROR getting document user.\n" + e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    appUser = documentSnapshot.toObject(User.class);
                    startActiveOrdersListeners();
                    if (appUser != null) {
                        userID = currentUser.getUid();
                        makeLogD(TAG, "(getUserData) success, got user:\n" + appUser.toString());
                        startActiveOrdersListeners();
                        if (appUser.isLastnik_kmetije())
                            getUserFarm();
                        else {
                            getAllFarmData();
                            findViewById(R.id.bottom_nav).setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeLogW(TAG, "(getUserData) User came back NULL!");
                    }
                });
    } // getUserData

    private void getUserFarm() { // Get farm data only once and open relative fragments (for onCreate)
        DocumentReference docRef = db.collection("Kmetije").document(currentUser.getUid());
        docRef.get()
                .addOnFailureListener(e -> makeLogW(TAG, "(getUserFarm) ERROR getting document user.\n" + e.getMessage()))
                .addOnSuccessListener(documentSnapshot -> {
                    appFarm = documentSnapshot.toObject(Farm.class);
                    if (appFarm != null) {
                        makeLogD(TAG, "(getUserFarm) success, got farm:\n" + appFarm.toString());
                        openFragment(2);
                    } else {
                        makeLogW(TAG, "(getUserFarm) Farm came back NULL!");
                    }
                });
    } // getUserFarm

    private void setFarmAppArticlesListener() {
        farmArticlesListener = FirebaseFirestore.getInstance()
                .collection("Kmetije").document(userID)
                .collection("Artikli")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "(setFarmAppArticlesListener) Listen failed.", e);
                        return;
                    }
                    appArticles = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        appArticles.add(doc.toObject(Article.class));
                    }
                    Log.d(TAG, "(setFarmAppArticlesListener) Current articles: " + appArticles);
                });
    } // setFarmAppArticlesListener

    private void setFarmAppCategoriesListener() {
        farmCategoriesListener = FirebaseFirestore.getInstance()
                .collection("Kmetije").document(userID)
                .collection("Kategorije")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "(setFarmAppCategoriesListener) Listen failed.", e);
                        return;
                    }
                    appCategories = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        appCategories.add(doc.toObject(Category.class));
                    }
                    Log.d(TAG, "(setFarmAppCategoriesListener) Current articles: " + appCategories);
                });
    } // setFarmAppCategoriesListener

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
//        if (GoToFragment != null) {
//            switch (GoToFragment) {
//                case "UserFunctionsFragment":
//                    id = 2;
//                    break;
//            }
//        }
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
            Type type = new TypeToken<ArrayList<Order>>() {
            }.getType();
            appBasket = gson.fromJson(jsonAppBasketTemp, type);
        }
    } // loadAppBasket

    public static String getSloDayName(String engDayName) {
        for (int i = 0; i < dayNamesEng.length; i++) {
            if (dayNamesEng[i].equals(engDayName)) {
                return dayNamesSlo[i];
            }
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFullPickupDateSlo(Date date) {
        String dayNameEng = new SimpleDateFormat("E").format(Objects.requireNonNull(date));
        return getSloDayName(dayNameEng) + " " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
    } // getFullDateSlo

    @SuppressLint("SimpleDateFormat")
    public static String getFullOrderDateSlo(Date date) {
        String dayNameEng = new SimpleDateFormat("E").format(Objects.requireNonNull(date));
        return getSloDayName(dayNameEng) + " " + new SimpleDateFormat("dd-MM-yyyy").format(date);
    } // getFullDateSlo

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        boolean handled = false;
        for (Fragment f : fragmentList) {
            if (f instanceof FarmDetailsFragment)
                handled = ((FarmDetailsFragment) f).onBackPressed();
            if (f instanceof ArticleDetailsFragment)
                handled = ((ArticleDetailsFragment) f).onBackPressed();
            if (handled)
                break;
        }
        if (!handled) {
            super.onBackPressed();
        }
    } // onBackPressed
}
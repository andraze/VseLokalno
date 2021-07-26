package diplomska.naloga.vselokalno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;

import diplomska.naloga.vselokalno.DataObjects.ImageSaver;
import diplomska.naloga.vselokalno.DataObjects.User;
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
                        if (appUser != null)
                            makeLogD(TAG, "(updateUI) success, got user:\n" + appUser.toString());
                        else
                            makeLogW(TAG, "User came back NULL!");
                    });
            storage.getReference().child(currentUser.getUid()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
//                        Save to local storage
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            ImageSaver imageSaver = new ImageSaver(this);
                            imageSaver.setDirectoryName("Vse Lokalno");
                            imageSaver.setFileName(currentUser.getUid());
                            imageSaver.save(bitmap);
                            makeLogD(TAG, "(updateUI) Image saved successfully.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(exception -> {
                        // Handle any errors
                        makeLogW(TAG, "(updateUI) ERROR\n" + exception.getMessage());
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
}

// TODO NEXT TIME: add farm, test sign in/up, making account + using pic, ..., start on map
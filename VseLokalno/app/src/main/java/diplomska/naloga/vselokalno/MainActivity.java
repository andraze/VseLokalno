package diplomska.naloga.vselokalno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import diplomska.naloga.vselokalno.SignInUp.SignInUpActivity;


public class MainActivity extends AppCompatActivity {

//    TAG:
    private static final String TAG = "MainActivity";
//    Firebase AUTH:
    private FirebaseAuth mAuth;
//    Firebase user:
    public FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        makeLogD(TAG, "(onStart) current user: " + currentUser);
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
//            Send to login/sign up page.
            makeLogD(TAG, "(updateUI) sending user to SignInUpActivity.");
            Intent loginIntent = new Intent(this, SignInUpActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    public static void makeLogD(String TAG, String Message) {
        Log.d(TAG, Message);
    } // makeLogD

    public static void makeLogW(String TAG, String Message) {
        Log.w(TAG, Message);
    } // makeLogW
}
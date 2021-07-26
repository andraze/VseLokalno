package diplomska.naloga.vselokalno.SignInUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;

public class SignInUpActivity extends AppCompatActivity {

//        TAG:
    private static final String TAG = "SignInUpActivity";
    //        Firebase AUTH:
    public FirebaseAuth mAuth;
    //    User data
    public static User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        userData = new User();

        mAuth = FirebaseAuth.getInstance();

        ChooseFragment chooseFragment = ChooseFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view_signINUP, chooseFragment)
                .commit();
    }// onCreate

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }// isValidEmail
}
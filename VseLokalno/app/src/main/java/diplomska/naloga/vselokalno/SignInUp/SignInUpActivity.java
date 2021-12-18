package diplomska.naloga.vselokalno.SignInUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import diplomska.naloga.vselokalno.DataObjects.Farm;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.R;
import diplomska.naloga.vselokalno.DataObjects.User;
import diplomska.naloga.vselokalno.SignInUp.CreateAFarm.FarmTimeFragment;

public class SignInUpActivity extends AppCompatActivity {

    //        TAG:
    private static final String TAG = "SignInUpActivity";
    //        Firebase AUTH:
    public FirebaseAuth mAuth;
    //    User data
    public static User userData;
    //    Farm data:
    public static Farm farmData;
    //    FarmTimeFragment
    public static FarmTimeFragment farmTimeFragment;
    // Sign in / up crop image
    public static ImageCropper signInUpImageCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        userData = new User();
        farmData = new Farm();

        mAuth = FirebaseAuth.getInstance();

        signInUpImageCropper = new ImageCropper(this, this);

        ChooseFragment chooseFragment = ChooseFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view_signINUP, chooseFragment)
                .commit();
    }// onCreate

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }// isValidEmail

    public void selectTime(View view) {
        MaterialButton btn = (MaterialButton) view;
        farmTimeFragment.selectTime(btn);
    } // selectTime
}
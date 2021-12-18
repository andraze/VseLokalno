package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category;

import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.R;

public class NewCategoryFragment extends DialogFragment {

    public interface NewCategoryRefreshAdapterInterface {
        void refreshAdapterNewCategory();
    }

    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "NewCategoryFragment";
    //    Other variables
    Category newCategory;
    NewCategoryRefreshAdapterInterface mInterface;

    public NewCategoryFragment() {
        // Required empty public constructor
    }

    public static NewCategoryFragment newInstance() {
        return new NewCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
    } // onStart

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_category, container, false);
        newCategory = new Category();
        // Article name:
        EditText categoryName = rootView.findViewById(R.id.newCategoryName_et);
        // Save button:
        AppCompatButton saveCategory = rootView.findViewById(R.id.add_categoryBtn);
        saveCategory.setOnClickListener(view -> {
            if (categoryName.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ime kategorije.", Toast.LENGTH_SHORT).show();
            else {
                newCategory.setFarm_id(userID);
                newCategory.setCategory_name(categoryName.getText().toString().trim());
                String uniqueString = String.valueOf(System.currentTimeMillis());
                newCategory.setCategory_id(newCategory.getCategory_name() + uniqueString);
                db.collection("Kmetije").document(userID)
                        .collection("Kategorije").document(newCategory.getCategory_id())
                        .set(newCategory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        makeLogD(TAG, "(onCreateView) creating category successful!");
                        this.dismiss();
                    } else {
                        makeLogW(TAG, "(onCreateView) creating category ERROR; task is not successful!");
                    }
                });
            }
        });
        return rootView;
    } // onCreateView

    public void setNewCategoryListener(NewCategoryRefreshAdapterInterface listener) {
        this.mInterface = listener;
    } // setNewCategoryListener

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mInterface.refreshAdapterNewCategory();
    } // onDismiss
}
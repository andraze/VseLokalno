package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Category;

import static diplomska.naloga.vselokalno.MainActivity.appArticles;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.R;

public class EditCategoryFragment extends DialogFragment {

    public interface EditCategoryRefreshAdapterInterface {
        void refreshAdapterEditCategory(String categoryName);
    }

    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "EditCategoryFragment";
    //    Other variables
    Category mCategory;
    EditCategoryRefreshAdapterInterface mInterface1;
    EditCategoryRefreshAdapterInterface mInterface2;

    public EditCategoryFragment() {
        // Required empty public constructor
    }

    public EditCategoryFragment(Category category) {
        this.mCategory = category;
    }

    public static EditCategoryFragment newInstance(Category category) {
        return new EditCategoryFragment(category);
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
        // Article name:
        EditText categoryName = rootView.findViewById(R.id.newCategoryName_et);
        categoryName.setText(mCategory.getCategory_name());
        // Delete category:
        TextView deleteCategory = rootView.findViewById(R.id.delete_categoryBtn);
        deleteCategory.setVisibility(View.VISIBLE);
        deleteCategory.setOnClickListener(v -> {
            // Delete category
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setMessage("Ste prepričani, da želite izbrisati kategorijo:\n" + mCategory.getCategory_name())
//                    .setTitle("Izbris kategorije");
//            builder.setPositiveButton("Da", (dialog, id) -> {
                if (checkIfCanDeleteCategory()){
                    db.collection("Kmetije").document(userID)
                            .collection("Kategorije").document(mCategory.getCategory_id())
                            .delete().addOnCompleteListener(task -> {
                        makeLogD(TAG, "(onCreateView) deleting category successful!");
                        mInterface1.refreshAdapterEditCategory("");
                        mInterface2.refreshAdapterEditCategory("");
                        this.dismiss();
                    });
                } else {
                    Toast.makeText(requireContext(), "Kategorije ne morete izbrisati, dokler so v njej artikli.", Toast.LENGTH_LONG).show();
                }
//            });
//            builder.setNegativeButton("Ne", ((dialog, which) -> dialog.cancel()));
        });
        // Save button:
        AppCompatButton saveCategory = rootView.findViewById(R.id.add_categoryBtn);
        saveCategory.setOnClickListener(view -> {
            // Save category
            if (categoryName.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ime kategorije.", Toast.LENGTH_SHORT).show();
            else {
                mCategory.setCategory_name(categoryName.getText().toString().trim());
                db.collection("Kmetije").document(userID)
                        .collection("Kategorije").document(mCategory.getCategory_id())
                        .set(mCategory).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        makeLogD(TAG, "(onCreateView) editing category successful!");
                        mInterface1.refreshAdapterEditCategory(mCategory.getCategory_name());
                        mInterface2.refreshAdapterEditCategory(mCategory.getCategory_name());
                        this.dismiss();
                    } else {
                        makeLogW(TAG, "(onCreateView) editinf category ERROR; task is not successful!");
                    }
                });
            }
        });
        return rootView;
    } // onCreateView

    private boolean checkIfCanDeleteCategory() {
        for (Article el : appArticles) {
            if (el.getCategory_id().equals(mCategory.getCategory_id()))
                return false;
        }
        return true;
    }

    public void setEditCategoryListener(EditCategoryRefreshAdapterInterface listener1, EditCategoryRefreshAdapterInterface listener2) {
        this.mInterface1 = listener1;
        this.mInterface2 = listener2;
    } // setEditCategoryListener1
}
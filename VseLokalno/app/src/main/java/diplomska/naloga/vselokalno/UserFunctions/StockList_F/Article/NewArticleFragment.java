package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Article;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.appImageCropper;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.Category;
import diplomska.naloga.vselokalno.DataObjects.DecimalDitgitsInputFilter;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.R;

public class NewArticleFragment extends Fragment implements ImageCropper.ImageCroppedCallbackListener {

    public interface NewArticleRefreshAdapterInterface {
        void refreshAdapterNewArticle();
    }

    //    Views
    ImageView articleImage;
    //    Image request code
    private final static int PICK_IMAGE = 100;
    //    Image uri
    Uri imageURI;
    //    Firebase firestore DB
    FirebaseFirestore db;
    //    TAG
    String TAG = "NewArticleFragment";
    //    Other variables
    boolean photo_changed = false;
    Article newArticle;
    Category mCurrentCategory;
    NewArticleRefreshAdapterInterface mInterface;

    public NewArticleFragment() {
        // Required empty public constructor
    }

    public NewArticleFragment(Category currentCategory) {
        this.mCurrentCategory = currentCategory;
    }

    public static NewArticleFragment newInstance(Category currentCategory) {
        return new NewArticleFragment(currentCategory);
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
        View rootView = inflater.inflate(R.layout.fragment_edit_article, container, false);
        // Add article image:
        articleImage = rootView.findViewById(R.id.article_image);
        articleImage.setOnClickListener(view -> {
            // Open gallery:
            openGallery();
        });
        // Article name:
        EditText articleName = rootView.findViewById(R.id.article_name);
        // Article price:
        EditText articlePrice = rootView.findViewById(R.id.article_price);
        articlePrice.setFilters(new InputFilter[]{new DecimalDitgitsInputFilter(9, 2)});
        // Stock unit:
        TextView stockUnit = rootView.findViewById(R.id.article_unit2);
        // Article unit:
        EditText articleUnit = rootView.findViewById(R.id.article_unit);
        articleUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                stockUnit.setText(s);
            }
        });
        // Stock size:
        EditText stockQuantity = rootView.findViewById(R.id.article_storage);
        stockQuantity.setFilters(new InputFilter[]{new DecimalDitgitsInputFilter(9, 2)});
        // Save button:
        AppCompatButton saveArticle = rootView.findViewById(R.id.save_article);
        saveArticle.setOnClickListener(view -> {
            if (articleName.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ime artikla.", Toast.LENGTH_SHORT).show();
            else if (articlePrice.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ceno artikla.", Toast.LENGTH_SHORT).show();
            else if (articleUnit.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite enoto artikla.", Toast.LENGTH_SHORT).show();
            else if (stockQuantity.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Omejite zalogo artikla", Toast.LENGTH_SHORT).show();
            else { // All data is present
                newArticle = new Article();
                newArticle.setArticle_name(articleName.getText().toString());
                newArticle.setArticle_price(Double.parseDouble(articlePrice.getText().toString()));
                newArticle.setArticle_unit(articleUnit.getText().toString());
                newArticle.setArticle_storage(Double.parseDouble(stockQuantity.getText().toString()));
                newArticle.setCategory_id(mCurrentCategory.getCategory_id());
                newArticle.setFarm_id(userID);
                String newArticleID = newArticle.getArticle_name() + "#" + System.currentTimeMillis();
                newArticle.setArticle_id(newArticleID);
                newArticle.setPicture(photo_changed);
                newArticle.setArticle_name_keywords(createKeywords(newArticle.getArticle_name()));
                if (photo_changed) {
                    try {
                        // Save to cloud storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // Create a storage reference from the app
                        StorageReference storageRef = storage.getReference();
                        String path = "Article Images/" + newArticleID;
                        StorageReference imagesRef = storageRef.child(path);
                        Uri file = Uri.fromFile(new File(String.valueOf(imageURI)));
                        UploadTask uploadTask = imagesRef.putFile(file);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                            makeLogW(TAG, "(onCreateView) " + exception);
                            Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                        });
                        uploadTask.addOnSuccessListener(taskSnapshot -> makeLogD(TAG, "Image uploaded!"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                uploadChanges();
            }
        });
        // Cancel button:
        ImageButton cancelArticle = rootView.findViewById(R.id.cancel_btn);
        cancelArticle.setOnClickListener(view -> getParentFragmentManager().popBackStack());
        return rootView;
    } // onCreateView

    private ArrayList<String> createKeywords(String article_name) {
        article_name = article_name.toLowerCase();
        article_name = article_name.trim();
        ArrayList<String> array = new ArrayList<>();
        StringBuilder stringTemp = new StringBuilder();
        for (int i = 0; i < article_name.length(); i++){
            stringTemp.append(article_name.charAt(i));
            array.add(String.valueOf(stringTemp));
        }
        String[] articleNameParts = article_name.split(" ");
        for (String onePart : articleNameParts) {
            stringTemp = new StringBuilder();
            for (int i = 0; i < onePart.length(); i++){
                stringTemp.append(onePart.charAt(i));
                array.add(String.valueOf(stringTemp));
            }
        }
        return array;
    }

    private void uploadChanges() {
        db.collection("Kmetije").document(userID)
                .collection("Artikli").document(newArticle.getArticle_id())
                .set(newArticle).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                makeLogD(TAG, "(uploadChanges) creating article successful!");
                mInterface.refreshAdapterNewArticle();
                getParentFragmentManager().popBackStack();
            } else {
                makeLogW(TAG, "(uploadChanges) creating article ERROR; task is not successful!");
            }
        });
    } // uploadChanges

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    } // openGallery

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                imageURI = data.getData();
                makeLogD(TAG, "My uri" + imageURI.toString());
                appImageCropper.startCrop(imageURI, this);
            } else makeLogW(TAG, "(onActivityResult) data == null!");
        }
    } // onActivityResult

    public void setNewArticleListener(NewArticleRefreshAdapterInterface listener) {
        this.mInterface = listener;
    } // setNewArticleListener

    @Override
    public void onImageCroppedCallback(@NonNull String path) {
        imageURI = Uri.parse(new File(path).toString());
        articleImage.setImageURI(imageURI);
        articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photo_changed = true;
    } // onImageCroppedCallback

}
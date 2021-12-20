package diplomska.naloga.vselokalno.UserFunctions.StockList_F.Article;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.appImageCropper;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import diplomska.naloga.vselokalno.DataObjects.Article;
import diplomska.naloga.vselokalno.DataObjects.GlideApp;
import diplomska.naloga.vselokalno.ImageCrop.ImageCropper;
import diplomska.naloga.vselokalno.R;

public class EditArticleFragment extends Fragment implements ImageCropper.ImageCroppedCallbackListener {

    public interface EditArticleRefreshAdapterInterface {
        void refreshAdapterEditArticle();
    }

    //    Views
    ImageView articleImage;
    //    Image request code
    private final static int PICK_IMAGE = 200;
    //    Image uri
    Uri imageURI;
    //    Firebase firestore DB
    FirebaseFirestore db;
    //    Firebase storage
    FirebaseStorage mStorage;
    //    TAG
    String TAG = "EditArticleFragment";
    // Current article
    Article currentArticle;
    //    Other variables
    boolean photo_changed = false;
    EditArticleRefreshAdapterInterface mInterface;

    public EditArticleFragment() {
        // Required empty public constructor
    }

    public EditArticleFragment(Article article) {
        currentArticle = article;
    }

    public static EditArticleFragment newInstance(Article article) {
        return new EditArticleFragment(article);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
    } // onStart

    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_article, container, false);
        mStorage = FirebaseStorage.getInstance();
        // Add article image:
        articleImage = rootView.findViewById(R.id.article_image);
        StorageReference imageRef = mStorage.getReference()
                .child("Article Images/" + currentArticle.getArticle_id());
        if (currentArticle.isPicture()) {
            GlideApp.with(requireContext()).load(imageRef)
                    .into(articleImage);
            articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        articleImage.setOnClickListener(view -> {
            // Open gallery:
            openGallery();
        });
        // Article name:
        EditText articleName = rootView.findViewById(R.id.article_name);
        articleName.setText(currentArticle.getArticle_name());
        // Article price:
        EditText articlePrice = rootView.findViewById(R.id.article_price);
        articlePrice.setText(String.format("%.2f", currentArticle.getArticle_price()));
        // Stock unit:
        TextView stockUnit = rootView.findViewById(R.id.article_unit2);
        stockUnit.setText(currentArticle.getArticle_unit());
        // Article unit:
        EditText articleUnit = rootView.findViewById(R.id.article_unit);
        articleUnit.setText(currentArticle.getArticle_unit());
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
        stockQuantity.setText(String.format("%.2f", currentArticle.getArticle_storage()));
        if (currentArticle.getArticle_storage() < 0.1)
            stockQuantity.setTextColor(getResources().getColor(R.color.red_normal));
        else if (currentArticle.getArticle_storage() < 1)
            stockQuantity.setTextColor(getResources().getColor(R.color.yellow_normal));
        // Save button:
        AppCompatButton saveArticle = rootView.findViewById(R.id.save_article);
        saveArticle.setOnClickListener(view -> {
            // Check if all data present
            if (articleName.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ime artikla.", Toast.LENGTH_SHORT).show();
            else if (articlePrice.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ceno artikla.", Toast.LENGTH_SHORT).show();
            else if (articleUnit.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite enoto artikla.", Toast.LENGTH_SHORT).show();
            else if (stockQuantity.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Omejite zalogo artikla.", Toast.LENGTH_SHORT).show();
            else { // All data is present
                try { // Try to parse the numbers:
                    Double.parseDouble(articlePrice.getText().toString());
                    Double.parseDouble(stockQuantity.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Cena in zaloga morata biti številki.", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentArticle.setArticle_name(articleName.getText().toString());
                currentArticle.setArticle_price(Double.parseDouble(articlePrice.getText().toString()));
                currentArticle.setArticle_unit(articleUnit.getText().toString());
                currentArticle.setArticle_storage(Double.parseDouble(stockQuantity.getText().toString()));
                currentArticle.setArticle_name_keywords(createKeywords(currentArticle.getArticle_name()));
                if (photo_changed) {
                    currentArticle.setPicture(true);
                    try {
                        // Save to cloud storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // Create a storage reference from the app
                        StorageReference storageRef = storage.getReference();
                        String path = "Article Images/" + currentArticle.getArticle_id();
                        StorageReference imagesRef = storageRef.child(path);
                        Uri file = Uri.fromFile(new File(String.valueOf(imageURI)));
                        UploadTask uploadTask = imagesRef.putFile(file);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                            makeLogW(TAG, "(onCreateView) " + exception);
                            Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                        });
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

        // DeleteArticle:
        TextView deleteArticle = rootView.findViewById(R.id.delete_article);
        deleteArticle.setVisibility(View.VISIBLE);
        deleteArticle.setOnClickListener(v -> deleteMyArticle());
        return rootView;
    } // onCreateView

    private ArrayList<String> createKeywords(String article_name) {
        article_name = article_name.toLowerCase();
        article_name = article_name.trim();
        ArrayList<String> array = new ArrayList<>();
        StringBuilder stringTemp = new StringBuilder();
        for (int i = 0; i < article_name.length(); i++) {
            stringTemp.append(article_name.charAt(i));
            array.add(String.valueOf(stringTemp));
        }
        String[] articleNameParts = article_name.split(" ");
        for (String onePart : articleNameParts) {
            stringTemp = new StringBuilder();
            for (int i = 0; i < onePart.length(); i++) {
                stringTemp.append(onePart.charAt(i));
                array.add(String.valueOf(stringTemp));
            }
        }
        return array;
    } // createKeywords

    private void uploadChanges() {
        db.collection("Kmetije").document(userID)
                .collection("Artikli").document(currentArticle.getArticle_id())
                .set(currentArticle).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                makeLogD(TAG, "(uploadChanges) creating article successful!");
                mInterface.refreshAdapterEditArticle();
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
                appImageCropper.startCrop(imageURI, this);
            } else makeLogW(TAG, "(onActivityResult) data == null!");
        }
    } // onActivityResult

    public void setEditArticleListener(EditArticleRefreshAdapterInterface listener) {
        this.mInterface = listener;
    } // setEditArticleListener

    void deleteMyArticle() {
        // TODO: Delete article image as well (in 30 days).
        db.collection("Kmetije").document(userID)
                .collection("Artikli").document(currentArticle.getArticle_id()).delete()
                .addOnCompleteListener(task -> {
                    makeLogD(TAG, "(deleteMyArticle) deleted article: " + currentArticle.getArticle_id());
                    mInterface.refreshAdapterEditArticle();
                });
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onImageCroppedCallback(@NonNull String path) {
        imageURI = Uri.parse(new File(path).toString());
        articleImage.setImageURI(imageURI);
        articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photo_changed = true;
    } // onImageCroppedCallback
}
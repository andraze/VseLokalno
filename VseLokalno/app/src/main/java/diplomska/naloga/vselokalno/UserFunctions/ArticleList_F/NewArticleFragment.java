package diplomska.naloga.vselokalno.UserFunctions.ArticleList_F;

import static android.app.Activity.RESULT_OK;
import static diplomska.naloga.vselokalno.MainActivity.appFarm;
import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;
import static diplomska.naloga.vselokalno.MainActivity.userID;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import diplomska.naloga.vselokalno.R;

public class NewArticleFragment extends DialogFragment {

    public interface NewArticleRefreshAdapterInterface {
        void refreshAdapterNewArticle();
    }

    //    Views
    ImageButton articleImage;
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
    Map<String, String> newArticle;
    String zaloga = "";
    NewArticleRefreshAdapterInterface mInterface;

    public NewArticleFragment() {
        // Required empty public constructor
    }

    public static NewArticleFragment newInstance() {
        return new NewArticleFragment();
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
        newArticle = new HashMap<>();
        // Add article image:
        articleImage = rootView.findViewById(R.id.article_image_editArticleFragment);
        articleImage.setOnClickListener(view -> {
            // Open gallery:
            openGallery();
        });
        // Article name:
        EditText articleName = rootView.findViewById(R.id.ime_artikel_et_editArticleFragment);
        // Article price:
        EditText articlePrice = rootView.findViewById(R.id.cena_artikel_et_editArticleFragment);
        // Article unit:
        EditText articleUnit = rootView.findViewById(R.id.enota_artikel_et_editArticleFragment);
        // Stock unit:
        EditText stockUnit = rootView.findViewById(R.id.enota_zaloge);
        // Stock size:
        EditText stockQuantity = rootView.findViewById(R.id.kolicina_zaloge);
        // Relative Layouts:
        RelativeLayout layout1 = rootView.findViewById(R.id.rel_layout1_editArticleFragment);
        RelativeLayout layout2 = rootView.findViewById(R.id.rel_layout2_editArticleFragment);
        // Save button:
        FloatingActionButton saveArticle = rootView.findViewById(R.id.save_artikel_fab_editArticleFragment);
        saveArticle.setOnClickListener(view -> {
            if (articleName.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ime artikla.", Toast.LENGTH_SHORT).show();
            else if (articlePrice.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite ceno artikla.", Toast.LENGTH_SHORT).show();
            else if (articleUnit.getText().toString().isEmpty())
                Toast.makeText(requireContext(), "Vpišite enoto artikla.", Toast.LENGTH_SHORT).show();
            else { // All data is present
                if (photo_changed) {
                    try {
                        long uniqueString = System.currentTimeMillis();
                        // Save to cloud storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // Create a storage reference from the app
                        StorageReference storageRef = storage.getReference();
                        String path = "Slike artiklov/" + userID + "/" + articleName.getText().toString() + "_" + uniqueString;
                        StorageReference imagesRef = storageRef.child(path);
                        UploadTask uploadTask = imagesRef.putFile(imageURI);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                            makeLogW(TAG, "(onCreateView) " + exception);
                            Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(taskSnapshot -> {
                            newArticle.put("id_artikel", articleName.getText().toString() + "_" + uniqueString);
                            newArticle.put("slika_artikel", path);
                            newArticle.put("ime_artikel", articleName.getText().toString());
                            newArticle.put("cena_artikel", articlePrice.getText().toString());
                            newArticle.put("enota_artikel", articleUnit.getText().toString());
                            zaloga = zaloga.trim();
                            if (zaloga.isEmpty())
                                newArticle.put("zaloga_artikel", "-1");
                            else
                                newArticle.put("zaloga_artikel", zaloga);
                            appFarm.addArtikel(newArticle);
                            db.collection("Kmetije").document(userID).set(appFarm).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    makeLogD(TAG, "(onCreateView) creating article successful!");
                                    this.dismiss();
                                } else {
                                    makeLogW(TAG, "(onCreateView) creating article ERROR; task is not successful!");
                                }
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String path = "Slike artiklov/default_article_image.png";
                    newArticle.put("slika_artikel", path);
                    long uniqueString = System.currentTimeMillis();
                    newArticle.put("id_artikel", articleName.getText().toString() + "_" + uniqueString);
                    newArticle.put("ime_artikel", articleName.getText().toString());
                    newArticle.put("cena_artikel", articlePrice.getText().toString());
                    newArticle.put("enota_artikel", articleUnit.getText().toString());
                    zaloga = zaloga.trim();
                    if (zaloga.isEmpty())
                        newArticle.put("zaloga_artikel", "-1");
                    else
                        newArticle.put("zaloga_artikel", zaloga);
                    appFarm.addArtikel(newArticle);
                    db.collection("Kmetije").document(userID).set(appFarm).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            makeLogD(TAG, "(onCreateView) creating article successful!");
                            this.dismiss();
                        } else {
                            makeLogW(TAG, "(onCreateView) creating article ERROR; task is not successful!");
                        }
                    });
                }
            }
        });
        // Cancel button:
        FloatingActionButton cancelArticle = rootView.findViewById(R.id.cancel_artikel_fab_editArticleFragment);
        cancelArticle.setOnClickListener(view -> this.dismiss());
        // Zaloga:
        Button omejiZalogoBtn = rootView.findViewById(R.id.omeji_zalogo);
        omejiZalogoBtn.setOnClickListener(view -> {
            makeSwitch(layout1, layout2);
            stockUnit.setText(articleUnit.getText().toString());
        });
        FloatingActionButton saveStock = rootView.findViewById(R.id.save_zaloge_fab_editArticleFragment);
        saveStock.setOnClickListener(view -> {
            if (Double.parseDouble(stockQuantity.getText().toString()) <= 0) {
                Toast.makeText(requireContext(), "Zaloga mora biti večja od 0!", Toast.LENGTH_SHORT).show();
            } else {
                zaloga = stockQuantity.getText().toString();
                articleUnit.setText(stockUnit.getText().toString());
                makeSwitch(layout2, layout1);
            }
        });
        FloatingActionButton cancelStock = rootView.findViewById(R.id.cancel_zaloge_fab_editArticleFragment);
        cancelStock.setOnClickListener(view -> makeSwitch(layout2, layout1));
        Button unlimitedSupply = rootView.findViewById(R.id.neomejeno_zaloge);
        unlimitedSupply.setOnClickListener(view -> {
            zaloga = "";
            articleUnit.setText(stockUnit.getText().toString());
            stockQuantity.setText("");
            makeSwitch(layout2, layout1);
        });
        return rootView;
    } // onCreateView

    private void makeSwitch(View view1, View view2) {
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.VISIBLE);
    } // makeSwitch

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
                articleImage.setImageURI(imageURI);
                photo_changed = true;
            } else makeLogW(TAG, "(onActivityResult) data == null!");
        }
    } // onActivityResult

    public void setNewArticleListener(NewArticleRefreshAdapterInterface listener) {
        this.mInterface = listener;
    } // setNewArticleListener

    public NewArticleRefreshAdapterInterface getNewArticleListener() {
        return this.mInterface;
    } // getNewArticleListener

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mInterface.refreshAdapterNewArticle();
    } // onDismiss
}
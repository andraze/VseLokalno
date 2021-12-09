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
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import diplomska.naloga.vselokalno.FarmLookup.List.GlideApp;
import diplomska.naloga.vselokalno.R;

public class EditArticleFragment extends DialogFragment {

    public interface EditArticleRefreshAdapterInterface {
        void refreshAdapterEditArticle(int position);
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ITEM_NUMBER = "Article_INDEX";
    // Parameter:
    private int articleIndexParam;
    //    Views
    ImageButton articleImage;
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
    //    Other variables
    boolean photo_changed = false;
    Map<String, String> mArticle;
    String zaloga = "";
    EditArticleRefreshAdapterInterface mInterface;

    public EditArticleFragment() {
        // Required empty public constructor
    }

    public static EditArticleFragment newInstance(int param1) {
        EditArticleFragment fragment = new EditArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_article, container, false);
        if (getArguments() != null) {
            articleIndexParam = getArguments().getInt(ARG_ITEM_NUMBER);
            mStorage = FirebaseStorage.getInstance();
        }
        mArticle = appFarm.getArtikli().get(articleIndexParam);
        // Add article image:
        articleImage = rootView.findViewById(R.id.article_image_editArticleFragment);
        StorageReference imageRef = mStorage.getReference()
                .child(Objects.requireNonNull(mArticle.get("slika_artikel")));
        GlideApp.with(requireContext()).load(imageRef).into(articleImage);
        articleImage.setOnClickListener(view -> {
            // Open gallery:
            openGallery();
        });
        // Article name:
        EditText articleName = rootView.findViewById(R.id.ime_artikel_et_editArticleFragment);
        articleName.setText(mArticle.get("ime_artikel"));
        // Article price:
        EditText articlePrice = rootView.findViewById(R.id.cena_artikel_et_editArticleFragment);
        articlePrice.setText(mArticle.get("cena_artikel"));
        // Article unit:
        EditText articleUnit = rootView.findViewById(R.id.enota_artikel_et_editArticleFragment);
        articleUnit.setText(mArticle.get("enota_artikel"));
        // Stock unit:
        EditText stockUnit = rootView.findViewById(R.id.enota_zaloge);
        // Stock size:
        EditText stockQuantity = rootView.findViewById(R.id.kolicina_zaloge);
        stockQuantity.setText(mArticle.get("zaloga_artikel"));
        // Relative Layouts:
        RelativeLayout layout1 = rootView.findViewById(R.id.rel_layout1_editArticleFragment);
        RelativeLayout layout2 = rootView.findViewById(R.id.rel_layout2_editArticleFragment);
        // Save button:
        FloatingActionButton saveArticle = rootView.findViewById(R.id.save_artikel_fab_editArticleFragment);
        saveArticle.setOnClickListener(view -> {
            // Check if all data present
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
                        String path = "Slike artiklov/" + userID + "/" + mArticle.get("id_artikel");
                        StorageReference imagesRefDelete = storageRef.child(Objects.requireNonNull(mArticle.get("slika_artikel")));
                        imagesRefDelete.delete().addOnFailureListener(e -> makeLogW(TAG, "(onCreateView)\n" + e.getMessage()));
                        StorageReference imagesRef = storageRef.child(Objects.requireNonNull(path));
                        UploadTask uploadTask = imagesRef.putFile(imageURI);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(exception -> {
                            // Handle unsuccessful uploads
                            makeLogW(TAG, "(onCreateView) " + exception);
                            Toast.makeText(requireContext(), "Prišlo je do napake! Poskusite ponovno.", Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(taskSnapshot -> {
                            mArticle.put("slika_artikel", path);
                            mArticle.put("ime_artikel", articleName.getText().toString());
                            mArticle.put("cena_artikel", articlePrice.getText().toString());
                            mArticle.put("enota_artikel", articleUnit.getText().toString());
                            zaloga = zaloga.trim();
                            if (zaloga.isEmpty())
                                mArticle.put("zaloga_artikel", "-1");
                            else
                                mArticle.put("zaloga_artikel", zaloga);
                            ArrayList<Map<String, String>> tempArrayList = appFarm.getArtikli();
                            tempArrayList.set(articleIndexParam, mArticle);
                            appFarm.setArtikli(tempArrayList);
                            db.collection("Kmetije").document(userID).set(appFarm).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    makeLogD(TAG, "(onCreateView) updating article successful!");
                                    this.dismiss();
                                } else {
                                    makeLogW(TAG, "(onCreateView) updating article ERROR; task is not successful!");
                                }
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mArticle.put("ime_artikel", articleName.getText().toString().trim());
                    mArticle.put("cena_artikel", articlePrice.getText().toString().trim());
                    mArticle.put("enota_artikel", articleUnit.getText().toString().trim());
                    zaloga = zaloga.trim();
                    if (zaloga.isEmpty())
                        mArticle.put("zaloga_artikel", "-1");
                    else
                        mArticle.put("zaloga_artikel", zaloga);
                    ArrayList<Map<String, String>> tempArrayList = appFarm.getArtikli();
                    tempArrayList.set(articleIndexParam, mArticle);
                    appFarm.setArtikli(tempArrayList);
                    db.collection("Kmetije").document(userID).set(appFarm).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            makeLogD(TAG, "(onCreateView) updating article successful!");
                            this.dismiss();
                        } else {
                            makeLogW(TAG, "(onCreateView) updating article ERROR; task is not successful!");
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

    public void setEditArticleListener(EditArticleRefreshAdapterInterface listener) {
        this.mInterface = listener;
    } // setEditArticleListener

    public EditArticleRefreshAdapterInterface getEditArticleListener() {
        return this.mInterface;
    } // getEditArticleListener

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mInterface.refreshAdapterEditArticle(articleIndexParam);
    } // onDismiss
}
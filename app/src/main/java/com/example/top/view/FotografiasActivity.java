package com.example.top.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.top.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FotografiasActivity extends AppCompatActivity {

    private static final int RC_GALERIA = 21;
    private static final int RC_CAMARA = 22;

    private static final int RP_CAMARA = 121;
    private static final int RP_ALMACENAMIENTO = 122;

    private static final String FOLDER_IMAGEN = "/FotografiaApp";
    private static final String MI_FOTO = "mi_foto";

    private static final String PATH_PERFIL = "perfil";
    private static final String PATH_FOTO_URL = "fotoURL";

    @BindView(R.id.btnSubir)
    Button btnSubir;
    @BindView(R.id.imgFoto)
    AppCompatImageView imgFoto;
    @BindView(R.id.btnBorrar)
    ImageButton btnBorrar;
    @BindView(R.id.container)
    ConstraintLayout container;
    private TextView mTextMessage;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String currentPhotoPath;
    private Uri photoSelectedUri;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_gallery:
                    mTextMessage.setText(R.string.main_label_gallery);
                    //fromGallery();
                    checkPermissionToApp(Manifest.permission.READ_EXTERNAL_STORAGE, RP_ALMACENAMIENTO);
                    return true;
                case R.id.navigation_camera:
                    mTextMessage.setText(R.string.main_label_camera);
                    //fromCamera();
                    //dispatchTakePictureIntent();
                    checkPermissionToApp(Manifest.permission.CAMERA, RP_CAMARA);
                    return true;
            }
            return false;
        }
    };

    private void checkPermissionToApp(String permissionStr, int requestPermission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permissionStr) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{permissionStr}, requestPermission);
                return;
            }
        }

        switch (requestPermission){
            case RP_ALMACENAMIENTO:
                fromGallery();
                break;
            case RP_CAMARA:
                dispatchTakePictureIntent();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            switch (requestCode){
                case RP_ALMACENAMIENTO:
                    fromGallery();
                    break;
                case RP_CAMARA:
                    dispatchTakePictureIntent();
                    break;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotografias);
        ButterKnife.bind(this);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        configFirebase();
        //cargarImagen();
        configFotoPerfil();
    }

    private void configFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child(PATH_PERFIL).child(PATH_FOTO_URL);
    }

    private void cargarImagen() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);

                if (!url.isEmpty()) {
                    mostrarImagen(Uri.parse(url));
//                    btnBorrar.setVisibility(View.VISIBLE);
//                    photoSelectedUri = Uri.parse(url);
//
//                    final RequestOptions requestOptions = new RequestOptions()
//                            .centerCrop();
//                    //.diskCacheStrategy(DiskCacheStrategy.ALL);
//
//                    Glide.with(FotografiasActivity.this)
//                            .load(url)
//                            .apply(requestOptions)
//                            .into(imgFoto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void mostrarImagen(Uri url){
        photoSelectedUri = url;
        btnBorrar.setVisibility(View.VISIBLE);

        final RequestOptions requestOptions = new RequestOptions()
                .centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(FotografiasActivity.this)
                .load(url)
                .apply(requestOptions)
                .into(imgFoto);
    }

    private void configFotoPerfil() {
        storageReference.child(PATH_PERFIL).child(MI_FOTO).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mostrarImagen(uri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(container, R.string.fotografia_message_error_notFound, Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_GALERIA);
    }

    private void fromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RC_CAMARA);
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File fotoFile = createImageFile();

            if (fotoFile != null){
                Uri fotoUri = FileProvider.getUriForFile(this,
                        "com.example.top", fotoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(takePictureIntent, RC_CAMARA);
            }
        }
    }

    private File createImageFile() {
        final String tiemStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss", Locale.ROOT).format(new Date());
        final String imageFileName = MI_FOTO + tiemStamp + "_";

        File stroraDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", stroraDir);
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_GALERIA:
                    if (data != null) {
                        photoSelectedUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoSelectedUri);
                            imgFoto.setImageBitmap(bitmap);
                            btnBorrar.setVisibility(View.GONE);
                            mTextMessage.setText(R.string.fotografia_message_question_upload);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case RC_CAMARA:
                    photoSelectedUri = addPicGallery();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoSelectedUri);
                        imgFoto.setImageBitmap(bitmap);
                        btnBorrar.setVisibility(View.GONE);
                        mTextMessage.setText(R.string.fotografia_message_question_upload);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                //Imagen Miniatura
//                case RC_CAMARA:
//                    Bundle extras = data.getExtras();
//                    Bitmap bitmap = (Bitmap)extras.get("data");
//                    imgFoto.setImageBitmap(bitmap);
//                    btnBorrar.setVisibility(View.GONE);
//                    break;
            }
        }
    }

    private Uri addPicGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        currentPhotoPath = null;

        return contentUri;
    }

    @OnClick(R.id.btnSubir)
    public void onViewClicked() {
        StorageReference perfilReference = storageReference.child(PATH_PERFIL);
        StorageReference fotoReference = perfilReference.child(MI_FOTO);

        fotoReference.putFile(photoSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(container, R.string.fotografia_message_upload_success, Snackbar.LENGTH_LONG).show();

                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //String imageUrl = uri.toString();
                                //createNewPost(imageUrl);
                                guardarFotoUrl(uri.toString());
                                btnBorrar.setVisibility(View.VISIBLE);
                                mTextMessage.setText(R.string.fotografia_message_done);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(container, R.string.fotografia_message_upload_error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }

//                //Uri uriDescarga = Uri.parse(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                String uriDescarga = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                guardarFotoUrl(uriDescarga);
//                btnBorrar.setVisibility(View.VISIBLE);
//                mTextMessage.setText(R.string.fotografia_message_done);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(container, R.string.fotografia_message_upload_error, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void guardarFotoUrl(String uriDescarga) {
        databaseReference.setValue(uriDescarga);
    }

    @OnClick(R.id.btnBorrar)
    public void borrarFoto() {
        storageReference.child(PATH_PERFIL).child(MI_FOTO).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.removeValue();
                imgFoto.setImageBitmap(null);
                btnBorrar.setVisibility(View.GONE);
                Snackbar.make(container, R.string.fotografia_message_delete_success, Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(container, R.string.fotografia_message_delete_error, Snackbar.LENGTH_LONG).show();
            }
        });

    }
}

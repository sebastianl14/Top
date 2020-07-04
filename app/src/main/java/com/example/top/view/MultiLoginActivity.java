package com.example.top.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.top.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MultiLoginActivity extends AppCompatActivity {


    @BindView(R.id.imgFotoPerfil)
    CircleImageView imgFotoPerfil;
    @BindView(R.id.tvNombreUsuario)
    TextView tvNombreUsuario;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvProvider)
    TextView tvProvider;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvProgress)
    TextView tvProgress;

    private static final int RC_SIGN_IN = 123;
    private static final int RC_GALERIA = 124;

    private static final String MI_FOTO_AUTE = "mi_foto_aute";
    private static final String PATH_PERFIL = "perfil";

    private static final String PASSWORD_FIREBASE = "firebase";
    private static final String FACEBOOK = "facebook.com";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //onSetDataUser(user.getDisplayName(), user.getEmail(), user.getProviderId());
                    onSetDataUser(user);
                } else {
                    onSignedOutCleanup();

                    AuthUI.IdpConfig facebookIdpConfig = new AuthUI.IdpConfig.FacebookBuilder()
                            .setPermissions(Arrays.asList("user_friends", "user_gender"))
                            .build();

                    AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                            //.setScopes(Arrays.asList(Scopes.GAMES))
                            .build();

                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setTosAndPrivacyPolicyUrls(
                                    "https://example.com/terms.html",
                                    "https://example.com/privacy.html")
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                    facebookIdpConfig, googleIdp))
                            .setTheme(R.style.GreenTheme)
                            .setLogo(R.drawable.img_multi_login)
                            .build(), RC_SIGN_IN);
                }
            }
        };

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.top",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void onSignedOutCleanup() {
        //onSetDataUser("", "", "");
        onSetDataUser(null);
    }

    private void onSetDataUser(String userName, String email, String provider) {
        tvNombreUsuario.setText(userName);
        tvEmail.setText(email);
        int drawableRes;

        switch (provider) {
            case EmailAuthProvider.PROVIDER_ID:
                drawableRes = R.drawable.ic_firebase;
                break;
            case FacebookAuthProvider.PROVIDER_ID:
                drawableRes = R.drawable.ic_facebook;
                break;
            default:
                drawableRes = R.drawable.ic_block_helper;
                provider = "Proveedor Desconocido";
                break;
        }

        tvProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0, 0, 0);
        tvProvider.setText(provider);
    }

    private void onSetDataUser(FirebaseUser usuario) {

        if (usuario != null) {
            tvNombreUsuario.setText(usuario.getDisplayName());
            tvEmail.setText(usuario.getEmail());
            int drawableRes;

            String provider = "";
            List<? extends UserInfo> infos = usuario.getProviderData();
            for (UserInfo ui : infos) {
                switch (ui.getProviderId()) {
                    case GoogleAuthProvider.PROVIDER_ID:
                        provider = GoogleAuthProvider.PROVIDER_ID;
                        break;
                    case FacebookAuthProvider.PROVIDER_ID:
                        provider = FacebookAuthProvider.PROVIDER_ID;
                        break;
                    case EmailAuthProvider.PROVIDER_ID:
                        provider = EmailAuthProvider.PROVIDER_ID;
                        break;
                    case PhoneAuthProvider.PROVIDER_ID:
                        provider = EmailAuthProvider.PROVIDER_ID;
                        break;
                }
            }

            switch (provider) {
                case EmailAuthProvider.PROVIDER_ID:
                    drawableRes = R.drawable.ic_firebase;
                    break;
                case FacebookAuthProvider.PROVIDER_ID:
                    drawableRes = R.drawable.ic_facebook;
                    break;
                case GoogleAuthProvider.PROVIDER_ID:
                    drawableRes = R.drawable.ic_google;
                    break;
                default:
                    drawableRes = R.drawable.ic_block_helper;
                    provider = "Proveedor Desconocido";
                    break;
            }

            tvProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0, 0, 0);
            tvProvider.setText(provider);
            cargarImagen(usuario.getPhotoUrl());

        } else {
            tvNombreUsuario.setText("");
            tvEmail.setText("");
            tvProvider.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Intente de nuevo", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_GALERIA && resultCode == RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);

            if (true) {
                subirImagenTarea(data.getData());
            } else {
                subirImagenStorage(data.getData());
            }
        }
    }

    private void subirImagenTarea(Uri selectedImageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child(PATH_PERFIL).child(MI_FOTO_AUTE);

        Bitmap bitmap;
        try {

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            bitmap = getResizedBitmap(bitmap, 1024);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = reference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();

                                user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cargarImagen(user.getPhotoUrl());
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MultiLoginActivity.this, "Se presento un error al actualizar la imagen",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    tvProgress.setText("Se cargo la imagen");
                    tvProgress.animate().alpha(0).setDuration(2000);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) /
                            taskSnapshot.getTotalByteCount();

                    progressBar.setProgress((int)progress);
                    tvProgress.setText(String.format("%s%%", progress));
                    tvProgress.animate().alpha(1).setDuration(200);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize){
            return bitmap;
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1){
            width = maxSize;
            height = (int)(width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int)(height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void subirImagenStorage(Uri selectedImageUri){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child(PATH_PERFIL).child(MI_FOTO_AUTE);

        //Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();

                                user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cargarImagen(user.getPhotoUrl());
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MultiLoginActivity.this, "Se presento un error al actualizar la imagen",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    tvProgress.setText("Se cargo la imagen");
                    tvProgress.animate().alpha(0).setDuration(2000);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) /
                            taskSnapshot.getTotalByteCount();

                    progressBar.setProgress((int)progress);
                    tvProgress.setText(String.format("%s%%", progress));
                    tvProgress.animate().alpha(1).setDuration(200);
                }
            });
        }
    }

    private void cargarImagen(Uri photoUrl) {
        final RequestOptions requestOptions = new RequestOptions()
                .centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(MultiLoginActivity.this)
                .load(photoUrl)
                .placeholder(R.mipmap.ic_launcher)
                .apply(requestOptions)
                .into(imgFotoPerfil);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cerrar_sesion:
                AuthUI.getInstance().signOut(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.imgFotoPerfil)
    public void seleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_GALERIA);
    }
}

package com.example.top;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddArtistActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.imgFoto)
    AppCompatImageView imgFoto;
    @BindView(R.id.etNombre)
    TextInputEditText etNombre;
    @BindView(R.id.etApellidos)
    TextInputEditText etApellidos;
    @BindView(R.id.etFechaNacimiento)
    TextInputEditText etFechaNacimiento;
    @BindView(R.id.etEstatura)
    TextInputEditText etEstatura;
    @BindView(R.id.etLugarNacimiento)
    TextInputEditText etLugarNacimiento;
    @BindView(R.id.etNotas)
    TextInputEditText etNotas;
    @BindView(R.id.spGenero)
    AppCompatSpinner spGenero;

    private Artista artista;
    private Calendar calendar;

    private final int RC_PHOTO_PICKER = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        configActionBar();
        configArtista(intent);
        configCalendar();
        //configSpinner();

    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configArtista(Intent intent) {
        artista = new Artista();
        artista.setFechaNacimiento(System.currentTimeMillis());
        artista.setOrden(intent.getIntExtra(Artista.ORDEN, 0));
    }

    private void configCalendar() {
        calendar = Calendar.getInstance();
        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()));
//        etFechaNacimiento.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
    }

    private void configSpinner() {

//        ArrayList<String> options = new ArrayList<>();
//        options.add("");
//        options.add("Masculina");
//        options.add("Femenino");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_genero));
        spGenero.setAdapter(adapter);

//        spGenero.setFocusable(true);
//        spGenero.setFocusableInTouchMode(true);
        //spGenero.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                saveArtist();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveArtist() {
        if (validateFields()) {
            artista.setNombre(etNombre.getText().toString().trim());
            artista.setApellidos(etApellidos.getText().toString().trim());
            artista.setEstatura(Short.parseShort(etEstatura.getText().toString().trim()));
            artista.setLugarNacimiento(etLugarNacimiento.getText().toString().trim());
            artista.setNotas(etNotas.getText().toString().trim());

            MainActivity.sArtista = artista;
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (etEstatura.getText().toString().trim().isEmpty() ||
                Integer.valueOf(etEstatura.getText().toString().trim()) < getResources().getInteger(R.integer.estatura_min)) {
            etEstatura.setError(getString(R.string.addArtist_error_estaturaMinima));
            etEstatura.requestFocus();

            isValid = false;
        }

        if (etApellidos.getText().toString().trim().isEmpty()) {
            etApellidos.setError(getString(R.string.addArtist_error_required));
            etApellidos.requestFocus();
            isValid = false;
        }

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError(getString(R.string.addArtist_error_required));
            etNombre.requestFocus();

            isValid = false;
        }

        if (spGenero.getSelectedItem().equals("")){
            TextView errorText = (TextView) spGenero.getSelectedView();
            errorText.setError(getString(R.string.addArtist_error_required));
//            errorText.setBackgroundColor(Color.BLACK);
//            errorText.setTextColor(Color.WHITE);//just to highlight that this is an error
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.addArtist_error_required));

            spGenero.setFocusable(true);
            spGenero.setFocusableInTouchMode(true);
            spGenero.requestFocus();

            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @OnClick(R.id.etFechaNacimiento)
    public void onSetFecha() {
        DialogSelectorFecha selectorFecha = new DialogSelectorFecha();
        selectorFecha.setListener(this);

        Bundle args = new Bundle();
        args.putLong(DialogSelectorFecha.FECHA, artista.getFechaNacimiento());
        selectorFecha.setArguments(args);
        selectorFecha.show(getSupportFragmentManager(), DialogSelectorFecha.SELECTED_DATE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        etFechaNacimiento.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTimeInMillis()));
        artista.setFechaNacimiento(calendar.getTimeInMillis());
    }

    @OnClick({R.id.imgDeleteFoto, R.id.imgFromGallery, R.id.imgFromUrl})
    public void imagesEvents(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.imgDeleteFoto:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, RC_PHOTO_PICKER);

                break;
            case R.id.imgFromGallery:
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                //intent.setType("image/jpeg");
//                //intent.setType("image/png");
//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator + "Sirius" + File.separator);
//                intent.setDataAndType(uri,"image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//
//                startActivityForResult(intent, RC_PHOTO_PICKER);

                intent = new Intent(Intent.ACTION_PICK);
                //intent.setType("image/jpeg");
                //intent.setData(Uri.fromFile(new File("/storage/emulated/0/Sirius/LogosEmpresa", "logocens_4p.jpg")));
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator + "Sirius" + File.separator + "LogosEmpresa");
                intent.setDataAndType(uri, "image/jpeg");
                startActivityForResult(intent, RC_PHOTO_PICKER);
                break;
            case R.id.imgFromUrl:
                showAddPhotoDialog();
                break;
        }
    }

    private void showAddPhotoDialog() {
        final EditText etFotoUrl = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.addArtist_dialogUrl_title)
                .setPositiveButton(R.string.label_dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configImageView(etFotoUrl.getText().toString().trim());
                    }
                })
                .setNegativeButton(R.string.label_dialog_cancel, null);

        builder.setView(etFotoUrl);
        builder.show();
    }

    private void configImageView(String fotoUrl) {
        if (fotoUrl != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop();

            Glide.with(this)
                    .load(fotoUrl)
                    .apply(requestOptions)
                    .into(imgFoto);
        } else {
            imgFoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_photo_size_select_actual));
        }

        artista.setFotoURL(fotoUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_PHOTO_PICKER:
                    artista.setFotoURL(data.getDataString());
                    configImageView(data.getDataString());
            }
        }
    }
}

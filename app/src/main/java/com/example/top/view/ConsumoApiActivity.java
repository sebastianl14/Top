package com.example.top.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.top.R;
import com.example.top.services.UsuarioApiResponse;
import com.example.top.services.UsuarioService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsumoApiActivity extends AppCompatActivity {

    @BindView(R.id.etIdUsuario)
    EditText etIdUsuario;
    @BindView(R.id.tvResultado)
    TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo_api);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnConsumirApi)
    public void onViewClicked() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        // Calling '/api/users/2'
        Long idUsuario = Long.parseLong(etIdUsuario.getText().toString());
        Call<UsuarioApiResponse> callSync = service.obtenerUsuario(idUsuario);
        tvResultado.setText("");

//        try {
//            Response<UsuarioApiResponse> response = callSync.execute();
//            UsuarioApiResponse apiResponse = response.body();
//            System.out.println(apiResponse);
//            tvResultado.setText(apiResponse.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        callSync.enqueue(new Callback<UsuarioApiResponse>() {
            @Override
            public void onResponse(Call<UsuarioApiResponse> call, Response<UsuarioApiResponse> response) {
                UsuarioApiResponse usuarioApiResponse = response.body();
                if (usuarioApiResponse != null){
                    tvResultado.setText(usuarioApiResponse.toString());
                }
            }
            @Override
            public void onFailure(Call<UsuarioApiResponse> call, Throwable t) {
                Toast.makeText(ConsumoApiActivity.this, "Error en al Consumir el Api", Toast.LENGTH_LONG).show();
            }
        });
    }
}

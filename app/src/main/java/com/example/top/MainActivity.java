package com.example.top;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dbflow5.config.FlowManager;
import com.dbflow5.database.DatabaseWrapper;
import com.dbflow5.query.SQLite;
import com.example.top.View.NotificacionLocalActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.containerMain)
    CoordinatorLayout containerMain;

    private ArtistaAdapter artistaAdapter;

    public static Artista sArtista = new Artista();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configToolBar();
        configAdapter();
        configRecyclerView();

        generateArtist();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void generateArtist() {
        String[] nombres = {"Rachel", "Cameron"};
        String[] apellidos = {"McAdams", "Diaz"};
        long[] nacimientos = {280108800000L, 280108800000L};
        String[] lugares = {"Canada", "USA"};
        short[] estaturas = {163, 174};
        String[] notas = {"Rachel Anne McAdams was born on November 17, 1978 in London, Ontario, Canada, to Sandra Kay (Gale), a nurse, and Lance Frederick McAdams, a truck driver and furniture mover. She is of English, Welsh, Irish, and Scottish descent. Rachel became involved with acting as a teenager and by the age of 13 was performing in Shakespearean productions in summer theater camp; she went on to graduate with honors with a BFA degree in Theater from York University. After her debut in an episode of Disney's The Famous Jett Jackson (1998), she co-starred in the Canadian TV series Slings and Arrows (2003), a comedy-drama about the trials and travails of a Shakespearean theater group, and won a Gemini award for her performance in 2003.",
                "A tall, strikingly attractive blue-eyed natural blonde, Cameron Diaz was born in 1972 in San Diego, the daughter of a Cuban-American father and a German mother. Self described as \"adventurous, independent and a tough kid,\" Cameron left home at 16 and for the next 5 years lived in such varied locales as Japan, Australia, Mexico, Morocco, and Paris. Returning to California at the age of 21, she was working as a model when she auditioned for a big part in The Mask (1994). To her amazement and despite having no previous acting experience, she was cast as the female lead in the film opposite Jim Carrey. Over the next 3 years, she honed her acting skills in such low budget independent films as The Last Supper (1995); Feeling Minnesota (1996); and Head Above Water (1996). She returned to main stream films in My Best Friend's Wedding (1997), in which she held her own against veteran actress Julia Roberts. She earned full fledged star status in 1998 for her performance in the box office smash There's Something About Mary (1998). With her name near the top on virtually every list of Hollyood's sexiest actresses, and firmly established as one of filmdom's hottest properties and most sought after actresses, Cameron Diaz appears to possess everything necessary to become one of the super stars of the new century."};
        String[] fotos = {"https://upload.wikimedia.org/wikipedia/commons/3/3e/Rachel_McAdams%2C_2016_%28cropped%29.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/3/36/Cameron_Diaz_WE_2012_Shankbone_2.JPG"};

        for (int i = 0; i < 2; i++) {
            Artista artista = new Artista(i + 1, nombres[i], apellidos[i], nacimientos[i], lugares[i],
                    estaturas[i], notas[i], i + 1, fotos[i]);
            //artistaAdapter.add(artista);
            artista.save(Artista.getWritableDatabase());
        }
    }


    private void configToolBar() {
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        artistaAdapter = new ArtistaAdapter(new ArrayList<Artista>(), this);
    }

    private void configRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(artistaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        artistaAdapter.setList(getArtistaFromDB());
    }

    private List<Artista> getArtistaFromDB() {
       return SQLite.select()
               .from(Artista.class)
               .orderBy(Artista_Table.orden, true)
               .queryList(Artista.getWritableDatabase());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;
        switch (id){
            case R.id.action_settings:
                intent = new Intent(MainActivity.this, NotificacionLocalActivity.class);
                startActivity(intent);
                break;
            case R.id.action_firebase:
                intent = new Intent(MainActivity.this, FireBaseActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Artista artista) {
        //sArtista = artista;
        Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
        intent.putExtra(Artista.ID, artista.getId());
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(Artista artista) {
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null){
            vibrator.vibrate(60);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.main_dialogDelete_title)
                .setMessage(String.format(getString(R.string.detalle_dialogDelete_message), artista.getNombre()))
                .setPositiveButton(R.string.detalle_dialogDelete_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                artista.delete(Artista.getWritableDatabase());
                                artistaAdapter.remove(artista);
                                showMessage(R.string.main_message_delete_success);
                            }
                        })
                .setNegativeButton(R.string.label_dialog_cancel, null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){
            sArtista.save(Artista.getWritableDatabase());
            artistaAdapter.add(sArtista);
        }
    }

    @OnClick(R.id.fab)
    public void addArtist() {

        Intent intent = new Intent(MainActivity.this, AddArtistActivity.class);
        intent.putExtra(Artista.ORDEN, artistaAdapter.getItemCount() + 1);

        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

    private void showMessage(int resources) {
        Snackbar.make(containerMain, resources, Snackbar.LENGTH_SHORT).show();
    }
}

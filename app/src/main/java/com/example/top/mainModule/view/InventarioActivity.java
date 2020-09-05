package com.example.top.mainModule.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top.R;
import com.example.top.addModule.view.AddProductFragment;
import com.example.top.common.pojo.Product;
import com.example.top.detailModule.view.DetailFragment;
import com.example.top.mainModule.InventarioPresenter;
import com.example.top.mainModule.InventarioPresenterClass;
import com.example.top.mainModule.view.adapters.OnItemClickListener;
import com.example.top.mainModule.view.adapters.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InventarioActivity extends AppCompatActivity implements OnItemClickListener, InventarioView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.contentMain)
    ConstraintLayout contentMain;

    private InventarioPresenter inventarioPresenter;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        ButterKnife.bind(this);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        configToolbar();
        configAdapter();
        configRecyclerView();

        inventarioPresenter = new InventarioPresenterClass(this);
        inventarioPresenter.onCreate();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        productAdapter = new ProductAdapter(new ArrayList<Product>(), this);
    }

    private void configRecyclerView() {
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.inventario_columns));
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(productAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inventarioPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        inventarioPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inventarioPresenter.onDestroy();
    }

    /*
     * InventarioView
     * */

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void add(Product product) {
        productAdapter.add(product);
    }

    @Override
    public void update(Product product) {
        productAdapter.update(product);
    }

    @Override
    public void remove(Product product) {
        productAdapter.remove(product);
    }

    @Override
    public void removeFail() {
        Snackbar.make(contentMain, R.string.inventario_error_remove, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onShowError(int resMsg) {
        Snackbar.make(contentMain, resMsg, Snackbar.LENGTH_LONG).show();
    }

    /*
     * OnItemClickListener
     * */

    @Override
    public void OnItemClick(Product product) {
        Bundle args = new Bundle();
        args.putString(Product.ID, product.getId());
        args.putString(Product.NAME, product.getName());
        args.putInt(Product.QUANTITY, product.getQuantity());
        args.putString(Product.PHOTO_URL, product.getPhotoUrl());

        Fragment fragment = new DetailFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.contentMain, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void OnLongItemClick(final Product product) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null){
            vibrator.vibrate(100);
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.inventario_dialog_remove_title)
                .setMessage(R.string.inventario_dialog_remove_message)
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inventarioPresenter.remove(product);
                    }
                })
                .setNegativeButton(R.string.label_dialog_cancel, null)
                .show();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        new AddProductFragment().show(getSupportFragmentManager(), getString(R.string.addProduct_title));
    }
}

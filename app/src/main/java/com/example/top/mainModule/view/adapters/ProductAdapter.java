package com.example.top.mainModule.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.top.R;
import com.example.top.common.pojo.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;
    private Context context;

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.setOnClickListener(product, listener);

        holder.tvData.setText(context.getString(R.string.item_product_data, product.getName(),
                product.getQuantity()));

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context)
                .load(product.getPhotoUrl())
                .apply(requestOptions)
                .into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void add(Product product){
        if (!products.contains(product)){
            products.add(product);
            notifyItemInserted(products.size() -1);
        } else {
            update(product);
        }
    }

    public void update(Product product) {
        if (products.contains(product)){
            final int index = products.indexOf(product);
            products.set(index, product);
            notifyItemChanged(index);
        }
    }

    public void remove(Product product){
        if (products.contains(product)){
            final int index = products.indexOf(product);
            products.remove(index);
            notifyItemRemoved(index);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgPhoto)
        AppCompatImageView imgPhoto;
        @BindView(R.id.tvData)
        AppCompatTextView tvData;

        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        void setOnClickListener(Product product, OnItemClickListener listener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(product);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.OnLongItemClick(product);
                    return true;
                }
            });
        }
    }
}

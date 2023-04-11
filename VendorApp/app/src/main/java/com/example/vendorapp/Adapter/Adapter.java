package com.example.vendorapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Client.ViewProducts;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ShopModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private List<ShopModel> shopModelList;
    public static Context context;

    public Adapter(List<ShopModel> shopModelList, Context context) {
        this.shopModelList = shopModelList;
        this.context = context;
    }

    // Context context;
    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoprow, parent, false);
        return new Adapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        ShopModel shopModel = shopModelList.get(position);
        holder.bind(shopModel);
    }

    @Override
    public int getItemCount() {
        return shopModelList.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imgonline;
        TextView tvname, tvlocation, tvstatus;
        String imageUri;
        String open;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.shopimg);
            imgonline = itemView.findViewById(R.id.imglocation);
            tvname = itemView.findViewById(R.id.shopname);
            tvlocation = itemView.findViewById(R.id.shoplocation);
            tvstatus = itemView.findViewById(R.id.openclose);
        }

        public void bind(ShopModel shopModel) {
            tvname.setText(shopModel.getName());
            tvlocation.setText(shopModel.getMarket());
            open = shopModel.getOnline();
            if (open.equals("true")) {
                tvstatus.setVisibility(View.INVISIBLE);
                imgonline.setVisibility(View.VISIBLE);
            } else {
                tvstatus.setVisibility(View.VISIBLE);
                imgonline.setVisibility(View.INVISIBLE);
            }

            imageUri = null;
            imageUri = shopModel.getImage();
            try {
                Picasso.with(context).load(imageUri).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.profile);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewProducts.class);
                    intent.putExtra("Shopid", shopModel.getUid());
                    intent.putExtra("Online", shopModel.getOnline());
                    context.startActivity(intent);
                }
            });
        }
    }
}

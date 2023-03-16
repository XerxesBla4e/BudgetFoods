package com.example.vendorapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Cart.DatabaseManager;
import com.example.vendorapp.R;
import com.example.vendorapp.CartModel;
import com.example.vendorapp.typemodel.DetsModel;
import com.squareup.picasso.Picasso;

import java.sql.SQLDataException;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    Context context;
    List<DetsModel> detsModelList;

    public ItemsAdapter(List<DetsModel> detsModelList, Context context) {
        this.context = context;
        this.detsModelList = detsModelList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetsrow, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetsModel detsModel = detsModelList.get(position);

        holder.tvname.setText(detsModel.getName());
        holder.tvid.setText(detsModel.getPid());
        holder.tvtotal.setText(String.valueOf(detsModel.getTotal()));
        holder.tvqty.setText(String.valueOf(detsModel.getQuantity()));
    }


    @Override
    public int getItemCount() {
        return detsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvid, tvname, tvtotal, tvqty;

        public ViewHolder(View itemView) {
            super(itemView);
            tvid = itemView.findViewById(R.id.prodid);
            tvqty = itemView.findViewById(R.id.qtty);
            tvtotal = itemView.findViewById(R.id.total1);
            tvname = itemView.findViewById(R.id.name1);

        }
    }

}

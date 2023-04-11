package com.example.vendorapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Client.OrderDetails;
import com.example.vendorapp.Client.OrderDetails2;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.OrdersModel;

import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyOrdersAdapterViewHolder> {
    List<OrdersModel> ordersModelList;
    Context context;

    public MyOrdersAdapter(Context context, List<OrdersModel> ordersModelList) {
        this.ordersModelList = ordersModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyOrdersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetsrow, parent, false);
        return new MyOrdersAdapter.MyOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOrdersAdapterViewHolder holder, int position) {
        OrdersModel ordersModel = ordersModelList.get(position);
        holder.tvorderid.setText(ordersModel.getOrderID());
        holder.tvtotalcost.setText(ordersModel.getCost());

        String status = ordersModel.getOrderStatus();

        if (status.equals("In Progress")) {
            holder.tvstatus.setText("In Progress");
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.green_pie));
        } else if (status.equals("Cancelled")) {
            holder.tvstatus.setText("Cancelled");
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.red));
        } else if (status.equals("Confirmed")) {
            holder.tvstatus.setText("Confirmed");
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.light_green));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetails2.class);
                intent.putExtra("orderID", ordersModel.getOrderID());
                intent.putExtra("orderBy", ordersModel.getOrderBy());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersModelList.size();
    }

    class MyOrdersAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvorderid, tvstatus, tvtotalcost;

        public MyOrdersAdapterViewHolder(View itemView) {
            super(itemView);
            tvorderid = itemView.findViewById(R.id.orderid);
            tvstatus = itemView.findViewById(R.id.status);
            tvtotalcost = itemView.findViewById(R.id.totalcost);
        }
    }
}

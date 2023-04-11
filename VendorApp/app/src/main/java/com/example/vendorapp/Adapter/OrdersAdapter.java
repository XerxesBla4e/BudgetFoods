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
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OrdersAdapter extends FirebaseRecyclerAdapter<OrdersModel, OrdersAdapter.ViewHolder> {
    String imageUri;
    Context context;

    public OrdersAdapter(Context context, FirebaseRecyclerOptions<OrdersModel> options) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordersrecycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int i, OrdersModel ordersModel) {

        //viewHolder.tvstatus.setText(ordersModel.getOrderStatus());
        viewHolder.tvorderid.setText(ordersModel.getOrderID());
        viewHolder.tvtotalcost.setText(ordersModel.getCost());

        String status = ordersModel.getOrderStatus();

        if (status.equals("In Progress")) {
            viewHolder.tvstatus.setText("In Progress");
            viewHolder.tvstatus.setTextColor(context.getResources().getColor(R.color.green_pie));
        } else if (status.equals("Cancelled")) {
            viewHolder.tvstatus.setText("Cancelled");
            viewHolder.tvstatus.setTextColor(context.getResources().getColor(R.color.red));
        } else if (status.equals("Confirmed")) {
            viewHolder.tvstatus.setText("Confirmed");
            viewHolder.tvstatus.setTextColor(context.getResources().getColor(R.color.light_green));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetails.class);
                intent.putExtra("orderID", ordersModel.getOrderID());
                intent.putExtra("orderBy", ordersModel.getOrderBy());
                context.startActivity(intent);
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvorderid, tvstatus, tvtotalcost;

        public ViewHolder(View itemView) {
            super(itemView);

            tvorderid = itemView.findViewById(R.id.orderid);
            tvstatus = itemView.findViewById(R.id.status);
            tvtotalcost = itemView.findViewById(R.id.totalcost);
        }

    }

}

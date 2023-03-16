package com.example.vendorapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.Vendor.EditItem;
import com.example.vendorapp.typemodel.ItemModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ItemAdapter extends FirebaseRecyclerAdapter<ItemModel, ItemAdapter.ViewHolder> {
    String imageUri;
    Context context;

    public ItemAdapter(Context context, FirebaseRecyclerOptions<ItemModel> options) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int i, ItemModel itemModel) {
        viewHolder.tvname.setText(itemModel.getItemname());
        viewHolder.tvprice.setText(itemModel.getPrice());

        Boolean discountavailable1 = itemModel.getDiscountavailable();

        if (discountavailable1) {
            viewHolder.tvdisdes.setText(itemModel.getDiscountdescr());
            viewHolder.tvdiscprice.setText(itemModel.getDiscountPrice());

            viewHolder.tvprice.setText(itemModel.getPrice());
            viewHolder.tvprice.setPaintFlags(viewHolder.tvprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.tvdisdes.setVisibility(View.INVISIBLE);
            viewHolder.tvdiscprice.setVisibility(View.INVISIBLE);
        }

        imageUri = null;
        imageUri = itemModel.getImage();
        try {
            Picasso.with(context).load(imageUri).into(viewHolder.imageView);
        } catch (Exception e) {
            viewHolder.imageView.setImageResource(R.drawable.item);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(itemModel);
            }
        });
    }

    private void detailsBottomSheet(ItemModel itemModel) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.itemdetails, null);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();

        ImageView imageView = view.findViewById(R.id.img);
        ImageView delete = view.findViewById(R.id.deletep);
        ImageView update = view.findViewById(R.id.editp);
        TextView textView = view.findViewById(R.id.itmname);
        TextView textView2 = view.findViewById(R.id.itmprice);
        TextView textView3 = view.findViewById(R.id.itmdescription);
        TextView textView4 = view.findViewById(R.id.itmquantity);
        TextView textView5 = view.findViewById(R.id.itmdiscountdesc);
        TextView textView6 = view.findViewById(R.id.itmdiscountprice);

        String id = itemModel.getPid();
        String realamount = itemModel.getPrice();
        String productname = itemModel.getItemname();
        String productdescription = itemModel.getItemdesc();
        String discountdesc = itemModel.getDiscountdescr();
        String discountamount = itemModel.getDiscountPrice();
        String itemqty = itemModel.getQuantity();
        Boolean discountavailable = itemModel.getDiscountavailable();
        String productimage = itemModel.getImage();

        textView.setText(productname);
        textView2.setText(realamount);
        textView3.setText(productdescription);
        textView4.setText(itemqty);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editactivity = new Intent(context, EditItem.class);
                editactivity.putExtra("productID", itemModel.getPid());
                editactivity.putExtra("userID", itemModel.getUid());
                editactivity.putExtra("timestamp", itemModel.getTimestamp());
                context.startActivity(editactivity);
            }
        });

        try {
            Picasso.with(context).load(productimage).into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.item);
        }

        if (discountavailable) {
            textView5.setVisibility(View.VISIBLE);
            textView6.setVisibility(View.VISIBLE);
            textView5.setText(discountdesc);
            textView6.setText(discountamount);
        } else {
            textView5.setVisibility(View.INVISIBLE);
            textView6.setVisibility(View.INVISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are You Sure");
                builder.setMessage("No Return After this");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Users").child(itemModel.getUid()).child("Products")
                                .child(id).removeValue();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Deletion Canceled", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            private void deleteItem(String id) {
                FirebaseDatabase.getInstance().getReference("Users").child(itemModel.getUid()).child("Products")
                        .child(id).removeValue();
            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvname, tvprice, tvdisdes, tvdiscprice;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recycleritemimg);
            tvname = itemView.findViewById(R.id.recycleritem1);
            tvprice = itemView.findViewById(R.id.recyclerprice1);
            tvdisdes = itemView.findViewById(R.id.itmdiscountdesc);
            tvdiscprice = itemView.findViewById(R.id.itmdiscountprice);
        }

    }

}

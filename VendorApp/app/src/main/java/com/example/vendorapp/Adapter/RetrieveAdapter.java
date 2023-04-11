package com.example.vendorapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Cart.DatabaseManager;

import com.example.vendorapp.CartModel;
import com.example.vendorapp.R;
import com.example.vendorapp.typemodel.ItemModel;
import com.example.vendorapp.typemodel.ShopModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.SQLDataException;

public class RetrieveAdapter extends FirebaseRecyclerAdapter<ItemModel, RetrieveAdapter.ViewHolder> {
    String imageUri;
    Context context;
    DatabaseManager databaseManager;
    DatabaseReference databaseReference;
    public int qty = 0;
    public int total;
    public ImageView add, closed;
    public ImageView remove;
    public EditText quantity;
    public Boolean alreadyadded = false;
    String itemname1;
    String itemnprice1;
    String itemID;
    String userID;
    String itemImg;
    String discountprice;
    String itemg, shopname1, sname;
    Boolean isDiscounted = false;

    public RetrieveAdapter(Context context, FirebaseRecyclerOptions<ItemModel> options) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item1recycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int i, ItemModel itemModel) {
        viewHolder.tvname.setText(itemModel.getItemname());
        viewHolder.tvprice.setText(itemModel.getPrice());

        Boolean discountavailable = itemModel.getDiscountavailable();

        if (discountavailable) {
            viewHolder.tvdisdes.setVisibility(View.VISIBLE);
            viewHolder.tvdiscprice.setVisibility(View.VISIBLE);

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

        viewHolder.add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                View cartview = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.cartmainrow, null);

                databaseManager = new DatabaseManager(context);

                try {
                    databaseManager.open();
                } catch (SQLDataException e) {
                    e.printStackTrace();
                }


                Button btn = (Button) cartview.findViewById(R.id.addcrt);
                ImageView cncl = (ImageView) cartview.findViewById(R.id.close3);
                add = (ImageView) cartview.findViewById(R.id.add);
                // closed = (ImageView) cartview.findViewById(R.id.closedialog);
                remove = (ImageView) cartview.findViewById(R.id.subtract);
                quantity = (EditText) cartview.findViewById(R.id.quantity);

                builder.setView(cartview);
                builder.setCancelable(true);
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                //builder.show();

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qty > 0) {
                            qty = qty - 1;
                            display(qty);
                        }
                    }
                });

                cncl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qty = qty + 1;
                        display(qty);
                    }

                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alreadyadded = databaseManager.checkAlreadyExists(itemModel.getItemname());
                        if (!alreadyadded) {
                            itemname1 = itemModel.getItemname();
                            itemnprice1 = itemModel.getPrice();
                            itemID = itemModel.getPid();
                            userID = itemModel.getUid();
                            itemImg = itemModel.getImage();
                            sname = retrieveShopName(userID);
                            discountprice = itemModel.getDiscountPrice();
                            isDiscounted = itemModel.getDiscountavailable();
                            if (itemImg.isEmpty()) {
                                itemg = "";
                            } else {
                                itemg = itemImg;
                            }

                            if (isDiscounted) {
                                total = Integer.parseInt(discountprice) * Integer.parseInt(quantity.getText().toString());
                            } else {
                                total = Integer.parseInt(itemnprice1) * Integer.parseInt(quantity.getText().toString());
                            }

                            int f = databaseManager.insert(itemname1, Integer.parseInt(quantity.getText().toString()), itemID, total, userID, itemg, sname);
                            if (f > 1) {
                                Toast.makeText(context, "Item " + itemModel.getItemname() + "Added To Cart", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Item " + itemModel.getItemname() + " Already Added To Cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    public String retrieveShopName(String userID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ShopModel shopModel = snapshot.getValue(ShopModel.class);

                if (shopModel != null) {
                    shopname1 = shopModel.getName();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return shopname1;
    }

    private void display(int qty) {
        quantity.setText("" + qty);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvname, tvprice, tvdisdes, tvdiscprice;
        Button add3;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recycleritemimg);
            tvname = itemView.findViewById(R.id.recycleritem1);
            tvprice = itemView.findViewById(R.id.recyclerprice1);
            tvdisdes = itemView.findViewById(R.id.itmdiscountdesc);
            add3 = itemView.findViewById(R.id.dd);
            tvdiscprice = itemView.findViewById(R.id.itmdiscountprice);
        }

    }

}

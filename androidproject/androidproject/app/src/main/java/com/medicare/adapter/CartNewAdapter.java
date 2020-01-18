package com.medicare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medicare.R;
import com.medicare.interfaces.OnCartItemDelete;
import com.medicare.model.Cart;

import java.util.ArrayList;

public class CartNewAdapter  extends RecyclerView.Adapter<CartNewAdapter.MyViewHolder> {

    private ArrayList<Cart> medicines;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_price, tv_des,tv_qty;
        public ImageView iv_right;
        public LinearLayout ll_delete;

        public MyViewHolder(View view) {
            super(view);
            ll_delete = view.findViewById(R.id.ll_delete);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_qty = (TextView) view.findViewById(R.id.tv_qty1);
            tv_des = (TextView) view.findViewById(R.id.tv_des);

        }
    }

    public CartNewAdapter(Context mcContext, ArrayList<Cart> medicines) {
        this.context = mcContext;
        this.medicines = medicines;
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Cart medicine = medicines.get(position);

        holder.tv_price.setText("MRP : " + medicine.getPrice());
        holder.tv_name.setText(medicine.getName());
        holder.tv_des.setText(medicine.getDes());
        holder.tv_qty.setText(String.valueOf(medicine.getQty()));
        holder.ll_delete.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart, parent, false);
        return new MyViewHolder(v);
    }
}
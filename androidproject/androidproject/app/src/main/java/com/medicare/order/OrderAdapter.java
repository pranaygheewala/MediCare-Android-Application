package com.medicare.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medicare.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private ArrayList<Order.Datum> medicines;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_status, tv_orderID, tv_orderdata;
        public Button btn_action;
        public LinearLayout ll_action;

        public MyViewHolder(View view) {
            super(view);

            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_orderID = (TextView) view.findViewById(R.id.tv_orderID);
            tv_orderdata = (TextView) view.findViewById(R.id.tv_orderdata);

        }
    }

    public OrderAdapter(Context mcContext, ArrayList<Order.Datum> medicines) {
        this.context = mcContext;
        this.medicines = medicines;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Order.Datum data = medicines.get(position);
        if(data.getStatus().equals("accepted")){
            holder.tv_status.setText("Order is already accepted");
        }else{
            holder.tv_status.setText("Pending for Review");
        }

        holder.tv_orderdata.setText(data.getCreated_at());
        holder.tv_orderID.setText(data.getOrder_id());
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order, parent, false);
        return new MyViewHolder(v);
    }
}
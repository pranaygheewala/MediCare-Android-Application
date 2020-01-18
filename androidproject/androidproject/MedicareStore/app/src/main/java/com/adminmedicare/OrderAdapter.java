package com.adminmedicare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private ArrayList<Order.Datum> medicines;
    private OnButtonActionListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_status, tv_orderID, tv_orderdata;
        public Button btn_action,btn_reject;
        public LinearLayout ll_action,ll_order_date_id;

        public MyViewHolder(View view) {
            super(view);
            ll_action = view.findViewById(R.id.ll_action);

            ll_order_date_id = view.findViewById(R.id.ll_order_date_id);

            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_orderID = (TextView) view.findViewById(R.id.tv_orderID);
            tv_orderdata = (TextView) view.findViewById(R.id.tv_orderdata);
            btn_action = (Button) view.findViewById(R.id.btn_action);
            btn_reject = (Button) view.findViewById(R.id.btn_reject);
            btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(medicines.get(getAdapterPosition()).getOrder_id(),"accept");
                }
            });

            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(medicines.get(getAdapterPosition()).getOrder_id(),"reject");
                }
            });
        }
    }

    public OrderAdapter(Context mcContext, ArrayList<Order.Datum> medicines, OnButtonActionListener listener) {
        this.context = mcContext;
        this.medicines = medicines;
        this.listener = listener;
    }

    public void setListerner(OnButtonActionListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Order.Datum data = medicines.get(position);
        if(data.getStatus().equals("accepted")){
            holder.ll_action.setVisibility(View.GONE);
            holder.tv_status.setText("Order is already accepted");
        }else if(data.getStatus().equals("rejected")){
            holder.ll_action.setVisibility(View.GONE);
            holder.tv_status.setText("Order is already rejected");
        }else{
                holder.ll_action.setVisibility(View.VISIBLE);
                holder.tv_status.setText("Pending for Review");

        }

        holder.tv_orderdata.setText(data.getCreated_at());
        holder.tv_orderID.setText(data.getOrder_id());

        holder.ll_order_date_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "" + data.getId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context,OrdarDetailActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id",data.getId());
                context.startActivity(i);
            }
        });

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
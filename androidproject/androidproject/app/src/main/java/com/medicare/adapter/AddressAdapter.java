package com.medicare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medicare.R;
import com.medicare.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter  extends
        RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private ArrayList<String> addressList;
    private List<Boolean> addressCheckList;
    private OnItemClickListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_id;
        public ImageView iv_right;

        public MyViewHolder(View view) {
            super(view);
            iv_right = (ImageView) view.findViewById(R.id.iv_right);
            tv_id = (TextView) view.findViewById(R.id.tv_id);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public AddressAdapter(Context mcContext, ArrayList<String> addressList, List<Boolean> addressCheckList,OnItemClickListener listener) {
        this.context = mcContext;
        this.addressList = addressList;
        this.addressCheckList = addressCheckList;
        this.listener = listener;
    }

    public void setListerner(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (addressCheckList.get(position) == false) {

            holder.iv_right.setVisibility(View.INVISIBLE);
        } else {
            holder.iv_right.setVisibility(View.VISIBLE);
        }
        holder.tv_id.setText(addressList.get(position));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(v);
    }
}

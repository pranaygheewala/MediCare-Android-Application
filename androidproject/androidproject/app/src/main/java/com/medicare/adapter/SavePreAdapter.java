package com.medicare.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.medicare.R;
import com.medicare.interfaces.OnPresciptionClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavePreAdapter extends RecyclerView.Adapter<SavePreAdapter.MyViewHolder> {

    private ArrayList<String> savelist;
    private OnPresciptionClickListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_pre_image;

        public MyViewHolder(View view) {
            super(view);

            iv_pre_image = (ImageView) view.findViewById(R.id.iv_pre_image);
            iv_pre_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSavePreClick(getAdapterPosition());
                }
            });
        }
    }

    public SavePreAdapter(Context mcContext, ArrayList<String> savelist, OnPresciptionClickListener listener) {
        this.context = mcContext;
        this.savelist = savelist;
        this.listener = listener;
    }

    public void setListerner(OnPresciptionClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.with(context).load(Uri.parse(savelist.get(position))).into(holder.iv_pre_image);
    }

    @Override
    public int getItemCount() {
        return savelist.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_save_pre, parent, false);
        return new MyViewHolder(v);
    }
}

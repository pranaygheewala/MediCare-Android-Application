package com.medicare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medicare.MedicineActivity;
import com.medicare.MedicineDetailActivity;
import com.medicare.R;
import com.medicare.interfaces.OnMedicineItemClick;
import com.medicare.model.Medicine;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MyViewHolder> implements Filterable {

    private ArrayList<Medicine> medicines;
    private ArrayList<Medicine> filteredData;
    Context context;
    private ItemFilter mFilter = new ItemFilter();

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_price, tv_des;
        public ImageView iv_right;

        public LinearLayout ll_whole;
        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_des = (TextView) view.findViewById(R.id.tv_des);
            ll_whole = view.findViewById(R.id.ll_whole);

        }
    }

    public MedicineAdapter(Context mcContext, ArrayList<Medicine> medicines) {
        this.context = mcContext;
        this.medicines = medicines;
        this.filteredData = medicines;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Medicine medicine = filteredData.get(position);

        holder.tv_price.setText("MRP : " + medicine.getPrice());
        holder.tv_name.setText(medicine.getName());
        holder.tv_des.setText(medicine.getDes());

        holder.ll_whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MedicineDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name", medicine.getName());
                intent.putExtra("des", medicine.getDes());
                intent.putExtra("qty", medicine.getQty());
                intent.putExtra("price", medicine.getPrice());
                intent.putExtra("id", medicine.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_medicine, parent, false);
        return new MyViewHolder(v);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Medicine> list = medicines;

            int count = list.size();
            final ArrayList<Medicine> nlist = new ArrayList<Medicine>(count);

            Medicine filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Medicine>) results.values;
            notifyDataSetChanged();
        }

    }

}

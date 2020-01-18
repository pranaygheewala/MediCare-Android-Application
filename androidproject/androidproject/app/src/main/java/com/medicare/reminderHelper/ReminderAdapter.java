package com.medicare.reminderHelper;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.medicare.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReminderAdapter extends ArrayAdapter {

    private Context context;
    private int layoutRes;
    private ArrayList<Reminder> arrayList;

    private LayoutInflater inflater;

    public ReminderAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Reminder> arrayList) {
        super(context, resource, arrayList);
        this.context=context;
        layoutRes=resource;
        this.arrayList=arrayList;

        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view=inflater.inflate(layoutRes,null);

        TextView date= (TextView) view.findViewById(R.id.textViewDate);
        TextView time= (TextView) view.findViewById(R.id.textViewTime);
        TextView name= (TextView) view.findViewById(R.id.textViewName);
        TextView alpha= (TextView) view.findViewById(R.id.textViewAlpha);
        CircleImageView imageView= (CircleImageView) view.findViewById(R.id.profile_image);

        Reminder toDoPojo=arrayList.get(position);
        imageView.setImageResource(toDoPojo.getImageRes());
        name.setText(toDoPojo.getName());

        String timeNew = toDoPojo.getTime();
        String[] timesplit = timeNew.split(":");

        if(Integer.parseInt(timesplit[0]) >= 12){
            int finalHour = Integer.parseInt(timesplit[0]) - 12;
            time.setText(""+finalHour+":"+timesplit[1] + " "+ "PM");
        }else{
            time.setText(""+timesplit[0]+":"+timesplit[1] + " "+ "AM");
        }
        //time.setText(toDoPojo.getTime());
        date.setText(toDoPojo.getDate());
        alpha.setText(toDoPojo.getAlpha());

        return view;
    }
}

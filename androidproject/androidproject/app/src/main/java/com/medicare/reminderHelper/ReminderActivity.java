package com.medicare.reminderHelper;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.medicare.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.attr.id;

public class ReminderActivity extends AppCompatActivity {

    public static boolean playing=false;
    TextView buttonStop;
    TextView textViewBack;
    Button buttonDate, buttonTime;
    EditText editTextAbout;
    ImageView imageButtonAdd;
    ListView listView;
    String timeET,dateET,todoET;
    ReminderAdapter arrayAdapter;
    ArrayList<Reminder> arrayListRem= new ArrayList<>();
    ArrayList<Integer> idArrayList= new ArrayList<>();
    AlarmManager alarmManager;
    //To delete and edit alarm
    boolean flagDeleteAlarm = false;
    boolean flagEditAlarm = false;
    //public static Activity fa;
    ImageView iv_back;
    String selectedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //fa=this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        init();

        setListeners();

        fetchDatabaseToArrayList();

        stopAlarm();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listView);
        imageButtonAdd = (ImageView) findViewById(R.id.imageButtonAdd);
        editTextAbout = (EditText) findViewById(R.id.editTextAbout);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonTime = (Button) findViewById(R.id.buttonTime);
        textViewBack = (TextView) findViewById(R.id.textViewBack);
        buttonStop = (TextView) findViewById(R.id.buttonStop);
        buttonStop.setVisibility(View.INVISIBLE);
        textViewBack.setVisibility(View.INVISIBLE);

        iv_back  = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void setListeners() {
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClocK();
            }
        });
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoET = editTextAbout.getText().toString();
                String textTime=buttonTime.getText().toString();
                String textDate=buttonDate.getText().toString();
                if(todoET.equals("")) {
                    Toast.makeText(ReminderActivity.this, "Add Reminder Name", Toast.LENGTH_SHORT).show();
                }
                else if(textDate.equals("Select Date")) {
                    Toast.makeText(ReminderActivity.this, "Add Date", Toast.LENGTH_SHORT).show();
                }
                else if(textTime.equals("Select Time")) {
                    Toast.makeText(ReminderActivity.this, "Add Time", Toast.LENGTH_SHORT).show();
                }
                else {
                    long id=insertDatabase();
                    setAlarm(id);
                    fetchDatabaseToArrayList();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showListDialog(position);
            }
        });
    }

    private long insertDatabase() {

        DatabaseHelper helper = new DatabaseHelper(ReminderActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Databases.TODO, todoET);
        cv.put(Databases.DATE, dateET);
        cv.put(Databases.TIME, timeET);

        long id = Databases.insert(db, cv);
        if (id>0) {
            //Toast.makeText(ReminderActivity.this, "Reminder Set", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ReminderActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
        }
        db.close();

        editTextAbout.setText("");
        buttonTime.setText("Select Time");
        buttonDate.setText("Select Date");
        return id;
    }

    private void setAlarm(long id) {
        DatabaseHelper helper =new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = Databases.ID +" = '"+id+"'";
        Cursor cursor = Databases.select(db,selection);
        String dateET[]=new String[3];
        String timeET[]=new String[2];
        String date = null,time=null,name = null;
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                date = cursor.getString(2);
                time = cursor.getString(3);
                name = cursor.getString(1);
            }
        }

        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent i = new Intent();
        i.setAction("listen.alaram");
        i.putExtra("ID",id);
        i.putExtra("TODO",name);
        i.putExtra("DATE",date);
        i.putExtra("TIME",time);

        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,(int)id,i,0);

        if(flagDeleteAlarm==true) {
            alarmManager.cancel(pendingIntent);
            flagDeleteAlarm=false;
        }
        else {
            int k=0;
            for(String s: date.split("-")) {
                dateET[k++]=s;
            }
            k=0;
            for(String s: time.split(":")) {
                timeET[k++]=s;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dateET[2]), Integer.parseInt(dateET[1])-1, Integer.parseInt(dateET[0]), Integer.parseInt(timeET[0]), Integer.parseInt(timeET[1]),0);
            long mili = calendar.getTimeInMillis();
            calendar.setTimeInMillis(mili);

            Calendar calendarCurrent = Calendar.getInstance();
            long miliCurrent = calendarCurrent.getTimeInMillis();
            calendarCurrent.setTimeInMillis(miliCurrent);
            long diff = mili - miliCurrent;

            long currentTime = System.currentTimeMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + diff, pendingIntent);
            diff=diff/1000;
            Toast.makeText(this, "Alarm set for " +diff+" secs" , Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchDatabaseToArrayList() {
        arrayListRem.clear();
        idArrayList.clear();
        DatabaseHelper mySqliteOpenHelper = new DatabaseHelper(this);
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        int k=0;
        Cursor cursor = Databases.select(db, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String todo = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                char alpha=todo.charAt(0);
                char alphaU= Character.toUpperCase(alpha);

                Reminder todopojo=new Reminder();
                todopojo.setName(todo);
                todopojo.setDate(date);
                todopojo.setTime(time);
                todopojo.setAlpha(""+alphaU);
                if(k%2==0) {
                    todopojo.setImageRes(R.drawable.redback);
                    k++;
                }
                else {
                    todopojo.setImageRes(R.drawable.redback2);
                    k++;
                }
                arrayListRem.add(todopojo);
                idArrayList.add(id);
            }
            cursor.close();
            db.close();

            arrayAdapter = new ReminderAdapter(ReminderActivity.this, R.layout.listview_back, arrayListRem);
            listView.setAdapter(arrayAdapter);
        }

    }

    private void showCalendar() {
        Calendar calendar= Calendar.getInstance();
        final int date=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog=new DatePickerDialog(ReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                buttonDate.setText(""+dayOfMonth+"-"+month+"-"+year);
                dateET=""+dayOfMonth+"-"+month+"-"+year;
            }
        },year,month,date);
        datePickerDialog.show();
    }
    private void showClocK() {
        Calendar calendar= Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog=new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if(hourOfDay >= 12){
                    int finalHour = hourOfDay - 12;
                    buttonTime.setText(""+finalHour+":"+minute + " "+ "PM");
                }else{
                    buttonTime.setText(""+hourOfDay+":"+minute + " "+ "AM");
                }

                timeET=""+hourOfDay+":"+minute;
            }
        },hour,minute,false);
        timePickerDialog.show();
    }
    private void showListDialog(final int pos) {
        showDelDialog(pos);
    }
    public void showDelDialog(final int position) {
        AlertDialog.Builder builder= new AlertDialog.Builder(ReminderActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper helper=new DatabaseHelper(ReminderActivity.this);
                SQLiteDatabase db=helper.getWritableDatabase();
                int id = idArrayList.get(position);
                String whereCluase = Databases.ID +" = '"+id+"'";

                int flag = Databases.delete(db, whereCluase);
                if (flag > 0) {
                    Toast.makeText(ReminderActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    arrayListRem.clear();
                    idArrayList.clear();
                    fetchDatabaseToArrayList();
                    arrayAdapter.notifyDataSetChanged();
                    flagDeleteAlarm = true;
                    //TO DELETE ALARM
                    setAlarm(id);
                }
                else {
                    Toast.makeText(ReminderActivity.this, "can't delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog deleteDialog=builder.create();
        deleteDialog.show();
    }


    private void stopAlarm() {
        if(playing==true) {
            textViewBack.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.VISIBLE);
            buttonStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReminderReceiver.player.pause();
                    textViewBack.setVisibility(View.INVISIBLE);
                    buttonStop.setVisibility(View.INVISIBLE);
                    playing=false;
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(flagEditAlarm==true) {
            fetchDatabaseToArrayList();
            setAlarm(id);
            flagEditAlarm=false;
        }
    }
}

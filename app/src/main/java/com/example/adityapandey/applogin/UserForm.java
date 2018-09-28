package com.example.adityapandey.applogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.gifview.library.GifView;
import com.example.adityapandey.applogin.Custom.EditText_Roboto_Regular;
import com.example.adityapandey.applogin.model.Login;
import com.example.adityapandey.applogin.model.User;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.orm.SugarContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class UserForm extends AppCompatActivity  {


    /*
        UserLogin or Not base
     */
    public static final String PREFS_NAME = "MyAppLogin";
    public static final String USER = "user";


    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private TextView starttxt, endtxt;

    private EditText_Roboto_Regular username;

    private SwitchDateTimeDialogFragment dateTimeFragment;

    private LinearLayout startbtn, endbtn, mainbody;

    private CheckBox sports,research,tech,entertainment;

    private boolean clicked = true;

    private Date startdate = null;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Getting all the object
        mainbody = (LinearLayout) findViewById(R.id.main_user);
        username = (EditText_Roboto_Regular) findViewById(R.id.username);
        starttxt = (TextView) findViewById(R.id.start_val);
        endtxt = (TextView) findViewById(R.id.end_val);
        startbtn = (LinearLayout) findViewById(R.id.start_btn);
        endbtn = (LinearLayout) findViewById(R.id.end_btn);
        sports = (CheckBox) findViewById(R.id.sports);
        research = (CheckBox) findViewById(R.id.research);
        tech = (CheckBox) findViewById(R.id.tech);
        entertainment = (CheckBox) findViewById(R.id.entertainment);

        if(SugarContext.getSugarContext() == null)
            SugarContext.init(getApplicationContext());

        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    getString(R.string.clean) // Optional
            );
        }


        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2018, Calendar.SEPTEMBER, 26).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                if(clicked) {
                    startdate = date;
                    starttxt.setText(myDateFormat.format(date));
                } else {
                    endtxt.setText(myDateFormat.format(date));
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                if(clicked) {
                    startdate = date;
                    starttxt.setText(R.string.start_time);
                } else {
                    endtxt.setText(R.string.end_time);
                }
            }
        });


        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                int user = settings.getInt(USER, -1);
                if(user == -1){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return;
                }
                if(username.getText().length() == 0 || starttxt.getText().length() == 10 || endtxt.getText().length() == 8){
                    showPopup(view, 1);
                    return;
                }

                Login login = Login.findById(Login.class, user);
                ArrayList<User> userlist = new ArrayList<>(User.find(User.class, "STARTDATE = ? and ENDDATE = ?", new String[]{starttxt.getText().toString(), endtxt.getText().toString()}, null, null, "10"));
                if(userlist.size() == 2){
                    showPopup(view, 0);
                } else {
                    User user1 = new User(login, username.getText().toString(), sports.isChecked(), research.isChecked(), tech.isChecked(), entertainment.isChecked(), starttxt.getText().toString(), endtxt.getText().toString());
                    user1.save();
                    showPopup(view, 2);
                }
            }
        });
    }

    public void timer(View view){
        dateTimeFragment.startAtCalendarView();
        if(view.getId() == R.id.end_btn) {
            dateTimeFragment.setMinimumDateTime(new Date(startdate.getTime()));
            dateTimeFragment.setDefaultDateTime(new Date(startdate.getTime()+100));
        } else {
            dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2018, Calendar.SEPTEMBER, 28, 15, 20).getTime());
        }
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        switch (view.getId()){
            case R.id.start_btn:
                clicked = true;
                break;
            case R.id.end_btn:
                clicked = false;
                break;
        }
    }

    public void logout(View view){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(USER, -1);
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void showPopup(View view, int status){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        GifView gifView = popupView.findViewById(R.id.gifview);
        TextView textView = popupView.findViewById(R.id.status);
        if(status == 0){
            gifView.setVisibility(View.GONE);
            textView.setText(R.string.booking_error);
        } else if(status == 1){
            gifView.setVisibility(View.GONE);
            textView.setText(R.string.booking_validation);
        }
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void closePopup(View view){
        popupWindow.dismiss();
    }

}

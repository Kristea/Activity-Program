package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import uk.ac.uea.activityprogram.Models.AppUser;
import uk.ac.uea.activityprogram.Models.NotifyService;
import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.Event;


/**
 * HomeScreen Activity - the screen user sees once they are logged into the app.
 * Allows the user to access other screens such as their calendar, settings, etc
 *
 * @author Kristiana.
 */
public class HomeScreen extends Activity implements View.OnClickListener {

    private ImageButton settingsBtn;
    private ImageButton activitiesCalBtn;
    private ImageButton feedBtn;
    private ImageButton calendarBtn;
    private ImageButton logoutBtn;
    private ImageButton infoBtn;
    private AppUser user ;
    private PendingIntent pendingIntent;
    private ArrayList<Event> events;
    private String[] dates;
    private Date date;

    /**
     * What happens on creation of an activity
     * initialise all the home screen buttons
     *  and setup the notifications
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        user = (AppUser)getIntent().getSerializableExtra("User");
        events = (ArrayList<Event>)getIntent().getSerializableExtra("Events");

        //call alarm method to set notification

        TextView emptyEmail = (TextView) findViewById(R.id.emptyEmail);
        emptyEmail.setText(user.getName());

       //set listeners for all the buttons
        activitiesCalBtn = (ImageButton) findViewById(R.id.activitiesCalBtn);
        activitiesCalBtn.setOnClickListener(this);

        calendarBtn = (ImageButton) findViewById(R.id.calendarBtn);
        calendarBtn.setOnClickListener(this);

        activitiesCalBtn = (ImageButton) findViewById(R.id.activitiesCalBtn);
        activitiesCalBtn.setOnClickListener(this);

        settingsBtn = (ImageButton) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(this);

        logoutBtn = (ImageButton) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

        infoBtn = (ImageButton) findViewById(R.id.infoBtn);
        infoBtn.setOnClickListener(this);

        //initiate alerts when the access the app
        alarmMethod();
    }

    /**
     * Handle what happens when a view is clicked
     * Go to the activities/launch methods associated with each button
     * @param v the view that was clicked
     */
    @Override
    public void onClick(View v) {
        AppDBHandler dbHandler = AppDBHandler.getInstance(this);
        switch(v.getId()){
            case R.id.settingsBtn:
                Intent intent = new Intent(HomeScreen.this, Settings_Activity.class);
                //intent.putExtra("Settings", dbHandler.getSettings(user.getSettings().getActivityProgram(),user.getEmail()));
                intent.putExtra("Events", events);
                intent.putExtra("User", user);
                startActivityForResult(intent, 1);
                break;

            case R.id.calendarBtn:
                Intent intent2 = new Intent(HomeScreen.this, UserCalendar.class);
                intent2.putExtra("User", user);
                showDateUserDialog(intent2);
                break;

            case R.id.activitiesCalBtn:
                Intent intent3 = new Intent(HomeScreen.this, ActivitiesCalendar.class);
                intent3.putExtra("User", user);
                showDateUEADialog(intent3);
                break;

            case R.id.logoutBtn:
                Intent intent5 = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent5);
                break;

            case R.id.infoBtn:
                infoDialog();
                break;

        }
    }

    /**
     * Info dialog which is shown when user clicks
     */
    public void infoDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        final View promptView = layoutInflater.inflate(R.layout.info_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreen.this);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false)
                .setTitle("Information")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        //create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     *Show the dialog for the uea open day calendar allowing user to pick a date
     * and start up activity giving the date to the calendar
     *
     * @param intent the intent to be passed to activity
     */
    public void showDateUEADialog(final Intent intent){
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        final View promptView = layoutInflater.inflate(R.layout.date_dialog, null);

        ArrayList<String> alldates = new ArrayList<>();

        Log.d("debug", "user activity program " + user.getSettings().getActivityProgram());
        for(Event e : events){
            if(e.getId().contains(user.getSettings().getActivityProgram())) {
                alldates.add(e.getDate());
            }
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(alldates);
        alldates.clear();
        alldates.addAll(hs);

        Collections.sort(alldates);

        dates = new String[alldates.size()];
        dates = alldates.toArray(dates);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreen.this);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false)
                .setTitle("Select an open day date")
                .setItems(dates, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("debug", "picked" + which + " = " + dates[which]);
                        //date corresponds to open day format to calendar
                        date = convertToDate(dates[which]);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        //pass in date from picker with intent
                        intent.putExtra("Events" , events);
                        intent.putExtra("Openday", cal);
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        //create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    /**
     *  The date dialog that pops up allowing the user to select an event from their subscribed events
     *
     * @param intent the intent to be passed to the activity
     */
    public void showDateUserDialog(final Intent intent){
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        final View promptView = layoutInflater.inflate(R.layout.date_dialog, null);

        ArrayList<String> userDates = new ArrayList<>();
        for(Event e : user.getSubscribedEvents()){
            userDates.add(e.getDate());
            }
        Set<String> hs = new HashSet<>();
        hs.addAll(userDates);
        userDates.clear();
        userDates.addAll(hs);

        Collections.sort(userDates);

        dates = new String[userDates.size()];
        dates = userDates.toArray(dates);
        //only show dialog if array isn't empty
        if(dates.length != 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreen.this);
            alertDialogBuilder.setView(promptView);

            alertDialogBuilder.setCancelable(false)
                    .setTitle("Select a date")
                    .setItems(dates, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("debug", "picked" + which + " = " + dates[which]);
                            //date corresponds to open day format to calendar
                            date = convertToDate(dates[which]);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            //pass in date from picker with intent
                            intent.putExtra("Events", events);
                            intent.putExtra("Openday", cal);
                            startActivityForResult(intent, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            //create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }else{
            Toast.makeText(HomeScreen.this,"Have no subscribed events", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calculates when an event within the user subscribed event is coming up and initialises the
     * alarm manager which passes an intent to the NotifyService class which creates a notification
     */
    private void alarmMethod(){
        user = (AppUser)getIntent().getSerializableExtra("User");

        Intent alertIntent = new Intent(this,NotifyService.class);

        long when = new GregorianCalendar().getTimeInMillis();
        ArrayList<Event> userEvents = user.getSubscribedEvents();
        Calendar alarmtime = Calendar.getInstance();

        if(!userEvents.isEmpty()) {
            for (Event e : userEvents) {
                String[] date = e.getDate().split("/");
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);

                String[] time = e.getTime().split(":");
                int hour = Integer.parseInt(time[0]);
                int min = Integer.parseInt(time[1]);
                int sec = 00;
                Log.d("debug", "date :" + day + month + year + hour + min + sec);
                alarmtime.set(year, month - 1, day, hour, min, sec);
                Log.d("debug", "alertTime :" + alarmtime);
                alertIntent.putExtra("Event", e);

            }
            alertIntent.putExtra("User", user);
            //if its time to remind about event
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //notification reminder 10 minutes before
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmtime.getTimeInMillis()-10*60000, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            Log.d("debug", "notification created");


            Log.d("debug", "Started reminders");
        }
    }


    /**
     * Method to convert a string to a date object
     * @param date the date string
     * @return date object
     */
    public Date convertToDate(String date){
        DateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);

        //convert time to date format for comparison
        Date conversion = new Date();
        try {
            conversion = sdfDateTime.parse(date+" 00:00");

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return conversion;
    }

    /**
     *Method to convert two strings to a date object
     * @param date date string
     * @param time time sting
     * @return date object
     */
    public Date convertToDateTime(String date,String time){
        DateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);


        //convert time to date format for comparison
        Date conversion = new Date();
        try {
            conversion = sdfDateTime.parse(date+" "+time);

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return conversion;
    }

    /**
     *On activity result object that recieves any events returned by returning to this activity from a previous activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                user = (AppUser)data.getSerializableExtra("User");
                events = (ArrayList<Event>)data.getSerializableExtra("Events");
                Log.d("debug","user " +user);
                Log.d("debug", "events " + events);

            }
        }
    }
}

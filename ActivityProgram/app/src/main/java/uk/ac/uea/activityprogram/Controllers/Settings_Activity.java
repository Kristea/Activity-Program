package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.uea.activityprogram.Models.AppUser;
import uk.ac.uea.activityprogram.Models.Settings;
import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.Event;

/**
 * Settings activity which manages the users settings.
 * Allows user to view and update their school, synchronisation interval, vibration and sound for notifications.
 *
 * @author Kristiana 01/01/2016
 */
public class Settings_Activity extends Activity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private Switch soundSwitch;
    private Switch vibSwitch;
    private Settings userSettings;
    private AppUser user;

    //variables for the settings
    private Boolean notifSound;
    private Boolean notifVib;
    private int syncInterval;
    private String schoolActivity;

    //reset buttons
    private Button resetSetBtn;
    private Button resetCalBtn;

    private AppDBHandler dbHandler = AppDBHandler.getInstance(this);

    /**
     *What happens when activity is created
     *
     *-initialise the sound and vibration switches - setting them to the values from the database (true or false)
     *-set listeners to the switches that listen for changes,
     *     change the user settings values depending if they are set to true or false
     *
     * -initialise the spinners ( option lists) for synch interval and activity program
     * - use an array adapter to set the spinners to values from an arraylist, and swithc statements
     * to change the values in database once a value is selected
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = (AppUser)getIntent().getSerializableExtra("User");
        userSettings = user.getSettings();

        notifSound = userSettings.getNotifSound();
        notifVib = userSettings.getNotifVibration();
        //update switches
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        vibSwitch = (Switch) findViewById(R.id.vibSwitch);
        //set switch to setting in database
        soundSwitch.setChecked(notifSound);
        vibSwitch.setChecked(notifVib);

        /**
         *
         */
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            String sound;
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set the sound setting to true and update
                    //userSettings.setNotifSound(true);
                    sound="true";
                    //update in database
                    Log.d("debug", "sound on");

                } else {
                    //set sound setting to false
                    sound ="false";
                    Log.d("debug", "sound off");
                }
                userSettings.setNotifSound(isChecked);
                dbHandler.updateNotifSound(user.getEmail(), sound);
                user.setSettings(userSettings);
            }
        });

        //update vibration switch depending on action
        vibSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            String vibration;
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set the sound setting to true and update
                    vibration="true";
                    Log.d("debug"," vibration on" );
                } else {
                    //set sound setting to false
                    vibration ="false";
                    Log.d("debug","vibration off");
                }
                userSettings.setNotifVibration(isChecked);
                dbHandler.updateNotifVibration(user.getEmail(), vibration);
                user.setSettings(userSettings);
            }
        });

        schoolActivity = userSettings.getActivityProgram();
        syncInterval = userSettings.getSynchInterval();

        Spinner interval = (Spinner)findViewById(R.id.spinner);
        String[] intervalTimes = new String[]{"15 minutes","30 minutes", "1 hr","5 hrs","Day", "Week"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, intervalTimes);
        interval.setAdapter(adapter);
        switch(syncInterval){
            case 15:
                interval.setSelection(0);
                break;
            case 30:
                interval.setSelection(1);
                break;
            case 1:
                interval.setSelection(2);
                break;
            case 5:
                interval.setSelection(3);
                break;
            case 24:
                interval.setSelection(4);
                break;
            case 168:
                interval.setSelection(5);
                break;
        }

        Spinner school = (Spinner)findViewById(R.id.spinner2);
        String[] schools = new String[]{"Arts", "Business", "Computing"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, schools);
        school.setAdapter(adapter);

        switch(schoolActivity) {
            case "art":
                school.setSelection(0);
                break;
            case "bis":
                school.setSelection(1);
                break;
            case "cmp":
                school.setSelection(2);
                break;
        }
        interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             *The custom on item selected listener for  interval spinner
             *@param parent the adapter view
             * @param view the selected view
             * @param pos position of item
             * @param id id of item
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String intervalSel = parent.getItemAtPosition(pos).toString();
                String dbInterval = "15";
                switch(intervalSel){
                        case "15 minutes":
                            userSettings.setSynchInterval(15);
                            dbInterval = "15";
                            break;
                        case "30 minutes":
                            userSettings.setSynchInterval(30);
                            dbInterval =  "30";
                            break;
                        case "1 hr":
                            userSettings.setSynchInterval(1);
                            dbInterval = "1";
                            break;
                        case "5 hrs":
                            userSettings.setSynchInterval(2);
                            dbInterval =  "5";
                            break;
                        case "Day":
                            userSettings.setSynchInterval(24);
                            dbInterval =  "24";
                            break;
                        case "Week":
                            userSettings.setSynchInterval(168);
                            dbInterval =  "168";
                            break;
                }
                dbHandler.updateSychInterval(user.getEmail(), dbInterval);
                user.setSettings(userSettings);
            }

            /**
             * Method that chooses what happens if nothing is selected with spinner
             * @param arg0 adapter argument
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Another interface callback
            }
        });

        school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             *The custom on item selected listener for activity school spinner
             *@param parent the adapter view
             * @param view the selected view
             * @param pos position of item
             * @param id id of item
             *
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String schoolSel = parent.getItemAtPosition(pos).toString();
                switch(schoolSel) {
                    case "Computing":
                       userSettings.setActivityProgram("cmp");
                        dbHandler.updateActivityprogram(user.getEmail(), "cmp");
                        break;
                    case "Arts":
                        userSettings.setActivityProgram("art");
                        dbHandler.updateActivityprogram(user.getEmail(), "art");
                        break;
                    case "Business":
                        userSettings.setActivityProgram("bis");
                        dbHandler.updateActivityprogram(user.getEmail(),"bis");
                        break;
                }
                user.setSettings(userSettings);
            }
            /**
             * Method that chooses what happens if nothing is selected with spinner
             * @param arg0 adapter argument
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Another interface callback
            }
        });

        //initialise buttons
        resetCalBtn = (Button) findViewById(R.id.resetCalBtn);
        resetCalBtn.setOnClickListener(this);

        resetSetBtn = (Button) findViewById(R.id.resetSetBtn);
        resetSetBtn.setOnClickListener(this);
    }


    /**
     * Interface method for selecting spinner items, deals with what happens when spinner is selected
     * overriden inside onCreate by specific spinner
     *
     * @param parent the adapter view
     * @param view the selected view
     * @param pos position of item
     * @param id id of item
     */
    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
       //implemented for all spinners
    }

    /**
     * Interface method for spinner, deals with what happens if spinner isnt selected
     *
     * @param parent the adapter view
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /**
     *
     * @param v the view
     */
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.resetCalBtn:
                defaultCalendar();
                break;
            case R.id.resetSetBtn:
                mementoDefaultSettings();
                break;
        }

    }

    /**
     * Reset user settings to default settings
     * 1hr syncinterval, cmp school and sound and vibration set to true;
     */
    public void mementoDefaultSettings(){

        user.getOriginator().restoreFromMemento(user.getCaretaker().getMemento(0));
        user.getMementoState().restoreState(dbHandler, user.getMementoState(), user.getEmail());

        user.setSettings(new Settings(user.getMementoState().getDefSettings().getActivityProgram(),
                user.getMementoState().getDefSettings().getSynchInterval(),
                user.getMementoState().getDefSettings().getNotifSound(),
                user.getMementoState().getDefSettings().getNotifVibration()));

        Log.d("debug", "user settings " + user.getSettings());

        Toast.makeText(this,"Settings reset! ",Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    /**
     * Reset users calendar of subscribed events to empty.

     */
    public void defaultCalendar(){
        ArrayList<Event> clearEvents = new ArrayList<>();
        user.setSubscribedEvents(clearEvents);
        dbHandler.deleteUserEventSub(user.getEmail());
        Toast.makeText(this,"Subscriptions Cleared! ",Toast.LENGTH_SHORT).show();
    }

    /**
     * What happens when back button is pressed in android
     * Saves user and events objects and passes it back to previous screen using setResult
     */
    @Override
    public void onBackPressed(){
        ArrayList<Event> events = (ArrayList<Event>)getIntent().getSerializableExtra("Events");
        Intent newIntent = new Intent(this, HomeScreen.class);
        newIntent.putExtra("Events",events);
        newIntent.putExtra("User", user);
        setResult(RESULT_OK, newIntent);
        finish();
        super.onBackPressed();
    }

}

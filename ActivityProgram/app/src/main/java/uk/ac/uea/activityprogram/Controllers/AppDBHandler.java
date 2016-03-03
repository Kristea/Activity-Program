package uk.ac.uea.activityprogram.Controllers;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.uea.activityprogram.Models.Settings;
import uk.ac.uea.framework.implementation.DatabaseHandler;
/**
 * This extends the framework DatabaseHandler class to create a database for this application and
 * allow manipulation and creation of new tables specific to this application.
 * This class implements the Singleton design patterns to ensure that only one instance of the
 * database is created throughout the application.
 * @version: 1
 */
public class AppDBHandler extends DatabaseHandler {
    private static AppDBHandler instance;
    public AppDBHandler(Context context) {
        super(context);
        setDatabaseName("app2db");
    }
    /**
     * Used by other classes to retreive the Singleton instance of the AppDBHandler class.
     * If the instance is null, it creates a new instance by using the constructor. Else the
     * existing instance is returned.
     * @param context   the activity Context object
     * @return  a Singleton AppDBHandler instance
     */
    public static AppDBHandler getInstance(Context context){
        if( instance == null){
            instance = new AppDBHandler(context);
            return instance;
        }else {
            return instance;
        }
    }
    /**
     * This table is called when the first instance of AppDBHandler is called to create the
     * remainder of the tables that are required for the application.
     * It uses the tablename passed in to determine which table to create. This can be extended to
     * include more tables.
     * @param tablename name of the table to be created.
     */
    public void createNewTables(String tablename){
        if(tablename.compareTo("Event") == 0){
            Log.d("debug", "--Table is not present");
            createTable("CREATE TABLE IF NOT EXISTS Event (Id TEXT PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT NOT NULL, Date TEXT NOT NULL, Time TEXT NOT NULL)");
            Log.d("debug", "--Table has been added");
        }else if(tablename.compareTo("UserSettings") == 0){
            Log.d("debug", "--Table is not present");
            createTable("CREATE TABLE IF NOT EXISTS UserSettings (Email TEXT NOT NULL, ActivityProgram TEXT NOT NULL, SyncInterval INTEGER NOT NULL, NotifSound TEXT NOT NULL, NotifVibration TEXT NOT NULL, FOREIGN KEY(Email) REFERENCES User(Email), PRIMARY KEY(Email,ActivityProgram))");
            Log.d("debug", "--Table has been added");
        }else if(tablename.compareTo("UserEvents") == 0){
            Log.d("debug", "--Table is not present");
            createTable("CREATE TABLE IF NOT EXISTS UserEvents (Email TEXT NOT NULL, EventID TEXT NOT NULL,  FOREIGN KEY(Email) REFERENCES User(Email), FOREIGN KEY(EventID) REFERENCES Event(Id), PRIMARY KEY(Email, EventID))");
            Log.d("debug", "--Table has been added");
        }
    }
    /**
     * Retrieves a single Setting from the database based on its primary key - ActivityProgram.
     * @param actprogram    activity program which is the primary key of the settings table.
     * @return  a Settings object, containing the information of the retrieved setting.
     */
    public Settings getSettings(String actprogram, String useremail){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("UserSettings", new String[]{"Email","ActivityProgram", "SyncInterval", "NotifSound", "NotifVibration"}, "Email"
                + "=? AND ActivityProgram = ?", new String[]{String.valueOf(actprogram)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        boolean notifSound = false;
        if(cursor.getString(3).compareTo("t") == 0){
            notifSound = true;
        }
        boolean notifVibration = false;
        if(cursor.getString(4).compareTo("t") == 0){
            notifVibration = true;
        }
        Settings setting = new Settings(cursor.getString(1), cursor.getInt(2), notifSound, notifVibration);
        return setting;
    }

    /**
     * Retreives the whole of the Settings table from the database.
     * Places each attribute of the table in a string where each attribute is separated by a hashtag.
     * Used for debugging and checking the contents of the table.
     * @return  a list of Strings, where each string represents a row in the table with each
     * attribute separated by a hashtag.
     */
    public ArrayList<String> getAllSettings(){
        ArrayList<String> settings = new ArrayList<String>();
        String selectQuery = "SELECT * FROM UserSettings";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String userEmail = cursor.getString(0);
            String activityprogram = cursor.getString(1);
            int syncInterval = cursor.getInt(2);
            String ns = cursor.getString(3);
            String nv = cursor.getString(4);
            boolean notifSound = false;
            if(ns.compareTo("t") == 0){
                notifSound = true;
            }
            boolean notifVibration = false;
            if(nv.compareTo("t") == 0){
                notifVibration = true;
            }
            //Settings s = new Settings(activityprogram, syncInterval, notifSound, notifVibration);
            settings.add(userEmail + "#" + activityprogram + "_" + syncInterval + "/" + notifSound + "~" + notifVibration);
        }
        return settings;
    }

    /**
     * Inserts an new entry into the UserSettings table of the database using the attributes given.
     * @param useremail
     */
    public void insertUserSetting(String useremail,String school, String syncInt, String notifSound, String notifVibration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Email", useremail);
        values.put("ActivityProgram", school);
        values.put("SyncInterval", syncInt); //will need parse to int
        values.put("NotifSound", notifSound); //will need parse to bool - look at login bit
        values.put("NotifVibration", notifVibration);

        db.insertOrThrow("UserSettings", null, values);

    }

    /**
     * Method for updating user activity program
     * @param useremail users email that wants to change the setting
     * @param school the activity program the user wants to change the school to
     * @return and update the
     */
    public int updateActivityprogram(String useremail,String school) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ActivityProgram", school);

        return db.update("UserSettings", values, "Email = ?",
             new String[]{String.valueOf(useremail)});
    }

    /**
     * Method for updating user synchronisation interval
     * @param useremail users email that wants to change the setting
     * @param interval interval the user wants to change  to
     * @return and update the
     */
    public int updateSychInterval(String useremail,String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SyncInterval",interval);

        return db.update("UserSettings",values, "Email =?",
                new String[]{String.valueOf(useremail)});
    }

    /**
     * Method for updating user notification sound setting
     * @param useremail users email that wants to change the setting
     * @param notifSound the setting user wants to change to - true/false
     * @return and update the
     */
    public int updateNotifSound(String useremail,String notifSound){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (notifSound.compareTo("true")==0) {
           notifSound = "t";
        }else{
            notifSound = "f";
        }
        values.put("NotifSound",notifSound);
        return db.update("UserSettings",values, "Email =?",
                new String[]{String.valueOf(useremail)});
    }

    /**
     * Method for updating user notification vibration setting
     * @param useremail users email that wants to change the setting
     * @param notifVibration the setting user wants to change to - true/false
     * @return and update the
     */
    public int updateNotifVibration(String useremail,String notifVibration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (notifVibration.compareTo("true")==0) {
            notifVibration = "t";
        }else{
            notifVibration = "f";
        }
        Log.d("debug", "before input to database " + notifVibration);
        values.put("NotifVibration",notifVibration);

        return db.update("UserSettings",values, "Email =?",
                new String[]{String.valueOf(useremail)});
    }
}
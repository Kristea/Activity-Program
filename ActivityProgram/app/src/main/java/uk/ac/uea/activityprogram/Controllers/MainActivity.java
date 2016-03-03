package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.ParsingStrategy;
import uk.ac.uea.framework.implementation.Event;
import uk.ac.uea.framework.implementation.XMLStrategy_ReadEvents;

/**
 * The main activity that is seen when user starts the app
 * Manage the loading of the events from an online storage location and read them in, store in an arraylist.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private Button login;
    private Button register;
    private ArrayList<Event> allEvents;

    /**
     * What happens  when the activity is created
     * -initialise login and register buttons
     * -get the event links and read the events
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        String [] links = {
                "https://bitbucket.org/kristea/se2-app-files/raw/5d1ec55e94996762a7529e38cbaa6ce80086c5c0/cmpevents.xml",
                "https://bitbucket.org/kristea/se2-app-files/raw/5d1ec55e94996762a7529e38cbaa6ce80086c5c0/bisevents.xml",
                "https://bitbucket.org/kristea/se2-app-files/raw/5d1ec55e94996762a7529e38cbaa6ce80086c5c0/artevents.xml"};

        allEvents = new ArrayList<Event>();
        new ReadEvents().execute(links); //calls do in background
    }


    @Override
    /**
     * Called when a view/button has been clicked
     * If user clicks login - go to login page,if register go to register page
     *
     * @param v the view that was clicked
     */
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.login:
                Intent logintent = new Intent(MainActivity.this, Login.class);
                startActivity(logintent);

                break;
            case R.id.register:
                Intent regintent = new Intent(MainActivity.this, Register.class);
                startActivity(regintent);
                break;
        }
    }

    /**
     *
     * This inner class extends the AsyncTask class to allow it to run in the background, in a
     * separate thread in order for the application to perform a network connection.
     */
    private class ReadEvents extends AsyncTask<String, Void, String> {
        /**
         * Performs all of the network connections in a background thread.
         * Creates an instance of the XMLStrategy_ReadEvents to parse the
         * read XML into various Event object to then be displayed.
         * @param urls  list of urls to connect to event xml files to load the events
         * @return  a String; in this case a message "done" to say the operation was complete
         */
        @Override
        protected String doInBackground(String... urls) {
            //Perform the network connection
            URL url = null;
            for(int i = 0; i < urls.length; i++) {
                try {
                    url = new URL(urls[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(10 * 1000);
                    connection.setConnectTimeout(10 * 1000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    //get the response code & log it to the console
                    int response = connection.getResponseCode();
                    Log.d("debug", "The response is: " + response);
                    //create an instance of RSSStrategy to pass the output from the url
                    ParsingStrategy parser = new XMLStrategy_ReadEvents();
                    //output from url passed to the parse() method of the RSSStrategy in the form of an InputStream
                    //parse() method returns a list of Feed objects
                    List<Event> tmpEvents = parser.parse(connection.getInputStream());
                    allEvents.addAll(tmpEvents);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "done";
        }

        /**
         * This method is executed by doInBackground() after it has completed.
         * @param e the execution string
         */
        @Override
        protected void onPostExecute(String e) {
            Log.d("debug", e);

            AppDBHandler dbhandler = AppDBHandler.getInstance(getApplicationContext());
            //check that user table for events exists, if not create one

            boolean doesTableExist = dbhandler.checkTableExists("UserEvents");
            if(doesTableExist == false){
                dbhandler.createNewTables("UserEvents");
            }else{
                Log.d("debug", "Table 'UserEvents' already exists.");
            }

            doesTableExist = dbhandler.checkTableExists("UserSettings");
            if(doesTableExist == false){
                dbhandler.createNewTables("UserSettings");
            }else{
                Log.d("debug", "Table 'UserSettings' already exists.");
            }

            doesTableExist = dbhandler.checkTableExists("Event");
            if(doesTableExist == false){
                dbhandler.createNewTables("Event");
            }else{
                Log.d("debug", "Table 'Event' already exists.");
            }


            //calls insert or update event -will be affected by synch interval
            for(Event ev: allEvents){
                dbhandler.insertEvent(ev.getId(), ev.getName(), ev.getDescription(), ev.getDate(), ev.getTime());
            }


        }
    }

}

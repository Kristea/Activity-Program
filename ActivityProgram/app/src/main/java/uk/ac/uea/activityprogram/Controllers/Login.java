package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.ac.uea.activityprogram.Models.AppUser;
import uk.ac.uea.activityprogram.Models.Settings;
import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.Originator;
import uk.ac.uea.framework.implementation.CareTaker;
import uk.ac.uea.framework.implementation.Event;
import uk.ac.uea.framework.implementation.User;


/**
 *
 * The login class for the app,
 * Manages what needs to be imported from the database for a user and checks if a user exists.
 *
 */
public class Login extends Activity implements View.OnClickListener {

    private Button loginBtn;
    private Button registerBtn;

    /**
     * What happens on creation of the app
     * Initialise the login and register buttons
     *
     * @param savedInstanceState the saved instance
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        //listen for clicks to buttons
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    /**
     * What happens when a button is clicked
     * Uses the database handler to find if user exists and checks their login details,
     * logging the user in i they have an account
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        AppDBHandler dbHandler = AppDBHandler.getInstance(this);

        switch(v.getId()){
            case R.id.loginBtn:

                EditText email_login = (EditText) findViewById(R.id.emailInput);
                String emailStr_login = email_login.getText().toString();

                EditText password_login = (EditText) findViewById(R.id.passwordInput);
                String passwordStr_login = password_login.getText().toString();

                //check user details
                boolean temp = dbHandler.checkUser(emailStr_login, passwordStr_login);

                List<User> urs = dbHandler.getAllUsers();
                for(User u :urs){
                    Log.d("debug","--User email " + u.getEmail());
                    Log.d("debug", "--User password  " + u.getPassword());
                }

                //if user details true
                if(temp == true){
                    List<String> userEventIds  = new ArrayList<String>();
                    List<String> userEvents = dbHandler.getUserEvents();

                    String email, id;
                    for(String s :userEvents){
                       Log.d("debug","user events" + s);;
                        email = s.substring(0, s.indexOf("#"));
                        id = s.substring(s.indexOf("#")+1);
                        if(email.compareTo(emailStr_login) == 0){
                            userEventIds.add(id);
                        }
                    }

                    ArrayList<Event> allEvents = dbHandler.getAllEvents();
                    ArrayList<Event> subscribedEvents  = new ArrayList<Event>();

                    Log.d("debug","user events " + userEventIds);
                    for(String s:userEventIds){
                        for(Event e: allEvents){
                            //if id of subscribed events matches current event id
                            if(s.compareTo(e.getId())==0){
                                subscribedEvents.add(e);
                            }
                        }
                    }

                    ArrayList<String> allSettings = dbHandler.getAllSettings();
                    Settings settings = new Settings();
                    //if user email matches settings email
                    for(String s : allSettings){
                        if(s.substring(0, s.indexOf("#")).compareTo(emailStr_login)==0){
                            settings.setActivityProgram(s.substring(s.indexOf("#")+1, s.indexOf("_")));
                            settings.setSynchInterval(Integer.parseInt(s.substring(s.indexOf("_")+1, s.indexOf("/"))));
                            settings.setNotifSound(Boolean.parseBoolean(s.substring(s.indexOf("/")+1, s.indexOf("~"))));
                            settings.setNotifVibration(Boolean.parseBoolean(s.substring(s.indexOf("~")+1)));
                            break;
                        }
                    }
                    //get user
                    User userType = dbHandler.getUser(emailStr_login);
                    AppUser user = new AppUser();

                    user.setName(userType.getName());
                    user.setPassword(userType.getPassword());
                    user.setEmail(userType.getEmail());
                    user.setSettings(settings);
                    user.setSubscribedEvents(subscribedEvents);

                    Settings defaultSettings = new Settings("cmp", 30, true, true);
                    ActivityMemento defaultState = new ActivityMemento(defaultSettings);
                    Originator originator = new Originator();
                    CareTaker caretaker = new CareTaker();
                    user.setOriginator(originator);
                    user.setCaretaker(caretaker);
                    user.setMementoState(defaultState);

                    intent = new Intent (Login.this, HomeScreen.class );
                    intent.putExtra("Events", allEvents);
                    intent.putExtra("User", user);
                    startActivityForResult(intent,1);

                }else{
                    Toast wrongPass = Toast.makeText(Login.this, "Email and password don't match", Toast.LENGTH_SHORT);
                    wrongPass.show();
                }
                dbHandler.close();
                break;

            case R.id.registerBtn:
                intent = new Intent(Login.this, Register.class);
                startActivity(intent);
        }
    }

}

package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.User;

/**
 * Registration class for the app.
 * Allows the user to register and creates a new usser object.
 */
public class Register extends Activity implements View.OnClickListener {

    private Button register;

    /**
     * what happens when activity is created
     *
     * initialise the register button
     *
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.registerBtn);
        register.setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *If user enters details and clicks register then create a new user with default settings  and add them to the database and take them to the login page
     *
     * If user clicks the login button take them to the login page
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        AppDBHandler dbHandler = AppDBHandler.getInstance(this);
        //DatabaseHandler dbHandler = new DatabaseHandler(this);

        switch(v.getId()){
            case R.id.registerBtn:
                EditText name = (EditText)findViewById(R.id.nameInput_register);
                EditText email = (EditText)findViewById(R.id.emailInput_register);
                EditText password = (EditText)findViewById(R.id.passwordInput_register);

                String nameStr = name.getText().toString();
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();

                List<User> usrs = dbHandler.getAllUsers();
                boolean userExists = false;
                for(User u:usrs) {
                    if(u.getEmail().compareTo(emailStr)==0){
                        Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                        userExists = true;
                        break;
                    }
                }

                if(!userExists) {
                    if (nameStr.compareTo("") == 0 || emailStr.compareTo("") == 0 || passwordStr.compareTo("") == 0) {
                        Toast.makeText(Register.this, "Please fll out all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = new User(emailStr, nameStr, passwordStr);

                        //create user settings with default values
                        String school = "Computing";
                        String syncInterval = "30";
                        String notifSound = "true";
                        String notifVib = "true";
                        dbHandler.insertUserSetting(emailStr, school, syncInterval, notifSound, notifVib);
                        dbHandler.insertUser(user);

                        List<User> us = dbHandler.getAllUsers();
                        for (User u : us) {
                            Log.d("debug", "user email " + u.getEmail());
                            Log.d("debug", "user password " + u.getPassword());
                        }

                        intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        break;

                    }
                }
        }

        dbHandler.close();
    }

}

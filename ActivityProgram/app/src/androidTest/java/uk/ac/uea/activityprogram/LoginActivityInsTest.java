package uk.ac.uea.activityprogram;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import uk.ac.uea.activityprogram.Controllers.Login;
import uk.ac.uea.activityprogram.Controllers.Register;

/**
 * Created by Kristiana on 13/01/2016.
 * Test the login activity
 */
public class LoginActivityInsTest extends ActivityInstrumentationTestCase2<Login> {

    private Login testLogin;
    private TextView emailText;
    private TextView passwordText;
    private Button testButton1;
    private Button testButton2;

    public LoginActivityInsTest() {
        super(Login.class);

    }

    /**
     * Set up tests
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        testLogin = getActivity();
        emailText = (TextView) testLogin.findViewById(R.id.emailInput);
        passwordText = (TextView) testLogin.findViewById(R.id.passwordInput);
        testButton1 = (Button) testLogin.findViewById(R.id.loginBtn);
        testButton2 = (Button) testLogin.findViewById(R.id.registerBtn);

    }


    /**
     * Method to test if the buttons open the next activities they are supposed to
     */
    public void testOpenNextActivity() {
        Instrumentation.ActivityMonitor activityMonitorRegister = getInstrumentation().addMonitor(Register.class.getName(), null, false);

        testLogin = getActivity();
        //testButton1 = (Button) testMain.findViewById(R.id.login);
        testLogin.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                testButton1.performClick();
                testButton2.performClick();
            }
        });

        //Watch for the timeout
        //example values 5000 if in ms, or 5 if it's in seconds.
        Register register = (Register) getInstrumentation().waitForMonitorWithTimeout(activityMonitorRegister, 5000);
        // next activity is opened and captured.
        assertNotNull(register);
        register.finish();

    }
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}

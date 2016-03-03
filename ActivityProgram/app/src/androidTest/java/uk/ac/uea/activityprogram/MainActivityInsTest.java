package uk.ac.uea.activityprogram;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import uk.ac.uea.activityprogram.Controllers.Login;
import uk.ac.uea.activityprogram.Controllers.MainActivity;
import uk.ac.uea.activityprogram.Controllers.Register;

/**
 * Instrumentation unit test for testing main activity
 */
public class MainActivityInsTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity testMain;
    private TextView mFirstTestText;
    private Button testButton1;
    private Button testButton2;

    public MainActivityInsTest() {
        super(MainActivity.class);
    }

    /**
     *Test what happens when activity is set upj
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testMain = getActivity();
        testButton1 = (Button) testMain.findViewById(R.id.login);
        testButton2 = (Button) testMain.findViewById(R.id.register);
    }

    /**
     * Method to test if the buttons open the next activities they are supposed to
     */
    public void testOpenNextActivity() {
        Instrumentation.ActivityMonitor activityMonitorLogin = getInstrumentation().addMonitor(Login.class.getName(), null, false);
        Instrumentation.ActivityMonitor activityMonitorRegister = getInstrumentation().addMonitor(Register.class.getName(), null, false);

        testMain = getActivity();
        //testButton1 = (Button) testMain.findViewById(R.id.login);
        testMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                testButton1.performClick();
                testButton2.performClick();
            }
        });

        //Watch for the timeout
        //example values 5000 if in ms, or 5 if it's in seconds.
        Login login = (Login) getInstrumentation().waitForMonitorWithTimeout(activityMonitorLogin, 5000);
        Register register = (Register) getInstrumentation().waitForMonitorWithTimeout(activityMonitorRegister, 5000);
        // next activity is opened and captured.
        assertNotNull(login);
        assertNotNull(register);
        login.finish();
        register.finish();

    }

    /**
     * Tear down method
     */
    @Override
    protected void tearDown() throws Exception {
      super.tearDown();

    }
}

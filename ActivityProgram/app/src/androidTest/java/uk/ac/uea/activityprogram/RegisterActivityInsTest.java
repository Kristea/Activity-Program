package uk.ac.uea.activityprogram;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.uea.activityprogram.Controllers.Register;

/**
 * Created by Kristiana on 13/01/2016.
 */
public class RegisterActivityInsTest extends ActivityInstrumentationTestCase2<Register> {

    private Register testRegister;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private String email,name,password;
    private Button testButton1;


    /**
     * construct junit class
     */
    public RegisterActivityInsTest() {
        super(Register.class);
    }

    /**
     * Setup method
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        testRegister = getActivity();
        emailText = (EditText) testRegister.findViewById(R.id.emailInput_register);
        passwordText = (EditText) testRegister.findViewById(R.id.passwordInput_register);
        nameText = (EditText) testRegister.findViewById(R.id.nameInput_register);
        testButton1 = (Button) testRegister.findViewById(R.id.registerBtn);

        email ="email@mail.com";
        password ="password";
        name = "name";
    }
    /**
     * Method to test if the buttons open the next activities they are supposed to
     */
    public void testText() {
    //now that it will pass the test case without Exception..
    // but if you want to compare text then you may have to set it in xml and then compare it.. doing it in onCreate() of Login may also work..
        assertEquals("",emailText.getText().toString());
        assertEquals("", passwordText.getText().toString());
        assertEquals("", nameText.getText().toString());
    }

    /**
     * testing the preconditions
     */
    public void testPreconditions() {
        assertNotNull(emailText);
        assertNotNull(passwordText);
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

}




package uk.ac.uea.activityprogram;

import junit.framework.TestCase;

/**
 * Created by Kristiana on 13/01/2016.
 */
public class RegisterActivityTest extends TestCase {
    private String name;
    private String email;
    private String password;

    protected void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Validate that the name is a string
     */
    public void testValidateName() {
        assertNotNull(R.id.nameInput_register);
    }

    /**
     * validate that the email is a string
     */
    public void testValidateEmailString() {
        assertNotNull(R.id.emailInput_register);
    }

    /**
     * validate that the password is a string
     */
    public void testValidatePassword() {
        assertNotNull(R.id.passwordInput_register);
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

}

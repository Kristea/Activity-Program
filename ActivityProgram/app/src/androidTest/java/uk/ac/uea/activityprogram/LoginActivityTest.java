package uk.ac.uea.activityprogram;

import junit.framework.TestCase;

/**
 * Created by Kristiana on 13/01/2016.
 */
public class LoginActivityTest extends TestCase {

    private String email;
    private String password;

    protected void setUp() throws Exception {
        super.setUp();
        email = "email@mail.com";
        password = "password";

    }

    public void testValidateEmailString() {
        assertEquals(email,R.id.emailInput);
    }
    public void testValidatePassword() {
        assertEquals(password,R.id.passwordInput);
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }
}

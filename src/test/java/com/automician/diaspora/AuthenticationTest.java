package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Users;
import org.junit.Test;
import com.automician.pages.Diaspora;
import com.automician.pages.Menu;

import static com.automician.core.Gherkin.*;

public class AuthenticationTest extends BaseTest {

    @Test
    public void testSignInAndLogOut() {
        WHEN("User is signed in");
        Diaspora.signInAs(Users.Pod1.ana);

        THEN("User's account is opened");
        Menu.assertLoggedUser(Users.Pod1.ana);

        WHEN("User is logged out");
        Diaspora.logOut();

        THEN("User's account is closed");
        Menu.assertLoggedOut();

    }

}

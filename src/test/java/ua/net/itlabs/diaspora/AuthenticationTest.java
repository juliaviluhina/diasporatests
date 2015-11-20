package ua.net.itlabs.diaspora;

import org.junit.Test;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;
import steps.Relation;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.FRIENDS;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class AuthenticationTest extends BaseTest {

    @Test
    public void testSignInAndLogOut() {
        WHEN("User is signed in");
        Diaspora.signInAs(Pod1.ana);

        THEN("User's account is opened");
        NavBar.assertLoggedUser(Pod1.ana);

        WHEN("User is logged out");
        Diaspora.logOut();

        THEN("User's account is closed");
        Menu.assertLoggedOut();
    }


}

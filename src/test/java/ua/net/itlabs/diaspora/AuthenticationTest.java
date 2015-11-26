package ua.net.itlabs.diaspora;

import concurrency.UserThreadManager;
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

    @Test
    public void test() {
//        Diaspora.ensureSignInAs(Pod1.ana);
//        Menu.openStream();
//        Diaspora.ensureSignInAs(Pod1.rob);
////        Menu.openStream();
//        Diaspora.ensureSignInAs(Pod1.ana);
////        Menu.openStream();


        UserThreadManager userThreadManager = new UserThreadManager();

        userThreadManager.ensureSinIn(Pod1.ana);
        Menu.openStream();
        userThreadManager.ensureLogOut();

        userThreadManager.ensureSinIn(Pod1.eve);
        Menu.openStream();
        userThreadManager.ensureLogOut();

        userThreadManager.ensureSinIn(Pod1.ana);
        Menu.openStream();
        userThreadManager.ensureLogOut();

    }


}

package ua.net.itlabs;

import datastructures.PodUser;
import org.junit.After;
import org.junit.BeforeClass;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;
import pages.Tags;

import java.io.IOException;

public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(Users.ANA);
            clearUserData(Users.BOB);
        }
    }

    @After
    public void tearDown() throws IOException {
        Menu.logOut();
    }

    public static void clearUserData(PodUser user) {
        Diaspora.signInAs(user);
        NavBar.expandTags();
        Tags.deleteAll();
        Menu.logOut();
    }

}

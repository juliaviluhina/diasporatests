package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import datastructures.DiasporaAspect;
import datastructures.PodUser;
import org.junit.After;
import org.junit.BeforeClass;
import pages.*;
import ua.net.itlabs.testDatas.DiasporaAspects;
import ua.net.itlabs.testDatas.Users;

import java.io.IOException;

public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 15000;
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(Users.ANA);
            clearUserData(Users.BOB);
            clearUserData(Users.ROB);
        }
    }

    @After
    public void tearDown() throws IOException {
        Menu.ensureNewSignIn();
    }

    public static void clearUserData(PodUser user) {
        Diaspora.signInAs(user);
        NavBar.openTags();
        Tags.deleteAll();

        NavBar.openMyActivity();
        Feed.deleteAllPosts(user);

        Menu.logOut();
    }

}

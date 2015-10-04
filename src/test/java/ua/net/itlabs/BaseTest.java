package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import datastructures.PodUser;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import pages.*;

import java.io.IOException;

import static core.helpers.UniqueDataHelper.clearThe;
import static ua.net.itlabs.testDatas.Users.*;

public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 30000;
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(ANA);
            clearUserData(BOB);
            clearUserData(ROB);
            clearUserData(SAM);
            clearUserData(EVE);
        }
    }

    @Before
    public void ActionsBeforeTest() {
        clearThe();
    }

    @After
    public void tearDown() throws IOException {
        Menu.ensureNewSignIn();
    }

    public static void clearUserData(PodUser user) {
        Diaspora.signInAs(user);

        NavBar.openStream();
        Feed.deleteAllPosts(user);

        NavBar.openTags();
        Tags.deleteAll();

        Menu.openContacts();
        Contacts.deleteAllUserAspects();

        Menu.logOut();
    }

}

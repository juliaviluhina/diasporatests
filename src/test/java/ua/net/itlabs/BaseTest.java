package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import datastructures.PodUser;
import org.junit.After;
import org.junit.BeforeClass;
import pages.Diaspora;
import pages.Menu;
import pages.Tags;
import ru.yandex.qatools.allure.annotations.Step;

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
        Tags.deleteTags();
        Menu.logOut();
    }

}

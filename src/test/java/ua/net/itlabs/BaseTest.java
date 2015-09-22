package ua.net.itlabs;

import datastructures.PodUser;
import org.junit.After;
import pages.Diaspora;
import pages.Menu;
import pages.Tags;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.IOException;

public class BaseTest {

    {
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            clearUserData(Users.ANA);
            clearUserData(Users.BOB);
        }
    }

    @After
    public void tearDown() throws IOException {
        Menu.logOut();
    }

    @Step
    public void clearUserData(PodUser user) {
        Diaspora.signInAs(user);
        Tags.expandTags();
        Tags.deleteTags();
        Menu.logOut();
    }

}

package core.steps;

import datastructures.PodUser;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;

public class Scenarios {

    @Step
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

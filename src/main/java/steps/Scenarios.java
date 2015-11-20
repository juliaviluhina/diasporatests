package steps;

import datastructures.PodUser;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;

import static pages.Aspects.*;

public class Scenarios {

    @Step
    public static void clearUserData(PodUser user) {
        Diaspora.ensureSignInAs(user);

        NavBar.openStream();
        Feed.deleteAllPosts(user);

        NavBar.openTags();
        Tags.deleteAll();

        Menu.openContacts();
        Contacts.deleteAllUserAspects();

        Menu.ensureLogOut();
    }

    @Step
    public static void waitStreamOpening() {
        //this spike wait is needed because several actions do not run correctly
        //when stream is loaded
        //user menu can be opened only if stream is opened - indirect check
        Menu.openMenu();
        Menu.search.click();//to close usermenu
    }

}

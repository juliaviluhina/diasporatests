package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import datastructures.PodUser;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;
import ua.net.itlabs.testDatas.Users;

import java.io.IOException;

import static core.helpers.UniqueDataHelper.clearThe;
import static ua.net.itlabs.testDatas.Users.*;

public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 20000;
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

    @Step
    public void setupLinksFor(PodUser user, PodUser linkedUser, String diasporaAspect, String followedTag, PodUser... unlinkedUsers) {
        //user relation setup
        Diaspora.signInAs(user);
        Menu.assertLoggedUser(user);
        //user have diasporaAspect relation with linkedUser
        Menu.search(linkedUser.fullName);
        Feed.assertPerson(linkedUser.fullName);
        Contacts.ensureAspectsForContact(diasporaAspect);
        //user have not any relation with unlinkedUser1
        for (PodUser unlinkedUser : unlinkedUsers) {
            Menu.search(unlinkedUser.fullName);
            Feed.assertPerson(unlinkedUser.fullName);
            Contacts.ensureNoAspectsForContact();
            Contacts.assertAspectsAreNotUsed();
        }
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(followedTag);
        Tags.assertExist(followedTag);
        Menu.logOut();
    }

    @Step
    public void setupLinksFor(PodUser user, String followedTag, PodUser... unlinkedUsers) {
        //user relation setup
        Diaspora.signInAs(user);
        Menu.assertLoggedUser(user);
        //user have not any relation with unlinkedUser1
        for (PodUser unlinkedUser : unlinkedUsers) {
            Menu.search(unlinkedUser.fullName);
            Feed.assertPerson(unlinkedUser.fullName);
            Contacts.ensureNoAspectsForContact();
            Contacts.assertAspectsAreNotUsed();
        }
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(followedTag);
        Tags.assertExist(followedTag);
        Menu.logOut();
    }

    @Step
    public void setupLinksFor(PodUser user, PodUser linkedUser, String followedTag, PodUser unlinkedUser, String... diasporaAspects) {
        //user relation setup
        Diaspora.signInAs(user);
        Menu.assertLoggedUser(user);
        //user have diasporaAspect relation with linkedUser
        Menu.search(linkedUser.fullName);
        Feed.assertPerson(linkedUser.fullName);
        Contacts.ensureAspectsForContact(diasporaAspects);
        //user have not any relation with unlinkedUser1
        Menu.search(unlinkedUser.fullName);
        Feed.assertPerson(unlinkedUser.fullName);
        Contacts.ensureNoAspectsForContact();
        Contacts.assertAspectsAreNotUsed();
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(followedTag);
        Tags.assertExist(followedTag);
        Menu.logOut();
    }

}

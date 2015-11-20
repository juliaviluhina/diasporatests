package ua.net.itlabs.diaspora;

import datastructures.PodUser;
import steps.Relation;
import org.junit.Test;
import pages.Contact;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class IgnoreModeTest extends BaseTest {

    @Test
    public void testIgnoreUserInStream() {

        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();
        clearUniqueData();

        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPost(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        WHEN("Author of post is ignored");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.ignoreAuthorOfPost(Pod1.ana, the("Ana for public"));

        THEN("Posts of ignored author is not shown in stream");
        Feed.assertNoPost(Pod1.ana, the("Ana for public"));
        Menu.ensureLogOut();

        EXPECT("Posts of ignored user is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for public"));
        Menu.ensureLogOut();
    }

    @Test
    public void testStopIgnoreUserInContactSite() {

        GIVEN("Public post is added, author of post is ignored by user");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPost(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Contact.ensureNoIgnoreMode();
        Feed.ignoreAuthorOfPost(Pod1.ana, the("Ana for public"));

        EXPECT("Post of author is not available to user");
        Feed.assertNoPost(Pod1.ana, the("Ana for public"));

        WHEN("Ignoring is stopped");
        Contact.stopIgnoring();

        THEN("Post of author is available to user");
        Feed.assertPost(Pod1.ana, the("Ana for public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testStartIgnoreUserInContactSite() {

        GIVEN("Public post is added, author of post is not ignored by user");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPost(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Contact.ensureNoIgnoreMode();

        EXPECT("Post of author is available to user");
        Feed.assertPost(Pod1.ana, the("Ana for public"));

        WHEN("Ignoring is started");
        Contact.startIgnoring();

        EXPECT("Post of author is not available to user");
        Feed.assertNoPost(Pod1.ana, the("Ana for public"));
        Menu.ensureLogOut();

    }

}

package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.Test;
import pages.Contact;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.FRIENDS;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class AdditionalOperationsTest extends BaseTest {

    @Test
    public void testHidePosts() {
        GIVEN("Setup mutual relation between users, add limited in aspect post");
        clearUniqueData();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, FRIENDS).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, FRIENDS).toUser(Pod1.rob, FRIENDS).doNotLogOut().ensure();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertPost(Pod1.ana, the("Ana for friends"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        EXPECT("Hidden post is not shown in stream");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.hidePost(Pod1.ana, the("Ana for friends"));
        Feed.assertNoPost(Pod1.ana, the("Ana for friends"));

        EXPECT("Hidden post is not shown in contact stream");
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Ana for friends"));
        Menu.ensureLogOut();

        EXPECT("Hidden post is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for friends"));
        Menu.ensureLogOut();

        EXPECT("After new signing in hidden post is not shown");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.assertNoPost(Pod1.ana, the("Ana for friends"));
        Menu.ensureLogOut();

    }

    @Test
    public void testIgnoreUserInStream() {
        GIVEN("Setup mutual relation between users, add public post");
        clearUniqueData();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, FRIENDS).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, FRIENDS).doNotLogOut().ensure();
        Menu.openStream();
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

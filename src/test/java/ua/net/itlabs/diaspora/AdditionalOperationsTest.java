package ua.net.itlabs.diaspora;

import org.junit.experimental.categories.Category;
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
import static ua.net.itlabs.testDatas.Phrases.POST_FOR_FRIENDS;
import static ua.net.itlabs.testDatas.Phrases.POST_FOR_HIDING;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class AdditionalOperationsTest extends BaseTest {

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testHidePosts() {
        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Post from author exists and is not hidden");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPostIsNotHidden(Pod1.ana, POST_FOR_HIDING);

        WHEN("Post of author is hidden by user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.hidePost(Pod1.ana, POST_FOR_HIDING);
        THEN("Author's post is not shown in user's stream");
        Feed.assertNoPost(Pod1.ana, POST_FOR_HIDING);

        EXPECT("Hidden post is not shown in author's contact stream");
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, POST_FOR_HIDING);

        EXPECT("Hidden post is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertPost(Pod1.ana, POST_FOR_HIDING);

        EXPECT("After new signing in hidden post is not shown in user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, POST_FOR_HIDING);

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

        WHEN("Author of post is ignored");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.ignoreAuthorOfPost(Pod1.ana, the("Ana for public"));

        THEN("Posts of ignored author is not shown in stream");
        Feed.assertNoPost(Pod1.ana, the("Ana for public"));

        EXPECT("Posts of ignored user is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for public"));
    }

    @Test
    public void testStopIgnoreUserInContactSite() {
        GIVEN("Public post is added, author of post is ignored by user");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPost(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
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
    }

    @Test
    public void testStartIgnoreUserInContactSite() {
        GIVEN("Public post is added, author of post is not ignored by user");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPost(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Contact.ensureNoIgnoreMode();

        EXPECT("Post of author is available to user");
        Feed.assertPost(Pod1.ana, the("Ana for public"));

        WHEN("Ignoring is started");
        Contact.startIgnoring();

        EXPECT("Post of author is not available to user");
        Feed.assertNoPost(Pod1.ana, the("Ana for public"));

    }

}

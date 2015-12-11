package ua.net.itlabs.diaspora;

import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.Test;
import pages.Contact;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.FRIENDS;
import static ua.net.itlabs.testDatas.Phrases.POST_FOR_HIDING;
import static ua.net.itlabs.testDatas.Phrases.PUBLIC_POST;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

@Category(ua.net.itlabs.categories.Smoke.class)
public class AdditionalOperationsTest extends BaseTest {

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

        GIVEN("Setup mutual relation between users");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, FRIENDS).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, FRIENDS).toUser(Pod1.ana, FRIENDS).doNotLogOut().ensure();

        GIVEN("Public post from author exists");
        Menu.openStream();
        Feed.ensurePublicPost(Pod2.bob, PUBLIC_POST);

        WHEN("Author of post is ignored");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.ignoreAuthorOfPost(Pod2.bob, PUBLIC_POST);

        THEN("Posts of ignored author is not shown in stream");
        Feed.assertNoPost(Pod2.bob, PUBLIC_POST);

        EXPECT("Posts of ignored user is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.search(Pod2.bob.fullName);
        Feed.assertPost(Pod2.bob, PUBLIC_POST);

    }

    @Test
    public void testStopIgnoreUserInContactSite() {

        GIVEN("Public post is added, author of post is ignored by user");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.ensurePublicPost(Pod2.bob, PUBLIC_POST);
        Diaspora.ensureSignInAs(Pod2.sam);
        Menu.search(Pod2.bob.fullName);
        Contact.ensureIgnoreMode();

        EXPECT("Post of author is not available to user");
        Feed.assertNoPost(Pod2.bob, PUBLIC_POST);

        WHEN("Ignoring is stopped");
        Contact.stopIgnoring();

        THEN("Post of author is available to user");
        Feed.assertPost(Pod2.bob, PUBLIC_POST);

    }

    @Test
    public void testStartIgnoreUserInContactSite() {

        GIVEN("Public post is added, author of post is not ignored by user");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.ensurePublicPost(Pod2.bob, PUBLIC_POST);
        Diaspora.ensureSignInAs(Pod2.sam);
        Menu.search(Pod2.bob.fullName);
        Contact.ensureNoIgnoreMode();

        EXPECT("Post of author is available to user");
        Feed.assertPost(Pod2.bob, PUBLIC_POST);

        WHEN("Ignoring is started");
        Contact.startIgnoring();

        EXPECT("Post of author is not available to user");
        Feed.assertNoPost(Pod2.bob, PUBLIC_POST);

    }

}

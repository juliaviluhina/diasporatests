package ua.net.itlabs.diaspora;

import core.steps.Relation;
import org.junit.Before;
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

public class AdditionalOperationsTest extends BaseTest {

    @Before
    public void setupForTest() {
        clearUniqueData();
    }

    @Test
    public void testHidePosts() {
        //GIVEN - setup mutual relation between users, add limited in aspect post
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, FRIENDS).build();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).build();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, FRIENDS).toUser(Pod1.rob, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertPostFrom(Pod1.ana, the("Ana for friends"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //hide post and check - post is not shown in stream
        Diaspora.signInAs(Pod1.eve);
        Feed.hidePost(Pod1.ana, the("Ana for friends"));
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for friends"));

        //check - in contact site hidden post is not shown
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        //check - in stream of another user this post is shown
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        //check - after new sign in hidden post is not shown
        Diaspora.signInAs(Pod1.eve);
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

    }

    @Test
    public void testIgnoreUserInStream() {
        //GIVEN - setup mutual relation between users, add public post
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, FRIENDS).build();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).build();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //ignore author of post
        Diaspora.signInAs(Pod1.eve);
        Feed.ignoreAuthorOfPost(Pod1.ana, the("Ana for public"));

        //check - posts of ignored author is not shown in stream
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for public"));
        Menu.logOut();

        //check - posts of ignored user is shown in stream of another user
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));
        Menu.logOut();
    }

    @Test
    public void testStopIgnoreUserInContactSite() {
        //GIVEN - public post, ignore author of post
        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Contact.ensureNoIgnoreMode();
        Feed.ignoreAuthorOfPost(Pod1.ana, the("Ana for public"));

        //check - in contact site post is not shown
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for public"));

        //stop ignoring
        Contact.stopIgnoring();

        //check - in contact site post is shown
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));
        Menu.logOut();
    }

    @Test
    public void testStartIgnoreUserInContactSite() {
        //GIVEN - public post, ensure no ignore mode
        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPost(the("Ana for public"));
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Contact.ensureNoIgnoreMode();

        //check - in contact site post is shown
        Feed.assertPostFrom(Pod1.ana, the("Ana for public"));

        //stop ignoring
        Contact.startIgnoring();

        //check - in contact site post is shown
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for public"));
        Menu.logOut();
    }

}

package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Contact;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.categories.AdditionalOperations;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.FAMILY;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;


@Category(AdditionalOperations.class)
public class DiasporaAdditionalOperationsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout
        setTimeOut();
    }

    @Before
    public void setupForTest() {
        //clear information about unique values
        clearThe();
    }

    @Test
    public void testHidePosts() {
        //GIVEN - setup mutual relation between users, add limited in aspect post
        Relation.forUser(EVE_P1).toUser(RON_P1, FRIENDS).build();
        Relation.forUser(ROB_P1).toUser(RON_P1, FRIENDS).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FRIENDS).toUser(ROB_P1, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for friends"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for friends"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //hide post and check - post is not shown in stream
        Diaspora.signInAs(EVE_P1);
        Feed.hidePost(RON_P1, the("Ron for friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));

        //check - in contact site hidden post is not shown
        Menu.search(RON_P1.fullName);
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Menu.logOut();

        //check - in stream of another user this post is shown
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(RON_P1, the("Ron for friends"));
        Menu.logOut();

        //check - after new sign in hidden post is not shown
        Diaspora.signInAs(EVE_P1);
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Menu.logOut();

    }

    @Test
    public void testIgnoreUserInStream() {
        //GIVEN - setup mutual relation between users, add public post
        Relation.forUser(EVE_P1).toUser(RON_P1, FRIENDS).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("Ron for public"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //ignore author of post
        Diaspora.signInAs(EVE_P1);
        Feed.ignoreAuthorOfPost(RON_P1, the("Ron for public"));

        //check - posts of ignored author is not shown in stream
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));
        Menu.logOut();

        //check - posts of ignored user is shown in stream of another user
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(RON_P1, the("Ron for public"));
        Menu.logOut();
    }

    @Test
    public void testStopIgnoreUserInContactSite() {
        //GIVEN - public post, ignore author of post
        Diaspora.signInAs(RON_P1);
        Feed.addPublicPost(the("Ron for public"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Diaspora.signInAs(EVE_P1);
        Menu.search(RON_P1.fullName);
        Contact.ensureNoIgnoreMode();
        Feed.ignoreAuthorOfPost(RON_P1, the("Ron for public"));

        //check - in contact site post is not shown
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));

        //stop ignoring
        Contact.stopIgnoring();

        //check - in contact site post is shown
        Feed.assertPostFrom(RON_P1, the("Ron for public"));
        Menu.logOut();
    }

    @Test
    public void testStartIgnoreUserInContactSite() {
        //GIVEN - public post, ensure no ignore mode
        Diaspora.signInAs(RON_P1);
        Feed.addPublicPost(the("Ron for public"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for public"));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Diaspora.signInAs(EVE_P1);
        Menu.search(RON_P1.fullName);
        Contact.ensureNoIgnoreMode();

        //check - in contact site post is shown
        Feed.assertPostFrom(RON_P1, the("Ron for public"));

        //stop ignoring
        Contact.startIgnoring();

        //check - in contact site post is shown
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));
        Menu.logOut();
    }

}

package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Contact;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.categories.AdditionalOperations;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.FRIENDS;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;


@Category(AdditionalOperations.class)
public class DiasporaAdditionalOperationsTest extends BaseTest{

    @Test
    public void testHidePosts() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(EVE_P1).toUser(RON_P1, FRIENDS).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FRIENDS).toUser(ROB_P1,FRIENDS).doNotLogOut().build();
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
    public void testIgnoreUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(EVE_P1).toUser(RON_P1, FRIENDS).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("Ron for public"));
        Feed.addAspectPost(FRIENDS, the("Ron for friends"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for friends"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //ignore author of post
        Diaspora.signInAs(EVE_P1);
        Feed.ignoreAuthorOfPost(RON_P1, the("Ron for friends"));

        //check - posts of ignored author is not shown in stream
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));

        //check - in contact site hidden post is not shown too
        Menu.search(RON_P1.fullName);
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));

        //stop ignoring
        Contact.stopIgnoring();

        //check - in stream of contact posts is shown
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Feed.assertPostFrom(RON_P1, the("Ron for public"));

        //start ignoring through button in contact site
        Contact.startIgnoring();

        //check - in stream of contact posts is shown
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));
        Menu.logOut();

        //check - after new sign in post of ignored author is not shown
        Diaspora.signInAs(EVE_P1);
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for public"));

        //add post for available earlier aspect
        Feed.addAspectPost(FRIENDS, the("Eve for friends1"));
        Feed.assertNthPostIs(0, EVE_P1, the("Eve for friends1"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - this post is not available to ignored user because of loss links in any aspects
        Diaspora.signInAs(RON_P1);
        Feed.assertNoPostFrom(EVE_P1, the("Eve for friends1"));
        Menu.logOut();

        //renew links in aspect
        Diaspora.signInAs(EVE_P1);
        Menu.search(RON_P1.fullName);
        //Contact.stopIgnoring();
        Contact.ensureAspectsForContact(FRIENDS);

        //new post in aspect
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Eve for friends2"));
        Feed.assertNthPostIs(0,EVE_P1,the("Eve for friends2"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check after stop ignoting
        // - old post is not shown because of loss links in any aspects at the moment of posting
        // - new post is shown
        Diaspora.signInAs(RON_P1);
        Feed.assertNoPostFrom(EVE_P1, the("Eve for friends1"));
        Feed.assertPostFrom(EVE_P1, the("Eve for friends2"));
        Menu.logOut();

    }

}

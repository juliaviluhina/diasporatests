package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;

public class DiasporaFeedTest extends BaseTest{

    @Test
    public void testAccessToPostsFromMutuallyLinkedUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPrivatePost(the("Private post from Ron"));
        Feed.addPublicPost(the("Public post from Ron"));
        Feed.addAllAspectsPost(the("Ron for all aspects"));
        Feed.addAspectPost(WORK, the("Ron for Work - unlinked aspect "));
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Relation.forUser(EVE_P1).toUser(RON_P1, ACQUAINTANCES).doNotLogOut().build();

        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Private post from Ron"));
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
    }

    @Test
    public void testAccessToPostsFromNonMutuallyLinkedUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(EVE_P1).notToUsers(RON_P1).withTags(the("#tag")).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPrivatePost(the("Private post from Ron"));
        Feed.addPublicPost(the("#tag")+the(" Public post from Ron with followed tag"));
        Feed.addPublicPost(the("Public post from Ron"));
        Feed.addAllAspectsPost(the("Ron for all aspects"));
        Feed.addAspectPost(WORK, the("Ron for Work - unlinked aspect "));
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(EVE_P1);
        //in stream post is not shown
        Feed.assertNoPostFrom(RON_P1, the("Private post from Ron"));
        Feed.assertPostFrom(RON_P1, the("#tag") + the(" Public post from Ron with followed tag"));
        Feed.assertNoPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));

        //but post is shown in search stream
        Menu.search(RON_P1.fullName);
        Feed.assertNoPostFrom(RON_P1, the("Private post from Ron"));
        Feed.assertPostFrom(RON_P1, the("#tag") + the(" Public post from Ron with followed tag"));
        Feed.assertPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
    }

    @Test
    public void testStreamConsistManage() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("#tag")+" Public post with followed tag");
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Relation.forUser(EVE_P1).toUser(RON_P1, ACQUAINTANCES).withTags(the("#tag")).doNotLogOut().build();

        //check - in stream posts from linked user is shown
        Menu.openStream();
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(ACQUAINTANCES, RON_P1);

        //check - after deletion contact limited post is not shown
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

        NavBar.openTags();
        Tags.delete(the("#tag"));

        //check - after deletion followed tag public post with followed tag is not shown
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertNoPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

    }

    @Test
    public void testAccessToPostsFromUnLinkedUsersOfOnePod() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(EVE_P1).notToUsers(RON_P1).withTags(the("#tag")).build();
        Relation.forUser(RON_P1).notToUsers(EVE_P1).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("Public post from Ron"));
        Feed.addPublicPost(the("#tag")+" Public post with followed tag");
        Feed.addAllAspectsPost(the("Ron for all aspects"));
        Feed.addAspectPost(WORK, the("Ron for Work"));
        Feed.addPrivatePost(the("Private post from Ron"));
        Feed.assertNthPostIs(0, RON_P1, the("Private post from Ron"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(EVE_P1);
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");
        Feed.assertNoPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work"));
        Feed.assertNoPostFrom(RON_P1, the("Private post from Ron"));

        Menu.search(RON_P1.fullName);
        Feed.assertPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");
        Feed.assertNoPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work"));
        Feed.assertNoPostFrom(RON_P1, the("Private post from Ron"));

    }

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

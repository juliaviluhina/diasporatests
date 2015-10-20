package ua.net.itlabs;

import core.steps.Relation;
import datastructures.PodUser;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import ua.net.itlabs.categories.Buggy;
import ua.net.itlabs.categories.Smoke;

import static com.codeborne.selenide.Condition.appear;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaUserOperationsTest extends BaseTest{

    @Test
    public void testSignInAndLogOut() {
        Diaspora.signInAs(ANA_P1);
        //Menu.assertLoggedUser(ANA_P1);
        NavBar.assertLoggedUser(ANA_P1);
        Menu.logOut();
        Menu.assertLoggedOut();
    }

    //for test case #6416 - Actual result
    @Test
    @Category(Buggy.class)
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(DAVE_P3);
        NavBar.should(appear);
    }

    @Test
    public void testAddDeletePosts() {
        //add post
        Diaspora.signInAs(ANA_P1);
        Feed.addPublicPost(the("Public from Ana"));
        Feed.assertPostFrom(ANA_P1, the("Public from Ana") );
        Menu.logOut();

        //check - posts of another user can not be deleted
        Diaspora.signInAs(RON_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertPostCanNotBeDeleted(ANA_P1,the("Public from Ana") );
        Menu.logOut();

        //delete post
        Diaspora.signInAs(ANA_P1);
        Feed.deletePost(ANA_P1,the("Public from Ana") );
        Feed.assertNoPostFrom(ANA_P1,the("Public from Ana") );
        Menu.logOut();

        //check - deleted post is not available for another user
        Diaspora.signInAs(RON_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertNoPostFrom(ANA_P1, the("Public from Ana") );

    }

    @Test
    public void testAddDeleteCommentsOfPosts() {

        //GIVEN - add relations between users and add post
        Relation.forUser(SAM_P2).toUser(BOB_P2, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Sam for friends"));

        //add comment for his own post
        Feed.addComment(SAM_P2, the("Sam for friends"), the("Sam comments"));
        Menu.logOut();

        //add relation - for visibility post in stream
        Relation.forUser(BOB_P2).toUser(SAM_P2, WORK).doNotLogOut().build();
        Menu.openStream();

        //check - comments of another users can not be deleted
        Feed.assertCommentCanNotBeDeleted(SAM_P2, the("Sam for friends"),SAM_P2, the("Sam comments"));

        //add comment for post of another user
        Feed.addComment(SAM_P2, the("Sam for friends"), the("Bob comments"));
        Feed.assertComment(SAM_P2, the("Sam for friends"),BOB_P2, the("Bob comments"));
        Menu.logOut();

        //check - author of post can delete comments even of another user
        Diaspora.signInAs(SAM_P2);
        Feed.deleteComment(SAM_P2, the("Sam for friends"),BOB_P2, the("Bob comments"));
        Feed.assertNoComment(SAM_P2, the("Sam for friends"), BOB_P2, the("Bob comments"));

        //delete his own comments for his own post
        Feed.deleteComment(SAM_P2, the("Sam for friends"), SAM_P2, the("Sam comments"));
        Feed.assertNoComment(SAM_P2, the("Sam for friends"), SAM_P2, the("Sam comments"));

    }

    @Test
    public void testAddDeleteLikesOfPosts() {
        //GIVEN - add relations between users and add post
        Relation.forUser(SAM_P2).toUser(BOB_P2, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAllAspectsPost(the("Sam all aspects"));

        //like his own post
        Feed.toggleLike(SAM_P2, the("Sam all aspects"));
        Feed.assertLikes(SAM_P2, the("Sam all aspects"), 1 );
        Menu.logOut();

        //add relation - for visibility post in stream
        Relation.forUser(BOB_P2).toUser(SAM_P2, WORK).doNotLogOut().build();
        Menu.openStream();

        //check information about previous likes
        Feed.assertLikes(SAM_P2, the("Sam all aspects"), 1 );

        //like post of another user
        Feed.toggleLike(SAM_P2, the("Sam all aspects"));
        Feed.assertLikes(SAM_P2, the("Sam all aspects"), 2 );

        //unlike post
        Feed.toggleLike(SAM_P2, the("Sam all aspects"));
        Feed.assertLikes(SAM_P2, the("Sam all aspects"), 1 );

    }

    @Test
    public void testResharingOfPosts() {
        //GIVEN - add relations between users and add public and limited post
        Relation.forUser(SAM_P2).toUser(BOB_P2, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Sam for friends"));
        Feed.addPublicPost(the("Public Sam"));

        //check - limited posts can not be reshared in any case
        Feed.assertPostCanNotBeReshared(SAM_P2, the("Sam for friends"));
        //check - his own public posts can not be rashared
        Feed.assertPostCanNotBeReshared(SAM_P2, the("Public Sam"));
        Menu.logOut();

        //add relation - for visibility post in stream
        Relation.forUser(BOB_P2).toUser(SAM_P2, WORK).doNotLogOut().build();
        Menu.openStream();

        //check - available limited posts can not be reshared in any case
        Feed.assertPostCanNotBeReshared(SAM_P2, the("Sam for friends"));

        //reshare post
        Feed.reshare(SAM_P2, the("Public Sam"));

        //check in stream - reshared post can be shown
        Feed.assertPostFrom(BOB_P2, the("Public Sam"));
        //check in stream - resharing post can be shown
        Feed.assertPostFrom(SAM_P2, the("Public Sam"));
        Menu.logOut();

        Diaspora.signInAs(SAM_P2);
        //check in stream - reshared by another user post can be shown
        Feed.assertPostFrom(BOB_P2, the("Public Sam"));
        //check in stream - resharing post can be shown
        Feed.assertPostFrom(SAM_P2, the("Public Sam"));

    }

    @Test
    public void testAddDeleteMentionPost(){
        Relation.forUser(BOB_P2).notToUsers(SAM_P2).build();
        Relation.forUser(SAM_P2).toUser(BOB_P2, WORK).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPostWithMentionAbout(BOB_P2, the("public mention"));
        Feed.assertNthPostIs(0, SAM_P2, the("public mention"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(BOB_P2);
        Feed.assertPostFrom(SAM_P2, the("public mention"));
        NavBar.openMentions();
        Feed.assertPostFrom(SAM_P2, the("public mention"));
        Menu.logOut();
    }

}

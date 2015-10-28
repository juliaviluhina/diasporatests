package ua.net.itlabs;

import core.steps.Relation;
import datastructures.PodUser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import ua.net.itlabs.categories.AdditionalOperations;
import ua.net.itlabs.categories.BasicOperations;
import ua.net.itlabs.categories.Buggy;
import ua.net.itlabs.categories.Smoke;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.confirm;
import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;

@Category(BasicOperations.class)
public class DiasporaBasicOperationsTest extends BaseTest {

    public static String post;

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
        //add public post
        Relation.forUser(EVE_P1).notToUsers(ANA_P1, ROB_P1).build();
        Relation.forUser(ROB_P1).toUser(ANA_P1, FRIENDS).notToUsers(EVE_P1).build();
        Relation.forUser(ANA_P1).toUser(ROB_P1, FRIENDS).notToUsers(EVE_P1).doNotLogOut().build();
        post = the("Ana public");
        Menu.openStream();
        Feed.addPublicPost(post);
        Feed.assertNthPostIs(0, ANA_P1, post);
        Menu.logOut();
    }

    @Test
    public void testDeletePost() {
        //GIVEN additional - add post for deletion
        Diaspora.signInAs(ANA_P1);
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertPostFrom(ANA_P1, the("Ana for friends"));
        Menu.logOut();

        //check - posts of another user can not be deleted
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostCanNotBeDeleted(ANA_P1, the("Ana for friends"));
        Menu.logOut();

        //delete post
        Diaspora.signInAs(ANA_P1);
        Feed.deletePost(ANA_P1, the("Ana for friends"));
        //check - post is not shown in author stream after deletion
        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
        Menu.logOut();

        //check - post is not shown in linked user's stream after deletion
        Diaspora.signInAs(ROB_P1);
        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
        Menu.logOut();

    }

    @Test
    public void testLikeUnlikePost() {
        //like post by linked user
        Diaspora.signInAs(ROB_P1);
        Feed.toggleLike(ANA_P1, post);
        Feed.assertLikes(ANA_P1, post, 1);
        Menu.logOut();

        //check counter of likes in author's stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertLikes(ANA_P1, post, 1);
        Menu.logOut();

        //like post by unlinked user in Contact's stream
        Diaspora.signInAs(EVE_P1);
        Menu.search(ANA_P1.fullName);
        Feed.toggleLike(ANA_P1, post);
        Feed.assertLikes(ANA_P1, post, 2);
        Menu.logOut();

        //unlike post by linked user
        Diaspora.signInAs(ROB_P1);
        Feed.assertLikes(ANA_P1, post, 2);
        Feed.toggleLike(ANA_P1, post);
        Feed.assertLikes(ANA_P1, post, 1);
        Menu.logOut();

    }


    @Test
    public void testAddDeleteCommentsOfPosts() {

        //add comment for post of another linked user
        Diaspora.signInAs(ROB_P1);
        Feed.addComment(ANA_P1, post, the("Rob answer"));
        Feed.assertComment(ANA_P1, post, ROB_P1, the("Rob answer"));
        Menu.logOut();

        //add comment for post of another unlinked user in contact's stream
        Diaspora.signInAs(EVE_P1);
        Menu.search(ANA_P1.fullName);
        Feed.addComment(ANA_P1, post, the("Eve answer"));
        Feed.assertComment(ANA_P1, post, EVE_P1, the("Eve answer"));

        //check added by another user comments cannot be deleted, indirect - visibility added comment in contact's stream of unlinked user
        Feed.assertCommentCanNotBeDeleted(ANA_P1, post, ROB_P1, the("Rob answer"));
        Menu.logOut();

        //check visibility all comments in author's stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertComment(ANA_P1, post, ROB_P1, the("Rob answer"));

        //delete comment of another user for own post
        Feed.deleteComment(ANA_P1, post, EVE_P1, the("Eve answer"));
        Feed.assertNoComment(ANA_P1, post, EVE_P1, the("Eve answer"));
        Menu.logOut();

        //check - deleted comment is not shown in stream
        Diaspora.signInAs(ROB_P1);
        Feed.assertNoComment(ANA_P1, post, EVE_P1, the("Eve answer"));

        //delete own comment for post of another user
        Feed.deleteComment(ANA_P1, post, ROB_P1, the("Rob answer"));
        Feed.assertNoComment(ANA_P1, post, ROB_P1, the("Rob answer"));
        Menu.logOut();

        //check comments visibility in author's stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertNoComment(ANA_P1, post, ROB_P1, the("Rob answer"));
        Feed.assertNoComment(ANA_P1, post, EVE_P1, the("Eve answer"));
        Menu.logOut();

    }

    @Test
    public void testResharingOfPosts() {

        //GIVEN additional - add limited and public posts
        Diaspora.signInAs(ANA_P1);
        Feed.addAspectPost(FRIENDS, the("Ana about resharing for friends"));
        Feed.addPublicPost(the("Ana about resharing for public"));

        //check - author can not reshare their own posts
        Feed.assertPostCanNotBeReshared(ANA_P1, the("Ana about resharing for friends"));
        Feed.assertPostCanNotBeReshared(ANA_P1, the("Ana about resharing for public"));
        Menu.logOut();

        //reshare public post
        Diaspora.signInAs(ROB_P1);
        Feed.reshare(ANA_P1,the("Ana about resharing for public"));
        Feed.assertPostFrom(ROB_P1, the("Ana about resharing for public"));

        //check - limited post from another user cannot be reshared
        Feed.assertPostCanNotBeReshared(ANA_P1, the("Ana about resharing for friends"));
        Menu.logOut();

        //check - resharing post is visible in contact's stream in unlinked user (resharing post is public)
        Diaspora.signInAs(EVE_P1);
        Menu.search(ROB_P1.fullName);
        Feed.assertPostFrom(ROB_P1, the("Ana about resharing for public"));
        Menu.logOut();

        //delete original post
        Diaspora.signInAs(ANA_P1);
        Feed.deletePost(ANA_P1, the("Ana about resharing for public"));
        Feed.assertNoPostFrom(ANA_P1, the("Ana about resharing for public"));

        //check - resharing posts do not contain information from original deleted post
        Menu.openStream();//other posts refresh only after reload stream
        Feed.assertNoPostFrom(ROB_P1, the("Ana about resharing for public"));
        Menu.logOut();

        //check - resharing posts do not contain deleted original information
        Diaspora.signInAs(ROB_P1);
        Feed.assertNoPostFrom(ANA_P1, the("Ana about resharing for public"));
        Feed.assertNoPostFrom(ROB_P1, the("Ana about resharing for public"));
        Menu.logOut();

    }

//    @Test
//    public void testAddDeleteMentionPost(){
//        Relation.forUser(BOB_P2).notToUsers(SAM_P2).build();
//        Relation.forUser(SAM_P2).toUser(BOB_P2, WORK).doNotLogOut().build();
//        Menu.openStream();
//        Feed.addPublicPostWithMentionAbout(BOB_P2, the("public mention"));
//        Feed.assertNthPostIs(0, SAM_P2, the("public mention"));//this check for wait moment when stream will be loaded
//        Menu.logOut();
//
//        Diaspora.signInAs(BOB_P2);
//        Feed.assertPostFrom(SAM_P2, the("public mention"));
//        NavBar.openMentions();
//        Feed.assertPostFrom(SAM_P2, the("public mention"));
//        Menu.logOut();
//    }

}

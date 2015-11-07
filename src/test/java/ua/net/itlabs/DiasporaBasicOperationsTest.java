package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaBasicOperationsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        clearUniqueData();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
        Relation.forUser(Pod1.eve).notToUsers(Pod1.ana, Pod1.rob).build();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).notToUsers(Pod1.eve).build();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, FRIENDS).notToUsers(Pod1.eve).build();

    }

    @Test
    public void testDeletePost() {
        //GIVEN additional - add post for deletion
        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        //check - posts of another user can not be deleted
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostCanNotBeDeleted(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        //delete post
        Diaspora.signInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, the("Ana for friends"));
        //check - post is not shown in author stream after deletion
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        //check - post is not shown in linked user's stream after deletion
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPostFrom(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

    }

    @Test
    public void testLikeUnlikePost() {
        //GIVEN additional - add public post
        Diaspora.signInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public"));
        Feed.assertPostFrom(Pod1.ana, the("Ana public"));
        Menu.logOut();

        //like post by linked user
        Diaspora.signInAs(Pod1.rob);
        Feed.toggleLike(Pod1.ana, the("Ana public"));
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

        //check counter of likes in author's stream
        Diaspora.signInAs(Pod1.ana);
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

        //like post by unlinked user in Contact's stream
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.toggleLike(Pod1.ana, the("Ana public"));
        Feed.assertLikes(Pod1.ana, the("Ana public"), 2);
        Menu.logOut();

        //unlike post by linked user
        Diaspora.signInAs(Pod1.rob);
        Feed.assertLikes(Pod1.ana, the("Ana public"), 2);
        Feed.toggleLike(Pod1.ana, the("Ana public"));
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

    }


    @Test
    public void testAddDeleteCommentsOfPosts() {
        //GIVEN additional - add public post
        Diaspora.signInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public question"));
        Feed.assertPostFrom(Pod1.ana, the("Ana public question"));
        Menu.logOut();

        //add comment for post of another linked user
        Diaspora.signInAs(Pod1.rob);
        Feed.addComment(Pod1.ana, the("Ana public question"), the("Rob answer"));
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        //add comment for post of another unlinked user in contact's stream
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.addComment(Pod1.ana, the("Ana public question"), the("Eve answer"));
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));

        //check added by another user comments cannot be deleted, indirect - visibility added comment in contact's stream of unlinked user
        Feed.assertCommentCanNotBeDeleted(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        //check visibility all comments in author's stream
        Diaspora.signInAs(Pod1.ana);
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));

        //delete comment of another user for own post
        Feed.deleteComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Menu.logOut();

        //check - deleted comment is not shown in stream
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));

        //delete own comment for post of another user
        Feed.deleteComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        //check comments visibility in author's stream
        Diaspora.signInAs(Pod1.ana);
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Menu.logOut();

    }

    @Test
    public void testResharePost() {

        //GIVEN additional - add limited and public posts
        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(FRIENDS, the("Ana about resharing for friends"));
        Feed.addPublicPost(the("Ana about resharing for public"));

        //check - author can not reshare their own posts
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for friends"));
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for public"));
        Menu.logOut();

        //reshare public post
        Diaspora.signInAs(Pod1.rob);
        Feed.reshare(Pod1.ana, the("Ana about resharing for public"));
        Feed.assertPostFrom(Pod1.rob, the("Ana about resharing for public"));

        //check - limited post from another user cannot be reshared
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for friends"));
        Menu.logOut();

        //check - resharing post is visible in contact's stream in unlinked user (resharing post is public)
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.rob.fullName);
        Feed.assertPostFrom(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

        //delete original post
        Diaspora.signInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, the("Ana about resharing for public"));
        Feed.assertNoPostFrom(Pod1.ana, the("Ana about resharing for public"));

        //check - resharing posts do not contain information from original deleted post
        Menu.openStream();//other posts refresh only after reload stream
        Feed.assertNoPostFrom(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

        //check - resharing posts do not contain deleted original information
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPostFrom(Pod1.ana, the("Ana about resharing for public"));
        Feed.assertNoPostFrom(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

    }

    @Test
    public void testAddMentionPost() {
        //add post with mention about linked user
        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPostWithMentionAbout(Pod1.rob, the("public mention"));
        Feed.assertPostFrom(Pod1.ana, the("public mention"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check visibility in stream and in mentions stream
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostFrom(Pod1.ana, the("public mention"));
        NavBar.openMentions();
        Feed.assertPostFrom(Pod1.ana, the("public mention"));
        Menu.logOut();

    }

}

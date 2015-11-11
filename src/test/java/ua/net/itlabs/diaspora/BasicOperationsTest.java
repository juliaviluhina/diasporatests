package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class BasicOperationsTest extends BaseTest {

    @BeforeClass
    public static void givenSetupUsersRelation() {

        GIVEN("Setup relations between users from the same pod");
        Relation.forUser(Pod1.eve).notToUsers(Pod1.ana, Pod1.rob).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, FRIENDS).notToUsers(Pod1.eve).ensure();

    }

    @Test
    public void testDeletePost() {

        GIVEN("Limited post is added by author");
        clearUniqueData();
        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertPost(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        EXPECT("Post is shown in user's stream and cannot be deleted");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostCanNotBeDeleted(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        WHEN("Post is deleted by author");
        Diaspora.signInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, the("Ana for friends"));
        THEN("Post is not shown in author's stream");
        Feed.assertNoPost(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

        EXPECT("Deleted post is not shown in linked user's stream");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Ana for friends"));
        Menu.logOut();

    }

    @Test
    public void testLikeUnlikePost() {

        GIVEN("Public post is added by author");
        clearUniqueData();
        Diaspora.signInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.logOut();

        WHEN("Post of author is liked by linked user");
        Diaspora.signInAs(Pod1.rob);
        Feed.toggleLikePost(Pod1.ana, the("Ana public"));
        THEN("Count of likes is incremented");
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

        EXPECT("Information about likes is available by post author");
        Diaspora.signInAs(Pod1.ana);
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

        WHEN("Post is liked by unlinked user in Contact's stream");
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.toggleLikePost(Pod1.ana, the("Ana public"));
        THEN("Count of likes is incremented");
        Feed.assertLikes(Pod1.ana, the("Ana public"), 2);
        Menu.logOut();

        EXPECT("Information about all likes is available by linked user");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertLikes(Pod1.ana, the("Ana public"), 2);

        WHEN("Post is unliked by linked user");
        Feed.toggleLikePost(Pod1.ana, the("Ana public"));
        THEN("Count of likes is decremented");
        Feed.assertLikes(Pod1.ana, the("Ana public"), 1);
        Menu.logOut();

    }


    @Test
    public void testAddDeleteCommentsOfPosts() {

        GIVEN("Public post is added by author");
        clearUniqueData();
        Diaspora.signInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public question"));
        Feed.assertPost(Pod1.ana, the("Ana public question"));
        Menu.logOut();

        WHEN("Comment for post of author is added by linked user");
        Diaspora.signInAs(Pod1.rob);
        Feed.addComment(Pod1.ana, the("Ana public question"), the("Rob answer"));
        THEN("Comment is shown in stream");
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        WHEN("Comment for post of author is added by unlinked user in contact's stream");
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Feed.addComment(Pod1.ana, the("Ana public question"), the("Eve answer"));

        THEN("Comment is shown in contact's stream");
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));

        EXPECT("Comment from another user to available post is shown in stream");
        AND("Comments added by another user cannot be deleted");
        Feed.assertCommentCanNotBeDeleted(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        EXPECT("All added comments is shown in author's stream");
        Diaspora.signInAs(Pod1.ana);
        Feed.assertComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));

        EXPECT("Comment of another user for author's post can be deleted by author");
        Feed.deleteComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Menu.logOut();

        EXPECT("Deleted comment is not shown in stream");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));

        EXPECT("Comment of user can be deleted by this user");
        Feed.deleteComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Menu.logOut();

        EXPECT("Deleted comments is not shown after new signing in");
        Diaspora.signInAs(Pod1.ana);
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.rob, the("Rob answer"));
        Feed.assertNoComment(Pod1.ana, the("Ana public question"), Pod1.eve, the("Eve answer"));
        Menu.logOut();

    }

    @Test
    public void testResharePost() {

        GIVEN("Limited and public posts are added by author");
        clearUniqueData();
        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(FRIENDS, the("Ana about resharing for friends"));
        Feed.addPublicPost(the("Ana about resharing for public"));

        EXPECT("Author cannot reshare their own posts");
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for friends"));
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for public"));
        Menu.logOut();

        WHEN("Public post of author is reshared by user");
        Diaspora.signInAs(Pod1.rob);
        Feed.resharePost(Pod1.ana, the("Ana about resharing for public"));
        THEN("New public post about original post is added by user");
        Feed.assertPost(Pod1.rob, the("Ana about resharing for public"));

        EXPECT("Limited post cannot be reshared");
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana about resharing for friends"));
        Menu.logOut();

        EXPECT("Resharing post is visible in contact's stream of unlinked user (resharing post is public)");
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.rob.fullName);
        Feed.assertPost(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

        WHEN("Original post is deleted");
        Diaspora.signInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, the("Ana about resharing for public"));
        Feed.assertNoPost(Pod1.ana, the("Ana about resharing for public"));

        THEN("Resharing posts do not contain information from original deleted post");
        Menu.openStream();//other posts refresh only after reload stream
        Feed.assertNoPost(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

        EXPECT("In another streams there are no information from deleted original post and resharing post");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Ana about resharing for public"));
        Feed.assertNoPost(Pod1.rob, the("Ana about resharing for public"));
        Menu.logOut();

    }

    @Test
    public void testAddMentionPost() {

        WHEN("Post with mention about linked user is added by author");
        clearUniqueData();
        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPostWithMentionAbout(Pod1.rob, the("public mention"));
        Feed.assertPost(Pod1.ana, the("public mention"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        THEN("Post is shown in mentions stream of this linked user");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("public mention"));
        NavBar.openMentions();
        Feed.assertPost(Pod1.ana, the("public mention"));
        Menu.logOut();

    }

}

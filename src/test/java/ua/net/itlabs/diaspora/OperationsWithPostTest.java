package ua.net.itlabs.diaspora;

import org.junit.Before;
import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.*;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class OperationsWithPostTest extends BaseTest {


    @Test
    public void testAlienPostCannotBeDeleted() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

        EXPECT("Post is shown in user's stream and cannot be deleted");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPostCanNotBeDeleted(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testDeletePost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

        WHEN("Post is deleted by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, the("Ana public"));
        THEN("Post is not shown in author's stream");
        Feed.assertNoPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

        EXPECT("Deleted post is not shown in linked user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testLikePost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public for likes"));
        Feed.assertPost(Pod1.ana, the("Ana public for likes"));
        Menu.ensureLogOut();

        EXPECT("Count of likes is zero for new post");
        Feed.assertNoLikes(Pod1.ana, the("Ana public for likes"));

        WHEN("Post is liked by unlinked user in Contact's stream");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.toggleLikePost(Pod1.ana, the("Ana public for likes"));
        THEN("Count of likes is incremented");
        Feed.assertLikes(Pod1.ana, the("Ana public for likes"), 1);
        Menu.ensureLogOut();

        EXPECT("Information about likes is available by post author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertLikes(Pod1.ana, the("Ana public for likes"), 1);
        Menu.ensureLogOut();

    }

    @Test
    public void testUnlikePost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post is added by author and liked by user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openStream();
        Feed.addPublicPost(the("Ana public to unlike"));
        Feed.assertPost(Pod1.ana, the("Ana public to unlike"));
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.toggleLikePost(Pod1.ana, the("Ana public to unlike"));

        EXPECT("Count of likes is incremented");
        Feed.assertLikes(Pod1.ana, the("Ana public to unlike"), 1);

        WHEN("Post of author is liked by unlinked user");
        Feed.toggleLikePost(Pod1.ana, the("Ana public to unlike"));
        THEN("Count of likes is decremented");
        Feed.assertNoLikes(Pod1.ana, the("Ana public to unlike"));
        Menu.ensureLogOut();

        EXPECT("Information about likes is available by post author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoLikes(Pod1.ana, the("Ana public to unlike"));
        Menu.ensureLogOut();

    }

    @Test
    public void testAddCommentToPost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Limited post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureAspectPost(Pod1.ana, FRIENDS, the("Ana for friends"));
        Feed.assertPost(Pod1.ana, the("Ana for friends"));
        Menu.ensureLogOut();

        WHEN("Comment for post of author is added by linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.addComment(Pod1.ana, the("Ana for friends"), the("Rob answer"));
        THEN("Comment is shown in stream");
        Feed.assertComment(Pod1.ana, the("Ana for friends"), Pod1.rob, the("Rob answer"));
        Menu.ensureLogOut();

        EXPECT("Added comment is shown in author's stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertComment(Pod1.ana, the("Ana for friends"), Pod1.rob, the("Rob answer"));

    }

    @Test
    public void testDeleteCommentByAuthorOfComment() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));

        WHEN("Comment is deleted by user");
        Feed.deleteComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        Menu.ensureLogOut();

        EXPECT("Deleted comment is not shown in author's stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));

    }

    @Test
    public void testDeleteCommentByAuthorOfPost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        Menu.ensureLogOut();

        WHEN("Comment is deleted by author of post");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.deleteComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        Menu.ensureLogOut();

        EXPECT("Deleted comment is not shown in user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoComment(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));

    }

    @Test
    public void testAlienCommentToAlienPostCannotBeDeleted() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        Menu.ensureLogOut();

        EXPECT("Alien comment to alien post cannot be deleted");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertCommentCanNotBeDeleted(Pod1.ana, the("Ana public"), Pod1.rob, the("Rob comment"));
        Menu.ensureLogOut();

    }

    @Test
    public void testPostCannotBeResharedByAuthor() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));

        EXPECT("Author cannot reshare their own posts");
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testLimitedPostCannotBeReshared() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Limited post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureAspectPost(Pod1.ana, FRIENDS, the("Ana for friends"));
        Feed.addPublicPost(the("Ana for friends"));
        Menu.ensureLogOut();

        EXPECT("Limited post cannot be reshared by any user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPostCanNotBeReshared(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testResharePublicPost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();

        WHEN("Public post is reshared by user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.resharePost(Pod1.ana, the("Ana public"));
        THEN("New public post about original post is added by user");
        Feed.assertPost(Pod1.rob, the("Ana public"));
        Menu.ensureLogOut();

        EXPECT("Resharing post is public and is shown for unlinked user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.rob.fullName);
        Feed.assertPost(Pod1.rob, the("Ana public"));
        Menu.ensureLogOut();

    }

    @Test
    public void testDeleteResharedPublicPost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        GIVEN("Public post from author is exists and reshared");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, the("Ana public"));
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.ensureResharePublicPost(Pod1.ana, the("Ana public"), Pod1.eve);
        Menu.ensureLogOut();

        EXPECT("Reshared and resharing posts are shown in stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertPost(Pod1.ana, the("Ana public"));
        Menu.search(Pod1.eve.fullName);
        Feed.assertPost(Pod1.eve, the("Ana public"));

        WHEN("Reshared original post is deleted");
        Menu.openStream();
        Feed.deletePost(Pod1.ana, the("Ana public"));

        THEN("Reshared posts is not shown in stream");
        AND("Resharing post does not contain original information from reshared post");
        Feed.assertNoPost(Pod1.ana, the("Ana public"));
        Menu.search(Pod1.eve.fullName);
        Feed.assertNoPost(Pod1.eve, the("Ana public"));
        Menu.ensureLogOut();

    }


    @Test
    public void testAddMentionPost() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();

        WHEN("Post with mention about linked user is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPostWithMentionAbout(Pod1.rob, the("public mention"));
        Feed.assertPost(Pod1.ana, the("public mention"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        THEN("Post is shown in mentions stream of this linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("public mention"));
        NavBar.openMentions();
        Feed.assertPost(Pod1.ana, the("public mention"));
        Menu.ensureLogOut();

    }

    @Test
    public void testHidePosts() {
        GIVEN("Ensure relations for users from one pod");
        ensureRelationsForUsersOfPod1();
        clearUniqueData();

        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(FRIENDS, the("Ana for friends about hiding"));
        Feed.assertPost(Pod1.ana, the("Ana for friends about hiding"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        EXPECT("Hidden post is not shown in stream");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.hidePost(Pod1.ana, the("Ana for friends about hiding"));
        Feed.assertNoPost(Pod1.ana, the("Ana for friends about hiding"));

        EXPECT("Hidden post is not shown in contact stream");
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Ana for friends about hiding"));
        Menu.ensureLogOut();

        EXPECT("Hidden post is shown in stream of another user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for friends about hiding"));
        Menu.ensureLogOut();

        EXPECT("After new signing in hidden post is not shown");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.assertNoPost(Pod1.ana, the("Ana for friends about hiding"));
        Menu.ensureLogOut();

    }


}

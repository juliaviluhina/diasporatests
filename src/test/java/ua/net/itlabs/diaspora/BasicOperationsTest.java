package ua.net.itlabs.diaspora;

import org.junit.Test;
import pages.*;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

public class BasicOperationsTest extends BaseTest {

    @Test
    public void testAddPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("There is no Ana's public post");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureNoPost(Pod1.ana, PUBLIC_POST);

        WHEN("Public post from author is added");
        Feed.addPublicPost(PUBLIC_POST);

        THEN("Post is shown in author's stream");
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Added post is shown in linked user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, PUBLIC_POST);

    }


    @Test
    public void testAlienPostCannotBeDeleted() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Post is shown in user's stream and cannot be deleted");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPostCanNotBeDeleted(Pod1.ana, PUBLIC_POST);

    }

    @Test
    public void testDeletePost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        WHEN("Post is deleted by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.deletePost(Pod1.ana, PUBLIC_POST);

        THEN("Post is not shown in author's stream");
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Deleted post is not shown in linked user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

    }

    @Test
    public void testLikePost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        GIVEN("There is no likes from user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.ensureNoLike(Pod1.ana, PUBLIC_POST);

        WHEN("Post is liked by unlinked user in Contact's stream");
        Feed.toggleLikePost(Pod1.ana, PUBLIC_POST);

        THEN("Like from user appears");
        Menu.search(Pod1.ana.fullName);//without refresh test doest't work
        Feed.assertLikeOfCurrentUser(Pod1.ana, PUBLIC_POST);

        EXPECT("Information about likes is available by post author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertLike(Pod1.ana, PUBLIC_POST, Pod1.eve);

    }

    @Test
    public void testUnlikePost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        GIVEN("Like from user is added");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureLike(Pod1.ana, PUBLIC_POST);

        WHEN("Post of author is unliked by linked user");
        Feed.toggleLikePost(Pod1.ana, PUBLIC_POST);
        Feed.assertNoLikeOfCurrentUser(Pod1.ana, PUBLIC_POST);

        EXPECT("In information about likes is no available by post author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoLike(Pod1.ana, PUBLIC_POST, Pod1.rob);

    }

    @Test
    public void testAddCommentToPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Limited post from author exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureAspectPost(Pod1.ana, FRIENDS, POST_FOR_FRIENDS);

        GIVEN("Comment for post of author from linked user does not exist");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureNoCommentForPost(Pod1.ana, POST_FOR_FRIENDS, Pod1.rob, COMMENT);

        WHEN("Comment for post of author is added by linked user");
        Feed.addComment(Pod1.ana, POST_FOR_FRIENDS, COMMENT);

        THEN("Comment is shown in stream");
        Feed.assertComment(Pod1.ana, POST_FOR_FRIENDS, Pod1.rob, COMMENT);

        EXPECT("Added comment is shown in author's stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertComment(Pod1.ana, POST_FOR_FRIENDS, Pod1.rob, COMMENT);

    }

    @Test
    public void testDeleteCommentByAuthorOfComment() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        GIVEN("Comment for post of author from linked user exists");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        WHEN("Comment is deleted by user");
        Feed.deleteComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        EXPECT("Deleted comment is not shown in author's stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

    }

    @Test
    public void testDeleteCommentByAuthorOfPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        WHEN("Comment is deleted by author of post");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.deleteComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        EXPECT("Deleted comment is not shown in user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoComment(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

    }

    @Test
    public void testAlienCommentToAlienPostCannotBeDeleted() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureCommentForPost(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

        EXPECT("Alien comment to alien post cannot be deleted");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertCommentCanNotBeDeleted(Pod1.ana, PUBLIC_POST, Pod1.rob, COMMENT);

    }

    @Test
    public void testPostCannotBeResharedByAuthor() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Author cannot reshare their own posts");
        Feed.assertPostCanNotBeReshared(Pod1.ana, PUBLIC_POST);

    }

    @Test
    public void testLimitedPostCannotBeReshared() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Limited post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureAspectPost(Pod1.ana, FRIENDS, POST_FOR_FRIENDS);
        Feed.ensurePublicPost(Pod1.ana, POST_FOR_FRIENDS);

        EXPECT("Limited post cannot be reshared by any user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPostCanNotBeReshared(Pod1.ana, POST_FOR_FRIENDS);

    }

    @Test
    public void testResharePublicPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        WHEN("Public post is reshared by user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.resharePost(Pod1.ana, PUBLIC_POST);

        THEN("New public post about original post is added by user");
        Feed.assertPost(Pod1.rob, PUBLIC_POST);

        EXPECT("Resharing post is public and is shown for unlinked user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.rob.fullName);
        Feed.assertPost(Pod1.rob, PUBLIC_POST);

    }

    @Test
    public void testDeleteResharedPublicPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post from author is exists and reshared");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.ensureResharePublicPost(Pod1.ana, PUBLIC_POST, Pod1.eve);

        EXPECT("Reshared and resharing posts are shown in stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertPost(Pod1.ana, PUBLIC_POST);
        Menu.search(Pod1.eve.fullName);
        Feed.assertPost(Pod1.eve, PUBLIC_POST);

        WHEN("Reshared original post is deleted");
        Menu.openStream();
        Feed.deletePost(Pod1.ana, PUBLIC_POST);

        THEN("Reshared posts is not shown in stream");
        AND("Resharing post does not contain original information from reshared post");
        Menu.openStream();//without stream refresh checks works unstable
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);
        Menu.search(Pod1.eve.fullName);
        Feed.assertNoPost(Pod1.eve, PUBLIC_POST);

    }


    @Test
    public void testAddMentionPost() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        WHEN("Post with mention about linked user is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPostWithMentionAbout(Pod1.rob, MENTION_POST);
        Feed.assertPost(Pod1.ana, MENTION_POST);//this check for wait moment when stream will be loaded

        THEN("Post is shown in mentions stream of this linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, MENTION_POST);
        NavBar.openMentions();
        Feed.assertPost(Pod1.ana, MENTION_POST);

    }

}

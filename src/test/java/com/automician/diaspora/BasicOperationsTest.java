package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import org.junit.Test;
import pages.*;

import static pages.Aspects.*;
import static core.Gherkin.*;

public class BasicOperationsTest extends BaseTest {

    @Test
    public void testAddPost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("There is no author's public post");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensureNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        WHEN("Public post from author is added");
        Feed.addPublicPost(Phrases.PUBLIC_POST);

        THEN("Post is shown in author's stream");
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Added post is shown in linked user's stream");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

    }


    @Test
    public void testAlienPostCannotBeDeleted() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Post is shown in user's stream and cannot be deleted");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertPostCanNotBeDeleted(Users.Pod1.ana, Phrases.PUBLIC_POST);

    }

    @Test
    public void testDeletePost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        WHEN("Post is deleted by author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.deletePost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        THEN("Post is not shown in author's stream");
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Deleted post is not shown in linked user's stream");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

    }

    @Test
    public void testLikePost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        GIVEN("There is no likes from user");
        Diaspora.ensureSignInAs(Users.Pod1.eve);
        Menu.search(Users.Pod1.ana.fullName);
        Feed.ensureNoLike(Users.Pod1.ana, Phrases.PUBLIC_POST);

        WHEN("Post is liked by unlinked user in Contact's stream");
        Feed.toggleLikePost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        THEN("Like from user appears");
        Menu.search(Users.Pod1.ana.fullName);//without refresh test doest't work
        Feed.assertLikeOfCurrentUser(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Information about likes is available by post author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertLike(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.eve);

    }

    @Test
    public void testUnlikePost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        GIVEN("Like from user is added");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureLike(Users.Pod1.ana, Phrases.PUBLIC_POST);

        WHEN("Post of author is unliked by linked user");
        Feed.toggleLikePost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Feed.assertNoLikeOfCurrentUser(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("In information about likes is no available by post author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertNoLike(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob);

    }

    @Test
    public void testAddCommentToPost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Limited post from author exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensureAspectPost(Users.Pod1.ana, FRIENDS, Phrases.POST_FOR_FRIENDS);

        GIVEN("Comment for post of author from linked user does not exist");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureNoCommentForPost(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS, Users.Pod1.rob, Phrases.COMMENT);

        WHEN("Comment for post of author is added by linked user");
        Feed.addComment(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS, Phrases.COMMENT);

        THEN("Comment is shown in stream");
        Feed.assertComment(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS, Users.Pod1.rob, Phrases.COMMENT);

        EXPECT("Added comment is shown in author's stream");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertComment(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS, Users.Pod1.rob, Phrases.COMMENT);

    }

    @Test
    public void testDeleteCommentByAuthorOfComment() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        GIVEN("Comment for post of author from linked user exists");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureCommentForPost(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        WHEN("Comment is deleted by user");
        Feed.deleteComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        EXPECT("Deleted comment is not shown in author's stream");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertNoComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

    }

    @Test
    public void testDeleteCommentByAuthorOfPost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureCommentForPost(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        WHEN("Comment is deleted by author of post");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.deleteComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        THEN("Comment is not shown in stream");
        Feed.assertNoComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        EXPECT("Deleted comment is not shown in user's stream");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertNoComment(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

    }

    @Test
    public void testAlienCommentToAlienPostCannotBeDeleted() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author exists, comment to post from linked user exist");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureCommentForPost(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

        EXPECT("Alien comment to alien post cannot be deleted");
        Diaspora.ensureSignInAs(Users.Pod1.eve);
        Menu.search(Users.Pod1.ana.fullName);
        Feed.assertCommentCanNotBeDeleted(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.rob, Phrases.COMMENT);

    }

    @Test
    public void testPostCannotBeResharedByAuthor() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Author cannot reshare their own posts");
        Feed.assertPostCanNotBeReshared(Users.Pod1.ana, Phrases.PUBLIC_POST);

    }

    @Test
    public void testLimitedPostCannotBeReshared() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Limited post from author is exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensureAspectPost(Users.Pod1.ana, FRIENDS, Phrases.POST_FOR_FRIENDS);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS);

        EXPECT("Limited post cannot be reshared by any user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertPostCanNotBeReshared(Users.Pod1.ana, Phrases.POST_FOR_FRIENDS);

    }

    @Test
    public void testResharePublicPost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author is exists");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        WHEN("Public post is reshared by user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.resharePost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        THEN("New public post about original post is added by user");
        Feed.assertPost(Users.Pod1.rob, Phrases.PUBLIC_POST);

        EXPECT("Resharing post is public and is shown for unlinked user");
        Diaspora.ensureSignInAs(Users.Pod1.eve);
        Menu.search(Users.Pod1.rob.fullName);
        Feed.assertPost(Users.Pod1.rob, Phrases.PUBLIC_POST);

    }

    @Test
    public void testDeleteResharedPublicPost() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Public post from author is exists and reshared");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensurePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Diaspora.ensureSignInAs(Users.Pod1.eve);
        Feed.ensureResharePublicPost(Users.Pod1.ana, Phrases.PUBLIC_POST, Users.Pod1.eve);

        EXPECT("Reshared and resharing posts are shown in stream");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertPost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Menu.search(Users.Pod1.eve.fullName);
        Feed.assertPost(Users.Pod1.eve, Phrases.PUBLIC_POST);

        WHEN("Reshared original post is deleted");
        Menu.openStream();
        Feed.deletePost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        THEN("Reshared posts is not shown in stream");
        AND("Resharing post does not contain original information from reshared post");
        Menu.openStream();//without stream refresh checks works unstable
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Menu.search(Users.Pod1.eve.fullName);
        Feed.assertNoPost(Users.Pod1.eve, Phrases.PUBLIC_POST);

    }


    @Test
    public void testAddMentionPost() {

        GIVEN("Setup relations among users of pod1");
        //Pod1.ensureRelations();

        GIVEN("There is no author's mention post");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensureNoPost(Users.Pod1.ana, Phrases.MENTION_POST);

        WHEN("Post with mention about linked user is added by author");
        Feed.addPublicPostWithMentionAbout(Users.Pod1.rob, Phrases.MENTION_POST);
        Feed.assertPost(Users.Pod1.ana, Phrases.MENTION_POST);//this check for wait moment when stream will be loaded

        THEN("Post is shown in mentions stream of this linked user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertPost(Users.Pod1.ana, Phrases.MENTION_POST);
        NavBar.openMentions();
        Feed.assertPost(Users.Pod1.ana, Phrases.MENTION_POST);

    }

}

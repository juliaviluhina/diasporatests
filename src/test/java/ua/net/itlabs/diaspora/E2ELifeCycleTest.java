package ua.net.itlabs.diaspora;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.FRIENDS;
import static ua.net.itlabs.testDatas.Phrases.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

@Category(ua.net.itlabs.categories.Smoke.class)
public class E2ELifeCycleTest extends BaseTest {

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("There is no Ana's public post");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensureNoPost(Pod1.ana, PUBLIC_POST);

        GIVEN("There is no Rob's post in Friends aspect and public post");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.ensureNoPost(Pod1.rob, POST_FOR_FRIENDS);
        Feed.ensureNoPost(Pod1.rob, PUBLIC_POST);

        WHEN("Public post is added by Ana");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(PUBLIC_POST);
        Feed.assertPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Post from Ana can be liked by linked Rob");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.toggleLikePost(Pod1.ana, PUBLIC_POST);
        Feed.assertLikes(Pod1.ana, PUBLIC_POST, 1);

        WHEN("Limited in right aspect post is added by Rob");
        Feed.addAspectPost(FRIENDS, POST_FOR_FRIENDS);
        Feed.assertPost(Pod1.rob, POST_FOR_FRIENDS);

        EXPECT("Post of Rob can be commented by linked Ana");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addComment(Pod1.rob, POST_FOR_FRIENDS, COMMENT);
        Feed.assertComment(Pod1.rob, POST_FOR_FRIENDS, Pod1.ana, COMMENT);

        EXPECT("Limited post of Rob cannot be reshared by Ana");
        Feed.assertPostCanNotBeReshared(Pod1.rob, POST_FOR_FRIENDS);

        EXPECT("Comments added by another user is shown for author of post");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertComment(Pod1.rob, POST_FOR_FRIENDS, Pod1.ana, COMMENT);

        EXPECT("Public post from Ana can be reshared by Rob");
        Feed.resharePost(Pod1.ana, PUBLIC_POST);
        Feed.assertPost(Pod1.rob, PUBLIC_POST);

        EXPECT("Liked post can be unliked");
        Feed.toggleLikePost(Pod1.ana, PUBLIC_POST);
        Feed.assertNoLikes(Pod1.ana, PUBLIC_POST);

        EXPECT("Resharing post from Rob is shown for Ana");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertPost(Pod1.rob, PUBLIC_POST);

        EXPECT("Reshared post can be deleted in My Activity stream");
        NavBar.openMyActivity();
        Feed.deletePost(Pod1.ana, PUBLIC_POST);
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

        EXPECT("Post of another user cannot be deleted");
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(Pod1.rob, POST_FOR_FRIENDS);

        EXPECT("Comment from user can be deleted by author of post ");
        Feed.deleteComment(Pod1.rob, POST_FOR_FRIENDS, Pod1.ana, COMMENT);

        EXPECT("Deleted post is not shown in user's stream");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

    }

}

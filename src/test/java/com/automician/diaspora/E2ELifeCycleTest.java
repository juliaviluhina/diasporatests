package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.categories.Smoke;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;

import static pages.Aspects.FRIENDS;
import static com.automician.core.Gherkin.*;

@Category(Smoke.class)
public class E2ELifeCycleTest extends BaseTest {

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("There is no Ana's public post");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.ensureNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        GIVEN("There is no Rob's post in Friends aspect and public post");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.ensureNoPost(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS);
        Feed.ensureNoPost(Users.Pod1.rob, Phrases.PUBLIC_POST);

        WHEN("Public post is added by Ana");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.addPublicPost(Phrases.PUBLIC_POST);
        Feed.assertPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Post from Ana can be liked by linked Rob");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.toggleLikePost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Feed.assertLikes(Users.Pod1.ana, Phrases.PUBLIC_POST, 1);

        WHEN("Limited in right aspect post is added by Rob");
        Feed.addAspectPost(FRIENDS, Phrases.POST_FOR_FRIENDS);
        Feed.assertPost(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS);

        EXPECT("Post of Rob can be commented by linked Ana");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.addComment(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS, Phrases.COMMENT);
        Feed.assertComment(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS, Users.Pod1.ana, Phrases.COMMENT);

        EXPECT("Limited post of Rob cannot be reshared by Ana");
        Feed.assertPostCanNotBeReshared(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS);

        EXPECT("Comments added by another user is shown for author of post");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertComment(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS, Users.Pod1.ana, Phrases.COMMENT);

        EXPECT("Public post from Ana can be reshared by Rob");
        Feed.resharePost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Feed.assertPost(Users.Pod1.rob, Phrases.PUBLIC_POST);

        EXPECT("Liked post can be unliked");
        Feed.toggleLikePost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Feed.assertNoLikes(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Resharing post from Rob is shown for Ana");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.assertPost(Users.Pod1.rob, Phrases.PUBLIC_POST);

        EXPECT("Reshared post can be deleted in My Activity stream");
        NavBar.openMyActivity();
        Feed.deletePost(Users.Pod1.ana, Phrases.PUBLIC_POST);
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

        EXPECT("Post of another user cannot be deleted");
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS);

        EXPECT("Comment from user can be deleted by author of post ");
        Feed.deleteComment(Users.Pod1.rob, Phrases.POST_FOR_FRIENDS, Users.Pod1.ana, Phrases.COMMENT);

        EXPECT("Deleted post is not shown in user's stream");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.assertNoPost(Users.Pod1.ana, Phrases.PUBLIC_POST);

    }

}

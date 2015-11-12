package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.Gherkin.*;

@Category(ua.net.itlabs.categories.Smoke.class)
public class E2ELifeCycleTest extends BaseTest {

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {
        GIVEN("Setup relation between users, some followed tag is added for users");
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, WORK).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, ACQUAINTANCES).doNotLogOut().ensure();

        WHEN("Public post is added by user1");
        Menu.openStream();
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.logOut();

        EXPECT("Post of user1 can be liked by linked user2");
        Diaspora.signInAs(Pod1.rob);
        Feed.toggleLikePost(Pod1.ana, the("Public Ana"));
        Feed.assertLikes(Pod1.ana, the("Public Ana"), 1);

        WHEN("Limited in right aspect post is added by user2");
        Feed.addAspectPost(WORK, the("Rob for work"));
        Feed.assertPost(Pod1.rob, the("Rob for work"));
        Menu.logOut();

        EXPECT("Post of user2 can be commented by linked user1");
        Diaspora.signInAs(Pod1.ana);
        Feed.addComment(Pod1.rob, the("Rob for work"), the("Comment from Ana"));
        Feed.assertComment(Pod1.rob, the("Rob for work"), Pod1.ana, the("Comment from Ana"));

        EXPECT("Limited post of user2 cannot be rashared by user1");
        Feed.assertPostCanNotBeReshared(Pod1.rob, the("Rob for work"));
        Menu.logOut();

        EXPECT("Comments added by another user is shown for author of post"); //check visibility of comments
        Diaspora.signInAs(Pod1.rob);
        Feed.assertComment(Pod1.rob, the("Rob for work"), Pod1.ana, the("Comment from Ana"));

        EXPECT("Public of user1 can be reshared by user2");
        Feed.resharePost(Pod1.ana, the("Public Ana"));
        Feed.assertPost(Pod1.rob, the("Public Ana"));

        EXPECT("Liked post can be unliked");
        Feed.toggleLikePost(Pod1.ana, the("Public Ana"));
        Feed.assertNoLikes(Pod1.ana, the("Public Ana"));
        Menu.logOut();

        EXPECT("Resharing post from user2 is shown for user1");
        Diaspora.signInAs(Pod1.ana);
        Feed.assertPost(Pod1.rob, the("Public Ana"));

        EXPECT("Reshared post can be deleted in My Activity stream");
        NavBar.openMyActivity();
        Feed.deletePost(Pod1.ana, the("Public Ana"));
        Feed.assertNoPost(Pod1.ana, the("Public Ana"));

        EXPECT("Post of another user cannot be deleted");
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(Pod1.rob, the("Rob for work"));

        EXPECT("Comment from user can be deleted by author of post ");
        Feed.deleteComment(Pod1.rob, the("Rob for work"), Pod1.ana, the("Comment from Ana"));
        Menu.logOut();

        EXPECT("Deleted post is not shown in user's stream");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Public Ana"));
        Menu.logOut();

    }

}

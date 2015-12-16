package ua.net.itlabs.diaspora;

import com.codeborne.selenide.Configuration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Phrases.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class FederationTest extends BaseTest {

    @BeforeClass
    public static void givenSetupUsersRelation() {

        Configuration.timeout = 90000;//for push data on another pod it is needed bigger timeout

        //setup relation among users from pod1(which aren't changed in all tests and built once)
        Pod1.ensureRelations();

        //in addition to relation among users from pod1
        //setup relation Pod1-+->Pod2
        Relation.forUser(Pod1.ana).toUser(Pod2.bob, ACQUAINTANCES).notToUsers(Pod2.sam).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod2.sam, FRIENDS).notToUsers(Pod2.bob).withTags(TAG).ensure();

        //setup relation among users from pod2 and Pod2-+->Pod1
        Relation.forUser(Pod2.sam).toUser(Pod1.rob, FAMILY).notToUsers(Pod1.ana, Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod1.ana, WORK).notToUsers(Pod1.rob, Pod2.sam).withTags(TAG).ensure();

    }

    //method added to insert given description on allure report
    @Before
    public void givenDescription() {

        GIVEN("POD1:         Ana<-+->Rob as Friends           , Eve<-X->Ana & Rob");
        GIVEN("POD2:         Sam<-X->Bob");
        GIVEN("POD1-+->POD2: P1.Ana-+->P2.Bob as Acquaintances, P1.Rob-+->P2.Sam as Friends");
        GIVEN("POD2-+->POD1: P2.Bob-+->P1.Ana as Work         , P2.Sam-+->P1.Rob as Family");

    }

    @AfterClass
    public static void restoreTimeOut() {
        Configuration.timeout = timeout;
    }

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testAvailabilityPublicPostForUnlinkedUsersOfDifferentPods() {

        GIVEN("Public post with tag is added by author from pod 1 from scratch");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.ensureNoPost(Pod2.bob, PUBLIC_POST_WITH_TAG);
        Feed.addPublicPost(PUBLIC_POST_WITH_TAG);
        Feed.assertPost(Pod2.bob, PUBLIC_POST_WITH_TAG);

        EXPECT("Post is shown in stream of unlinked user from pod2 who has the same followed tag");
        AND("This post can be commented");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.addComment(Pod2.bob, PUBLIC_POST_WITH_TAG, COMMENT);
        Feed.assertComment(Pod2.bob, PUBLIC_POST_WITH_TAG, Pod1.rob, COMMENT);

        EXPECT("Post with comment from user from another pod is shown in author's stream");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.assertComment(Pod2.bob, PUBLIC_POST_WITH_TAG, Pod1.rob, COMMENT);

    }

    @Test
    public void testAvailabilityLimitedPostForLinkedUsersOfDifferentPods() {

        GIVEN("Limited in right aspect post is added by author from pod 2");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.addAspectPost(WORK, the("Bob for work"));
        Feed.assertPost(Pod2.bob, the("Bob for work"));

        EXPECT("Post is shown in stream of linked in right aspect user from pod1");
        AND("This post can be commented");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addComment(Pod2.bob, the("Bob for work"), the("Comment from Ana"));
        Feed.assertComment(Pod2.bob, the("Bob for work"), Pod1.ana, the("Comment from Ana"));

        EXPECT("Post with comment from user from another pod is shown in author's stream");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.assertComment(Pod2.bob, the("Bob for work"), Pod1.ana, the("Comment from Ana"));

    }

    private static long timeout = Configuration.timeout;

}

package ua.net.itlabs.diaspora;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class FederationTest extends BaseTest {

    private static String tag;

    @BeforeClass
    public static void givenSetupUsersRelation() {

        GIVEN("Setup relation between users from two pods, some followed tag is added for users");
        tag = "#ana_bob_rob_sam";
        Configuration.timeout = 90000;//for push data on another pod it is needed bigger timeout
        Relation.forUser(Pod1.ana).toUser(Pod2.bob, ACQUAINTANCES).notToUsers(Pod1.rob, Pod2.sam).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod2.sam, FRIENDS).notToUsers(Pod1.ana, Pod2.bob).withTags(tag).ensure();
        Relation.forUser(Pod2.sam).toUser(Pod1.rob, FAMILY).notToUsers(Pod1.ana, Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod1.ana, WORK).notToUsers(Pod1.rob, Pod2.sam).withTags(tag).ensure();

    }

    @Before
    public void addGivenDescriptionToAllure() {

        GIVEN("Setup relation between users from two pods, some followed tag is added for users");
        GIVEN("Ana from pod1 is linked with Bob from pod2 as Acquaintance");
        AND("Ana is not linked with Rob from pod1 and Sam from pod2");
        GIVEN("Rob from pod1 is linked with Sam from pod2 as Friend");
        AND("Rob is not linked with Ana from pod1 and Bob from pod2");
        AND("Rob has tag "+tag);
        GIVEN("Sam from pod2 is linked with Rob from pod1 as Family");
        AND("Sam is not linked with Ana from pod1 and Bob from pod2");
        AND("Rob has tag "+tag);
        GIVEN("Bob from pod2 is linked with Ana from pod1 as Work");
    }

    @Test
    public void testAvailabilityPublicPostForUnlinkedUsersOfDifferentPods() {

        GIVEN("Public post with tag is added by author from pod 1");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertPost(Pod2.bob, the(tag + " Public Bob"));

        EXPECT("Post is shown in stream of unlinked user from pod2 who has the same followed tag");
        AND("This post can be commented");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.addComment(Pod2.bob, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(Pod2.bob, the(tag + " Public Bob"), Pod1.rob, the("Comment from Rob"));

        EXPECT("Post with comment from user from another pod is shown in author's stream");
        Diaspora.ensureSignInAs(Pod2.bob);
        Feed.assertComment(Pod2.bob, the(tag + " Public Bob"), Pod1.rob, the("Comment from Rob"));

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

}

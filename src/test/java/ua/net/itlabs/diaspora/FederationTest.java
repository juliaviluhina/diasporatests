package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;

public class FederationTest extends BaseTest {

    private static String tag;

    @BeforeClass
    public static void givenSetupUsersRelation() {
        clearUniqueData();

        //GIVEN - setup relation between users, addition one the same followed tag
        tag = "#ana_bob_rob_sam";
        Relation.forUser(Pod1.ana).toUser(Pod2.bob, ACQUAINTANCES).notToUsers(Pod1.rob, Pod2.sam).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod2.sam, FRIENDS).notToUsers(Pod1.ana, Pod2.bob).withTags(tag).ensure();
        Relation.forUser(Pod2.sam).toUser(Pod1.rob, FAMILY).notToUsers(Pod1.ana, Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod1.ana, WORK).notToUsers(Pod1.rob, Pod2.sam).withTags(tag).ensure();
    }

    @Test
    public void testAvailabilityPublicPostForUnlinkedUsersOfDifferentPods() {

        //public post with tag
        Diaspora.signInAs(Pod2.bob);
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertPost(Pod2.bob, the(tag + " Public Bob"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(Pod1.rob);

        //comment post in stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.addComment(Pod2.bob, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(Pod2.bob, the(tag + " Public Bob"), Pod1.rob, the("Comment from Rob"));
        Menu.logOut();

        //check visibility comment from unlinked user from another pod
        Diaspora.signInAs(Pod2.bob);
        Feed.assertComment(Pod2.bob, the(tag + " Public Bob"), Pod1.rob, the("Comment from Rob"));

    }

    @Test
    public void testAvailabilityLimitedPostForLinkedUsersOfDifferentPods() {

        //post in right aspect
        Diaspora.signInAs(Pod2.bob);
        Feed.addAspectPost(WORK, the("Bob for work"));
        Feed.assertPost(Pod2.bob, the("Bob for work"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(Pod1.ana);

        //comment post in stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.addComment(Pod2.bob, the("Bob for work"), the("Comment from Ana"));
        Feed.assertComment(Pod2.bob, the("Bob for work"), Pod1.ana, the("Comment from Ana"));
        Menu.logOut();

        //check visibility comment from linked user from another pod
        Diaspora.signInAs(Pod2.bob);
        Feed.assertComment(Pod2.bob, the("Bob for work"), Pod1.ana, the("Comment from Ana"));

    }

}

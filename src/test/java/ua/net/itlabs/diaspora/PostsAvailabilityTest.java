package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.ACQUAINTANCES;
import static core.Gherkin.*;

public class PostsAvailabilityTest extends BaseTest {

    @BeforeClass
    public static void givenSetupUsersRelation() {
        GIVEN("Setup relation between users from the same pod");
        Relation.forUser(Pod1.eve).notToUsers(Pod1.ana, Pod1.rob).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, FRIENDS).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, ACQUAINTANCES).notToUsers(Pod1.eve).ensure();
    }

    @Test
    public void testAvailabilityPublicPost() {

        GIVEN("Public post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.ensureLogOut();

        EXPECT("Public post without tag is not shown in stream of unlinked user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.assertNoPost(Pod1.ana, the("Public Ana"));

        EXPECT("Public post without tag is shown in contact stream of unlinked user");
        Menu.search(Pod1.ana.fullName);
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.ensureLogOut();

        EXPECT("Public post is shown in stream of linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.ensureLogOut();

    }

    @Test
    public void testAvailabilityPrivatePost() {

        GIVEN("Private post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertPost(Pod1.ana, the("Private Ana"));
        Menu.ensureLogOut();

        EXPECT("Private post is not shown in contact's stream for unlinked user ");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));
        Menu.ensureLogOut();

        EXPECT("Private post is not shown in stream of linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));
        Menu.ensureLogOut();
    }

    @Test
    public void testAvailabilityLimitedPosts() {

        GIVEN("Limited posts in different aspects ere added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addAllAspectsPost(the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.addAspectPost(FRIENDS, the("Ana for Friends"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Ana for Acquaintances"));
        Feed.assertPost(Pod1.ana, the("Ana for Acquaintances"));
        Menu.ensureLogOut();

        EXPECT("Limited post is not shown in contact's stream for unlinked user ");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Friends"));
        Menu.ensureLogOut();

        EXPECT("Posts limited in some aspects are shown in stream of linked user who linked in this aspects with author");
        AND("Posts limited in some aspects are not shown in stream of linked user who linked in another aspects with author");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Acquaintances"));
        Menu.ensureLogOut();

    }
}

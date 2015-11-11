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
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.ACQUAINTANCES;

public class PostsAvailabilityTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        clearUniqueData();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
        Relation.forUser(Pod1.eve).notToUsers(Pod1.ana, Pod1.rob).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, FRIENDS).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, ACQUAINTANCES).notToUsers(Pod1.eve).ensure();
    }

    @Test
    public void testAvailabilityPublicPost() {
        //add public post
        Diaspora.signInAs(Pod1.ana);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.logOut();

        //check - public post without tag for unlinked user is not available in Stream
        Diaspora.signInAs(Pod1.eve);
        Feed.assertNoPost(Pod1.ana, the("Public Ana"));

        //check - public post without tag for unlinked user is available in Contact Stream
        Menu.search(Pod1.ana.fullName);
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.logOut();

        //check - public post is available for linked user
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Public Ana"));
        Menu.logOut();
    }

    @Test
    public void testAvailabilityPrivatePost() {
        //add private post
        Diaspora.signInAs(Pod1.ana);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertPost(Pod1.ana, the("Private Ana"));
        Menu.logOut();

        //check - private post for unlinked user is not available even in Contact's Stream
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));
        Menu.logOut();

        //check - private post is not available for linked user
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));
        Menu.logOut();
    }

    @Test
    public void testAvailabilityLimitedPosts() {
        //add limited posts
        Diaspora.signInAs(Pod1.ana);
        Feed.addAllAspectsPost(the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.addAspectPost(FRIENDS, the("Ana for Friends"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Ana for Acquaintances"));
        Feed.assertPost(Pod1.ana, the("Ana for Acquaintances"));
        Menu.logOut();

        //check - limited post for unlinked user is not available even in Contact's Stream
        Diaspora.signInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Friends"));
        Menu.logOut();

        //check - limited post is available for linked user in right aspect
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Acquaintances"));
        Menu.logOut();
    }
}

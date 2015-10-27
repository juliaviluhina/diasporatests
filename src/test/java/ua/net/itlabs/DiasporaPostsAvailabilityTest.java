package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;


@Category(ua.net.itlabs.categories.Availability.class)
public class DiasporaPostsAvailabilityTest extends BaseTest {


    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
//        Relation.forUser(ANA_P1).toUser(ROB_P1, FRIENDS).notToUsers(EVE_P1).build();
//        Relation.forUser(ROB_P1).toUser(ANA_P1, ACQUAINTANCES).notToUsers(EVE_P1).build();
    }

    @Test
    public void testAvailabilityPublicPost() {
        //add public post
        Diaspora.signInAs(ANA_P1);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0,ANA_P1, the("Public Ana"));
        Menu.logOut();

        //check - public post without tag for unlinked user is not available in Stream
        Diaspora.signInAs(EVE_P1);
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));
        //check - public post without tag for unlinked user is available in Contact Stream
        Menu.search(ANA_P1.fullName);
        Feed.assertPostFrom(ANA_P1, the("Public Ana"));
        Menu.logOut();

        //check - public post is available for linked user
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(ANA_P1, the("Public Ana"));
        Menu.logOut();
    }

    @Test
    public void testAvailabilityPrivatePost() {
        //add private post
        Diaspora.signInAs(ANA_P1);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertNthPostIs(0,ANA_P1, the("Private Ana"));
        Menu.logOut();

        //check - private post for unlinked user is not available even in Contact's Stream
        Diaspora.signInAs(EVE_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertNoPostFrom(ANA_P1, the("Private Ana"));
        Menu.logOut();

        //check - private post is not available for linked user
        Diaspora.signInAs(ROB_P1);
        Feed.assertNoPostFrom(ANA_P1, the("Private Ana"));
        Menu.logOut();
    }

    @Test
    public void testAvailabilityLimitedPosts() {
        //add limited posts
        Diaspora.signInAs(ANA_P1);
        Feed.addAllAspectsPost(the("Ana for All aspects"));
        Feed.assertNthPostIs(0,ANA_P1, the("Ana for All aspects"));
        Feed.addAspectPost(FRIENDS, the("Ana for Friends"));
        Feed.assertNthPostIs(0,ANA_P1, the("Ana for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Ana for Acquaintances"));
        Feed.assertNthPostIs(0,ANA_P1, the("Ana for Acquaintances"));
        Menu.logOut();

        //check - limited post for unlinked user is not available even in Contact's Stream
        Diaspora.signInAs(EVE_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertNoPostFrom(ANA_P1, the("Ana for All aspects"));
        Feed.assertNoPostFrom(ANA_P1, the("Ana for Friends"));
        Menu.logOut();

        //check - limited post is available for linked user in right aspect
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(ANA_P1, the("Ana for All aspects"));
        Feed.assertPostFrom(ANA_P1, the("Ana for Friends"));
        Feed.assertNoPostFrom(ANA_P1, the("Ana for Acquaintances"));
        Menu.logOut();
    }
}

package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import pages.NavBar;
import ua.net.itlabs.categories.Federation;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.BOB_P2;

@Category(Federation.class)
public class DiasporaFederationTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();
    }

    @Test
    public void testAvailabilityPublicPostForUnlinkedUsersOfDifferentPods() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#ana_bob_rob_sam";
        Relation.forUser(ANA_P1).toUser(BOB_P2,ACQUAINTANCES).notToUsers(ROB_P1, SAM_P2).build();
        Relation.forUser(ROB_P1).toUser(SAM_P2, FRIENDS).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
        Relation.forUser(SAM_P2).toUser(ROB_P1, FAMILY).notToUsers(ANA_P1, BOB_P2).build();
        Relation.forUser(BOB_P2).toUser(ANA_P1,WORK).notToUsers(ROB_P1, SAM_P2).withTags(tag).doNotLogOut().build();

        //public post with tag
        Menu.openStream();
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, BOB_P2, the(tag + " Public Bob"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(ROB_P1);
        //comment post in stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.addComment(BOB_P2, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
        Menu.logOut();

        Diaspora.signInAs(BOB_P2);
        Feed.assertComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));

    }

}

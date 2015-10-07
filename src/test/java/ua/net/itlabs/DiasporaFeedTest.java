package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;

public class DiasporaFeedTest extends BaseTest{


    @Test
    public void testAccessToPostsFromMutuallyLinkedUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("Public post from Ron"));
        Feed.addAllAspectsPost(the("Ron for all aspects"));
        Feed.addAspectPost(WORK, the("Ron for Work - unlinked aspect "));
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Relation.forUser(EVE_P1).toUser(RON_P1, ACQUAINTANCES).doNotLogOut().build();

        Menu.openStream();
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
    }

    @Test
    public void testAccessToPostsFromNonMutuallyLinkedUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(EVE_P1).notToUsers(RON_P1).withTags(the("#tag")).build();
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("#tag")+the(" Public post from Ron with followed tag"));
        Feed.addPublicPost(the("Public post from Ron"));
        Feed.addAllAspectsPost(the("Ron for all aspects"));
        Feed.addAspectPost(WORK, the("Ron for Work - unlinked aspect "));
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(EVE_P1);
        //in stream post is not shown
        Feed.assertPostFrom(RON_P1, the("#tag") + the(" Public post from Ron with followed tag"));
        Feed.assertNoPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));

        //but post is shown in search stream
        Menu.search(RON_P1.fullName);
        Feed.assertPostFrom(RON_P1, the("#tag") + the(" Public post from Ron with followed tag"));
        Feed.assertPostFrom(RON_P1, the("Public post from Ron"));
        Feed.assertPostFrom(RON_P1, the("Ron for all aspects"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for Work - unlinked aspect "));
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
    }

    @Test
    public void testStreamConsistManage() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(the("#tag")+" Public post with followed tag");
        Feed.addAspectPost(FAMILY, the("Ron for Family - linked aspect "));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for Family - linked aspect "));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Relation.forUser(EVE_P1).toUser(RON_P1, ACQUAINTANCES).withTags(the("#tag")).doNotLogOut().build();

        //check - in stream posts from linked user is shown
        Menu.openStream();
        Feed.assertPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(ACQUAINTANCES, RON_P1);

        //check - after deletion contact limited post is not shown
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

        NavBar.openTags();
        Tags.delete(the("#tag"));

        //check - after deletion followed tag public post with followed tag is not shown
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Ron for Family - linked aspect "));
        Feed.assertNoPostFrom(RON_P1, the("#tag") + " Public post with followed tag");

    }

}

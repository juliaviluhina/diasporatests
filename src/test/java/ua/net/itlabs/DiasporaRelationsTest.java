package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.ACQUAINTANCES;
import static pages.Aspects.FAMILY;
import static pages.Aspects.FRIENDS;
import static pages.Contact.contact;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;

public class DiasporaRelationsTest extends BaseTest{


    @Test
    public void testAccessToLimitedPostsForMutuallyLinkedUser() {
        //GIVEN - setup relation between users in some aspect
        //add posts for aspect with link
        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Ron for Family"));
        Menu.logOut();
        Relation.forUser(EVE_P1).toUser(RON_P1, ACQUAINTANCES).doNotLogOut().build();

        Menu.openStream();
        Feed.assertPostFrom(RON_P1, the("Ron for Family"));
        //Menu.logOut();

    }

//    @Test
//    public void testAccessToLimitedPostsForNonMutuallyLinkedUser() {
//        //GIVEN - setup relation between users in some aspect
//        //add posts for aspect with link
//        Relation.forUser(RON_P1).toUser(EVE_P1, FAMILY).doNotLogOut().build();
//        Menu.openStream();
//        Feed.addAspectPost(FAMILY, the("Ron for Family"));
//        Menu.logOut();
//
//        Relation.forUser(EVE_P1).notToUsers(RON_P1).doNotLogOut().build();
//
//        //in stream post is not shown
//        Menu.openStream();
//        Feed.assertNoPostFrom(RON_P1, the("Ron for Family"));
//
//        //but posi is shown in search stream
//        Menu.search(ROB_P1.fullName);
//        Feed.assertPostFrom(RON_P1, the("Ron for Family"));
//        //Menu.logOut();
//    }

}

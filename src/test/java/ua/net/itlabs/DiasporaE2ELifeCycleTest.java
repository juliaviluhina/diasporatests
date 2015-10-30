package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import ua.net.itlabs.categories.Federation;
import ua.net.itlabs.categories.Smoke;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;


@Category(Smoke.class)
public class DiasporaE2ELifeCycleTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();
    }

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {
        //GIVEN - setup relation between users, addition one the same followed tag
        Relation.forUser(ROB_P1).toUser(ANA_P1, WORK).build();
        Relation.forUser(ANA_P1).toUser(ROB_P1, ACQUAINTANCES).doNotLogOut().build();

        //public post
        Menu.openStream();
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Public Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(ROB_P1);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Rob for work"));
        Menu.logOut();

        //comment post, indirect check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA_P1);
        Feed.addComment(ROB_P1, the("Rob for work"), the("Comment from Ana"));
        Feed.assertComment(ROB_P1, the("Rob for work"), ANA_P1, the("Comment from Ana"));

        //check - for limited post is no possibility for resharing, indirect check - post is added
        Feed.assertPostCanNotBeReshared(ROB_P1, the("Rob for work"));
        Menu.logOut();


        //check visibility of comments
        Diaspora.signInAs(ROB_P1);
        Feed.assertComment(ROB_P1, the("Rob for work"), ANA_P1, the("Comment from Ana"));

        //reshare public post
        Feed.reshare(ANA_P1, the("Public Ana"));
        Feed.assertPostFrom(ROB_P1, the("Public Ana"));

        //unlike post
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertNoLikes(ANA_P1, the("Public Ana"));
        Menu.logOut();

        //check visibility of reshared post
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostFrom(ROB_P1, the("Public Ana"));

        //delete post
        NavBar.openMyActivity();
        Feed.deletePost(ANA_P1, the("Public Ana"));
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));

        //check post of another user can not be deleted
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(ROB_P1,  the("Rob for work"));

        //delete comment
        Feed.deleteComment(ROB_P1, the("Rob for work"), ANA_P1, the("Comment from Ana"));
        Menu.logOut();


        Diaspora.signInAs(ROB_P1);
        //check - deleted post is not shown
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));
        Menu.logOut();

    }

}

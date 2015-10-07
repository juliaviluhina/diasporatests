package ua.net.itlabs;

import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import pages.NavBar;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.appear;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaUserOperationsTest extends BaseTest{

    @Test
    public void testSignInAndLogOut() {
        Diaspora.signInAs(ANA_P1);
        Menu.assertLoggedUser(ANA_P1);
        Menu.logOut();
        Menu.assertLoggedOut();
    }

    //for test case #6416 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(DAVE_P3);
        NavBar.should(appear);
    }

    @Test
    public void testAddDeletePosts() {
        Diaspora.signInAs(ANA_P1);
        Feed.addPublicPost(the("Public from Ana"));
        Feed.assertPostFrom(ANA_P1, the("Public from Ana") );
        Menu.logOut();

        Diaspora.signInAs(RON_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertPostCanNotBeDeleted(ANA_P1,the("Public from Ana") );
        Menu.logOut();

        Diaspora.signInAs(ANA_P1);
        Feed.deletePost(ANA_P1,the("Public from Ana") );
        Feed.assertNoPostFrom(ANA_P1,the("Public from Ana") );
        Menu.logOut();

        Diaspora.signInAs(RON_P1);
        Menu.search(ANA_P1.fullName);
        Feed.assertNoPostFrom(ANA_P1, the("Public from Ana") );
    }

}

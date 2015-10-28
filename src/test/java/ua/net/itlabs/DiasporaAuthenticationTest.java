package ua.net.itlabs;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;
import ua.net.itlabs.categories.AdditionalOperations;
import ua.net.itlabs.categories.Authentication;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.confirm;
import static core.helpers.UniqueDataHelper.clearThe;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.DAVE_P3;

@Category(Authentication.class)
public class DiasporaAuthenticationTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();
    }

    @Test
    public void testSignInAndLogOut() {
        Diaspora.signInAs(ANA_P1);
        //Menu.assertLoggedUser(ANA_P1);
        NavBar.assertLoggedUser(ANA_P1);
        Menu.logOut();
        Menu.assertLoggedOut();
    }

//    //for test case #6416 - Expected & Actual result
//    @Test
//    public void testSignInForAccountWithoutPosts() {
//        Diaspora.signInAs(DAVE_P3);
//        NavBar.should(appear);
//        Menu.logOut();
//        confirm(null);
//    }
}

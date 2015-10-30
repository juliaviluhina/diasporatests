package ua.net.itlabs;

import core.steps.Relation;
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
import static ua.net.itlabs.testDatas.Users.ROB_P1;

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
        NavBar.assertLoggedUser(ANA_P1);
        Menu.logOut();
        Menu.assertLoggedOut();
        Relation.forUser(ROB_P1).doNotLogOut().build();
    }

}

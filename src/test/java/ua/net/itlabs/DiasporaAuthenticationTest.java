package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaAuthenticationTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        clearUniqueData();
    }

    @Test
    public void testSignInAndLogOut() {
        Diaspora.signInAs(Pod1.ana);
        NavBar.assertLoggedUser(Pod1.ana);
        Menu.logOut();
        Menu.assertLoggedOut();
    }

}

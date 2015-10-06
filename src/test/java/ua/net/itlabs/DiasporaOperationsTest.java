package ua.net.itlabs;

import org.junit.Test;
import pages.Diaspora;
import pages.NavBar;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.appear;
import static ua.net.itlabs.testDatas.Users.BOB_P2;
import static ua.net.itlabs.testDatas.Users.DAVE_P3;

public class DiasporaOperationsTest extends BaseTest{

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts() {
        Diaspora.signInAs(BOB_P2);
        NavBar.should(appear);
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(DAVE_P3);
        NavBar.should(appear);
    }
}

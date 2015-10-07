package ua.net.itlabs;

import org.junit.Test;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.appear;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaOperationsTest extends BaseTest{

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts() {
        Diaspora.signInAs(ANA_P1);
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
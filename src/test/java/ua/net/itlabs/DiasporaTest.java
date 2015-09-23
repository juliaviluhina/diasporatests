package ua.net.itlabs;

import org.junit.Test;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;
import pages.Tags;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.visible;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaTest extends BaseTest {

    @Test
    public void testAllOperationsWithTags() {
        //todo - new public post linked with tag1 in user account userBob

        Diaspora.signInAs(Users.ANA);
        NavBar.expandTags();

        Tags.add(the("#tag1"));
        Tags.assertExist(the("#tag1"));

        //todo - select tag1 and check existing public post from userBob

        //todo - add new unique tag2, select tag1 and check there are not any posts according tag2

        Tags.delete(the("#tag1"));
        Tags.assertNotExist(the("#tag1"));
    }

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts() {
        Diaspora.signInAs(Users.BOB);
        NavBar.tagsHeader.shouldBe(visible);
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(Users.DAVE);
        NavBar.tagsHeader.shouldBe(visible);
    }

    //for test case #2
    @Test
    @Buggy
    public void testTagsOrder() {
        //step 1,2 - add tag Y and Z
        Diaspora.signInAs(Users.ANA);
        NavBar.expandTags();
        Tags.add(the("#Ytag"));
        Tags.assertExist(the("#Ytag"));
        Tags.add(the("#Ztag"));
        Tags.assertExist(the("#Ztag"));

        //step 3 - check order - like in actual result
        Tags.assertNthIs(0, the("#Ztag"));
        Tags.assertNthIs(1, the("#Ytag"));

        //step 4
        Menu.logOut();
        Diaspora.signInAs(Users.ANA);
        NavBar.expandTags();

        //actual result - tag order is different
        Tags.assertNthIs(0, the("#Ztag"));
        Tags.assertNthIs(1, the("#Ytag"));

    }

}

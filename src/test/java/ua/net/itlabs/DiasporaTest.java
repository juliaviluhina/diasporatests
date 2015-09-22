package ua.net.itlabs;

import org.junit.Test;
import pages.Diaspora;
import pages.Menu;
import pages.Tags;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.visible;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaTest extends BaseTest {

    @Test
    public void testAllOperationsWithTags(){
        //todo - new public post linked with tag1 in user account userBob

        Diaspora.signInAs(Users.ANA);
        Tags.expandTags();

        Tags.addTag(the("#tag1"));
        Tags.assertTags();

        //todo - select tag1 and check existing public post from userBob

        //todo - add new unique tag2, select tag1 and check there are not any posts according tag2

        Tags.deleteTag(the("#tag1"));
        Tags.assertTags();
    }

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts(){
        Diaspora.signInAs(Users.BOB);
        Tags.tagsHeader.shouldBe(visible);
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts(){
        Diaspora.signInAs(Users.DAVE);
        Tags.tagsHeader.shouldBe(visible);
    }

    //for test case #2
    @Test
    @Buggy
      public void testTagsOrder(){
        //step 1,2 - add tag Y and Z
        Diaspora.signInAs(Users.ANA);
        Tags.expandTags();
        Tags.addTag(the("#Ytag"));
        Tags.addTag(the("#Ztag"));

        //step 3 - check order - like in actual result
        String[] expectedTagNames = Tags.expectedTagNames();
        Tags.assertTagsInOrder(expectedTagNames);

        //step 4
        Menu.logOut();
        Diaspora.signInAs(Users.ANA);
        Tags.expandTags();

        //actual result - tag order is different
        Tags.assertTagsInOrder(expectedTagNames);
    }

}

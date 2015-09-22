package ua.net.itlabs;

import org.junit.After;
import org.junit.Test;
import pages.Diaspora;
import pages.Stream;
import ua.net.itlabs.categories.Buggy;

import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaTest {

    @After
    public void tearDown() throws IOException {
        Stream.cleanAddedData();
        Stream.logOut();
    }

    @Test
    public void testAllOperationsWithTags(){
        //todo - new public post linked with tag1 in user account userBob

        Diaspora.signInAs(Users.ANA);
        Stream.expandTags();

        Stream.addTag(the("#tag1"));
        Stream.assertTags();

        //todo - select tag1 and check existing public post from userBob

        //todo - add new unique tag2, select tag1 and check there are not any posts according tag2

        Stream.deleteTag(the("#tag1"));
        Stream.assertTags();
    }

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts(){
        Diaspora.signInAs(Users.BOB);
        Stream.tagsHeader.shouldBe(visible);;
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts(){
        Diaspora.signInAs(Users.DAVE);
        Stream.tagsHeader.shouldBe(visible);;
    }

    //for test case #2
    @Test
    @Buggy
      public void testTagsOrder(){
        //step 1,2 - add tag Y and Z
        Diaspora.signInAs(Users.ANA);
        Stream.expandTags();
        Stream.addTag(the("#Ytag"));
        Stream.addTag(the("#Ztag"));

        //step 3 - check order - like in actual result
        String[] expectedTagNames = Stream.expectedTagNames();
        Stream.assertTagsInOrder(expectedTagNames);

        //step 4
        Stream.logOut();
        Diaspora.signInAs(Users.ANA);
        Stream.expandTags();

        //Save tags in list for cleaning after test
        Stream.addTagsForCleaning(Stream.expectedTagNames());

        //actual result - tag order is different
        Stream.assertTagsInOrder(expectedTagNames);
    }

}

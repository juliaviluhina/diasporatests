package ua.net.itlabs;


import core.AtDiasporaWithReportedScreenshotsTest;
import org.junit.Test;
import pages.StreamPage;
import pages.SignInPage;



public class DiasporaTest extends AtDiasporaWithReportedScreenshotsTest {

    @Test
    public void testAllOperationsWithTags(){
        //GIVEN - new unique tags and new post linked with tag1
        String tag1 = newUniqueTagName();
        //must to be done - new public post linked with tag1 in user account userBob

        signInPage.signIn(userAna);
        streamPage.expandTags();

        streamPage.addTag(tag1);
        streamPage.assertTags();

        //must to be done - select tag1 and check existing public post from userBob

        //must to be done - add new unique tag2, select tag1 and check there are not any posts according tag2

        streamPage.deleteTag(tag1);
        streamPage.assertTags();
    }

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts(){
        signInPage.signIn(userBob);
        streamPage.assertTagsHeaderIsVisible();
    }

    //for test case #1 - Actual result
    //Failed
    @Test
    public void testSignInForAccountWithoutPosts(){
        signInPage.signIn(userDave);
        streamPage.assertTagsHeaderIsVisible();
    }

    //for test case #2
    //Failed
    @Test
    public void testTagsOrder(){
        //GIVEN - new unique tags Z and Y
        String tagZ = newUniqueTagName("Z");
        String tagY = newUniqueTagName("Y");

        //step 1,2 - add tag Y and Z
        signInPage.signIn(userAna);
        streamPage.expandTags();
        streamPage.addTag(tagY);
        streamPage.addTag(tagZ);

        //step 3 - check order - like in actual result
        String[] expectedTagNames = streamPage.expectedTagNames();
        streamPage.assertTagsInOrder(expectedTagNames);

        //step 4
        streamPage.logOut();
        signInPage.signIn(userAna);
        streamPage.expandTags();

        //actual result - tag order is different
        streamPage.assertTagsInOrder(expectedTagNames);

    }

}

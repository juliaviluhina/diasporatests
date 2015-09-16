package ua.net.itlabs;


import com.codeborne.selenide.Screenshots;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Test;
import pages.SignInPage;
import pages.StreamPage;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.IOException;

import static ua.net.itlabs.Users.*;
import static core.Helpers.*;


public class DiasporaTest {

    public SignInPage signInPage = new SignInPage();
    public StreamPage streamPage = new StreamPage();

    @After
    public void postScreensAfterTest() throws IOException {
        streamPage.logOut();
    }

    @Test
    public void testAllOperationsWithTags(){
        //GIVEN - new unique tags and new post linked with tag1
        String tag1 = newUniqueTagName();
        //must to be done - new public post linked with tag1 in user account userBob

        signInPage.signIn(ana);
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
        signInPage.signIn(bob);
        streamPage.assertTagsHeaderIsVisible();
    }

    //for test case #1 - Actual result
    //Failed
    @Test
    public void testSignInForAccountWithoutPosts(){
        signInPage.signIn(dave);
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
        signInPage.signIn(ana);
        streamPage.expandTags();
        streamPage.addTag(tagY);
        streamPage.addTag(tagZ);

        //step 3 - check order - like in actual result
        String[] expectedTagNames = streamPage.expectedTagNames();
        streamPage.assertTagsInOrder(expectedTagNames);

        //step 4
        streamPage.logOut();
        signInPage.signIn(ana);
        streamPage.expandTags();

        //actual result - tag order is different
        streamPage.assertTagsInOrder(expectedTagNames);

    }

}

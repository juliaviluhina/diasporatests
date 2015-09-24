package ua.net.itlabs;

import org.junit.Test;
import pages.*;
import ua.net.itlabs.categories.Buggy;
import ua.net.itlabs.testDatas.DiasporaAspects;
import ua.net.itlabs.testDatas.Users;

import static com.codeborne.selenide.Condition.visible;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaTest extends BaseTest {

    @Test
    public void testFollowedTags() {
        //GIVEN - new public posts linked with tags in user account from the same pod
        String post1 = the("Public post with tag " + the("#tag1") + " : ");
        String post2 = the("Public post with tag " + the("#tag2") + " : ");
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Feed.addPublicPost(post1);
        Feed.assertNthPostIs(0, Users.ROB, post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0, Users.ROB, post2);
        Menu.logOut();

        Diaspora.signInAs(Users.ANA);

        //tags is not used and posts is not shown
        Feed.assertPostIsNotShown(Users.ROB, post1);
        Feed.assertPostIsNotShown(Users.ROB, post2);

        NavBar.openTags();

        Tags.add(the("#tag1"));
        Tags.assertExist(the("#tag1"));

        //after addition stream do not refresh
        //Feed.assertPostIsShown(Users.ROB, post1);
        //Feed.assertPostIsNotShown(Users.ROB, post2);

        Tags.add(the("#tag2"));
        Tags.assertExist(the("#tag2"));

        //after addition stream do not refresh
        //Feed.assertPostIsShown(Users.ROB, post1);
        //Feed.assertPostIsShown(Users.ROB, post2);

        //only posts with filtered tag are shown
        Tags.filter(the("#tag1"));
        Feed.assertPostIsShown(Users.ROB, post1);
        Feed.assertPostIsNotShown(Users.ROB, post2);

        //without filtering both posts are shown
        Menu.openStream();
        NavBar.openTags();
        Feed.assertPostIsShown(Users.ROB, post1);
        Feed.assertPostIsShown(Users.ROB, post2);

        Tags.delete(the("#tag1"));
        Tags.assertNotExist(the("#tag1"));

        //after deletion stream do not refresh
        //Feed.assertPostIsNotShown(Users.ROB, post1);
        //Feed.assertPostIsShown(Users.ROB, post2);

        //in view mode of whole stream posts with followed text are shown
        NavBar.openStream();
        Feed.assertPostIsNotShown(Users.ROB, post1);
        Feed.assertPostIsShown(Users.ROB, post2);

    }

    @Test
    public void testPosting() {
        //todo - use posts with different aspects
        String post1 = the("Public post with tag " + the("#tag1") + " : ");
        String post2 = the("Public post with tag " + the("#tag2") + " : ");
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Feed.addPublicPost(post1);
        Feed.assertNthPostIs(0, Users.ROB, post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0, Users.ROB, post2);

        Feed.deletePost(Users.ROB, post1);
        Feed.assertNthPostIs(0, Users.ROB, post2);
        Feed.deletePost(Users.ROB, post2);
    }


    @Test
    public void testAspects() {
        //todo - add public and limited in different way posts in account from the same pod
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Menu.search(Users.ANA.fullName);
        People.assertPerson(Users.ANA.fullName);
        People.ensureAddContact();
        People.manageContact.click();
        People.clickAspect(DiasporaAspects.ACQUAINTANCES);
        People.assertAspectIsUsed(DiasporaAspects.ACQUAINTANCES);
        Menu.logOut();


        //todo - add public and limited in different way posts in account from different pod
        //todo - clean contacts for ANA.
        Diaspora.signInAs(Users.ANA);
        //todo - check showing in stream particular posts
        //todo - check search - in search mode all public posts will be shown
    }


    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts() {
        Diaspora.signInAs(Users.BOB);
        NavBar.navBar.shouldBe(visible);
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(Users.DAVE);
        NavBar.navBar.shouldBe(visible);
    }

    //for test case #2
    @Test
    @Buggy
    public void testTagsOrder() {
        //step 1,2 - add tag Y and Z
        Diaspora.signInAs(Users.ANA);
        NavBar.openTags();
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
        NavBar.openTags();

        //actual result - tag order is different
        Tags.assertNthIs(0, the("#Ztag"));
        Tags.assertNthIs(1, the("#Ytag"));

    }

}

package ua.net.itlabs;

import org.junit.Test;
import pages.*;
import ua.net.itlabs.categories.Buggy;
import ua.net.itlabs.testDatas.DiasporaAspects;
import ua.net.itlabs.testDatas.Users;

import static com.codeborne.selenide.Condition.visible;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.DiasporaAspects.*;

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
    public void testUserActivitiesAndLimitOfAccess() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#ana_bob_rob_sam";
        //who with whom through which aspect, which followed tag, with whom are not any links
        setupLinksFor(ANA, BOB, ACQUAINTANCES, tag, ROB, SAM);
        setupLinksFor(BOB, ANA, WORK, tag, ROB, SAM);
        setupLinksFor(ROB, SAM, FRIENDS, tag, ANA, BOB);
        setupLinksFor(SAM, ROB, FAMILY, tag, ANA, BOB);

        //public post
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA, the("Public Ana"));
        Menu.logOut();
        //check - public post is shown in stream of linked user
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        Feed.assertPostIsShown(ANA, the("Public Ana")); //FAILED

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Bob for work"));
        Feed.assertNthPostIs(0, BOB, the("Bob for work"));
        Menu.logOut();
        //check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostIsShown(BOB,the("Bob for work"));

        //limited post in wrong aspect
        Feed.addAspectPost(FAMILY, the("Ana for family"));
        Feed.assertNthPostIs(0, ANA, the("Ana for family"));
        Menu.logOut();
        //check - limited post in wrong aspect is not shown in stream of linked user
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        Feed.assertPostIsNotShown(ANA, the("Ana for family"));

        //public post with tag
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, BOB, the(tag + " Public Bob"));
        Menu.logOut();
        //check - public post with tag is shown in stream of linked used
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostIsShown(BOB,the(tag + " Public Bob"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.assertPostIsNotShown(ANA, the("Public Ana"));
        //check - public post is shown in searching stream of unlinked user
        Menu.search(ANA.fullName);
        People.assertPerson(ANA.fullName);
        Feed.assertPostIsShown(ANA, the("Public Ana"));
        //check - limited post is not shown in stream of unlinked user
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));
        //check - limited post is not shown in searching stream of unlinked user
        Menu.search(BOB.fullName);
        People.assertPerson(BOB.fullName);
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));
        //check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.assertPostIsShown(BOB,the(tag + " Public Bob"));
        Menu.openStream();
        Feed.assertPostIsShown(BOB,the(tag + " Public Bob"));
        Menu.logOut();

        //todo - include operations with private post
        //todo - include operations with post - comments, likes, reshares
        //todo - include deletion posts

    }



    @Test
    public void testAspects() {
        //todo - add public and limited in different way posts in account from the same pod
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Menu.search(Users.ANA.fullName);
        People.assertPerson(Users.ANA.fullName);
        People.ensureNoAspectsForContact();
        People.manageContact.click();
        People.assertAspectIsUsed(People.toggleAspect(DiasporaAspects.ACQUAINTANCES));
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

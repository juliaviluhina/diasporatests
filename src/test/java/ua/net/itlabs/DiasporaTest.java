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
    public void testUserActivitiesAndLimitOfAccess() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#ana_bob_rob";
        //ANA relation setup
        Diaspora.signInAs(Users.ANA);
        Menu.assertLoggedUser(Users.ANA);
        //ANA have ACQUAINTANCE relation with BOB
        Menu.search(Users.BOB.fullName);
        People.assertPerson(Users.BOB.fullName);
        People.ensureAddContact();
        People.manageContact.click();
        People.assertAspectIsUsed(People.toggleAspect(DiasporaAspects.ACQUAINTANCES));
        //ANA have not any relation with ROB
        Menu.search(Users.ROB.fullName);
        People.assertPerson(Users.ROB.fullName);
        People.ensureAddContact();
        People.assertAspectsAreNotUsed();
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(tag);
        Tags.assertExist(tag);
        Menu.logOut();

        //BOB relation setup
        Diaspora.signInAs(Users.BOB);
        Menu.assertLoggedUser(Users.BOB);
        //BOB have WORK relation with ANA
        Menu.search(Users.ANA.fullName);
        People.assertPerson(Users.ANA.fullName);
        People.ensureAddContact();
        People.manageContact.click();
        People.assertAspectIsUsed(People.toggleAspect(DiasporaAspects.WORK));
        //BOB have not any relation with ROB
        Menu.search(Users.ROB.fullName);
        People.assertPerson(Users.ROB.fullName);
        People.ensureAddContact();
        People.assertAspectsAreNotUsed();
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(tag);
        Tags.assertExist(tag);
        Menu.logOut();

        //ROB relation setup
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        //ROB have not any relation with ANA
        Menu.search(Users.ANA.fullName);
        People.assertPerson(Users.ANA.fullName);
        People.ensureAddContact();
        People.assertAspectsAreNotUsed();
        //ROB have not any relation with BOB
        Menu.search(Users.BOB.fullName);
        People.assertPerson(Users.BOB.fullName);
        People.ensureAddContact();
        People.assertAspectsAreNotUsed();
        //Addition followed tag
        Menu.openStream();
        NavBar.openTags();
        Tags.add(tag);
        Tags.assertExist(tag);
        Menu.logOut();

        //ANAs posts with different sharing
        Diaspora.signInAs(Users.ANA);
        Menu.assertLoggedUser(Users.ANA);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, Users.ANA, the("Public Ana"));
        Feed.addPublicPost(the(tag + " Public Ana"));
        Feed.assertNthPostIs(0, Users.ANA, the(tag + " Public Ana"));
        Feed.addAspectPost(DiasporaAspects.ACQUAINTANCES, the("Ana for acquaintances"));
        Feed.assertNthPostIs(0, Users.ANA, the("Ana for acquaintances"));
        Feed.addAspectPost(DiasporaAspects.WORK, the("Ana for work"));
        Feed.assertNthPostIs(0, Users.ANA, the("Ana for work"));
        Menu.logOut();

        //checks - ANAs posts in BOB feed
        Diaspora.signInAs(Users.BOB);
        Menu.assertLoggedUser(Users.BOB);
        Feed.assertPostIsShown(Users.ANA, the("Public Ana"));
        Feed.assertPostIsShown(Users.ANA, the(tag + " Public Ana"));
        Feed.assertPostIsShown(Users.ANA, the("Ana for acquaintances"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for work"));
        //todo - reshare, comment ... ANA`s posts

        //BOB`s posts with different sharing
        Feed.addPublicPost(the("Public Bob"));
        Feed.assertNthPostIs(0, Users.BOB, the("Public Bob"));
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, Users.BOB, the(tag + " Public Bob"));
        Feed.addAspectPost(DiasporaAspects.ACQUAINTANCES, the("Bob for acquaintances"));
        Feed.assertNthPostIs(0, Users.BOB, the("Bob for acquaintances"));
        Feed.addAspectPost(DiasporaAspects.WORK, the("Bob for work"));
        Feed.assertNthPostIs(0, Users.BOB, the("Bob for work"));
        Menu.logOut();

        //checks - ANA`s and BOB`s posts in ROB feed
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Feed.assertPostIsShown(Users.ANA, the(tag + " Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the("Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for acquaintances"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for work"));
        Feed.assertPostIsShown(Users.BOB, the(tag + " Public Bob"));
        //todo - ... BOB's post
        Feed.assertPostIsNotShown(Users.BOB, the("Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for acquaintances"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for work"));
        //todo - search Ana and check both public post should be visible and limited posts should not be visible
        //todo - reshare, comment ... post ANA`s posts

        //ROB`s posts with different sharing
        Feed.addPublicPost(the("Public Rob"));
        Feed.assertNthPostIs(0, Users.ROB, the("Public Rob"));
        Feed.addPublicPost(the(tag + " Public Rob"));
        Feed.assertNthPostIs(0, Users.ROB, the(tag + " Public Rob"));
        Feed.addAspectPost(DiasporaAspects.ACQUAINTANCES, the("Rob for acquaintances"));
        Feed.assertNthPostIs(0, Users.ROB, the("Rob for acquaintances"));
        Feed.addAspectPost(DiasporaAspects.WORK, the("Rob for work"));
        Feed.assertNthPostIs(0, Users.ROB, the("Rob for work"));
        Menu.logOut();

        //checks - ANA`s, ROB`s and BOB`s posts in ANA feed
        Diaspora.signInAs(Users.ANA);
        Menu.assertLoggedUser(Users.ANA);
        Feed.assertPostIsShown(Users.ANA, the("Public Ana"));
        Feed.assertPostIsShown(Users.ANA, the(tag + " Public Ana"));
        Feed.assertPostIsShown(Users.ANA, the("Ana for acquaintances"));
        Feed.assertPostIsShown(Users.ANA, the("Ana for work"));
        Feed.assertPostIsShown(Users.BOB, the(tag + " Public Bob"));
        Feed.assertPostIsShown(Users.BOB, the("Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for acquaintances"));
        Feed.assertPostIsShown(Users.BOB, the("Bob for work"));
        Feed.assertPostIsShown(Users.ROB, the(tag + " Public Rob"));
        Feed.assertPostIsNotShown(Users.ROB, the("Public Rob"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for acquaintances"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for work"));
        //todo - check BOB`s and ROB`s activity with ANA`s posts and answer them
        //todo - ... BOB`s and ROB`s posts
        Menu.logOut();

        //checks - Rob's posts in Bob feed
        Diaspora.signInAs(Users.BOB);
        Menu.assertLoggedUser(Users.BOB);
        Feed.assertPostIsNotShown(Users.ROB, the(tag + " Public Rob")); //!!!
        Feed.assertPostIsNotShown(Users.ROB, the("Public Rob"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for acquaintances"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for work"));
        //todo - check ANA`s and ROB`s activity with BOB`s posts and answer them
        //todo - switch to my activity and delete some activity (not posts)
        //todo - check deletion activity on feed
        Menu.logOut();

        //checks - BOB`s and ROB`s activities in ANA feed
        Diaspora.signInAs(Users.ANA);
        Menu.assertLoggedUser(Users.ANA);
        //todo - check activities

        //deletion ANA`s posts
        Feed.deletePost(Users.ANA, the("Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the("Public Ana"));
        Feed.deletePost(Users.ANA, the(tag + " Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the(tag + " Public Ana"));
        Feed.deletePost(Users.ANA, the("Ana for acquaintances"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for acquaintances"));
        Feed.deletePost(Users.ANA, the("Ana for work"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for work"));
        Menu.logOut();

        //checks - ANA's post is not available for ROB
        Diaspora.signInAs(Users.ROB);
        Menu.assertLoggedUser(Users.ROB);
        Feed.assertPostIsNotShown(Users.ANA, the(tag + " Public Ana"));
        //todo - how about activities linked with deleted ANA's posts

        //deletion ROB's posts
        Feed.deletePost(Users.ROB, the(tag + " Public Rob"));
        Feed.assertPostIsNotShown(Users.ROB, the(tag + " Public Rob"));
        Feed.deletePost(Users.ROB, the("Public Rob"));
        Feed.assertPostIsNotShown(Users.ROB, the("Public Rob"));
        Feed.deletePost(Users.ROB, the("Rob for acquaintances"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for acquaintances"));
        Feed.deletePost(Users.ROB, the("Rob for work"));
        Feed.assertPostIsNotShown(Users.ROB, the("Rob for work"));
        Menu.logOut();

        //checks - Ana`s and Rob`s post is not available dor Bob
        Diaspora.signInAs(Users.BOB);
        Menu.assertLoggedUser(Users.BOB);
        Feed.assertPostIsNotShown(Users.ANA, the("Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the(tag + " Public Ana"));
        Feed.assertPostIsNotShown(Users.ANA, the("Ana for work"));
        Feed.assertPostIsNotShown(Users.ROB, the(tag + " Public Rob"));

        //deletion BOB's posts
        Feed.deletePost(Users.BOB, the(tag + " Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the(tag + " Public Bob"));
        Feed.deletePost(Users.BOB, the("Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the("Public Bob"));
        Feed.deletePost(Users.BOB, the("Bob for acquaintances"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for acquaintances"));
        Feed.deletePost(Users.BOB, the("Bob for work"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for work"));
        Menu.logOut();

        //checks - ROB`s and BOB`s posts in ANA feed do not exist
        Diaspora.signInAs(Users.ANA);
        Menu.assertLoggedUser(Users.ANA);
        Feed.assertPostIsNotShown(Users.BOB, the(tag + " Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the("Public Bob"));
        Feed.assertPostIsNotShown(Users.BOB, the("Bob for work"));
        Feed.assertPostIsNotShown(Users.ROB, the(tag + " Public Rob"));
        //todo - search Bob's account and check posts
        Menu.logOut();

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

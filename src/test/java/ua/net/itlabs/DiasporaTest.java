package ua.net.itlabs;

import org.junit.Test;
import pages.*;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.helpers.PodUsersRelationsHelper.*;
import static pages.Contact.contact;

public class DiasporaTest extends BaseTest {

    @Test
    public void testFollowedTags() {
        //GIVEN - setup relation between users, addition one the same followed tag
        //new public posts linked with tags in user account from the same pod
        setupLinksFor(ROB,"",ANA);
        setupLinksFor(ANA,"",ROB);
        String post1 = the("Public post with tag " + the("#tag1") + " : ");
        String post2 = the("Public post with tag " + the("#tag2") + " : ");
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.addPublicPost(post1);
        Feed.assertNthPostIs(0, ROB, post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0, ROB, post2);
        Menu.logOut();

        Diaspora.signInAs(ANA);

        //tags is not used and posts is not shown
        Feed.assertPostIsNotShown(ROB, post1);
        Feed.assertPostIsNotShown(ROB, post2);

        NavBar.openTags();

        Tags.add(the("#tag1"));
        Tags.assertExist(the("#tag1"));

        Tags.add(the("#tag2"));
        Tags.assertExist(the("#tag2"));

        //only posts with filtered tag are shown
        Tags.filter(the("#tag1"));
        Feed.assertPostIsShown(ROB, post1);
        Feed.assertPostIsNotShown(ROB, post2);

        //without filtering both posts are shown
        Menu.openStream();
        NavBar.openTags();
        Feed.assertPostIsShown(ROB, post1);
        Feed.assertPostIsShown(ROB, post2);

        Tags.delete(the("#tag1"));
        Tags.assertNotExist(the("#tag1"));

        //in view mode of whole stream posts with followed text are shown
        NavBar.openStream();
        Feed.assertPostIsNotShown(ROB, post1);
        Feed.assertPostIsShown(ROB, post2);

    }

    @Test
    public void testUserActivitiesAndAccessForUsersOfDifferentPods() {
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

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        //Feed.assertPostIsShown(ANA, the("Public Ana"));
        Feed.toggleLike(ANA, the("Public Ana"));
        Feed.assertLikes(ANA, the("Public Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Bob for work"));
        //check - for limited post is no possibility for resharing, indirect check - post is addded
        //Feed.assertNthPostIs(0, BOB, the("Bob for work"));
        Feed.assertReshareIsImpossible(BOB, the("Bob for work"));
        Menu.logOut();

        //like and comment post, indirect check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        //Feed.assertPostIsShown(BOB,the("Bob for work"));
        Feed.toggleLike(BOB, the("Bob for work"));
        Feed.assertLikes(BOB, the("Bob for work"), 1);
        Feed.addComment(BOB, the("Bob for work"), the("Comment from Ana"));
        Feed.assertComment(BOB, the("Bob for work"), ANA, the("Comment from Ana"));

        //limited post in wrong aspect
        Feed.addAspectPost(FAMILY, the("Ana for family"));
        Feed.assertNthPostIs(0, ANA, the("Ana for family"));
        Menu.logOut();

        //check - limited post in wrong aspect is not shown in stream of linked user
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        Feed.assertPostIsNotShown(ANA, the("Ana for family"));

        //comment post, check visibility of other comments
        Feed.assertComment(BOB, the("Bob for work"), ANA, the("Comment from Ana"));
        Feed.addComment(BOB, the("Bob for work"), the("Comment from Bob"));
        Feed.assertComment(BOB, the("Bob for work"), BOB, the("Comment from Bob"));

        //public post with tag
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, BOB, the(tag + " Public Bob"));
        Menu.logOut();

        //reshare post, indirect check - public post with tag is shown in stream of linked used
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        //Feed.assertPostIsShown(BOB,the(tag + " Public Bob"));
        Feed.reshare(BOB, the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, ANA, the(tag + " Public Bob"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.assertPostIsNotShown(ANA, the("Public Ana"));

        //like public post on searching stream, indirect check - public post is shown in searching stream of unlinked user
        Menu.search(ANA.fullName);
        Feed.toggleLike(ANA, the("Public Ana"));
        Feed.assertLikes(ANA, the("Public Ana"), 2);

        //check - limited post is not shown in stream of unlinked user
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));

        //check - limited post is not shown in searching stream of unlinked user
        Menu.search(BOB.fullName);
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));

        //comment post in searching stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        //Feed.assertPostIsShown(BOB,the(tag + " Public Bob"));
        Feed.addComment(BOB, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(BOB, the(tag + " Public Bob"), ROB, the("Comment from Rob"));

        //delete comment in stream
        Menu.openStream();
        //NavBar.openTags();
        //Tags.filter(tag);
        Feed.deleteComment(BOB, the(tag + " Public Bob"), ROB, the("Comment from Rob"));
        Feed.assertCommentIsNotExist(BOB, the(tag + " Public Bob"), ROB, the("Comment from Rob"));

        //unlike through MyActivities, indirect check - posts which liked earlier by this user is shown on my activity
        NavBar.openMyActivity();
        Feed.toggleLike(ANA, the("Public Ana"));
        Feed.assertLikes(ANA, the("Public Ana"), 1);
        Menu.logOut();

        //delete public post
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        NavBar.openMyActivity();
        Feed.deletePost(ANA, the("Public Ana"));
        Feed.assertPostIsNotShown(ANA, the("Public Ana"));

        //delete reshared post
        NavBar.openStream();
        Feed.deletePost(ANA, the(tag + " Public Bob"));
        Feed.assertPostIsNotShown(ANA, the(tag + " Public Bob"));

        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(BOB, the("Bob for work"), BOB, the("Comment from Bob"));
        Menu.logOut();

        //delete limited post in my activity stream
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        NavBar.openMyActivity();
        Feed.deleteComment(BOB, the("Bob for work"), BOB, the("Comment from Bob"));
        Feed.deletePost(BOB, the("Bob for work"));
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));
        Menu.logOut();

        //check post of another user can not be deleted
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostCanNotBeDeleted(BOB, the(tag + " Public Bob"));

        //check - limited post is not shown after deletion
        Feed.assertPostIsNotShown(BOB, the("Bob for work"));

        //check - after deletion reshared post in resharing post is no old content
        Feed.assertPostIsNotShown(ANA, the(tag + " Public Bob"));
        Menu.logOut();

    }

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#a_r";
        //who with whom through which aspect, which followed tag, with whom are not any links
        setupLinksFor(ANA, ROB, ACQUAINTANCES, tag, EVE);
        setupLinksFor(ROB, ANA, WORK, tag, EVE);
        setupLinksFor(EVE, tag, ANA, ROB);

        //public post
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA, the("Public Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.toggleLike(ANA, the("Public Ana"));
        Feed.assertLikes(ANA, the("Public Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Rob for work"));
        //check - for limited post is no possibility for resharing, indirect check - post is added
        Feed.assertReshareIsImpossible(ROB, the("Rob for work"));
        Menu.logOut();

        //like and comment post, indirect check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.toggleLike(ROB, the("Rob for work"));
        Feed.assertLikes(ROB, the("Rob for work"), 1);
        Feed.addComment(ROB, the("Rob for work"), the("Comment from Ana"));
        Feed.assertComment(ROB, the("Rob for work"), ANA, the("Comment from Ana"));

        //limited post in wrong aspect
        Feed.addAspectPost(FAMILY, the("Ana for family"));
        Feed.assertNthPostIs(0, ANA, the("Ana for family"));
        Menu.logOut();

        //check - limited post in wrong aspect is not shown in stream of linked user
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.assertPostIsNotShown(ANA, the("Ana for family"));

        //comment post, check visibility of other comments
        Feed.assertComment(ROB, the("Rob for work"), ANA, the("Comment from Ana"));
        Feed.addComment(ROB, the("Rob for work"), the("Comment from Rob"));
        Feed.assertComment(ROB, the("Rob for work"), ROB, the("Comment from Rob"));

        //public post with tag
        Feed.addPublicPost(the(tag + " Public Rob"));
        Feed.assertNthPostIs(0, ROB, the(tag + " Public Rob"));
        Menu.logOut();

        //reshare post, indirect check - public post with tag is shown in stream of linked used
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.reshare(ROB, the(tag + " Public Rob"));
        Feed.assertNthPostIs(0, ANA, the(tag + " Public Rob"));

        //delete post
        NavBar.openMyActivity();
        Feed.deletePost(ANA, the("Public Ana"));
        Feed.assertPostIsNotShown(ANA, the("Public Ana"));

        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(ROB, the("Rob for work"), ROB, the("Comment from Rob"));

        //check post of another user can not be deleted
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(ROB, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        //check - resharing post is shown
        Feed.assertPostIsShown(ANA, the(tag + " Public Rob"));
        //check - deleted post is not shown
        Feed.assertPostIsNotShown(ANA, the("Public Ana"));

        NavBar.openMyActivity();
        //delete comment in my activity stream
        Feed.deleteComment(ROB, the("Rob for work"), ROB, the("Comment from Rob"));
        //delete limited post in my activity stream
        Feed.deletePost(ROB, the("Rob for work"));
        Feed.assertPostIsNotShown(ROB, the("Rob for work"));
        //delete reshared post
        Feed.deletePost(ROB, the(tag + " Public Rob"));
        Feed.assertPostIsNotShown(ROB, the(tag + " Public Rob"));
        Menu.logOut();

        //add private post
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertNthPostIs(0, ANA, the("Private Ana"));

        //add all aspects post
        Feed.addAllAspectsPost(the("All aspects Ana"));
        Feed.assertNthPostIs(0, ANA, the("All aspects Ana"));

        //check - limited post is not shown after deletion
        Feed.assertPostIsNotShown(ROB, the("Rob for work"));

        //check - reshared post is not shown after deletion
        Feed.assertPostIsNotShown(ANA, the(tag + " Public Rob"));

        //check - resharing post do not contain data from deleted reshared post
        Feed.assertPostIsNotShown(ANA, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        //add new public post with tag, like, comment
        Feed.addPublicPost(the(tag + " Public Rob next"));
        Feed.toggleLike(ROB, the(tag + " Public Rob next"));
        Feed.assertLikes(ROB, the(tag + " Public Rob next"), 1);
        Feed.addComment(ROB, the(tag + " Public Rob next"), "Comment from Rob");
        Feed.assertComment(ROB, the(tag + " Public Rob next"), ROB, "Comment from Rob");
        //check post for all aspects is shown
        Feed.assertPostIsShown(ANA, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertPostIsNotShown(ANA, the("Private Ana"));
        Menu.logOut();

        Diaspora.signInAs(EVE);
        Menu.assertLoggedUser(EVE);
        //like public post, indirect check - public post with tag is shown
        Feed.toggleLike(ROB, the(tag + " Public Rob next"));
        ;
        Feed.assertLikes(ROB, the(tag + " Public Rob next"), 2);
        //add comment, delete comment
        Feed.addComment(ROB, the(tag + " Public Rob next"), "Comment from Eve");
        Feed.deleteComment(ROB, the(tag + " Public Rob next"), EVE, "Comment from Eve");
        //check - comments of other users is shown and can not be deleted
        Feed.assertCommentCanNotBeDeleted(ROB, the(tag + " Public Rob next"), ROB, "Comment from Rob");
        //check post for all aspects is shown
        Feed.assertPostIsNotShown(ANA, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertPostIsNotShown(ANA, the("Private Ana"));
        //deleted public post with tag is not shown
        Feed.assertPostIsNotShown(ROB, the(tag + " Public Rob"));
        Menu.logOut();

    }


    @Test
    public void testAspects() {
        //GIVEN - setup relation between users in some aspect
        //who with whom through which aspects
        String tag = "#a_r";
        setupLinksFor(ANA, ROB, tag, EVE, FAMILY, FRIENDS);
        setupLinksFor(ROB, ANA, tag, EVE, WORK, ACQUAINTANCES);
        setupLinksFor(EVE, tag, ANA, ROB);
        //add posts - Eve
        Diaspora.signInAs(EVE);
        Menu.assertLoggedUser(EVE);
        Feed.addPublicPost(the(tag + " Public Eve"));
        Feed.assertNthPostIs(0, EVE, the(tag + " Public Eve"));
        Feed.addAllAspectsPost(the("All aspects Eve"));
        Feed.assertNthPostIs(0, EVE, the("All aspects Eve"));
        Menu.logOut();
        //add posts - Ana
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertNthPostIs(0, ANA, the("Ana for friends"));
        Feed.addAspectPost(WORK, the("Ana for work"));
        Feed.assertNthPostIs(0, ANA, the("Ana for work"));
        Menu.logOut();
        //Add posts - Rob
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.addAspectPost(FAMILY, the("Rob for family"));

        //check - all available posts in Rob's stream
        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //filtering - all aspects is disabled - all aspects
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");
        Feed.assertNthPostIs(0, ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //filtering - Friends is enabled
        Aspects.toggleAspect(FRIENDS);
        Feed.assertPostIsNotShown(ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //filtering - Family, Friends is enabled
        Aspects.toggleAspect(FAMILY);
        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //filtering - Family, Friends, Work is enabled
        Aspects.toggleAspect(WORK);
        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //add new aspect and link user in aspect
        Aspects.add(the("Aspect"));
        Menu.search(EVE.fullName);
        Contact.ensureAspectsForContact(the("Aspect"));
        //add new post in this aspect
        Menu.openStream();
        Feed.addAspectPost(the("Aspect"), the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(ROB, the(the("Aspect") + " Rob for new aspect"));

        //deselect aspect work and select added aspect
        NavBar.openMyAspects();
        Aspects.toggleAspect(WORK);
        Aspects.toggleAspect(the("Aspect"));

        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsShown(ROB, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //edit aspect
        Aspects.switchToEditMode(the("Aspect"));
        Contacts.rename(the("Asp"));
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertAspectIsNotShownInNavBar(the("Aspect"));

        //delete aspect
        Aspects.switchToEditMode(the("Asp"));
        Contacts.deleteAspect();
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertAspectIsNotShownInNavBar(the("Asp"));

        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(ROB, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));

        //select all
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");
        Feed.assertPostIsNotShown(ROB, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(ROB, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA, the("Ana for work"));
    }


//    //for test case #1 - Expected result
//    @Test
//    public void testSignInForAccountWithPosts() {
//        Diaspora.signInAs(BOB);
//        NavBar.navBar.shouldBe(visible);
//    }
//
//    //for test case #1 - Actual result
//    @Test
//    @Buggy
//    public void testSignInForAccountWithoutPosts() {
//        Diaspora.signInAs(DAVE);
//        NavBar.navBar.shouldBe(visible);
//    }
//
//    //for test case #2
//    @Test
//    @Buggy
//    public void testTagsOrder() {
//        //step 1,2 - add tag Y and Z
//        Diaspora.signInAs(ANA);
//        NavBar.openTags();
//        Tags.add(the("#Ytag"));
//        Tags.assertExist(the("#Ytag"));
//        Tags.add(the("#Ztag"));
//        Tags.assertExist(the("#Ztag"));
//
//        //step 3 - check order - like in actual result
//        Tags.assertNthIs(0, the("#Ztag"));
//        Tags.assertNthIs(1, the("#Ytag"));
//
//        //step 4
//        Menu.logOut();
//        Diaspora.signInAs(ANA);
//        NavBar.openTags();
//
//        //actual result - tag order is different
//        Tags.assertNthIs(0, the("#Ztag"));
//        Tags.assertNthIs(1, the("#Ytag"));
//
//    }

    @Test
    public void testContacts() {
        //GIVEN - setup relation between users in some aspect
        //who with whom through which aspects
        setupLinksFor(ROB, ANA, "", EVE, FAMILY, FRIENDS);
        //add posts for different aspects
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Feed.addAspectPost(FAMILY, the("Rob for Family"));
        Feed.assertNthPostIs(0, ROB, the("Rob for Family"));
        Feed.addAspectPost(FRIENDS, the("Rob for Friends"));
        Feed.assertNthPostIs(0, ROB, the("Rob for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances"));
        Feed.assertNthPostIs(0, ROB, the("Rob for Acquaintances"));
        //add new aspect in Contacts page, add relation in this aspect
        Menu.openContacts();
        Contacts.addAspect(the("Aspect"));
        Contacts.selectAspect(the("Aspect"));
        Contacts.addLinkedContactForAspect(the("Aspect"), ANA);
        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
        Contacts.assertCountContactsInAspect(the("Aspect"), 1);

        //delete aspect for contact
        int countFriends = Contacts.countContactsInAspect(FRIENDS);
        Contacts.deleteLinkedContactForAspect(FRIENDS, ANA);
        countFriends--;
        //check aspect counter
        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
        Contacts.assertCountContactsInAspect(FRIENDS, countFriends);

        //add post for Friends after deletion aspect for contact
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Rob for Friends 2 "));
        Feed.assertNthPostIs(0, ROB, the("Rob for Friends 2 "));

        //add post for new aspect
        Feed.addAspectPost(the("Aspect"), the("Aspect")+the(" Rob for new aspect"));
        Feed.assertNthPostIs(0, ROB, the("Aspect")+the(" Rob for new aspect"));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostIsShown(ROB, the("Rob for Family"));
        Feed.assertPostIsShown(ROB, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB, the("Aspect")+the(" Rob for new aspect"));
        Menu.logOut();

        //check posts in Eve`s stream
        Diaspora.signInAs(EVE);
        Menu.assertLoggedUser(EVE);
        Feed.assertPostIsNotShown(ROB, the("Rob for Family"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Acquaintances"));
        Feed.assertPostIsNotShown(ROB, the("Aspect") + the(" Rob for new aspect"));
        Menu.logOut();

        //change Rob`s aspects for Ana through button
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Menu.openContacts();
        Contacts.openAllContacts();
        Contact.ensureAspectsForContact(contact(ANA), ACQUAINTANCES, the("Aspect"));

        //add post for Friends after addition aspect for contact
        Menu.openStream();
        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances 2 "));
        Feed.assertNthPostIs(0, ROB, the("Rob for Acquaintances 2 "));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostIsShown(ROB, the("Rob for Family"));
        Feed.assertPostIsShown(ROB, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB, the("Rob for Acquaintances 2 "));
        Feed.assertPostIsShown(ROB, the("Aspect")+the(" Rob for new aspect"));

        Menu.logOut();

        //delete Rob`s aspect
        Diaspora.signInAs(ROB);
        Menu.assertLoggedUser(ROB);
        Menu.openContacts();
        Contacts.selectAspect(the("Aspect"));
        Contacts.deleteAspect();
        Contacts.assertAspectIsNotShown(the("Aspect"));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA);
        Menu.assertLoggedUser(ANA);
        Feed.assertPostIsShown(ROB, the("Rob for Family"));
        Feed.assertPostIsShown(ROB, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB, the("Rob for Acquaintances 2 "));
        Feed.assertPostIsShown(ROB, the("Aspect") + the(" Rob for new aspect"));
        Menu.logOut();

    }

    @Test
    public void testChainOfSearch() {
        Diaspora.signInAs(BOB);
        Menu.assertLoggedUser(BOB);
        Menu.search(ANA.fullName);
        Menu.search(ROB.fullName);
        Menu.search(SAM.fullName);
        open(BOB.podLink+"/people?q=eve_tjvi%40diaspora.koehn.com");
        Contact.ensureSearchedContact(EVE.fullName);
    }
}

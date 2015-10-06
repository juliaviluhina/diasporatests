package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;
import ua.net.itlabs.categories.Buggy;

import static com.codeborne.selenide.Condition.appear;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static pages.Contact.contact;

public class DiasporaTest extends BaseTest {

    @Test
    public void testFollowedTags() {
        //GIVEN - setup relation between users, addition one the same followed tag
        //new public posts linked with tags in user account from the same pod
        Relation.forUser(ROB_P1).notToUsers(ANA_P1).build();
        Relation.forUser(ANA_P1).notToUsers(ROB_P1).build();
        String post1 = the("Public post with tag " + the("#tag1") + " : ");
        String post2 = the("Public post with tag " + the("#tag2") + " : ");
        Diaspora.signInAs(ROB_P1);
        Feed.addPublicPost(post1);
        Feed.addPublicPost(post2);
        Menu.logOut();

        Diaspora.signInAs(ANA_P1);

        //tags is not used and public posts is not shown in stream
        Feed.assertPostIsNotShown(ROB_P1, post1);
        Feed.assertPostIsNotShown(ROB_P1, post2);

        NavBar.openTags();

        Tags.add(the("#tag1"));
        //only posts with filtered tag are shown
        Tags.filter(the("#tag1"));
        Feed.assertPostIsShown(ROB_P1, post1);
        Feed.assertPostIsNotShown(ROB_P1, post2);

        Menu.openStream();
        NavBar.openTags();

        Tags.add(the("#tag2"));
        Tags.filter(the("#tag2"));
        Feed.assertPostIsNotShown(ROB_P1, post1);
        Feed.assertPostIsShown(ROB_P1, post2);

        Menu.openStream();
        Feed.assertPostIsShown(ROB_P1, post1);
        Feed.assertPostIsShown(ROB_P1, post2);

        NavBar.openTags();
        Tags.delete(the("#tag1"));
        Tags.assertNotExist(the("#tag1"));

        //in view mode of whole stream posts with followed text are shown
        NavBar.openStream();
        Feed.assertPostIsNotShown(ROB_P1, post1);
        Feed.assertPostIsShown(ROB_P1, post2);

    }

    @Test
    public void testUserActivitiesAndAccessForUsersOfDifferentPods() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#ana_bob_rob_sam";
        Relation.forUser(ANA_P1).toUser(BOB_P2,ACQUAINTANCES).notToUsers(ROB_P1, SAM_P2).withTags(tag).build();
        Relation.forUser(BOB_P2).toUser(ANA_P1,WORK).notToUsers(ROB_P1, SAM_P2).withTags(tag).build();
        Relation.forUser(ROB_P1).toUser(SAM_P2, FRIENDS).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
        Relation.forUser(SAM_P2).toUser(ROB_P1, FAMILY).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();

        //public post
        Diaspora.signInAs(ANA_P1);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Public Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(BOB_P2);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Bob for work"));
        //check - for limited post is no possibility for resharing, indirect check - post is addded
        Feed.assertReshareIsImpossible(BOB_P2, the("Bob for work"));
        Menu.logOut();

        //like and comment post, indirect check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA_P1);
        Feed.toggleLike(BOB_P2, the("Bob for work"));
        Feed.assertLikes(BOB_P2, the("Bob for work"), 1);
        Feed.addComment(BOB_P2, the("Bob for work"), the("Comment from Ana"));
        Feed.assertComment(BOB_P2, the("Bob for work"), ANA_P1, the("Comment from Ana"));

        //limited post in wrong aspect
        Feed.addAspectPost(FAMILY, the("Ana for family"));
        Feed.assertNthPostIs(0, ANA_P1, the("Ana for family"));
        Menu.logOut();

        //check - limited post in wrong aspect is not shown in stream of linked user
        Diaspora.signInAs(BOB_P2);
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for family"));

        //comment post, check visibility of other comments
        Feed.assertComment(BOB_P2, the("Bob for work"), ANA_P1, the("Comment from Ana"));
        Feed.addComment(BOB_P2, the("Bob for work"), the("Comment from Bob"));
        Feed.assertComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));

        //public post with tag
        Feed.addPublicPost(the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, BOB_P2, the(tag + " Public Bob"));
        Menu.logOut();

        //reshare post, indirect check - public post with tag is shown in stream of linked used
        Diaspora.signInAs(ANA_P1);
        Feed.reshare(BOB_P2, the(tag + " Public Bob"));
        Feed.assertNthPostIs(0, ANA_P1, the(tag + " Public Bob"));
        Menu.logOut();

        //check - public post is not shown in stream of unlinked user
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostIsNotShown(ANA_P1, the("Public Ana"));

        //like public post on searching stream, indirect check - public post is shown in searching stream of unlinked user
        Menu.search(ANA_P1.fullName);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 2);

        //check - limited post is not shown in stream of unlinked user
        Feed.assertPostIsNotShown(BOB_P2, the("Bob for work"));

        //check - limited post is not shown in searching stream of unlinked user
        Menu.search(BOB_P2.fullName);
        Feed.assertPostIsNotShown(BOB_P2, the("Bob for work"));

        //comment post in searching stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.addComment(BOB_P2, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));

        //delete comment in stream
        Menu.openStream();
        Feed.deleteComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
        Feed.assertCommentIsNotExist(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));

        //unlike through MyActivities, indirect check - posts which liked earlier by this user is shown on my activity
        NavBar.openMyActivity();
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);
        Menu.logOut();

        //delete public post
        Diaspora.signInAs(ANA_P1);
        NavBar.openMyActivity();
        Feed.deletePost(ANA_P1, the("Public Ana"));
        Feed.assertPostIsNotShown(ANA_P1, the("Public Ana"));

        //delete reshared post
        NavBar.openStream();
        Feed.deletePost(ANA_P1, the(tag + " Public Bob"));
        Feed.assertPostIsNotShown(ANA_P1, the(tag + " Public Bob"));

        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
        Menu.logOut();

        //delete limited post in my activity stream
        Diaspora.signInAs(BOB_P2);
        NavBar.openMyActivity();
        Feed.deleteComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
        Feed.deletePost(BOB_P2, the("Bob for work"));
        Feed.assertPostIsNotShown(BOB_P2, the("Bob for work"));
        Menu.logOut();

        //check post of another user can not be deleted
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostCanNotBeDeleted(BOB_P2, the(tag + " Public Bob"));

        //check - limited post is not shown after deletion
        Feed.assertPostIsNotShown(BOB_P2, the("Bob for work"));

        //check - after deletion reshared post in resharing post is no old content
        Feed.assertPostIsNotShown(ANA_P1, the(tag + " Public Bob"));
        Menu.logOut();

    }

    @Test
    public void testUserActivitiesAndAccessForUsersOfOnePod() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#a_r";
        Relation.forUser(ANA_P1).toUser(ROB_P1,ACQUAINTANCES).notToUsers(EVE_P1).withTags(tag).build();
        Relation.forUser(ROB_P1).toUser(ANA_P1,WORK).notToUsers(EVE_P1).withTags(tag).build();
        Relation.forUser(EVE_P1).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();

        //public post
        Diaspora.signInAs(ANA_P1);
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Public Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(ROB_P1);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Rob for work"));
        //check - for limited post is no possibility for resharing, indirect check - post is added
        Feed.assertReshareIsImpossible(ROB_P1, the("Rob for work"));
        Menu.logOut();

        //like and comment post, indirect check - limited post in right aspect is shown in stream of linked user
        Diaspora.signInAs(ANA_P1);
        Feed.toggleLike(ROB_P1, the("Rob for work"));
        Feed.assertLikes(ROB_P1, the("Rob for work"), 1);
        Feed.addComment(ROB_P1, the("Rob for work"), the("Comment from Ana"));
        Feed.assertComment(ROB_P1, the("Rob for work"), ANA_P1, the("Comment from Ana"));

        //limited post in wrong aspect
        Feed.addAspectPost(FAMILY, the("Ana for family"));
        Feed.assertNthPostIs(0, ANA_P1, the("Ana for family"));
        Menu.logOut();

        //check - limited post in wrong aspect is not shown in stream of linked user
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for family"));

        //comment post, check visibility of other comments
        Feed.assertComment(ROB_P1, the("Rob for work"), ANA_P1, the("Comment from Ana"));
        Feed.addComment(ROB_P1, the("Rob for work"), the("Comment from Rob"));
        Feed.assertComment(ROB_P1, the("Rob for work"), ROB_P1, the("Comment from Rob"));

        //public post with tag
        Feed.addPublicPost(the(tag + " Public Rob"));
        Feed.assertNthPostIs(0, ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();

        //reshare post, indirect check - public post with tag is shown in stream of linked used
        Diaspora.signInAs(ANA_P1);
        Feed.reshare(ROB_P1, the(tag + " Public Rob"));
        Feed.assertNthPostIs(0, ANA_P1, the(tag + " Public Rob"));

        //delete post
        NavBar.openMyActivity();
        Feed.deletePost(ANA_P1, the("Public Ana"));
        Feed.assertPostIsNotShown(ANA_P1, the("Public Ana"));

        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(ROB_P1, the("Rob for work"), ROB_P1, the("Comment from Rob"));

        //check post of another user can not be deleted
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB_P1);
        //check - resharing post is shown
        Feed.assertPostIsShown(ANA_P1, the(tag + " Public Rob"));
        //check - deleted post is not shown
        Feed.assertPostIsNotShown(ANA_P1, the("Public Ana"));

        NavBar.openMyActivity();
        //delete comment in my activity stream
        Feed.deleteComment(ROB_P1, the("Rob for work"), ROB_P1, the("Comment from Rob"));
        //delete limited post in my activity stream
        Feed.deletePost(ROB_P1, the("Rob for work"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for work"));
        //delete reshared post
        Feed.deletePost(ROB_P1, the(tag + " Public Rob"));
        Feed.assertPostIsNotShown(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();

        //add private post
        Diaspora.signInAs(ANA_P1);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Private Ana"));

        //add all aspects post
        Feed.addAllAspectsPost(the("All aspects Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("All aspects Ana"));

        //check - limited post is not shown after deletion
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for work"));

        //check - reshared post is not shown after deletion
        Feed.assertPostIsNotShown(ANA_P1, the(tag + " Public Rob"));

        //check - resharing post do not contain data from deleted reshared post
        Feed.assertPostIsNotShown(ANA_P1, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB_P1);
        //add new public post with tag, like, comment
        Feed.addPublicPost(the(tag + " Public Rob next"));
        Feed.toggleLike(ROB_P1, the(tag + " Public Rob next"));
        Feed.assertLikes(ROB_P1, the(tag + " Public Rob next"), 1);
        Feed.addComment(ROB_P1, the(tag + " Public Rob next"), "Comment from Rob");
        Feed.assertComment(ROB_P1, the(tag + " Public Rob next"), ROB_P1, "Comment from Rob");
        //check post for all aspects is shown
        Feed.assertPostIsShown(ANA_P1, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertPostIsNotShown(ANA_P1, the("Private Ana"));
        Menu.logOut();

        Diaspora.signInAs(EVE_P1);
        //like public post, indirect check - public post with tag is shown
        Feed.toggleLike(ROB_P1, the(tag + " Public Rob next"));
        Feed.assertLikes(ROB_P1, the(tag + " Public Rob next"), 2);
        //add comment, delete comment
        Feed.addComment(ROB_P1, the(tag + " Public Rob next"), "Comment from Eve");
        Feed.deleteComment(ROB_P1, the(tag + " Public Rob next"), EVE_P1, "Comment from Eve");
        //check - comments of other users is shown and can not be deleted
        Feed.assertCommentCanNotBeDeleted(ROB_P1, the(tag + " Public Rob next"), ROB_P1, "Comment from Rob");
        //check post for all aspects is shown
        Feed.assertPostIsNotShown(ANA_P1, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertPostIsNotShown(ANA_P1, the("Private Ana"));
        //deleted public post with tag is not shown
        Feed.assertPostIsNotShown(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();

    }


    @Test
    public void testAspects() {
        //GIVEN - setup relation between users in some aspect
        String tag = "#a_r";
        Relation.forUser(ANA_P1).toUser(ROB_P1, FAMILY, FRIENDS).notToUsers(EVE_P1).withTags(tag).build();
        Relation.forUser(ROB_P1).toUser(ANA_P1, WORK, ACQUAINTANCES).notToUsers(EVE_P1).withTags(tag).build();
        Relation.forUser(EVE_P1).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();

        //add posts - Eve
        Diaspora.signInAs(EVE_P1);
        Feed.addPublicPost(the(tag + " Public Eve"));
        Feed.assertNthPostIs(0, EVE_P1, the(tag + " Public Eve"));
        Feed.addAllAspectsPost(the("All aspects Eve"));
        Feed.assertNthPostIs(0, EVE_P1, the("All aspects Eve"));
        Menu.logOut();
        //add posts - Ana
        Diaspora.signInAs(ANA_P1);
        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
        Feed.assertNthPostIs(0, ANA_P1, the("Ana for friends"));
        Feed.addAspectPost(WORK, the("Ana for work"));
        Feed.assertNthPostIs(0, ANA_P1, the("Ana for work"));
        Menu.logOut();
        //Add posts - Rob
        Diaspora.signInAs(ROB_P1);
        Feed.addAspectPost(FAMILY, the("Rob for family"));

        //check - all available posts in Rob's stream
        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //filtering - all aspects is disabled - all aspects
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");
        Feed.assertNthPostIs(0, ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //filtering - Friends is enabled
        Aspects.toggleAspect(FRIENDS);
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //filtering - Family, Friends is enabled
        Aspects.toggleAspect(FAMILY);
        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //filtering - Family, Friends, Work is enabled
        Aspects.toggleAspect(WORK);
        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //add new aspect and link user in aspect
        Aspects.add(the("Aspect"));
        Menu.search(EVE_P1.fullName);
        Contact.ensureAspectsForContact(the("Aspect"));
        //add new post in this aspect
        Menu.openStream();
        Feed.addAspectPost(the("Aspect"), the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(ROB_P1, the(the("Aspect") + " Rob for new aspect"));

        //deselect aspect work and select added aspect
        NavBar.openMyAspects();
        Aspects.toggleAspect(WORK);
        Aspects.toggleAspect(the("Aspect"));

        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsShown(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

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

        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));

        //select all
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");
        Feed.assertPostIsNotShown(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for family"));
        Feed.assertPostIsNotShown(EVE_P1, the(tag + " Public Eve"));
        Feed.assertPostIsNotShown(EVE_P1, the("All aspects Eve"));
        Feed.assertPostIsShown(ANA_P1, the("Ana for friends"));
        Feed.assertPostIsNotShown(ANA_P1, the("Ana for work"));
    }

    //for test case #1 - Expected result
    @Test
    public void testSignInForAccountWithPosts() {
        Diaspora.signInAs(BOB_P2);
        NavBar.should(appear);
    }

    //for test case #1 - Actual result
    @Test
    @Buggy
    public void testSignInForAccountWithoutPosts() {
        Diaspora.signInAs(DAVE_P3);
        NavBar.should(appear);
    }

    //for test case #2
    @Test
    @Buggy
    public void testTagsOrder() {
        //step 1,2 - add tag Y and Z
        Diaspora.signInAs(ANA_P1);
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
        Diaspora.signInAs(ANA_P1);
        NavBar.openTags();

        //actual result - tag order is different
        Tags.assertNthIs(0, the("#Ztag"));
        Tags.assertNthIs(1, the("#Ytag"));

    }

    @Test
    public void testContacts() {
        //GIVEN - setup relation between users in some aspect
        //add posts for different aspects
        Relation.forUser(BOB_P2).toUser(ANA_P1, FAMILY, FRIENDS).notToUsers(EVE_P1).build();
        Diaspora.signInAs(ROB_P1);
        Feed.addAspectPost(FAMILY, the("Rob for Family"));
        Feed.addAspectPost(FRIENDS, the("Rob for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances"));

        //add new aspect in Contacts page, add relation in this aspect
        Menu.openContacts();
        Contacts.addAspect(the("Aspect"));
        Contacts.selectAspect(the("Aspect"));
        Contacts.addLinkedContactForAspect(the("Aspect"), ANA_P1);
        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
        Contacts.assertCountContactsInAspect(the("Aspect"), 1);

        //delete aspect for contact
        int countFriends = Contacts.countContactsInAspect(FRIENDS);
        Contacts.deleteLinkedContactForAspect(FRIENDS, ANA_P1);
        countFriends--;
        //check aspect counter
        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
        Contacts.assertCountContactsInAspect(FRIENDS, countFriends);

        //add post for Friends after deletion aspect for contact
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Rob for Friends 2 "));
        Feed.assertNthPostIs(0, ROB_P1, the("Rob for Friends 2 "));

        //add post for new aspect
        Feed.addAspectPost(the("Aspect"), the("Aspect")+the(" Rob for new aspect"));
        Feed.assertNthPostIs(0, ROB_P1, the("Aspect")+the(" Rob for new aspect"));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostIsShown(ROB_P1, the("Rob for Family"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB_P1, the("Aspect")+the(" Rob for new aspect"));
        Menu.logOut();

        //check posts in Eve`s stream
        Diaspora.signInAs(EVE_P1);
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Family"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Acquaintances"));
        Feed.assertPostIsNotShown(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
        Menu.logOut();

        //change Rob`s aspects for Ana through button
        Diaspora.signInAs(ROB_P1);
        Menu.openContacts();
        Contacts.openAllContacts();
        Contact.ensureAspectsForContact(contact(ANA_P1), ACQUAINTANCES, the("Aspect"));

        //add post for Friends after addition aspect for contact
        Menu.openStream();
        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances 2 "));
        Feed.assertNthPostIs(0, ROB_P1, the("Rob for Acquaintances 2 "));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostIsShown(ROB_P1, the("Rob for Family"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for Acquaintances 2 "));
        Feed.assertPostIsShown(ROB_P1, the("Aspect")+the(" Rob for new aspect"));

        Menu.logOut();

        //delete Rob`s aspect
        Diaspora.signInAs(ROB_P1);
        Menu.openContacts();
        Contacts.selectAspect(the("Aspect"));
        Contacts.deleteAspect();
        Contacts.assertAspectIsNotShown(the("Aspect"));
        Menu.logOut();

        //check posts in Ana`s stream
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostIsShown(ROB_P1, the("Rob for Family"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for Friends"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Friends 2"));
        Feed.assertPostIsNotShown(ROB_P1, the("Rob for Acquaintances"));
        Feed.assertPostIsShown(ROB_P1, the("Rob for Acquaintances 2 "));
        Feed.assertPostIsShown(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
        Menu.logOut();

    }

    @Test
    public void testChainOfSearch() {
//        Diaspora.signInAs(BOB_P2);
//        Menu.assertLoggedUser(BOB_P2);
//        Menu.search(ANA_P1.fullName);
//        Menu.search(ROB_P1.fullName);
//        Menu.search(SAM_P2.fullName);
//        open(BOB_P2.podLink+"/people?q=eve_tjvi%40diaspora.koehn.com");
//        Contact.ensureSearchedContact(EVE_P1.fullName);
        Relation.forUser(ANA_P1).notToUsers(BOB_P2, ROB_P1, SAM_P2, EVE_P1).build();
        Relation.forUser(BOB_P2).notToUsers(ANA_P1, ROB_P1, EVE_P1, SAM_P2).build();
        Relation.forUser(SAM_P2).notToUsers(ANA_P1, EVE_P1, BOB_P2, ROB_P1).build();
        Relation.forUser(EVE_P1).notToUsers(EVE_P1, BOB_P2, ROB_P1, SAM_P2).build();
    }
}

package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;

public class DiasporaE2ELifeCycleTest extends BaseTest {

//    @Test
//    public void testUserActivitiesAndAccessForUsersOfDifferentPods() {
//        //GIVEN - setup relation between users, addition one the same followed tag
//        String tag = "#ana_bob_rob_sam";
//        Relation.forUser(ANA_P1).toUser(BOB_P2,ACQUAINTANCES).notToUsers(ROB_P1, SAM_P2).withTags(tag).build();
//        Relation.forUser(BOB_P2).toUser(ANA_P1,WORK).notToUsers(ROB_P1, SAM_P2).withTags(tag).build();
//        Relation.forUser(ROB_P1).toUser(SAM_P2, FRIENDS).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
//        Relation.forUser(SAM_P2).toUser(ROB_P1, FAMILY).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
//
//        //public post
//        Diaspora.signInAs(ANA_P1);
//        Feed.addPublicPost(the("Public Ana"));
//        Feed.assertNthPostIs(0, ANA_P1, the("Public Ana"));
//        Menu.logOut();
//
//        //like post, indirect check - public post is shown in stream of linked user
//        Diaspora.signInAs(BOB_P2);
//        Feed.toggleLike(ANA_P1, the("Public Ana"));
//        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);
//
//        //limited post in right aspect
//        Feed.addAspectPost(WORK, the("Bob for work"));
//        //check - for limited post is no possibility for resharing, indirect check - post is addded
//        Feed.assertPostCanNotBeReshared(BOB_P2, the("Bob for work"));
//        Menu.logOut();
//
//        //like and comment post, indirect check - limited post in right aspect is shown in stream of linked user
//        Diaspora.signInAs(ANA_P1);
//        Feed.toggleLike(BOB_P2, the("Bob for work"));
//        Feed.assertLikes(BOB_P2, the("Bob for work"), 1);
//        Feed.addComment(BOB_P2, the("Bob for work"), the("Comment from Ana"));
//        Feed.assertComment(BOB_P2, the("Bob for work"), ANA_P1, the("Comment from Ana"));
//
//        //limited post in wrong aspect
//        Feed.addAspectPost(FAMILY, the("Ana for family"));
//        Feed.assertNthPostIs(0, ANA_P1, the("Ana for family"));
//        Menu.logOut();
//
//        //check - limited post in wrong aspect is not shown in stream of linked user
//        Diaspora.signInAs(BOB_P2);
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for family"));
//
//        //comment post, check visibility of other comments
//        Feed.assertComment(BOB_P2, the("Bob for work"), ANA_P1, the("Comment from Ana"));
//        Feed.addComment(BOB_P2, the("Bob for work"), the("Comment from Bob"));
//        Feed.assertComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
//
//        //public post with tag
//        Feed.addPublicPost(the(tag + " Public Bob"));
//        Feed.assertNthPostIs(0, BOB_P2, the(tag + " Public Bob"));
//        Menu.logOut();
//
//        //reshare post, indirect check - public post with tag is shown in stream of linked used
//        Diaspora.signInAs(ANA_P1);
//        Feed.reshare(BOB_P2, the(tag + " Public Bob"));
//        Feed.assertNthPostIs(0, ANA_P1, the(tag + " Public Bob"));
//        Menu.logOut();
//
//        //check - public post is not shown in stream of unlinked user
//        Diaspora.signInAs(ROB_P1);
//        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));
//
//        //like public post on searching stream, indirect check - public post is shown in searching stream of unlinked user
//        Menu.search(ANA_P1.fullName);
//        Feed.toggleLike(ANA_P1, the("Public Ana"));
//        Feed.assertLikes(ANA_P1, the("Public Ana"), 2);
//
//        //check - limited post is not shown in stream of unlinked user
//        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
//
//        //check - limited post is not shown in searching stream of unlinked user
//        Menu.search(BOB_P2.fullName);
//        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
//
//        //comment post in searching stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
//        Feed.addComment(BOB_P2, the(tag + " Public Bob"), the("Comment from Rob"));
//        Feed.assertComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
//
//        //delete comment in stream
//        Menu.openStream();
//        Feed.deleteComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
//        Feed.assertNoComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
//
//        //unlike through MyActivities, indirect check - posts which liked earlier by this user is shown on my activity
//        NavBar.openMyActivity();
//        Feed.toggleLike(ANA_P1, the("Public Ana"));
//        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);
//        Menu.logOut();
//
//        //delete public post
//        Diaspora.signInAs(ANA_P1);
//        NavBar.openMyActivity();
//        Feed.deletePost(ANA_P1, the("Public Ana"));
//        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));
//
//        //delete reshared post
//        NavBar.openStream();
//        Feed.deletePost(ANA_P1, the(tag + " Public Bob"));
//        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Bob"));
//
//        //check comment of another user can not be deleted
//        Feed.assertCommentCanNotBeDeleted(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
//        Menu.logOut();
//
//        //delete limited post in my activity stream
//        Diaspora.signInAs(BOB_P2);
//        NavBar.openMyActivity();
//        Feed.deleteComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
//        Feed.deletePost(BOB_P2, the("Bob for work"));
//        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
//        Menu.logOut();
//
//        //check post of another user can not be deleted
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostCanNotBeDeleted(BOB_P2, the(tag + " Public Bob"));
//
//        //check - limited post is not shown after deletion
//        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
//
//        //check - after deletion reshared post in resharing post is no old content
//        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Bob"));
//        Menu.logOut();
//
//    }

    @Test
    public void testUserActivitiesAndAccessWithPublicPostsForUsersOfDifferentPods() {
        //GIVEN - setup relation between users, addition one the same followed tag
        String tag = "#ana_bob_rob_sam";
        Relation.forUser(BOB_P2).toUser(ANA_P1,WORK).notToUsers(ROB_P1, SAM_P2).withTags(tag).build();
        Relation.forUser(ROB_P1).toUser(SAM_P2, FRIENDS).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
        Relation.forUser(SAM_P2).toUser(ROB_P1, FAMILY).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
        Relation.forUser(ANA_P1).toUser(BOB_P2,ACQUAINTANCES).notToUsers(ROB_P1, SAM_P2).withTags(tag).doNotLogOut().build();

        //public post
        Menu.openStream();
        Feed.addPublicPost(the("Public Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Public Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(BOB_P2);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);

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
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));

        //like public post on searching stream, indirect check - public post is shown in searching stream of unlinked user
        Menu.search(ANA_P1.fullName);
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 2);

        Menu.openStream();
        //comment post in searching stream, indirect check - public post with tag is shown in stream of unlinked user with the same followed tag
        Feed.addComment(BOB_P2, the(tag + " Public Bob"), the("Comment from Rob"));
        Feed.assertComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));

        //delete comment
        Feed.deleteComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));
        Feed.assertNoComment(BOB_P2, the(tag + " Public Bob"), ROB_P1, the("Comment from Rob"));

        //unlike through MyActivities, indirect check - posts which liked earlier by this user is shown on my activity
        NavBar.openMyActivity();
        Feed.toggleLike(ANA_P1, the("Public Ana"));
        Feed.assertLikes(ANA_P1, the("Public Ana"), 1);
        Menu.logOut();

        //delete public post
        Diaspora.signInAs(ANA_P1);
        NavBar.openMyActivity();
        Feed.deletePost(ANA_P1, the("Public Ana"));
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));

        //delete reshared post
        NavBar.openStream();
        Feed.deletePost(ANA_P1, the(tag + " Public Bob"));
        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Bob"));

        //check post of another user can not be deleted
        Feed.assertPostFrom(BOB_P2, the(tag + " Public Bob"));//without this check second check often do not pass
        Feed.assertPostCanNotBeDeleted(BOB_P2, the(tag + " Public Bob"));

        //check - after deletion reshared post in resharing post is no old content
        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Bob"));
        Menu.logOut();

    }

    @Test
    public void testUserActivitiesAndAccessWithLimitedPostsForUsersOfDifferentPods() {
        //GIVEN - setup relation between users
        Relation.forUser(BOB_P2).toUser(ANA_P1, WORK).notToUsers(ROB_P1).build();
        Relation.forUser(ANA_P1).toUser(BOB_P2, ACQUAINTANCES).notToUsers(ROB_P1).doNotLogOut().build();

        //all aspects post
        Menu.openStream();
        Feed.addPublicPost(the("All aspects Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("All aspects Ana"));
        Menu.logOut();

        //like post, indirect check - public post is shown in stream of linked user
        Diaspora.signInAs(BOB_P2);
        Feed.toggleLike(ANA_P1, the("All aspects Ana"));
        Feed.assertLikes(ANA_P1, the("All aspects Ana"), 1);

        //limited post in right aspect
        Feed.addAspectPost(WORK, the("Bob for work"));
        //check - for limited post is no possibility for resharing, indirect check - post is addded
        Feed.assertPostCanNotBeReshared(BOB_P2, the("Bob for work"));
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
        Feed.assertNoPostFrom(ANA_P1, the("Ana for family"));

        //comment post, check visibility of other comments
        Feed.assertComment(BOB_P2, the("Bob for work"), ANA_P1, the("Comment from Ana"));
        Feed.addComment(BOB_P2, the("Bob for work"), the("Comment from Bob"));
        Feed.assertComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
        //unlike through MyActivities, indirect check - posts which liked earlier by this user is shown on my activity
        Feed.toggleLike(ANA_P1, the("All aspects Ana"));
        Feed.assertNoLikes(ANA_P1, the("All aspects Ana"));
        Menu.logOut();

        //checks in stream of unlinked user
        Diaspora.signInAs(ROB_P1);
        //check - limited post is not shown in searching stream of unlinked user
        Menu.search(BOB_P2.fullName);
        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
        Menu.logOut();

        //delete post
        Diaspora.signInAs(ANA_P1);
        NavBar.openMyActivity();
        Feed.deletePost(ANA_P1, the("All aspects Ana"));
        Feed.assertNoPostFrom(ANA_P1, the("All aspects Ana"));
        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
        //check post of another user can not be deleted
        Feed.assertPostCanNotBeDeleted(BOB_P2, the("Bob for work"));
        Menu.logOut();

        //delete limited post in my activity stream
        Diaspora.signInAs(BOB_P2);
        NavBar.openMyActivity();
        Feed.deleteComment(BOB_P2, the("Bob for work"), BOB_P2, the("Comment from Bob"));
        Feed.deletePost(BOB_P2, the("Bob for work"));
        Feed.assertNoPostFrom(BOB_P2, the("Bob for work"));
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
        Feed.assertPostCanNotBeReshared(ROB_P1, the("Rob for work"));
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
        Feed.assertNoPostFrom(ANA_P1, the("Ana for family"));

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
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));

        //check comment of another user can not be deleted
        Feed.assertCommentCanNotBeDeleted(ROB_P1, the("Rob for work"), ROB_P1, the("Comment from Rob"));

        //check post of another user can not be deleted
        NavBar.openStream();
        Feed.assertPostCanNotBeDeleted(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB_P1);
        //check - resharing post is shown
        Feed.assertPostFrom(ANA_P1, the(tag + " Public Rob"));
        //check - deleted post is not shown
        Feed.assertNoPostFrom(ANA_P1, the("Public Ana"));

        NavBar.openMyActivity();
        //delete comment in my activity stream
        Feed.deleteComment(ROB_P1, the("Rob for work"), ROB_P1, the("Comment from Rob"));
        //delete limited post in my activity stream
        Feed.deletePost(ROB_P1, the("Rob for work"));
        Feed.assertNoPostFrom(ROB_P1, the("Rob for work"));
        //delete reshared post
        Feed.deletePost(ROB_P1, the(tag + " Public Rob"));
        Feed.assertNoPostFrom(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();

        //add private post
        Diaspora.signInAs(ANA_P1);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("Private Ana"));

        //add all aspects post
        Feed.addAllAspectsPost(the("All aspects Ana"));
        Feed.assertNthPostIs(0, ANA_P1, the("All aspects Ana"));

        //check - limited post is not shown after deletion
        Feed.assertNoPostFrom(ROB_P1, the("Rob for work"));

        //check - reshared post is not shown after deletion
        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Rob"));

        //check - resharing post do not contain data from deleted reshared post
        Feed.assertNoPostFrom(ANA_P1, the(tag + " Public Rob"));
        Menu.logOut();


        Diaspora.signInAs(ROB_P1);
        //add new public post with tag, like, comment
        Feed.addPublicPost(the(tag + " Public Rob next"));
        Feed.toggleLike(ROB_P1, the(tag + " Public Rob next"));
        Feed.assertLikes(ROB_P1, the(tag + " Public Rob next"), 1);
        Feed.addComment(ROB_P1, the(tag + " Public Rob next"), "Comment from Rob");
        Feed.assertComment(ROB_P1, the(tag + " Public Rob next"), ROB_P1, "Comment from Rob");
        //check post for all aspects is shown
        Feed.assertPostFrom(ANA_P1, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertNoPostFrom(ANA_P1, the("Private Ana"));
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
        Feed.assertNoPostFrom(ANA_P1, the("All aspects Ana"));
        //check private post of another user is not shown
        Feed.assertNoPostFrom(ANA_P1, the("Private Ana"));
        //deleted public post with tag is not shown
        Feed.assertNoPostFrom(ROB_P1, the(tag + " Public Rob"));
        Menu.logOut();

    }

}

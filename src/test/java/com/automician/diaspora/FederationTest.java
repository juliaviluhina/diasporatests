package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import com.codeborne.selenide.Configuration;
import org.junit.AfterClass;
import org.junit.Before;
import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;

import static pages.Aspects.*;
import static com.automician.core.Gherkin.*;

public class FederationTest extends BaseTest {

    @BeforeClass
    public static void givenSetupUsersRelation() {

        Configuration.timeout = 90000;//for push data on another pod it is needed bigger timeout

        //setup relation among users from pod1(which aren't changed in all tests and built once)
        Users.Pod1.ensureRelations();

        //in addition to relation among users from pod1
        //setup relation Pod1-+->Pod2
        Relation.forUser(Users.Pod1.ana).toUser(Users.Pod2.bob, ACQUAINTANCES).notToUsers(Users.Pod2.sam).ensure();
        Relation.forUser(Users.Pod1.rob).toUser(Users.Pod2.sam, FRIENDS).notToUsers(Users.Pod2.bob).withTags(Phrases.TAG).ensure();

        //setup relation among users from pod2 and Pod2-+->Pod1
        Relation.forUser(Users.Pod2.sam).toUser(Users.Pod1.rob, FAMILY).notToUsers(Users.Pod1.ana, Users.Pod2.bob).ensure();
        Relation.forUser(Users.Pod2.bob).toUser(Users.Pod1.ana, WORK).notToUsers(Users.Pod1.rob, Users.Pod2.sam).withTags(Phrases.TAG).ensure();

    }

    //method added to insert given description on allure report
    @Before
    public void givenDescription() {

        GIVEN("POD1:         Ana<-+->Rob as Friends           , Eve<-X->Ana & Rob");
        GIVEN("POD2:         Sam<-X->Bob");
        GIVEN("POD1-+->POD2: P1.Ana-+->P2.Bob as Acquaintances, P1.Rob-+->P2.Sam as Friends");
        GIVEN("POD2-+->POD1: P2.Bob-+->P1.Ana as Work         , P2.Sam-+->P1.Rob as Family");

    }

    @AfterClass
    public static void restoreTimeOut() {
        Configuration.timeout = timeout;
    }


    @Test
    public void testAvailabilityPublicPostForUnlinkedUsersOfDifferentPods() {

        GIVEN("Public post with tag is added by author from pod 1 from scratch");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.PUBLIC_POST_WITH_TAG);
        Feed.addPublicPost(Phrases.PUBLIC_POST_WITH_TAG);
        Feed.assertPost(Users.Pod2.bob, Phrases.PUBLIC_POST_WITH_TAG);

        EXPECT("Post is shown in stream of unlinked user from pod2 who has the same followed tag");
        AND("This post can be commented");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Feed.addComment(Users.Pod2.bob, Phrases.PUBLIC_POST_WITH_TAG, Phrases.COMMENT);
        Feed.assertComment(Users.Pod2.bob, Phrases.PUBLIC_POST_WITH_TAG, Users.Pod1.rob, Phrases.COMMENT);

        EXPECT("Post with comment from user from another pod is shown in author's stream");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        Feed.assertComment(Users.Pod2.bob, Phrases.PUBLIC_POST_WITH_TAG, Users.Pod1.rob, Phrases.COMMENT);

    }

    @Test
    public void testAvailabilityLimitedPostForLinkedUsersOfDifferentPods() {

        GIVEN("Limited in right aspect post is added by author from pod 2 from scratch");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);
        Feed.addAspectPost(WORK, Phrases.POST_FOR_WORK);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);

        EXPECT("Post is shown in stream of linked in right aspect user from pod1");
        AND("This post can be commented");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Feed.addComment(Users.Pod2.bob, Phrases.POST_FOR_WORK, Phrases.COMMENT);
        Feed.assertComment(Users.Pod2.bob, Phrases.POST_FOR_WORK, Users.Pod1.ana, Phrases.COMMENT);

        EXPECT("Post with comment from user from another pod is shown in author's stream");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        Feed.assertComment(Users.Pod2.bob, Phrases.POST_FOR_WORK, Users.Pod1.ana, Phrases.COMMENT);

    }

    private static long timeout = Configuration.timeout;

}

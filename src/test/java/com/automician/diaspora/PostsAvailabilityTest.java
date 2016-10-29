package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import steps.Relation;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;

import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static com.automician.core.Gherkin.*;

public class PostsAvailabilityTest extends BaseTest {

    @Test
    public void testPostAvailabilityForUnlinkedUsers() {

        GIVEN("Sam<-X->Bob");
        Relation.forUser(Users.Pod2.sam).notToUsers(Users.Pod2.bob).ensure();
        Relation.forUser(Users.Pod2.bob).notToUsers(Users.Pod2.sam).doNotLogOut().ensure();

        GIVEN("Posts is added by author from scratch: public, private, limited");
        Menu.openStream();
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.PUBLIC_POST);
        Feed.addPublicPost(Phrases.PUBLIC_POST);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);
        Feed.addPrivatePost(Phrases.PRIVATE_POST);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);
        Feed.addAspectPost(WORK, Phrases.POST_FOR_WORK);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);

        EXPECT("Public post without tag is not shown in stream of unlinked user");
        Diaspora.ensureSignInAs(Users.Pod2.sam);
        Feed.assertNoPost(Users.Pod2.bob, Phrases.PUBLIC_POST);

        EXPECT("Private post is not shown in stream of unlinked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);

        EXPECT("Limited post is not shown in stream of unlinked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);

        EXPECT("Public post without tag is shown in contact stream of unlinked user");
        Menu.search(Users.Pod2.bob.fullName);
        Feed.assertPost(Users.Pod2.bob, Phrases.PUBLIC_POST);

        EXPECT("Private post is not shown in contact stream of unlinked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);

        EXPECT("Limited post is not shown in contact stream of unlinked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);

    }

    @Test
    public void testPostAvailabilityForLinkedUsers() {

        GIVEN("Sam-->Bob as Acquaintances, Bob-->Sam as Work");
        Relation.forUser(Users.Pod2.sam).toUser(Users.Pod2.bob, ACQUAINTANCES).ensure();
        Relation.forUser(Users.Pod2.bob).toUser(Users.Pod2.sam, WORK).doNotLogOut().ensure();

        GIVEN("Posts is added by author from scratch: public, private, limited (All aspects, right aspect, another aspect)");
        Menu.openStream();
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.PUBLIC_POST);
        Feed.addPublicPost(Phrases.PUBLIC_POST);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);
        Feed.addPrivatePost(Phrases.PRIVATE_POST);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);
        Feed.addAspectPost(WORK, Phrases.POST_FOR_WORK);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_ACQUAINTANCES);
        Feed.addAspectPost(ACQUAINTANCES, Phrases.POST_FOR_ACQUAINTANCES);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_ALL_ASPECTS);
        Feed.addAllAspectsPost(Phrases.POST_FOR_ALL_ASPECTS);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_ALL_ASPECTS);

        EXPECT("Public post without tag is shown in stream of linked user");
        Diaspora.ensureSignInAs(Users.Pod2.sam);
        Feed.assertPost(Users.Pod2.bob, Phrases.PUBLIC_POST);

        EXPECT("Private post is not shown in stream of linked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);

        EXPECT("Limited post in right aspect is shown in stream of linked user");
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_WORK);

        EXPECT("Limited post in another aspect is not shown in stream of linked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_ACQUAINTANCES);

        EXPECT("Limited post in all aspect is shown in stream of linked user");
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_ALL_ASPECTS);

        EXPECT("Private post is not shown in contact stream of linked user");
        Menu.openContacts();
        Feed.assertNoPost(Users.Pod2.bob, Phrases.PRIVATE_POST);

        EXPECT("Limited post in another aspect is not shown in contact stream of linked user");
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_ACQUAINTANCES);

    }

}

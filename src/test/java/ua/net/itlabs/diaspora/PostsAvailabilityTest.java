package ua.net.itlabs.diaspora;

import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.ACQUAINTANCES;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

@Category(ua.net.itlabs.categories.Smoke.class)
public class PostsAvailabilityTest extends BaseTest {

    @Test
    public void testPostAvailabilityForUnlinkedUsers() {

        GIVEN("Sam<-X->Bob");
        Relation.forUser(Pod2.sam).notToUsers(Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).notToUsers(Pod2.sam).doNotLogOut().ensure();

        GIVEN("Posts is added by author from scratch: public, private, limited");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, PUBLIC_POST);
        Feed.addPublicPost(PUBLIC_POST);
        Feed.ensureNoPost(Pod2.bob, PRIVATE_POST);
        Feed.addPrivatePost(PRIVATE_POST);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_WORK);
        Feed.addAspectPost(WORK, POST_FOR_WORK);
        Feed.assertPost(Pod2.bob, POST_FOR_WORK);

        EXPECT("Public post without tag is not shown in stream of unlinked user");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertNoPost(Pod2.bob, PUBLIC_POST);

        EXPECT("Private post is not shown in stream of unlinked user");
        Feed.assertNoPost(Pod2.bob, PRIVATE_POST);

        EXPECT("Limited post is not shown in stream of unlinked user");
        Feed.assertNoPost(Pod2.bob, POST_FOR_WORK);

        EXPECT("Public post without tag is shown in contact stream of unlinked user");
        Menu.search(Pod2.bob.fullName);
        Feed.assertPost(Pod2.bob, PUBLIC_POST);

        EXPECT("Private post is not shown in contact stream of unlinked user");
        Feed.assertNoPost(Pod2.bob, PRIVATE_POST);

        EXPECT("Limited post is not shown in contact stream of unlinked user");
        Feed.assertNoPost(Pod2.bob, POST_FOR_WORK);

    }

    @Test
    public void testPostAvailabilityForLinkedUsers() {

        GIVEN("Sam-->Bob as Acquaintances, Bob-->Sam as Work");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, ACQUAINTANCES).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, WORK).doNotLogOut().ensure();

        GIVEN("Posts is added by author from scratch: public, private, limited (All aspects, right aspect, another aspect)");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, PUBLIC_POST);
        Feed.addPublicPost(PUBLIC_POST);
        Feed.ensureNoPost(Pod2.bob, PRIVATE_POST);
        Feed.addPrivatePost(PRIVATE_POST);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_WORK);
        Feed.addAspectPost(WORK, POST_FOR_WORK);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_ACQUAINTANCES);
        Feed.addAspectPost(ACQUAINTANCES, POST_FOR_ACQUAINTANCES);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_ALL_ASPECTS);
        Feed.addAllAspectsPost(POST_FOR_ALL_ASPECTS);
        Feed.assertPost(Pod2.bob, POST_FOR_ALL_ASPECTS);

        EXPECT("Public post without tag is shown in stream of linked user");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertPost(Pod2.bob, PUBLIC_POST);

        EXPECT("Private post is not shown in stream of linked user");
        Feed.assertNoPost(Pod2.bob, PRIVATE_POST);

        EXPECT("Limited post in right aspect is shown in stream of linked user");
        Feed.assertPost(Pod2.bob, POST_FOR_WORK);

        EXPECT("Limited post in another aspect is not shown in stream of linked user");
        Feed.assertNoPost(Pod2.bob, POST_FOR_ACQUAINTANCES);

        EXPECT("Limited post in all aspect is shown in stream of linked user");
        Feed.assertPost(Pod2.bob, POST_FOR_ALL_ASPECTS);

        EXPECT("Private post is not shown in contact stream of linked user");
        Menu.openContacts();
        Feed.assertNoPost(Pod2.bob, PRIVATE_POST);

        EXPECT("Limited post in another aspect is not shown in contact stream of linked user");
        Feed.assertNoPost(Pod2.bob, POST_FOR_ACQUAINTANCES);

    }

}

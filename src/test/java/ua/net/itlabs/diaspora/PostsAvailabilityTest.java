package ua.net.itlabs.diaspora;

import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.Test;
import pages.Diaspora;
import pages.Feed;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.ACQUAINTANCES;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

public class PostsAvailabilityTest extends BaseTest {

    @Category(ua.net.itlabs.categories.Smoke.class)
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
    public void testAvailabilityPrivatePost() {

        GIVEN("Private post is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addPrivatePost(the("Private Ana"));
        Feed.assertPost(Pod1.ana, the("Private Ana"));

        EXPECT("Private post is not shown in contact's stream for unlinked user ");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));

        EXPECT("Private post is not shown in stream of linked user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.ana, the("Private Ana"));
    }

    @Test
    public void testAvailabilityLimitedPosts() {

        GIVEN("Limited posts in different aspects ere added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addAllAspectsPost(the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.addAspectPost(FRIENDS, the("Ana for Friends"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.addAspectPost(ACQUAINTANCES, the("Ana for Acquaintances"));
        Feed.assertPost(Pod1.ana, the("Ana for Acquaintances"));

        EXPECT("Limited post is not shown in contact's stream for unlinked user ");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.search(Pod1.ana.fullName);
        Feed.assertNoPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Friends"));

        EXPECT("Posts limited in some aspects are shown in stream of linked user who linked in this aspects with author");
        AND("Posts limited in some aspects are not shown in stream of linked user who linked in another aspects with author");
        Diaspora.ensureSignInAs(Pod1.rob);
        Feed.assertPost(Pod1.ana, the("Ana for All aspects"));
        Feed.assertPost(Pod1.ana, the("Ana for Friends"));
        Feed.assertNoPost(Pod1.ana, the("Ana for Acquaintances"));

    }
}

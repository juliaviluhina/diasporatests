package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;
import pages.Feed;
import pages.Tags;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class TagsTest extends BaseTest {

    @BeforeClass
    public static void givenSetupUsersRelation() {

        GIVEN("Setup relation between users, followed tags is added for users, public posts with this tags is added by author");
        post1 = the("Public post with tag " + the("#tag1") + " : ");
        post2 = the("Public post with tag " + the("#tag2") + " : ");
        Relation.forUser(Pod1.ana).notToUsers(Pod1.rob).ensure();
        Relation.forUser(Pod1.rob).notToUsers(Pod1.ana).doNotLogOut().ensure();
        Menu.openStream();
        Feed.addPublicPost(post1);
        Feed.addPublicPost(post2);
        Feed.assertPost(Pod1.rob, post2); //this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

    }

    @Test
    public void testAddTag() {

        EXPECT("Public post with not followed tag is not shown in stream of unlinked user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoPost(Pod1.rob, post1);

        WHEN("Tag is followed by user");
        NavBar.openTags();
        Tags.add(the("#tag1"));

        THEN("Public post with followed tag is shown in stream of unlinked user");
        NavBar.openStream();
        Feed.assertPost(Pod1.rob, post1);

    }

    @Test
    public void testFilterFeedByTag() {

        GIVEN("Some tag is followed by user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Tags.ensureTag(the("#tag2"));

        WHEN("This followed tag is selected in NavBar");
        NavBar.openTags();
        Tags.filter(the("#tag2"));

        THEN("Only posts with this tag are shown");
        Feed.assertPost(Pod1.rob, post2);
        Feed.assertNoPost(Pod1.rob, post1);

    }

    @Test
    public void testDeleteTag() {

        GIVEN("Some tag is followed by user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Tags.ensureTag(the("#tag2"));

        EXPECT("Public post with followed tag is shown in stream of unlinked user");
        NavBar.openStream();
        Feed.assertPost(Pod1.rob, post2);

        WHEN("Followed tag is deleted");
        NavBar.openTags();
        Tags.delete(the("#tag2"));

        THEN("Public post with this tag from unlinked user is not shown in stream of unlinked user");
        NavBar.openStream();
        Feed.assertNoPost(Pod1.rob, post2);

    }

    @Test
    public void testTagsOrderAndSafety() {

        GIVEN("User does not follow any tags");
        Diaspora.ensureSignInAs(Pod1.ana);
        Tags.ensureNoTags();

        WHEN("Tags are added in not alphabetical order");
        NavBar.openTags();
        Tags.add(the("#Ytag1"), the("#Ztag"), the("#Ytag2"));
        THEN("Added tags are shown in alphabetical order");
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"), the("#Ztag"));

        EXPECT("Added tags are shown in alphabetical order after next signing in");
        Menu.ensureLogOut();
        Diaspora.ensureSignInAs(Pod1.ana);
        NavBar.openTags();
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"), the("#Ztag"));

    }

    private static String post1;
    private static String post2;

}

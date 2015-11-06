package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;
import pages.Feed;
import pages.Tags;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaTagsTest extends BaseTest {

    private static String post1;
    private static String post2;

    @BeforeClass
    public static void buildGivenForTests() {

        clearUniqueData();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
        //new public posts linked with tags
        post1 = the("Public post with tag " + the("#tag1") + " : ");
        post2 = the("Public post with tag " + the("#tag2") + " : ");
        Relation.forUser(Pod1.ana).notToUsers(Pod1.rob).build();
        Relation.forUser(Pod1.rob).notToUsers(Pod1.ana).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0, Pod1.rob, post2); //this check for wait moment when stream will be loaded
        Menu.logOut();

    }

    @Test
    public void testAddTag() {

        //tag is not used and public post with tag from unlinked user is not shown in stream
        Diaspora.signInAs(Pod1.ana);
        Feed.assertNoPostFrom(Pod1.rob, post1);

        NavBar.openTags();
        Tags.add(the("#tag1"));

        //tag is used and public post with tag from unlinked user is shown in stream
        NavBar.openStream();
        Feed.assertPostFrom(Pod1.rob, post1);

    }

    @Test
    public void testFilterFeedByTag() {

        //GIVEN additional - tag 2 is followed by Anna
        Diaspora.signInAs(Pod1.ana);
        Tags.ensureTag(the("#tag2"));

        NavBar.openTags();
        Tags.filter(the("#tag2"));

        //only posts with filtered tag are shown
        Feed.assertPostFrom(Pod1.rob, post2);
        Feed.assertNoPostFrom(Pod1.rob, post1);

    }

    @Test
    public void testDeleteTag() {

        //GIVEN additional - tag 1 is followed by Anna
        Diaspora.signInAs(Pod1.ana);
        Tags.ensureTag(the("#tag2"));

        //tag is used and public post with tag from unlinked user is shown in stream
        NavBar.openStream();
        Feed.assertPostFrom(Pod1.rob, post2);

        NavBar.openTags();
        Tags.delete(the("#tag2"));

        //tag is not used and public post with tag from unlinked user is not shown in stream
        NavBar.openStream();
        Feed.assertNoPostFrom(Pod1.rob, post2);

    }

    @Test
    public void testTagsOrderAndSafety() {

        //GIVEN additional - tag list should be empty
        Diaspora.signInAs(Pod1.ana);
        Tags.ensureNoTags();

        NavBar.openTags();
        //add tags in not alphabetical order
        Tags.add(the("#Ytag1"), the("#Ztag"), the("#Ytag2"));
        //check - after addition tags in alphabetical order
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"), the("#Ztag"));

        //check order after logout and sign in
        Menu.logOut();
        Diaspora.signInAs(Pod1.ana);
        NavBar.openTags();
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"), the("#Ztag"));

    }
}

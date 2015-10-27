package ua.net.itlabs;

import core.steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import pages.Feed;
import pages.Tags;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;

@Category(ua.net.itlabs.categories.Tags.class)
public class DiasporaTagsTest extends BaseTest {

    public static String post1;
    public static String post2;


    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();

        //GIVEN - for all tests of this class
        //setup relation between users from the same pod
        //new public posts linked with tags
        post1 = the("Public post with tag " + the("#tag1") + " : ");
        post2 = the("Public post with tag " + the("#tag2") + " : ");
        Relation.forUser(ANA_P1).notToUsers(ROB_P1).build();
        Relation.forUser(ROB_P1).notToUsers(ANA_P1).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0,ROB_P1, post2); //this check for wait moment when stream will be loaded
        Menu.logOut();

    }

    @Test
    public void testAddTag() {

        Diaspora.signInAs(ANA_P1);

        //tag is not used and public post with tag from unlinked user is not shown in stream
        Feed.assertNoPostFrom(ROB_P1, post1);

        NavBar.openTags();
        Tags.add(the("#tag1"));

        //tag is used and public post with tag from unlinked user is shown in stream
        NavBar.openStream();
        Feed.assertPostFrom(ROB_P1, post1);

    }

    @Test
    public void testFilterFeedByTag() {
        //GIVEN additional - tag 2 is followed by Anna
        Diaspora.signInAs(ANA_P1);
        Tags.ensureTag(the("#tag2"));

        NavBar.openTags();
        Tags.filter(the("#tag2"));

        //only posts with filtered tag are shown
        Feed.assertPostFrom(ROB_P1, post2);
        Feed.assertCountPosts(1);

    }

    @Test
    public void testDeleteTag() {
        //GIVEN additional - tag 1 is followed by Anna
        Diaspora.signInAs(ANA_P1);
        Tags.ensureTag(the("#tag2"));

        //tag is used and public post with tag from unlinked user is shown in stream
        NavBar.openStream();
        Feed.assertPostFrom(ROB_P1, post2);

        NavBar.openTags();
        Tags.delete(the("#tag2"));

        //tag is not used and public post with tag from unlinked user is not shown in stream
        NavBar.openStream();
        Feed.assertNoPostFrom(ROB_P1, post2);
    }

    @Test
    public void testTagsOrderAndSafety() {
        //GIVEN additional - tag list should be empty
        Diaspora.signInAs(ANA_P1);
        Tags.ensureNoTags();

        NavBar.openTags();
        //add tags in not alphabetical order
        Tags.add(the("#Ytag1"), the("#Ztag"), the("#Ytag2") );
        //check - after addition tags in alphabetical order
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"),the("#Ztag") );

        //check order after logout and sign in
        Menu.logOut();
        Diaspora.signInAs(ANA_P1);
        NavBar.openTags();
        Tags.assertTags(the("#Ytag1"), the("#Ytag2"),the("#Ztag") );

    }
}

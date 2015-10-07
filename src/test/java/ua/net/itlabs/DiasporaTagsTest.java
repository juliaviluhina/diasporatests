package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;
import ua.net.itlabs.categories.Buggy;

import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;

public class DiasporaTagsTest extends BaseTest {

    @Test
    public void testFollowedTags() {
        //GIVEN - setup relation between users, addition one the same followed tag
        //new public posts linked with tags in user account from the same pod
        String post1 = the("Public post with tag " + the("#tag1") + " : ");
        String post2 = the("Public post with tag " + the("#tag2") + " : ");
        Relation.forUser(ANA_P1).notToUsers(ROB_P1).build();
        Relation.forUser(ROB_P1).notToUsers(ANA_P1).doNotLogOut().build();
        Menu.openStream();
        Feed.addPublicPost(post1);
        Feed.addPublicPost(post2);
        Feed.assertNthPostIs(0,ROB_P1, post2); //this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(ANA_P1);

        //tags is not used and public posts is not shown in stream
        Feed.assertNoPostFrom(ROB_P1, post1);
        Feed.assertNoPostFrom(ROB_P1, post2);

        NavBar.openTags();

        Tags.add(the("#tag1"));
        //only posts with filtered tag are shown
        Tags.filter(the("#tag1"));
        Feed.assertPostFrom(ROB_P1, post1);
        Feed.assertNoPostFrom(ROB_P1, post2);

        Menu.openStream();
        NavBar.openTags();

        Tags.add(the("#tag2"));
        Tags.filter(the("#tag2"));
        Feed.assertNoPostFrom(ROB_P1, post1);
        Feed.assertPostFrom(ROB_P1, post2);

        Menu.openStream();
        Feed.assertPostFrom(ROB_P1, post1);
        Feed.assertPostFrom(ROB_P1, post2);

        NavBar.openTags();
        Tags.delete(the("#tag1"));
        Tags.assertNotExist(the("#tag1"));

        //in view mode of whole stream posts with followed text are shown
        NavBar.openStream();
        Feed.assertNoPostFrom(ROB_P1, post1);
        Feed.assertPostFrom(ROB_P1, post2);

    }


    //for test case #6417
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
}

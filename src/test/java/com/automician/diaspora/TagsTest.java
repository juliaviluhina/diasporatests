package com.automician.diaspora;

import com.automician.BaseTest;
import org.junit.Test;
import pages.*;
import pages.Feed;
import pages.Tags;

import static com.automician.core.Gherkin.*;
import static com.automician.testDatas.Users.*;
import static com.automician.testDatas.Phrases.*;

public class TagsTest extends BaseTest {

    @Test
    public void testAddTag() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Eve does not follow tag");
        Diaspora.ensureSignInAs(Pod1.eve);
        Tags.ensureNoTag(TAG);

        GIVEN("Public post from Ana exists");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST_WITH_TAG);

        EXPECT("Public post with not followed tag is not shown in stream of unlinked user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST_WITH_TAG);

        WHEN("Tag is followed by user");
        NavBar.openTags();
        Tags.add(TAG);

        THEN("Public post with followed tag is shown in stream of unlinked user");
        NavBar.openStream();
        Feed.assertPost(Pod1.ana, PUBLIC_POST_WITH_TAG);

    }

    @Test
    public void testFilterFeedByTag() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public posts from Ana exist - with tag and without tag");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST_WITH_TAG);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST);

        GIVEN("Rob follows tag");
        Diaspora.ensureSignInAs(Pod1.rob);
        Tags.ensureTag(TAG);

        EXPECT("Both public posts are in Rob's stream");
        Menu.openStream();
        Feed.assertPost(Pod1.ana, PUBLIC_POST_WITH_TAG);
        Feed.assertPost(Pod1.ana, PUBLIC_POST);

        WHEN("This followed tag is selected in NavBar");
        NavBar.openTags();
        Tags.filter(TAG);

        THEN("Only posts with this tag are shown");
        Feed.assertPost(Pod1.ana, PUBLIC_POST_WITH_TAG);
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

    }

    @Test
    public void testDeleteTag() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Public post with tag from Ana exist");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.ensurePublicPost(Pod1.ana, PUBLIC_POST_WITH_TAG);

        GIVEN("Eve follows tag");
        Diaspora.ensureSignInAs(Pod1.eve);
        Tags.ensureTag(TAG);

        EXPECT("Public post with followed tag is shown in stream of unlinked user");
        NavBar.openStream();
        Feed.assertPost(Pod1.ana, PUBLIC_POST_WITH_TAG);

        WHEN("Followed tag is deleted");
        NavBar.openTags();
        Tags.delete(TAG);

        THEN("Public post with this tag is not shown in stream of unlinked user");
        Menu.openStream();
        Feed.assertNoPost(Pod1.ana, PUBLIC_POST);

    }

    @Test
    public void testTagsOrderAndSafety() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("User does not follow any tags");
        Diaspora.ensureSignInAs(Pod1.ana);
        Tags.ensureNoTags();

        WHEN("Tags are added in not alphabetical order");
        NavBar.openTags();
        Tags.add(Y_Tag, Z_Tag, X_Tag);

        THEN("Added tags are shown in alphabetical order");
        Tags.assertTags(X_Tag, Y_Tag, Z_Tag);

        EXPECT("Added tags are shown in alphabetical order after next signing in");
        Diaspora.ensureLogOut();
        Diaspora.signInAs(Pod1.ana);//usage signIn (not ensure) - for opening separate webrdiver in separate mode
        NavBar.openTags();
        Tags.assertTags(X_Tag, Y_Tag, Z_Tag);

    }

}

package ua.net.itlabs.diaspora;

import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.Test;
import pages.*;
import pages.Aspects;
import pages.Contacts;
import pages.Feed;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static pages.Aspects.WORK;
import static steps.Scenarios.waitStreamOpening;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

public class AspectsTest extends BaseTest {
    
    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testAddAspectInNavBar() {

        GIVEN("Aspect "+ASPECT1+" is not used by author");
        Diaspora.ensureSignInAs(Pod2.bob);
        Aspects.ensureNoAspect(ASPECT1);

        GIVEN("Post in aspect "+ASPECT1+" does not exist");
        Feed.ensureNoPost(Pod2.bob, POST_IN_ASPECT1);

        WHEN("New aspest is added");
        NavBar.openMyAspects();
        Aspects.add(ASPECT1);
        Contacts.assertAspect(ASPECT1);//this check for wait moment when operation is done

        EXPECT("Added aspect is shown in Nav Bar");
        Menu.openStream();
        waitStreamOpening();//this wait for wait moment when stream will be loaded
        NavBar.openMyAspects();
        Aspects.assertAspectInNavBar(ASPECT1);//this check for wait moment when stream will be loaded

        THEN("New aspect can be used for contact setup");
        Menu.search(Pod2.sam.fullName);
        Contact.ensureAspectsForContact(ASPECT1);

        EXPECT("New aspect can be used for post setup");
        Menu.openStream();
        Feed.addAspectPost(ASPECT1, POST_IN_ASPECT1);
        Feed.assertPost(Pod2.bob, POST_IN_ASPECT1);

        WHEN("New aspect is selected in NavBar aspects list");
        NavBar.openMyAspects();
        Aspects.toggleAspect(ASPECT1);

        THEN("Limited post in this aspect is shown in stream");
        Feed.assertPost(Pod2.bob, POST_IN_ASPECT1);

        EXPECT("Limited in aspect post is available for linked in this aspect user");
        Diaspora.ensureSignInAs(Pod2.sam);
        Menu.search(Pod2.bob.fullName);
        Feed.assertPost(Pod2.bob, POST_IN_ASPECT1);
        Menu.ensureLogOut();

    }

    @Test
    public void testSwitchToEditModeInNavBar() {

        WHEN("Aspect is in edit mode");
        Diaspora.ensureSignInAs(Pod1.rob);
        NavBar.openMyAspects();
        Aspects.switchToEditMode(FRIENDS);

        THEN("Contact site with this aspect is loaded");
        Contacts.assertAspect(FRIENDS);

    }

    @Test
    public void testFilterAspectsInNavBar() {

        GIVEN("Setup relation between users, add limited in aspects posts from both users");
        clearUniqueData();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, WORK).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).doNotLogOut().ensure();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Rob for new friends"));
        Feed.addAspectPost(FAMILY, the("Rob for new family"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.addAspectPost(WORK, the("Ana for work"));
        Feed.assertPost(Pod1.ana, the("Ana for work"));

        WHEN("In NavBar aspects list all aspects is deselected");
        Diaspora.ensureSignInAs(Pod1.rob);
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));

        WHEN("In NavBar aspects list some aspects is selected");
        Aspects.toggleAspect(ACQUAINTANCES);
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in these aspects is shown");
        AND("posts of users linked in these aspects is shown");
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new family"));

        WHEN("In NavBar aspects list some aspect is deselected");
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in this aspect is not shown");
        AND("posts of users linked in this aspect is not shown");
        Feed.assertNoPost(Pod1.ana, the("Ana for work"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new family"));

        WHEN("In NavBar aspects list all aspects is selected");
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));

    }

}

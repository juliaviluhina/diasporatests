package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.Test;
import pages.*;
import pages.Aspects;
import pages.Contacts;
import pages.Feed;
import ua.net.itlabs.BaseTest;

import static pages.Aspects.*;
import static pages.Aspects.WORK;
import static steps.Scenarios.waitStreamOpening;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

public class AspectsTest extends BaseTest {

    @Test
    public void testAddAspectInNavBar() {

        GIVEN("Aspect " + ASPECT1 + " is not used by author");
        Diaspora.ensureSignInAs(Pod2.bob);
        Aspects.ensureNoAspect(ASPECT1);

        GIVEN("Post in aspect " + ASPECT1 + " does not exist");
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
        Diaspora.ensureSignInAs(Pod2.bob);
        NavBar.openMyAspects();
        Aspects.switchToEditMode(FRIENDS);

        THEN("Contact site with this aspect is loaded");
        Contacts.assertAspect(FRIENDS);

    }

    @Test
    public void testFilterAspectsInNavBar() {

        GIVEN("Sam-+->Bob as Work");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, WORK).doNotLogOut().ensure();

        GIVEN("Work aspect post from Sam is added after setup relation");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.sam, POST_FOR_WORK);
        Feed.addAspectPost(WORK, POST_FOR_WORK);

        GIVEN("Bob-+->Sam as Friends ");
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, FRIENDS).doNotLogOut().ensure();

        GIVEN("Friends and Family aspects post from Bob are added after setup relation");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FRIENDS);
        Feed.addAspectPost(FRIENDS, POST_FOR_FRIENDS);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY);
        Feed.addAspectPost(FAMILY, POST_FOR_FAMILY);

        WHEN("In NavBar aspects list all aspects is deselected");
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Pod2.sam, POST_FOR_WORK);
        Feed.assertPost(Pod2.bob, POST_FOR_FRIENDS);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("In NavBar aspects list some aspects is selected");
        Aspects.toggleAspect(ACQUAINTANCES);
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in these aspects is shown");
        AND("posts of users linked in these aspects is shown");
        Feed.assertPost(Pod2.sam, POST_FOR_WORK);
        Feed.assertPost(Pod2.bob, POST_FOR_FRIENDS);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("In NavBar aspects list some aspect is deselected");
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in this aspect is not shown");
        AND("posts of users linked in this aspect is not shown");
        Feed.assertNoPost(Pod2.sam, POST_FOR_WORK);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FRIENDS);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("In NavBar aspects list all aspects is selected");
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Pod2.sam, POST_FOR_WORK);
        Feed.assertPost(Pod2.bob, POST_FOR_FRIENDS);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);

    }

}

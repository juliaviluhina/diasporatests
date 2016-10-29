package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import com.automician.steps.Relation;
import org.junit.Test;
import com.automician.pages.*;
import com.automician.pages.Aspects;
import com.automician.pages.Contacts;
import com.automician.pages.Feed;

import static com.automician.pages.Aspects.*;
import static com.automician.pages.Aspects.WORK;
import static com.automician.steps.Scenarios.waitStreamOpening;
import static com.automician.core.Gherkin.*;

public class AspectsTest extends BaseTest {

    @Test
    public void testAddAspectInNavBar() {

        GIVEN("Aspect " + Phrases.ASPECT1 + " is not used by author");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        Aspects.ensureNoAspect(Phrases.ASPECT1);

        GIVEN("Post in aspect " + Phrases.ASPECT1 + " does not exist");
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_IN_ASPECT1);

        WHEN("New aspest is added");
        NavBar.openMyAspects();
        Aspects.add(Phrases.ASPECT1);
        Contacts.assertAspect(Phrases.ASPECT1);//this check for wait moment when operation is done

        EXPECT("Added aspect is shown in Nav Bar");
        Menu.openStream();
        waitStreamOpening();//this wait for wait moment when stream will be loaded
        NavBar.openMyAspects();
        Aspects.assertAspectInNavBar(Phrases.ASPECT1);//this check for wait moment when stream will be loaded

        THEN("New aspect can be used for contact setup");
        Menu.search(Users.Pod2.sam.fullName);
        Contact.ensureAspectsForContact(Phrases.ASPECT1);

        EXPECT("New aspect can be used for post setup");
        Menu.openStream();
        Feed.addAspectPost(Phrases.ASPECT1, Phrases.POST_IN_ASPECT1);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_IN_ASPECT1);

        WHEN("New aspect is selected in NavBar aspects list");
        NavBar.openMyAspects();
        Aspects.toggleAspect(Phrases.ASPECT1);

        THEN("Limited post in this aspect is shown in stream");
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_IN_ASPECT1);

        EXPECT("Limited in aspect post is available for linked in this aspect user");
        Diaspora.ensureSignInAs(Users.Pod2.sam);
        Menu.search(Users.Pod2.bob.fullName);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_IN_ASPECT1);
        Menu.ensureLogOut();

    }

    @Test
    public void testSwitchToEditModeInNavBar() {

        WHEN("Aspect is in edit mode");
        Diaspora.ensureSignInAs(Users.Pod2.bob);
        NavBar.openMyAspects();
        Aspects.switchToEditMode(FRIENDS);

        THEN("Contact site with this aspect is loaded");
        Contacts.assertAspect(FRIENDS);

    }

    @Test
    public void testFilterAspectsInNavBar() {

        GIVEN("Sam-+->Bob as Work");
        Relation.forUser(Users.Pod2.sam).toUser(Users.Pod2.bob, WORK).doNotLogOut().ensure();

        GIVEN("Work aspect post from Sam is added after setup relation");
        Menu.openStream();
        Feed.ensureNoPost(Users.Pod2.sam, Phrases.POST_FOR_WORK);
        Feed.addAspectPost(WORK, Phrases.POST_FOR_WORK);

        GIVEN("Bob-+->Sam as Friends ");
        Relation.forUser(Users.Pod2.bob).toUser(Users.Pod2.sam, FRIENDS).doNotLogOut().ensure();

        GIVEN("Friends and Family aspects post from Bob are added after setup relation");
        Menu.openStream();
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_FRIENDS);
        Feed.addAspectPost(FRIENDS, Phrases.POST_FOR_FRIENDS);
        Feed.ensureNoPost(Users.Pod2.bob, Phrases.POST_FOR_FAMILY);
        Feed.addAspectPost(FAMILY, Phrases.POST_FOR_FAMILY);

        WHEN("In NavBar aspects list all aspects is deselected");
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Users.Pod2.sam, Phrases.POST_FOR_WORK);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_FRIENDS);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_FAMILY);

        WHEN("In NavBar aspects list some aspects is selected");
        Aspects.toggleAspect(ACQUAINTANCES);
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in these aspects is shown");
        AND("posts of users linked in these aspects is shown");
        Feed.assertPost(Users.Pod2.sam, Phrases.POST_FOR_WORK);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_FRIENDS);
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_FAMILY);

        WHEN("In NavBar aspects list some aspect is deselected");
        Aspects.toggleAspect(FRIENDS);

        THEN("Author's posts limited in this aspect is not shown");
        AND("posts of users linked in this aspect is not shown");
        Feed.assertNoPost(Users.Pod2.sam, Phrases.POST_FOR_WORK);
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_FRIENDS);
        Feed.assertNoPost(Users.Pod2.bob, Phrases.POST_FOR_FAMILY);

        WHEN("In NavBar aspects list all aspects is selected");
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");

        THEN("All posts in all aspects is shown");
        Feed.assertPost(Users.Pod2.sam, Phrases.POST_FOR_WORK);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_FRIENDS);
        Feed.assertPost(Users.Pod2.bob, Phrases.POST_FOR_FAMILY);

    }

}

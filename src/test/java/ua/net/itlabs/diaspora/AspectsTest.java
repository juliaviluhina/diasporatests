package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.Before;
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
import static ua.net.itlabs.testDatas.Users.*;

public class AspectsTest extends BaseTest {

    @Before
    public void setupForTest() {
        clearUniqueData();
    }

    @Test
    public void testAddAspectInNavBar() {

        //add new aspect
        Diaspora.signInAs(Pod1.rob);
        NavBar.openMyAspects();
        Aspects.add(the("Asp1"));
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertAspectInNavBar(the("Asp1"));//this check for wait moment when stream will be loaded

        //add contact in added aspect, indirect check - new aspect can be used for limited post
        Menu.search(Pod1.ana.fullName);
        Contact.ensureAspectsForContact(the("Asp1"));

        //add limited post in this in aspect, indirect check - new aspect can be used for limited post
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the("Rob for new aspect"));
        Feed.assertPost(Pod1.rob, the("Rob for new aspect"));

        //filtering - only new aspect is enabled
        NavBar.openMyAspects();
        Aspects.toggleAspect(the("Asp1"));
        Feed.assertPost(Pod1.rob, the("Rob for new aspect"));
        Menu.logOut();

        //check - post in this aspect is available for linked user (check in contact
        Diaspora.signInAs(Pod1.ana);
        Menu.search(Pod1.rob.fullName);
        Feed.assertPost(Pod1.rob, the("Rob for new aspect"));
        Menu.logOut();

    }

    @Test
    public void testSwitchToEditModeInNavBar() {

        //switch to edit mode, indirect check -
        Diaspora.signInAs(Pod1.rob);
        NavBar.openMyAspects();
        Aspects.switchToEditMode(FRIENDS);

        //check - Contacts site with this current aspect is loaded
        Contacts.assertAspect(FRIENDS);

    }

    @Test
    public void testFilterAspectsInNavBar() {

        //GIVEN - setup relation and add limited in aspect posts
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, WORK).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).doNotLogOut().ensure();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Rob for new friends"));
        Feed.addAspectPost(FAMILY, the("Rob for new family"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));
        Menu.logOut();
        Diaspora.signInAs(Pod1.ana);
        Feed.addAspectPost(WORK, the("Ana for work"));
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Menu.logOut();

        Diaspora.signInAs(Pod1.rob);

        //deselect all aspects
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");

        //check - when in filter is not any aspect - all posts is shown
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));

        //change filter - select to filter two aspects
        Aspects.toggleAspect(ACQUAINTANCES);
        Aspects.toggleAspect(FRIENDS);

        //check - only author's posts for aspects
        // and posts of linked in this aspects users is shown
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new family"));

        //change filter - deselect from filter aspect
        Aspects.toggleAspect(FRIENDS);

        //check posts visibility according to filter
        Feed.assertNoPost(Pod1.ana, the("Ana for work"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertNoPost(Pod1.rob, the("Rob for new family"));

        //select all aspects
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");

        //check - when in filter is all aspects - all posts is shown
        Feed.assertPost(Pod1.ana, the("Ana for work"));
        Feed.assertPost(Pod1.rob, the("Rob for new friends"));
        Feed.assertPost(Pod1.rob, the("Rob for new family"));

    }

}

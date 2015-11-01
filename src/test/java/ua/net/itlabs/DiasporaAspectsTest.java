package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import pages.Aspects;
import pages.Contacts;
import pages.Feed;
import ua.net.itlabs.categories.*;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;

@Category(ua.net.itlabs.categories.Aspects.class)
public class DiasporaAspectsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout
        setTimeOut();
    }

    @Before
    public void setupForTest() {
        //clear information about unique values
        clearThe();
    }

    @Test
    public void testAddAspectInNavBar() {

        //add new aspect
        Diaspora.signInAs(RON_P1);
        NavBar.openMyAspects();
        Aspects.add(the("Asp1"));
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertAspectInNavBar(the("Asp1"));//this check for wait moment when stream will be loaded

        //add contact in added aspect, indirect check - new aspect can be used for limited post
        Menu.search(ANA_P1.fullName);
        Contact.ensureAspectsForContact(the("Asp1"));

        //add limited post in this in aspect, indirect check - new aspect can be used for limited post
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the("Ron for new aspect"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for new aspect"));

        //filtering - only new aspect is enabled
        NavBar.openMyAspects();
        Aspects.toggleAspect(the("Asp1"));
        Feed.assertPostFrom(RON_P1, the("Ron for new aspect"));
        Menu.logOut();

        //check - post in this aspect is available for linked user (check in contact
        Diaspora.signInAs(ANA_P1);
        Menu.search(RON_P1.fullName);
        Feed.assertPostFrom(RON_P1, the("Ron for new aspect"));
        Menu.logOut();

    }

    @Test
    public void testSwitchToEditModeInNavBar() {

        //switch to edit mode, indirect check -
        Diaspora.signInAs(RON_P1);
        NavBar.openMyAspects();
        Aspects.switchToEditMode(FRIENDS);

        //check - Contacts site with this current aspect is loaded
        Contacts.assertAspect(FRIENDS);

    }

    @Test
    public void testFilterAspectsInNavBar() {

        //GIVEN - setup relation and add limited in aspect posts
        Relation.forUser(ANA_P1).toUser(RON_P1, WORK).build();
        Relation.forUser(RON_P1).toUser(ANA_P1, FRIENDS).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for new friends"));
        Feed.addAspectPost(FAMILY, the("Ron for new family"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for new family"));
        Menu.logOut();
        Diaspora.signInAs(ANA_P1);
        Feed.addAspectPost(WORK, the("Ana for work"));
        Feed.assertNthPostIs(0, ANA_P1, the("Ana for work"));
        Menu.logOut();

        Diaspora.signInAs(RON_P1);

        //deselect all aspects
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");

        //check - when in filter is not any aspect - all posts is shown
        Feed.assertPostFrom(ANA_P1, the("Ana for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for new friends"));
        Feed.assertPostFrom(RON_P1, the("Ron for new family"));

        //change filter - select to filter two aspects
        Aspects.toggleAspect(ACQUAINTANCES);
        Aspects.toggleAspect(FRIENDS);

        //check - only author's posts for aspects
        // and posts of linked in this aspects users is shown
        Feed.assertPostFrom(ANA_P1, the("Ana for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for new friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for new family"));

        //change filter - deselect from filter aspect
        Aspects.toggleAspect(FRIENDS);

        //check posts visibility according to filter
        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for new friends"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for new family"));

        //select all aspects
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");

        //check - when in filter is all aspects - all posts is shown
        Feed.assertPostFrom(ANA_P1, the("Ana for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for new friends"));
        Feed.assertPostFrom(RON_P1, the("Ron for new family"));

    }

}

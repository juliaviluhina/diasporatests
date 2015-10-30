package ua.net.itlabs;

import core.steps.Relation;
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

        //setup - suitable timeout and clear information about unique values
        clearThe();
        setTimeOut();

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
        Relation.forUser(ANA_P1).toUser(RON_P1,WORK).build();
        Relation.forUser(RON_P1).toUser(ANA_P1,FRIENDS).doNotLogOut().build();
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



//    //from contacts
//    @Test
//    public void testOperationWithNewAspect() {
//        //GIVEN - setup relation between users
//        Relation.forUser(ANA_P1).toUser(RON_P1, WORK).build();
//        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).doNotLogOut().build();
//
//        //add new aspect on contacts site
//        Menu.openContacts();
//        Contacts.addAspect(the("Asp1"));
//        Contacts.selectAspect(the("Asp1"));
//        Contacts.assertCountContactsInAspect(the("Asp1"),0);
//
//        //add link with this aspect
//        Contacts.addLinkedContactForAspect(the("Asp1"), ANA_P1);
//        Contacts.selectAspect(the("Asp1"));//only after this action counter is changed
//        Contacts.assertCountContactsInAspect(the("Asp1"), 1);
//
//        //add post with this aspect
//        Menu.openStream();
//        Feed.addAspectPost(the("Asp1"), the("Asp1") + " Post for new Aspect from Ron");
//        Feed.assertNthPostIs(0, RON_P1, the("Asp1") + " Post for new Aspect from Ron" );//this check for wait moment when stream will be loaded
//        Menu.logOut();
//
//        //check post visibility in stream of linked in new aspect contact
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
//        Menu.logOut();
//
//        //rename aspect on contacts site
//        Diaspora.signInAs(RON_P1);
//        Menu.openContacts();
//        Contacts.selectAspect(the("Asp1"));
//        Contacts.rename(the("Asp2"));
//
//        //check changes in aspects in contacts site
//        Contacts.assertAspect(the("Asp2"));
//        Contacts.assertNoAspect(the("Asp1"));
//
//        //add new post for renamed aspect
//        Menu.openStream();
//        Feed.addAspectPost(the("Asp2"), the("Asp2") + " Post for renamed Aspect from Ron");
//        Feed.assertNthPostIs(0, RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
//        Menu.logOut();
//
//        //check post visibility in stream of linked in new aspect contact
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
//        Feed.assertPostFrom(RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
//        Menu.logOut();
//
//        //delete added aspect
//        Diaspora.signInAs(RON_P1);
//        Menu.openContacts();
//        Contacts.selectAspect(the("Asp2"));
//        Contacts.deleteAspect();
//
//        //check - is no aspect in contact site after deletion
//        Contacts.assertNoAspect(the("Asp2"));
//        Menu.logOut();
//
//        //check post visibility in stream of linked contact (post limited in deleted aspect and earlier was available)
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
//        Feed.assertPostFrom(RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
//        Menu.logOut();
//
//    }
//
//
//    @Test
//    public void testAspectsFilteringInNavBar() {
//        //GIVEN - setup mutual relation between users in some different aspects
//        //add post in this aspect
//        Relation.forUser(RON_P1).toUser(EVE_P1, ACQUAINTANCES).build();
//        Relation.forUser(EVE_P1).toUser(RON_P1, WORK).doNotLogOut().build();
//        Menu.openStream();
//        Feed.addAspectPost(WORK, the("Eve for work"));
//        Feed.assertNthPostIs(0, EVE_P1, the("Eve for work"));//this check for wait moment when stream will be loaded
//        Menu.logOut();
//        Diaspora.signInAs(RON_P1);
//        Feed.addAspectPost(WORK, the("Ron for work"));
//        Feed.addAspectPost(ACQUAINTANCES, the("Ron for acquaintances"));
//        Feed.assertNthPostIs(0, RON_P1, the("Ron for acquaintances"));//this check for wait moment when stream will be loaded
//
//        //filtering - all aspects is disabled - all aspects
//        NavBar.openMyAspects();
//        Aspects.toggleAll();
//        Aspects.assertToggleAllText("Select all");
//        Feed.assertPostFrom(RON_P1, the("Ron for work"));
//        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
//        Feed.assertPostFrom(EVE_P1, the("Eve for work"));
//
//        //filtering - work - personal post in this aspect and posts of another user linked with this aspect
//        Aspects.toggleAspect(WORK);
//        Feed.assertPostFrom(RON_P1, the("Ron for work"));
//        Feed.assertNoPostFrom(RON_P1, the("Ron for acquaintances"));
//        Feed.assertNoPostFrom(EVE_P1, the("Eve for work"));
//
//        //filtering - Work is disabled, Acquaintances is enabled
//        Aspects.toggleAspect(WORK);
//        Aspects.toggleAspect(ACQUAINTANCES);
//        Feed.assertNoPostFrom(RON_P1, the("Ron for work"));
//        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
//        Feed.assertPostFrom(EVE_P1, the("Eve for work"));
//
//        //select all aspects
//        Aspects.toggleAll();
//        Aspects.assertToggleAllText("Deselect all");
//        Feed.assertPostFrom(RON_P1, the("Ron for work"));
//        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
//        Feed.assertPostFrom(EVE_P1, the("Eve for work"));
//
//    }


}

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
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.helpers.UniqueDataHelper.the;

@Category(ua.net.itlabs.categories.Contacts.class)
public class DiasporaContactsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout and clear information about unique values
        setTimeOut();
  }

    @Test
    public void testAddContacts() {
        //GIVEN - setup relation between users
        clearThe();
        Relation.forUser(ROB_P1).toUser(RON_P1, WORK).build();
        Relation.forUser(RON_P1).toUser(ROB_P1, WORK).doNotLogOut().build();

        //add posts in not used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Ron for family before manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family before manage contacts"));//this check for wait moment when stream will be loaded

        //add contacts in this aspect
        Menu.openContacts();
        Contacts.addLinkedContactForAspect(FAMILY, ROB_P1);

        //add post in this aspect
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check stream linked in this aspect user
        //earlier published post does not appear in stream
        //later published post appears in stream
        Diaspora.signInAs(ROB_P1);
        Feed.assertNoPostFrom(RON_P1, the("Ron for family before manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for family after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testDeleteContacts() {
        //GIVEN - setup relation between users
        clearThe();
        Relation.forUser(ROB_P1).toUser(RON_P1, WORK).build();
        Relation.forUser(RON_P1).toUser(ROB_P1, FRIENDS).doNotLogOut().build();

        //add posts in used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for friends before manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for friends before manage contacts"));//this check for wait moment when stream will be loaded

        //manage contacts - delete contact in aspect
        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(FRIENDS, ROB_P1);

        //add posts in deleted aspect after managing contacts
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for friends after manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for friends after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check stream linked in this aspect user
        //earlier published post remind in stream
        //later published post does not appear in stream
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(RON_P1, the("Ron for friends before manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testManageContacts() {
        //GIVEN - setup relation between users
        clearThe();
        Relation.forUser(ANA_P1).notToUsers(RON_P1).build();
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).doNotLogOut().build();

        //manage contacts - delete, add contacts in some aspects
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(ANA_P1, FRIENDS, FAMILY);

        //add posts in used aspects after managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - in stream available post from unlinked user is not shown
        Diaspora.signInAs(ANA_P1);
        Feed.assertNoPostFrom(RON_P1, the("Ron for work after manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for family after manage contacts"));

        //add contact in some aspect through Manage Comtacts
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(RON_P1, ACQUAINTANCES);

        //check stream  - limited posts in right aspect is shown in stream
        Menu.openStream();
        Feed.assertNoPostFrom(RON_P1, the("Ron for work after manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for family after manage contacts"));
        Menu.logOut();
    }

}

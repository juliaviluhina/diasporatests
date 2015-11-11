package ua.net.itlabs.diaspora;


import steps.Relation;
import org.junit.Before;
import org.junit.Test;
import pages.*;
import pages.Contacts;
import pages.Feed;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.helpers.UniqueDataHelper.the;
import static core.Gherkin.*;

public class ContactsTest extends BaseTest {

    @Test
    public void testAddContacts() {
        GIVEN("Setup relation between users");
        clearUniqueData();
        Relation.forUser(Pod1.rob).toUser(Pod1.eve, WORK).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.rob, WORK).doNotLogOut().ensure();

        WHEN("Limited post in not used aspects is added by author");
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Eve for family before manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family before manage contacts"));//this check for wait moment when stream will be loaded

        AND("After that contact with user in this aspect is added by author");
        Menu.openContacts();
        Contacts.addLinkedContactForAspect(FAMILY, Pod1.rob);

        AND("Limited post in this aspect is added by author");
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Eve for family after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();


        THEN("Post of author added before linking is not shown in user's stream");
        AND("Post of author added after linking is shown in user's stream");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPost(Pod1.eve, the("Eve for family before manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testDeleteContacts() {

        GIVEN("Setup relation between users");
        clearUniqueData();
        Relation.forUser(Pod1.rob).toUser(Pod1.eve, WORK).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.rob, FRIENDS).doNotLogOut().ensure();

        WHEN("Limited post in used aspects is added by author");
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Eve for friends before manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for friends before manage contacts"));//this check for wait moment when stream will be loaded

        AND("After that contact with user in this aspect is deleted by author");
        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(FRIENDS, Pod1.rob);

        AND("Limited post in this aspect is added by author");
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Eve for friends after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for friends after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        THEN("Post of author added before link deletion is shown in user's stream");
        AND("Post of author added after link deletion is not shown in user's stream");
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPost(Pod1.eve, the("Eve for friends before manage contacts"));
        Feed.assertNoPost(Pod1.eve, the("Eve for friends after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testManageContacts() {
        GIVEN("Setup relation between users");
        clearUniqueData();
        Relation.forUser(Pod1.ana).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, WORK).doNotLogOut().ensure();

        //manage contacts - delete, add contacts in some aspects
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.ana, FRIENDS, FAMILY);

        //add posts in used aspects after managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Eve for work after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Eve for family after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - in stream available post from unlinked user is not shown
        Diaspora.signInAs(Pod1.ana);
        Feed.assertNoPost(Pod1.eve, the("Eve for work after manage contacts"));
        Feed.assertNoPost(Pod1.eve, the("Eve for family after manage contacts"));

        //add contact in some aspect through Manage Comtacts
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.eve, ACQUAINTANCES);

        //check stream  - limited posts in right aspect is shown in stream
        Menu.openStream();
        Feed.assertNoPost(Pod1.eve, the("Eve for work after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));
        Menu.logOut();
    }


    @Test
    public void testAddAspectInContacts() {
        //GIVEN - setup relation between users
        clearUniqueData();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, WORK).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, WORK).doNotLogOut().ensure();

        //add new aspect on contacts site
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));
        Contacts.selectAspect(the("Asp1"));
        Contacts.assertCountContactsInAspect(the("Asp1"), 0);

        //add link with this aspect
        Contacts.addLinkedContactForAspect(the("Asp1"), Pod1.ana);
        Contacts.selectAspect(the("Asp1"));//only after this action counter is changed
        Contacts.assertCountContactsInAspect(the("Asp1"), 1);

        //add post with this aspect
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the("Asp1") + " Post for new Aspect from Ron");
        Feed.assertPost(Pod1.eve, the("Asp1") + " Post for new Aspect from Ron");//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check post visibility in stream of linked in new aspect contact
        Diaspora.signInAs(Pod1.ana);
        Feed.assertPost(Pod1.eve, the("Asp1") + " Post for new Aspect from Ron");
        Menu.logOut();

    }

    @Test
    public void testRenameAspectInContacts() {
        //GIVEN - add aspect
        clearUniqueData();
        Diaspora.signInAs(Pod1.eve);
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));

        //rename current aspect
        Contacts.selectAspect(the("Asp1"));
        Contacts.rename(the("Asp2"));

        //check changes in aspects in contacts site
        Contacts.assertNoAspect(the("Asp1"));
        Contacts.assertAspect(the("Asp2"));

        //check changes in aspects in manage aspect button for new post
        Menu.openStream();
        Feed.assertNoAspectForNewPost(the("Asp1"));
        Feed.assertAspectForNewPost(the("Asp2"));

        Menu.logOut();

    }

    @Test
    public void testDeleteAspectInContacts() {
        //GIVEN - setup relation between users, add aspect
        clearUniqueData();
        Diaspora.signInAs(Pod1.eve);
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));

        //delete added aspect
        Contacts.selectAspect(the("Asp1"));
        Contacts.deleteAspect();

        //check changes in aspects in contacts site
        Contacts.assertNoAspect(the("Asp1"));

        //check changes in aspects in manage aspect button for new post
        Menu.openStream();
        Feed.assertNoAspectForNewPost(the("Asp1"));

        Menu.logOut();

    }

}

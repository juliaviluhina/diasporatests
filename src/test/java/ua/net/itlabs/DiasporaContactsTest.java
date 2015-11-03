package ua.net.itlabs;


import core.steps.Relation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.*;
import pages.Contacts;
import pages.Feed;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaContactsTest extends BaseTest {

    @Before
    public void setupForTest() {
        clearUniqueData();
    }

    @Test
    public void testAddContacts() {
        //GIVEN - setup relation between users
        Relation.forUser(Pod1.rob).toUser(Pod1.ron, WORK).build();
        Relation.forUser(Pod1.ron).toUser(Pod1.rob, WORK).doNotLogOut().build();

        //add posts in not used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Ron for family before manage contacts"));
        Feed.assertNthPostIs(0, Pod1.ron, the("Ron for family before manage contacts"));//this check for wait moment when stream will be loaded

        //add contacts in this aspect
        Menu.openContacts();
        Contacts.addLinkedContactForAspect(FAMILY, Pod1.rob);

        //add post in this aspect
        Menu.openStream();
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0, Pod1.ron, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check stream linked in this aspect user
        //earlier published post does not appear in stream
        //later published post appears in stream
        Diaspora.signInAs(Pod1.rob);
        Feed.assertNoPostFrom(Pod1.ron, the("Ron for family before manage contacts"));
        Feed.assertPostFrom(Pod1.ron, the("Ron for family after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testDeleteContacts() {
        //GIVEN - setup relation between users
        Relation.forUser(Pod1.rob).toUser(Pod1.ron, WORK).build();
        Relation.forUser(Pod1.ron).toUser(Pod1.rob, FRIENDS).doNotLogOut().build();

        //add posts in used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for friends before manage contacts"));
        Feed.assertNthPostIs(0, Pod1.ron, the("Ron for friends before manage contacts"));//this check for wait moment when stream will be loaded

        //manage contacts - delete contact in aspect
        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(FRIENDS, Pod1.rob);

        //add posts in deleted aspect after managing contacts
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, the("Ron for friends after manage contacts"));
        Feed.assertNthPostIs(0, Pod1.ron, the("Ron for friends after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check stream linked in this aspect user
        //earlier published post remind in stream
        //later published post does not appear in stream
        Diaspora.signInAs(Pod1.rob);
        Feed.assertPostFrom(Pod1.ron, the("Ron for friends before manage contacts"));
        Feed.assertNoPostFrom(Pod1.ron, the("Ron for friends after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void testManageContacts() {
        //GIVEN - setup relation between users
        Relation.forUser(Pod1.ana).notToUsers(Pod1.ron).build();
        Relation.forUser(Pod1.ron).toUser(Pod1.ana, WORK).doNotLogOut().build();

        //manage contacts - delete, add contacts in some aspects
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.ana, FRIENDS, FAMILY);

        //add posts in used aspects after managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0, Pod1.ron, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - in stream available post from unlinked user is not shown
        Diaspora.signInAs(Pod1.ana);
        Feed.assertNoPostFrom(Pod1.ron, the("Ron for work after manage contacts"));
        Feed.assertNoPostFrom(Pod1.ron, the("Ron for family after manage contacts"));

        //add contact in some aspect through Manage Comtacts
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.ron, ACQUAINTANCES);

        //check stream  - limited posts in right aspect is shown in stream
        Menu.openStream();
        Feed.assertNoPostFrom(Pod1.ron, the("Ron for work after manage contacts"));
        Feed.assertPostFrom(Pod1.ron, the("Ron for family after manage contacts"));
        Menu.logOut();
    }


    @Test
    public void testAddAspectInContacts() {
        //GIVEN - setup relation between users
        Relation.forUser(Pod1.ana).toUser(Pod1.ron, WORK).build();
        Relation.forUser(Pod1.ron).toUser(Pod1.ana, WORK).doNotLogOut().build();

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
        Feed.assertNthPostIs(0, Pod1.ron, the("Asp1") + " Post for new Aspect from Ron");//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check post visibility in stream of linked in new aspect contact
        Diaspora.signInAs(Pod1.ana);
        Feed.assertPostFrom(Pod1.ron, the("Asp1") + " Post for new Aspect from Ron");
        Menu.logOut();

    }

    @Test
    public void testRenameAspectInContacts() {
        //GIVEN - add aspect
        Diaspora.signInAs(Pod1.ron);
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
        Diaspora.signInAs(Pod1.ron);
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

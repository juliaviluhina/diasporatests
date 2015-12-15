package ua.net.itlabs.diaspora;


import org.junit.experimental.categories.Category;
import steps.Relation;
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
import static ua.net.itlabs.testDatas.Phrases.*;

public class ContactsTest extends BaseTest {

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testAddContacts() {

        GIVEN("Sam<-+->Bob as Work");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, WORK).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, WORK).doNotLogOut().ensure();

        GIVEN("Limited post in Family aspect is not exists");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("Limited post in not used aspects is added by author");
        Feed.addAspectPost(FAMILY, POST_FOR_FAMILY);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);//this check for wait moment when stream will be loaded

        AND("After that contact with user in this aspect is added by author");
        Menu.openContacts();
        Contacts.addLinkedContactForAspect(FAMILY, Pod2.sam);

        AND("Limited post in this aspect is added by author");
        Menu.openStream();
        Feed.addAspectPost(FAMILY, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);//this check for wait moment when stream will be loaded

        THEN("Post of author added before linking is not shown in user's stream");
        AND("Post of author added after linking is shown in user's stream");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FAMILY);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);

    }

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testDeleteContacts() {

        GIVEN("Sam-+->Bob as Work, Bob-+->Sam as Friends");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, WORK).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, FRIENDS).doNotLogOut().ensure();

        GIVEN("Limited post in Friends aspect is not exists");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("Limited post in used aspects is added by author");
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, POST_FOR_FAMILY);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);//this check for wait moment when stream will be loaded

        AND("After that contact with user in this aspect is deleted by author");
        Menu.openContacts();
        Contacts.deleteLinkedContactForAspect(FRIENDS, Pod2.sam);

        AND("Limited post in this aspect is added by author");
        Menu.openStream();
        Feed.addAspectPost(FRIENDS, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);//this check for wait moment when stream will be loaded

        THEN("Post of author added before link deletion is shown in user's stream");
        AND("Post of author added after link deletion is not shown in user's stream");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FAMILY_AFTER_MANAGING_CONTACTS);

    }

    @Test
    public void testManageContacts() {
        GIVEN("Setup relation between users");
        clearUniqueData();
        Relation.forUser(Pod1.ana).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, WORK).doNotLogOut().ensure();

        WHEN("User is linked by author in some aspects in All contacts");
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.ana, FRIENDS, FAMILY);

        AND("Limited in this and another aspects posts is added by author");
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Eve for work after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Eve for family after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));//this check for wait moment when stream will be loaded

        THEN("Author's limited post in right aspect is available by user in contact's stream when user doesn't have link to author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertNoPost(Pod1.eve, the("Eve for work after manage contacts"));
        Feed.assertNoPost(Pod1.eve, the("Eve for family after manage contacts"));

        WHEN("Author is linked by user in some aspects in All contacts");
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod1.eve, ACQUAINTANCES);

        THEN("In stream of user available limited posts of author is shown");
        Menu.openStream();
        Feed.assertNoPost(Pod1.eve, the("Eve for work after manage contacts"));
        Feed.assertPost(Pod1.eve, the("Eve for family after manage contacts"));
    }


    @Test
    public void testAddAspectInContacts() {
        GIVEN("Setup relation between users");
        clearUniqueData();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, WORK).ensure();
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, WORK).doNotLogOut().ensure();

        WHEN("Aspect is added by author");
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));

        THEN("New aspect is shown in summary information about aspects");
        Contacts.selectAspect(the("Asp1"));
        Contacts.assertCountContactsInAspect(the("Asp1"), 0);

        WHEN("Linked in another aspect user is added to this aspect");
        Contacts.addLinkedContactForAspect(the("Asp1"), Pod1.ana);
        Contacts.selectAspect(the("Asp1"));//only after this action counter is changed

        THEN("In summary information count of contacts in this aspect is incremented");
        Contacts.assertCountContactsInAspect(the("Asp1"), 1);

        WHEN("Limited in this aspect post is added by author");
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the("Asp1") + " Post for new Aspect from Ron");
        Feed.assertPost(Pod1.eve, the("Asp1") + " Post for new Aspect from Ron");//this check for wait moment when stream will be loaded

        THEN("This post is shown in user's stream");
        Diaspora.ensureSignInAs(Pod1.ana);
        Feed.assertPost(Pod1.eve, the("Asp1") + " Post for new Aspect from Ron");

    }

    @Test
    public void testRenameAspectInContacts() {
        GIVEN("Aspect is added");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));

        WHEN("Aspect is renamed");
        Contacts.selectAspect(the("Asp1"));
        Contacts.rename(the("Asp2"));

        THEN("In summary information is no aspect with old name and is aspect with new name");
        Contacts.assertNoAspect(the("Asp1"));
        Contacts.assertAspect(the("Asp2"));

        EXPECT("New aspect is available to manage aspects of post");
        Menu.openStream();
        Feed.assertNoAspectForNewPost(the("Asp1"));
        Feed.assertAspectForNewPost(the("Asp2"));

    }

    @Test
    public void testDeleteAspectInContacts() {
        GIVEN("Setup relation between users, aspect is added");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));

        WHEN("Aspect is deleted");
        Contacts.selectAspect(the("Asp1"));
        Contacts.deleteAspect();

        THEN("In summary information deleted aspect is not shown");
        Contacts.assertNoAspect(the("Asp1"));

        EXPECT("Deleted aspect is not available to manage aspects of post");
        Menu.openStream();
        Feed.assertNoAspectForNewPost(the("Asp1"));

    }

}

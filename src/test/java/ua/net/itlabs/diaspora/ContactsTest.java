package ua.net.itlabs.diaspora;


import org.junit.experimental.categories.Category;
import steps.Relation;
import org.junit.Test;
import pages.*;
import pages.Contacts;
import pages.Feed;
import steps.Scenarios;
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

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testManageContacts() {

        GIVEN("Sam-X->Bob, Bob-+->Sam as Work");
        Relation.forUser(Pod2.sam).notToUsers(Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, WORK).doNotLogOut().ensure();

        GIVEN("Limited post in Work, Family aspects is not exists");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, POST_FOR_WORK);
        Feed.ensureNoPost(Pod2.bob, POST_FOR_FAMILY);

        WHEN("User is linked by author in some aspects in All contacts");
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod2.sam, FRIENDS, FAMILY);

        AND("Limited in this and another aspects posts is added by author");
        Menu.openStream();
        Feed.addAspectPost(WORK, POST_FOR_WORK);
        Feed.addAspectPost(FAMILY, POST_FOR_FAMILY);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);//this check for wait moment when stream will be loaded

        THEN("Author's limited post in right aspect is not shown in user's stream because of non linking user to author");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertNoPost(Pod2.bob, POST_FOR_FAMILY);
        Feed.assertNoPost(Pod2.bob, POST_FOR_WORK);

        WHEN("Author is linked by user in some aspects in All contacts");
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(Pod2.bob, ACQUAINTANCES);

        THEN("In stream of user available limited posts of author is shown");
        Menu.openStream();
        Feed.assertNoPost(Pod2.bob, POST_FOR_WORK);
        Feed.assertPost(Pod2.bob, POST_FOR_FAMILY);
    }

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testAddAspectInContacts() {

        GIVEN("Sam<-+->Bob as Work");
        Relation.forUser(Pod2.sam).toUser(Pod2.bob, WORK).ensure();
        Relation.forUser(Pod2.bob).toUser(Pod2.sam, WORK).doNotLogOut().ensure();

        GIVEN("Limited post in Aspect1 aspect is not exist. Aspect1 is not exist.");
        Menu.openStream();
        Feed.ensureNoPost(Pod2.bob, POST_IN_ASPECT1);
        Aspects.ensureNoAspect(ASPECT1);

        WHEN("Aspect is added by author");
        Menu.openContacts();
        Contacts.addAspect(ASPECT1);

        THEN("New aspect is shown in summary information about aspects");
        Contacts.selectAspect(ASPECT1);
        Contacts.assertCountContactsInAspect(ASPECT1, 0);

        WHEN("Linked in another aspect user is added to this aspect");
        Contacts.addLinkedContactForAspect(ASPECT1, Pod2.sam);
        Contacts.selectAspect(ASPECT1);//only after this action counter is changed

        THEN("In summary information count of contacts in this aspect is incremented");
        Contacts.assertCountContactsInAspect(ASPECT1, 1);

        WHEN("Limited in this aspect post is added by author");
        Menu.openStream();
        Feed.addAspectPost(ASPECT1, POST_IN_ASPECT1);
        Feed.assertPost(Pod2.bob, POST_IN_ASPECT1);//this check for wait moment when stream will be loaded

        THEN("This post is shown in user's stream");
        Diaspora.ensureSignInAs(Pod2.sam);
        Feed.assertPost(Pod2.bob, POST_IN_ASPECT1);

    }

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testRenameAspectInContacts() {

        GIVEN("Aspect1 is exist, Aspect2 is not exist");
        Diaspora.ensureSignInAs(Pod2.bob);
        Menu.openContacts();
        Contacts.ensureAspect(ASPECT1);
        Contacts.ensureNoAspect(ASPECT2);

        WHEN("Aspect is renamed");
        Contacts.selectAspect(ASPECT1);
        Contacts.rename(ASPECT2);

        THEN("In summary information is no aspect with old name and is aspect with new name");
        Contacts.assertNoAspect(ASPECT1);
        Contacts.assertAspect(ASPECT2);

        EXPECT("New aspect is available to manage aspects of post");
        Menu.openStream();
        Feed.assertNoAspectForNewPost(ASPECT1);
        Feed.assertAspectForNewPost(ASPECT2);

    }

    @Test
    public void testDeleteAspectInContacts() {

        GIVEN("Aspect1 is exist");
        Diaspora.ensureSignInAs(Pod2.bob);
        Menu.openContacts();
        Contacts.ensureAspect(ASPECT1);

        WHEN("Aspect is deleted");
        Contacts.selectAspect(ASPECT1);
        Contacts.deleteAspect();

        THEN("In summary information deleted aspect is not shown");
        Contacts.assertNoAspect(ASPECT1);

        EXPECT("Deleted aspect is not available to manage aspects of post");
        Menu.openStream();
        Feed.assertNoAspectForNewPost(ASPECT1);

    }

}

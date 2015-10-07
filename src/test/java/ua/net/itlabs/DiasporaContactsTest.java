package ua.net.itlabs;


import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static ua.net.itlabs.testDatas.Users.*;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.FAMILY;
import static pages.Aspects.WORK;
import static pages.Aspects.ACQUAINTANCES;
import static core.helpers.UniqueDataHelper.the;

public class DiasporaContactsTest extends BaseTest {

    @Test
    public void addDeleteContactsTest() {
        //GIVEN - setup relation between users
        Relation.forUser(ROB_P1).toUser(RON_P1,WORK).build();
        Relation.forUser(RON_P1).toUser(ROB_P1, WORK, FRIENDS).doNotLogOut().build();
        //add posts in used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work before manage contacts"));
        Feed.addAspectPost(FRIENDS, the("Ron for friends before manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family before manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family before manage contacts"));//this check for wait moment when stream will be loaded

        //manage contacts - delete, add contacts in some aspects
        Menu.openContacts();
        Contacts.addLinkedContactForAspect(FAMILY, ROB_P1);
        Contacts.deleteLinkedContactForAspect(FRIENDS, ROB_P1);

        //add posts in used aspects after managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work after manage contacts"));
        Feed.addAspectPost(FRIENDS, the("Ron for friends after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check stream Rob (before - Work, Friends, after - Family, Work)
        //earlier published posts are not changed in stream
        Diaspora.signInAs(ROB_P1);
        Feed.assertPostFrom(RON_P1, the("Ron for work before manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for work after manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for friends before manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends after manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for family before manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for family after manage contacts"));
        Menu.logOut();

    }

    @Test
    public void manageContactsTest() {
        //GIVEN - setup relation between users
        Relation.forUser(ANA_P1).notToUsers(RON_P1).build();
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).doNotLogOut().build();
        //add posts in used aspects before managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work before manage contacts"));
        Feed.addAspectPost(FRIENDS, the("Ron for friends before manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family before manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family before manage contacts"));//this check for wait moment when stream will be loaded

        //manage contacts - delete, add contacts in some aspects
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(ANA_P1, FRIENDS, FAMILY);

        //add posts in used aspects after managing contacts
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Ron for work after manage contacts"));
        Feed.addAspectPost(FRIENDS, the("Ron for friends after manage contacts"));
        Feed.addAspectPost(FAMILY, the("Ron for family after manage contacts"));
        Feed.assertNthPostIs(0,RON_P1, the("Ron for family after manage contacts"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //only after linking from Ana`a side posts from Ron can be shown in stream
        Menu.openContacts();
        Contacts.openAllContacts();
        Contacts.ensureAspectsForContact(RON_P1, ACQUAINTANCES);

        //check stream ANA  - posts is show in stream  - according to links before(Work) and after (Friends, Family)
        Menu.openStream();
        Feed.assertPostFrom(RON_P1, the("Ron for work before manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for friends before manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for family before manage contacts"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for work after manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for friends after manage contacts"));
        Feed.assertPostFrom(RON_P1, the("Ron for family after manage contacts"));
        Menu.logOut();
    }


//    @Test
//    public void testContacts() {
//        //GIVEN - setup relation between users in some aspect
//        //add posts for different aspects
//        Relation.forUser(ROB_P1).toUser(ANA_P1, FAMILY, FRIENDS).notToUsers(EVE_P1).build();
//        Diaspora.signInAs(ROB_P1);
//        Feed.addAspectPost(FAMILY, the("Rob for Family"));
//        Feed.addAspectPost(FRIENDS, the("Rob for Friends"));
//        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances"));
//        Feed.assertNthPostIs(0, ROB_P1, the("Rob for Acquaintances"));//this check for wait moment when stream will be loaded
//        //add new aspect in Contacts page, add relation in this aspect
//        Menu.openContacts();
//        Contacts.addAspect(the("Aspect"));
//        Contacts.selectAspect(the("Aspect"));
//        Contacts.addLinkedContactForAspect(the("Aspect"), ANA_P1);
//        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
//        Contacts.assertCountContactsInAspect(the("Aspect"), 1);
//
//        //delete aspect for contact
//        int countFriends = Contacts.countContactsInAspect(FRIENDS);
//        Contacts.deleteLinkedContactForAspect(FRIENDS, ANA_P1);
//        countFriends--;
//        //check aspect counter
//        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
//        Contacts.assertCountContactsInAspect(FRIENDS, countFriends);
//
//        //add post for Friends after deletion aspect for contact
//        Menu.openStream();
//        Feed.addAspectPost(FRIENDS, the("Rob for Friends 2 "));
//        Feed.assertNthPostIs(0, ROB_P1, the("Rob for Friends 2 "));
//
//        //add post for new aspect
//        Feed.addAspectPost(the("Aspect"), the("Aspect")+the(" Rob for new aspect"));
//        Feed.assertNthPostIs(0, ROB_P1, the("Aspect")+the(" Rob for new aspect"));
//        Menu.logOut();
//
//        //check posts in Ana`s stream
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(ROB_P1, the("Rob for Family"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for Friends"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Friends 2"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Acquaintances"));
//        Feed.assertPostFrom(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
//        Menu.logOut();
//
//        //check posts in Eve`s stream
//        Diaspora.signInAs(EVE_P1);
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Family"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Friends"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Friends 2"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Acquaintances"));
//        Feed.assertNoPostFrom(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
//        Menu.logOut();
//
//        //change Rob`s aspects for Ana through button
//        Diaspora.signInAs(ROB_P1);
//        Menu.openContacts();
//        Contacts.openAllContacts();
//        Contact.ensureAspectsForContact(contact(ANA_P1), ACQUAINTANCES, the("Aspect"));
//
//        //add post for Friends after addition aspect for contact
//        Menu.openStream();
//        Feed.addAspectPost(ACQUAINTANCES, the("Rob for Acquaintances 2 "));
//        Feed.assertNthPostIs(0, ROB_P1, the("Rob for Acquaintances 2 "));
//        Menu.logOut();
//
//        //check posts in Ana`s stream
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(ROB_P1, the("Rob for Family"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for Friends"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Friends 2"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Acquaintances"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for Acquaintances 2 "));
//        Feed.assertPostFrom(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
//
//        Menu.logOut();
//
//        //delete Rob`s aspect
//        Diaspora.signInAs(ROB_P1);
//        Menu.openContacts();
//        Contacts.selectAspect(the("Aspect"));
//        Contacts.deleteAspect();
//        Contacts.assertNoAspect(the("Aspect"));
//        Menu.logOut();
//
//        //check posts in Ana`s stream
//        Diaspora.signInAs(ANA_P1);
//        Feed.assertPostFrom(ROB_P1, the("Rob for Family"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for Friends"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Friends 2"));
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for Acquaintances"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for Acquaintances 2 "));
//        Feed.assertPostFrom(ROB_P1, the("Aspect") + the(" Rob for new aspect"));
//        Menu.logOut();
//
//    }

}

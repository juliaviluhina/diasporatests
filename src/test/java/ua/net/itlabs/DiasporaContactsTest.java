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
        Diaspora.signInAs(ANA_P1);
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

    @Test
    public void OperationWithNewAspectTest() {
        //GIVEN - setup relation between users
        Relation.forUser(ANA_P1).toUser(RON_P1, WORK).build();
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).doNotLogOut().build();

        //add new aspect on contacts site
        Menu.openContacts();
        Contacts.addAspect(the("Asp1"));
        Contacts.selectAspect(the("Asp1"));
        Contacts.assertCountContactsInAspect(the("Asp1"),0);

        //add link with this aspect
        Contacts.addLinkedContactForAspect(the("Asp1"), ANA_P1);
        Contacts.selectAspect(the("Asp1"));//only after this action counter is changed
        Contacts.assertCountContactsInAspect(the("Asp1"), 1);

        //add post with this aspect
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the("Asp1") + " Post for new Aspect from Ron");
        Feed.assertNthPostIs(0, RON_P1, the("Asp1") + " Post for new Aspect from Ron" );//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check post visibility in stream of linked in new aspect contact
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
        Menu.logOut();

        //rename aspect on contacts site
        Diaspora.signInAs(RON_P1);
        Menu.openContacts();
        Contacts.selectAspect(the("Asp1"));
        Contacts.rename(the("Asp2"));

        //check changes in aspects in contacts site
        Contacts.assertAspect(the("Asp2"));
        Contacts.assertNoAspect(the("Asp1"));

        //add new post for renamed aspect
        Menu.openStream();
        Feed.addAspectPost(the("Asp2"), the("Asp2") + " Post for renamed Aspect from Ron");
        Feed.assertNthPostIs(0, RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
        Menu.logOut();

        //check post visibility in stream of linked in new aspect contact
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
        Feed.assertPostFrom(RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
        Menu.logOut();

        //delete added aspect
        Diaspora.signInAs(RON_P1);
        Menu.openContacts();
        Contacts.selectAspect(the("Asp2"));
        Contacts.deleteAspect();

        //check - is no aspect in contact site after deletion
        Contacts.assertNoAspect(the("Asp2"));
        Menu.logOut();

        //check post visibility in stream of linked contact (post limited in deleted aspect and earlier was available)
        Diaspora.signInAs(ANA_P1);
        Feed.assertPostFrom(RON_P1, the("Asp1") + " Post for new Aspect from Ron");
        Feed.assertPostFrom(RON_P1, the("Asp2") + " Post for renamed Aspect from Ron");
        Menu.logOut();

    }

//    @Test
//    public void testContacts() {
//        Menu.openContacts();
//        Contacts.addAspect(the("Aspect"));
//        Contacts.selectAspect(the("Aspect"));
//        Contacts.addLinkedContactForAspect(the("Aspect"), ANA_P1);
//        Contacts.selectAspect(FRIENDS);//only after this action counter is changed
//        Contacts.assertCountContactsInAspect(the("Aspect"), 1);
//
//        //add post for new aspect
//        Feed.addAspectPost(the("Aspect"), the("Aspect")+the(" Rob for new aspect"));
//        Feed.assertNthPostIs(0, ROB_P1, the("Aspect")+the(" Rob for new aspect"));
//        Menu.logOut();
//
//    }

}

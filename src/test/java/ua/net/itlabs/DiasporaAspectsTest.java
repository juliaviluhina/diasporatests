package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import pages.*;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;

public class DiasporaAspectsTest extends BaseTest {

//    @Test
//    public void testAspects() {
//        //GIVEN - setup relation between users in some aspect
//        String tag = "#a_r";
//        Relation.forUser(ANA_P1).toUser(ROB_P1, FAMILY, FRIENDS).notToUsers(EVE_P1).withTags(tag).build();
//        Relation.forUser(ROB_P1).toUser(ANA_P1, WORK, ACQUAINTANCES).notToUsers(EVE_P1).withTags(tag).build();
//        Relation.forUser(EVE_P1).notToUsers(ANA_P1, BOB_P2).withTags(tag).build();
//
//        //add posts - Eve
//        Diaspora.signInAs(EVE_P1);
//        Feed.addPublicPost(the(tag + " Public Eve"));
//        Feed.assertNthPostIs(0, EVE_P1, the(tag + " Public Eve"));
//        Feed.addAllAspectsPost(the("All aspects Eve"));
//        Feed.assertNthPostIs(0, EVE_P1, the("All aspects Eve"));
//        Menu.logOut();
//        //add posts - Ana
//        Diaspora.signInAs(ANA_P1);
//        Feed.addAspectPost(FRIENDS, the("Ana for friends"));
//        Feed.assertNthPostIs(0, ANA_P1, the("Ana for friends"));
//        Feed.addAspectPost(WORK, the("Ana for work"));
//        Feed.assertNthPostIs(0, ANA_P1, the("Ana for work"));
//        Menu.logOut();
//        //Add posts - Rob
//        Diaspora.signInAs(ROB_P1);
//        Feed.addAspectPost(FAMILY, the("Rob for family"));
//
//        //check - all available posts in Rob's stream
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //filtering - all aspects is disabled - all aspects
//        NavBar.openMyAspects();
//        Aspects.toggleAll();
//        Aspects.assertToggleAllText("Select all");
//        Feed.assertNthPostIs(0, ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //filtering - Friends is enabled
//        Aspects.toggleAspect(FRIENDS);
//        Feed.assertNoPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //filtering - Family, Friends is enabled
//        Aspects.toggleAspect(FAMILY);
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //filtering - Family, Friends, Work is enabled
//        Aspects.toggleAspect(WORK);
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //add new aspect and link user in aspect
//        Aspects.add(the("Aspect"));
//        Menu.search(EVE_P1.fullName);
//        Contact.ensureAspectsForContact(the("Aspect"));
//        //add new post in this aspect
//        Menu.openStream();
//        Feed.addAspectPost(the("Aspect"), the(the("Aspect") + " Rob for new aspect"));
//        Feed.assertPostFrom(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
//
//        //deselect aspect work and select added aspect
//        NavBar.openMyAspects();
//        Aspects.toggleAspect(WORK);
//        Aspects.toggleAspect(the("Aspect"));
//
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertPostFrom(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
//        Feed.assertPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //edit aspect
//        Aspects.switchToEditMode(the("Aspect"));
//        Contacts.rename(the("Asp"));
//        Menu.openStream();
//        NavBar.openMyAspects();
//        Aspects.assertNoAspectInNavBar(the("Aspect"));
//
//        //delete aspect
//        Aspects.switchToEditMode(the("Asp"));
//        Contacts.deleteAspect();
//        Menu.openStream();
//        NavBar.openMyAspects();
//        Aspects.assertNoAspectInNavBar(the("Asp"));
//
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//
//        //select all
//        Aspects.toggleAll();
//        Aspects.assertToggleAllText("Deselect all");
//        Feed.assertNoPostFrom(ROB_P1, the(the("Aspect") + " Rob for new aspect"));
//        Feed.assertPostFrom(ROB_P1, the("Rob for family"));
//        Feed.assertNoPostFrom(EVE_P1, the(tag + " Public Eve"));
//        Feed.assertNoPostFrom(EVE_P1, the("All aspects Eve"));
//        Feed.assertPostFrom(ANA_P1, the("Ana for friends"));
//        Feed.assertNoPostFrom(ANA_P1, the("Ana for work"));
//    }

}

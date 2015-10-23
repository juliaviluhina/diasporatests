package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.*;
import pages.Aspects;
import pages.Contacts;
import pages.Feed;
import ua.net.itlabs.categories.*;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static pages.Aspects.WORK;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;

@Category(ua.net.itlabs.categories.Aspects.class)
public class DiasporaAspectsTest extends BaseTest {


    @Test
    public void testAspectsFilteringInNavBar() {
        //GIVEN - setup mutual relation between users in some different aspects
        //add post in this aspect
        Relation.forUser(RON_P1).toUser(EVE_P1, ACQUAINTANCES).build();
        Relation.forUser(EVE_P1).toUser(RON_P1, WORK).doNotLogOut().build();
        Menu.openStream();
        Feed.addAspectPost(WORK, the("Eve for work"));
        Feed.assertNthPostIs(0, EVE_P1, the("Eve for work"));//this check for wait moment when stream will be loaded
        Menu.logOut();
        Diaspora.signInAs(RON_P1);
        Feed.addAspectPost(WORK, the("Ron for work"));
        Feed.addAspectPost(ACQUAINTANCES, the("Ron for acquaintances"));
        Feed.assertNthPostIs(0, RON_P1, the("Ron for acquaintances"));//this check for wait moment when stream will be loaded

        //filtering - all aspects is disabled - all aspects
        NavBar.openMyAspects();
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Select all");
        Feed.assertPostFrom(RON_P1, the("Ron for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
        Feed.assertPostFrom(EVE_P1, the("Eve for work"));

        //filtering - work - personal post in this aspect and posts of another user linked with this aspect
        Aspects.toggleAspect(WORK);
        Feed.assertPostFrom(RON_P1, the("Ron for work"));
        Feed.assertNoPostFrom(RON_P1, the("Ron for acquaintances"));
        Feed.assertNoPostFrom(EVE_P1, the("Eve for work"));

        //filtering - Work is disabled, Acquaintances is enabled
        Aspects.toggleAspect(WORK);
        Aspects.toggleAspect(ACQUAINTANCES);
        Feed.assertNoPostFrom(RON_P1, the("Ron for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
        Feed.assertPostFrom(EVE_P1, the("Eve for work"));

        //select all aspects
        Aspects.toggleAll();
        Aspects.assertToggleAllText("Deselect all");
        Feed.assertPostFrom(RON_P1, the("Ron for work"));
        Feed.assertPostFrom(RON_P1, the("Ron for acquaintances"));
        Feed.assertPostFrom(EVE_P1, the("Eve for work"));

    }


    @Test
    public void testAddEditDeleteAspectsFromNavBar() {

        Diaspora.signInAs(RON_P1);

        //add new aspect
        NavBar.openMyAspects();
        Aspects.add(the("Asp1"));
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertAspectInNavBar(the("Asp1"));//this check for wait moment when stream will be loaded

        //add limited post in this in aspect
        Menu.openStream();
        Feed.addAspectPost(the("Asp1"), the(the("Asp1") + " Ron for new aspect"));

        //filtering - only new aspect is enabled
        NavBar.openMyAspects();
        Aspects.toggleAspect(the("Asp1"));
        Feed.assertPostFrom(RON_P1, the(the("Asp1") + " Ron for new aspect"));

        //edit aspect
        Aspects.switchToEditMode(the("Asp1"));
        Contacts.rename(the("Asp2"));
        Contacts.assertAspect(the("Asp2"));//without this check latest checks is unstable incorrect
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertNoAspectInNavBar(the("Asp1"));
        Feed.assertPostFrom(RON_P1, the(the("Asp1") + " Ron for new aspect"));

        //delete aspect
        Aspects.switchToEditMode(the("Asp2"));
        Contacts.deleteAspect();
        Menu.openStream();
        NavBar.openMyAspects();
        Aspects.assertNoAspectInNavBar(the("Asp2"));
        Feed.assertNoPostFrom(RON_P1, the(the("Asp1") + " Ron for new aspect"));

    }

}

package ua.net.itlabs;


import core.steps.Relation;
import org.junit.Test;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;

import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;

public class DiasporaConversationsTest extends BaseTest {

    @Test
    public void testNewConversations(){
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).build();
        Relation.forUser(ANA_P1).toUser(RON_P1, FRIENDS).doNotLogOut().build();
        Menu.openConversations();
        Conversations.sendNewConversationTo(RON_P1, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        Diaspora.signInAs(RON_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject"), the("text"));
        Menu.logOut();

    }
}

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
        //GIVEN - setup mutual relation between users in some different aspects
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

    @Test
    public void testReply(){
        //GIVEN
        //setup mutual relation between users in some different aspects
        //send new conversation
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
        Conversations.replyToCurrentConversation(the("reply"));
        Conversations.assertMessageInCurrentConversation(RON_P1, the("reply"));
        Menu.logOut();

        Diaspora.signInAs(ANA_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertMessageInCurrentConversation(ANA_P1, the("text"));
        Conversations.assertMessageInCurrentConversation(RON_P1, the("reply"));
    }

    @Test
    public void testHideAndDeleteConversation(){
        //GIVEN
        //setup mutual relation between users in some different aspects
        //send new conversation
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).build();
        Relation.forUser(ANA_P1).toUser(RON_P1, FRIENDS).doNotLogOut().build();
        Menu.openConversations();
        Conversations.sendNewConversationTo(RON_P1, the("subject1"), the("text1"));
        Conversations.sendNewConversationTo(RON_P1, the("subject2"), the("text2"));
        Conversations.assertInInboxBySubject(the("subject2"));//this check for wait moment when stream will be loaded

        //hide own conversation
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertMessageInCurrentConversation(ANA_P1, the("text1"));//this check for wait moment when mesage will be loaded
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));
        Menu.logOut();

        Diaspora.signInAs(RON_P1);
        Menu.openConversations();
        //hidden conversation from another user can be deleted
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject1"), the("text1"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));

        //shown conversation from another user can be hidden
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject2"), the("text2"));
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.logOut();

        Diaspora.signInAs(ANA_P1);
        Menu.openConversations();
        //hidden own conversation by another user can be deleted
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertMessageInCurrentConversation(ANA_P1, the("text2"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.logOut();

    }

}

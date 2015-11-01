package ua.net.itlabs;


import core.steps.Relation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;
import ua.net.itlabs.categories.Smoke;

import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.ANA_P1;
import static ua.net.itlabs.testDatas.Users.EVE_P1;
import static ua.net.itlabs.testDatas.Users.ROB_P1;
import static ua.net.itlabs.testDatas.Users.RON_P1;

@Category(ua.net.itlabs.categories.Conversations.class)
public class DiasporaConversationsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //setup - suitable timeout
        setTimeOut();

        //GIVEN - setup mutual relation between users in some different aspects
        Relation.forUser(RON_P1).toUser(ANA_P1, WORK).build();
        Relation.forUser(ANA_P1).toUser(RON_P1, FRIENDS).notToUsers(ROB_P1).build();
        Relation.forUser(ROB_P1).notToUsers(ANA_P1).build();
    }

    @Before
    public void setupForTest() {
        //clear information about unique values
        clearThe();
    }

    @Test
    public void testNewConversation(){

        Diaspora.signInAs(ANA_P1);

        //add new conversation
        Menu.openConversations();
        Conversations.sendNewConversationTo(RON_P1, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - sent message is shown for recipient
        Diaspora.signInAs(RON_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject"), the("text"));
        Menu.logOut();

    }

    @Test
    public void testReply(){
        //GIVEN additional - add conversation
        Diaspora.signInAs(ANA_P1);
        Menu.openConversations();
        Conversations.sendNewConversationTo(RON_P1, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //reply
        Diaspora.signInAs(RON_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject"), the("text"));//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(the("reply"));
        Conversations.assertMessageInCurrentConversation(RON_P1, the("reply"));
        Menu.logOut();

        //check - both messages is shown
        Diaspora.signInAs(ANA_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertMessageInCurrentConversation(ANA_P1, the("text"));
        Conversations.assertMessageInCurrentConversation(RON_P1, the("reply"));
    }

    @Test
    public void testHideAndDeleteConversations(){
        //GIVEN additional - add conversation
        Diaspora.signInAs(ANA_P1);
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

    @Test
    public void testNewConversationFromContactSite(){
        Diaspora.signInAs(ANA_P1);

        //send message from contact site to searched mutual user
        Menu.search(RON_P1.fullName);
        Contact.sendMessageToContact(the("subject"), the("text"));

        //check - in conversation list message is shown
        Conversations.assertInInboxBySubject(the("subject"));

        //check - it is not possible to send message to not mutual user
        Menu.search(ROB_P1.fullName);
        Contact.assertNoMessaging();
        Menu.logOut();

        //check - sent message is shown for recipient
        Diaspora.signInAs(RON_P1);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(ANA_P1, the("subject"), the("text"));
        Menu.logOut();

    }

}

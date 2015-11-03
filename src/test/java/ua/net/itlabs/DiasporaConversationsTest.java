package ua.net.itlabs;

import core.steps.Relation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;

public class DiasporaConversationsTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        //GIVEN - setup mutual relation between users in some different aspects
        Relation.forUser(Pod1.ron).toUser(Pod1.ana, WORK).build();
        Relation.forUser(Pod1.ana).toUser(Pod1.ron, FRIENDS).notToUsers(Pod1.rob).build();
        Relation.forUser(Pod1.rob).notToUsers(Pod1.ana).build();
    }

    @Before
    public void setupForTest() {
        //clear information about unique values
        clearUniqueData();
    }

    @Test
    public void testNewConversation() {

        //add new conversation
        Diaspora.signInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.ron, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //check - sent message is shown for recipient
        Diaspora.signInAs(Pod1.ron);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));
        Menu.logOut();

    }

    @Test
    public void testReply() {

        //GIVEN additional - add conversation
        Diaspora.signInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.ron, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.logOut();

        //reply
        Diaspora.signInAs(Pod1.ron);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(the("reply"));
        Conversations.assertMessageInCurrentConversation(Pod1.ron, the("reply"));
        Menu.logOut();

        //check - both messages is shown
        Diaspora.signInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text"));
        Conversations.assertMessageInCurrentConversation(Pod1.ron, the("reply"));
    }

    @Test
    public void testHideAndDeleteConversations() {

        //GIVEN additional - add conversation
        Diaspora.signInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.ron, the("subject1"), the("text1"));
        Conversations.sendNewConversationTo(Pod1.ron, the("subject2"), the("text2"));
        Conversations.assertInInboxBySubject(the("subject2"));//this check for wait moment when stream will be loaded

        //hide own conversation
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text1"));//this check for wait moment when mesage will be loaded
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));
        Menu.logOut();

        //hidden conversation from another user can be deleted
        Diaspora.signInAs(Pod1.ron);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject1"), the("text1"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));

        //shown conversation from another user can be hidden
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject2"), the("text2"));
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.logOut();

        //hidden own conversation by another user can be deleted
        Diaspora.signInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text2"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.logOut();

    }

    @Test
    public void testNewConversationFromContactSite() {

        //send message from contact site to searched mutual user
        Diaspora.signInAs(Pod1.ana);
        Menu.search(Pod1.ron.fullName);
        Contact.sendMessageToContact(the("subject"), the("text"));

        //check - in conversation list message is shown
        Conversations.assertInInboxBySubject(the("subject"));

        //check - it is not possible to send message to not mutual user
        Menu.search(Pod1.rob.fullName);
        Contact.assertNoMessaging();
        Menu.logOut();

        //check - sent message is shown for recipient
        Diaspora.signInAs(Pod1.ron);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));
        Menu.logOut();

    }

}

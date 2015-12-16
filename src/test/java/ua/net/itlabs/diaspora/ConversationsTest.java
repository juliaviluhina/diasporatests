package ua.net.itlabs.diaspora;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

public class ConversationsTest extends BaseTest {

    @Category(ua.net.itlabs.categories.Smoke.class)
    @Test
    public void testNewConversation() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();
        GIVEN("Message in Ana's and Rob's conversations is not exist");
        Conversations.ensureNoConversation(Pod1.ana, CONVERSATION_SUBJECT);
        Conversations.ensureNoConversation(Pod1.rob, CONVERSATION_SUBJECT);

        WHEN("Conversation to user is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.rob, CONVERSATION_SUBJECT, CONVERSATION_TEXT);
        Conversations.assertInInboxBySubject(CONVERSATION_SUBJECT);//this check for wait moment when stream will be loaded

        THEN("Conversation is shown for user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION_SUBJECT, CONVERSATION_TEXT);

    }

    @Test
    public void testReply() {

        GIVEN("Conversation to user is added by author");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.eve, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded

        EXPECT("User can reply on this conversation");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(the("reply"));
        Conversations.assertMessageInCurrentConversation(Pod1.eve, the("reply"));

        EXPECT("Original message and reply is shown for author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text"));
        Conversations.assertMessageInCurrentConversation(Pod1.eve, the("reply"));
    }

    @Test
    public void testHideAndDeleteConversations() {

        GIVEN("Conversation to user is added by author");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.eve, the("subject1"), the("text1"));
        Conversations.sendNewConversationTo(Pod1.eve, the("subject2"), the("text2"));
        Conversations.assertInInboxBySubject(the("subject2"));//this check for wait moment when stream will be loaded

        WHEN("Added conversation is hidden by author");
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text1"));//this check for wait moment when mesage will be loaded
        Conversations.hideCurrentConversation();

        THEN("This conversation is not shown for author");
        Conversations.assertNoConversationBySubject(the("subject1"));

        EXPECT("Conversation from author shown for user can be hidden by user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject2"), the("text2"));
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));

        EXPECT("Conversation hidden for author is shown for user");
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject1"), the("text1"));

        EXPECT("Conversation hidden for author can be deleted by user");
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));

        EXPECT("Conversation of author hidden for user can be deleted by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text2"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));

    }

    @Test
    public void testNewConversationFromContactSite() {

        clearUniqueData();

        WHEN("Message is sent from contact site to searched mutual user");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.search(Pod1.eve.fullName);
        Contact.sendMessageToContact(the("subject"), the("text"));

        THEN("Conversation is shown for author");
        Conversations.assertInInboxBySubject(the("subject"));

        EXPECT("It is not possible to send message to not mutual user");
        Menu.search(Pod1.rob.fullName);
        Contact.assertNoMessaging();

        EXPECT("Message sent from author to user is shown for user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));

    }

}

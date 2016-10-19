package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Phrases;
import com.automician.testDatas.Users;
import org.junit.Test;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;

import static core.Gherkin.*;

public class ConversationsTest extends BaseTest {

    @Test
    public void testNewConversation() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Message in Ana's and Rob's conversations is not exist");
        Conversations.ensureNoConversation(Users.Pod1.ana, Phrases.CONVERSATION1_SUBJECT);
        Conversations.ensureNoConversation(Users.Pod1.rob, Phrases.CONVERSATION1_SUBJECT);

        WHEN("Conversation to user is added by author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Users.Pod1.rob, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);
        Conversations.assertInInboxBySubject(Phrases.CONVERSATION1_SUBJECT);//this check for wait moment when stream will be loaded

        THEN("Conversation is shown for user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

    }

    @Test
    public void testReply() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Message from Ana to Rob is added from scratch");
        Conversations.ensureAddConversationFromScratch(Users.Pod1.ana, Users.Pod1.rob, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

        EXPECT("Rob can reply on this conversation");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(Phrases.CONVERSATION1_REPLY);
        Conversations.assertMessageInCurrentConversation(Users.Pod1.rob, Phrases.CONVERSATION1_REPLY);

        EXPECT("Original message and reply is shown for author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_TEXT);
        Conversations.assertMessageInCurrentConversation(Users.Pod1.rob, Phrases.CONVERSATION1_REPLY);
    }

    @Test
    public void testHideAndDeleteConversations() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Messages from Ana to Rob are added from scratch");
        Conversations.ensureAddConversationFromScratch(Users.Pod1.ana, Users.Pod1.rob, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);
        Conversations.ensureAddConversationFromScratch(Users.Pod1.ana, Users.Pod1.rob, Phrases.CONVERSATION2_SUBJECT, Phrases.CONVERSATION2_TEXT);

        WHEN("Added conversation is hidden by author");
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_TEXT);//this check for wait moment when mesage will be loaded
        Conversations.hideCurrentConversation();

        THEN("This conversation is not shown for author");
        Conversations.assertNoConversationBySubject(Phrases.CONVERSATION1_SUBJECT);

        EXPECT("Conversation from author shown for user can be hidden by user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION2_SUBJECT);
        Conversations.assertCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION2_SUBJECT, Phrases.CONVERSATION2_TEXT);
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(Phrases.CONVERSATION2_SUBJECT);

        EXPECT("Conversation hidden for author is shown for user");
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

        EXPECT("Conversation hidden for author can be deleted by user");
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(Phrases.CONVERSATION1_SUBJECT);

        EXPECT("Conversation of author hidden for user can be deleted by author");
        Diaspora.ensureSignInAs(Users.Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION2_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION2_TEXT);
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(Phrases.CONVERSATION2_SUBJECT);

    }

    @Test
    public void testNewConversationFromContactSite() {

        GIVEN("Setup relations among users of pod1");
        Users.Pod1.ensureRelations();

        GIVEN("Message from Ana to Rob is added from scratch");
        Conversations.ensureAddConversationFromScratch(Users.Pod1.ana, Users.Pod1.rob, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

        WHEN("Message is sent from contact site to searched mutual user");
        Menu.search(Users.Pod1.rob.fullName);
        Contact.sendMessageToContact(Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

        THEN("Conversation is shown for author");
        Conversations.assertInInboxBySubject(Phrases.CONVERSATION1_SUBJECT);

        EXPECT("It is not possible to send message to not mutual user");
        Menu.search(Users.Pod1.eve.fullName);
        Contact.assertNoMessaging();

        EXPECT("Message sent from author to user is shown for user");
        Diaspora.ensureSignInAs(Users.Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(Phrases.CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Users.Pod1.ana, Phrases.CONVERSATION1_SUBJECT, Phrases.CONVERSATION1_TEXT);

    }

}

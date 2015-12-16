package ua.net.itlabs.diaspora;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;
import steps.Scenarios;
import ua.net.itlabs.BaseTest;

import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;
import static ua.net.itlabs.testDatas.Phrases.*;

@Category(ua.net.itlabs.categories.Smoke.class)
public class ConversationsTest extends BaseTest {

    @Test
    public void testNewConversation() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Message in Ana's and Rob's conversations is not exist");
        Conversations.ensureNoConversation(Pod1.ana, CONVERSATION1_SUBJECT);
        Conversations.ensureNoConversation(Pod1.rob, CONVERSATION1_SUBJECT);

        WHEN("Conversation to user is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.rob, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);
        Conversations.assertInInboxBySubject(CONVERSATION1_SUBJECT);//this check for wait moment when stream will be loaded

        THEN("Conversation is shown for user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

    }

    @Test
    public void testReply() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Message from Ana to Rob is added from scratch");
        Conversations.ensureAddConversationFromScratch(Pod1.ana, Pod1.rob, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

        EXPECT("Rob can reply on this conversation");
        Diaspora.ensureSignInAs(Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(CONVERSATION1_REPLY);
        Conversations.assertMessageInCurrentConversation(Pod1.rob, CONVERSATION1_REPLY);

        EXPECT("Original message and reply is shown for author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Pod1.ana, CONVERSATION1_TEXT);
        Conversations.assertMessageInCurrentConversation(Pod1.rob, CONVERSATION1_REPLY);
    }

    @Test
    public void testHideAndDeleteConversations() {

        GIVEN("Setup relations among users of pod1");
        //Pod1.ensureRelations();

        GIVEN("Messages from Ana to Rob are added from scratch");
        Conversations.ensureAddConversationFromScratch(Pod1.ana, Pod1.rob, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);
        Conversations.ensureAddConversationFromScratch(Pod1.ana, Pod1.rob, CONVERSATION2_SUBJECT, CONVERSATION2_TEXT);

        WHEN("Added conversation is hidden by author");
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Pod1.ana, CONVERSATION1_TEXT);//this check for wait moment when mesage will be loaded
        Conversations.hideCurrentConversation();

        THEN("This conversation is not shown for author");
        Scenarios.waitStreamOpening();
        Conversations.assertNoConversationBySubject(CONVERSATION1_SUBJECT);

        EXPECT("Conversation from author shown for user can be hidden by user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION2_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION2_SUBJECT, CONVERSATION2_TEXT);
        Conversations.hideCurrentConversation();
        Scenarios.waitStreamOpening();
        Conversations.assertNoConversationBySubject(CONVERSATION2_SUBJECT);

        EXPECT("Conversation hidden for author is shown for user");
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

        EXPECT("Conversation hidden for author can be deleted by user");
        Conversations.deleteCurrentConversation();
        Scenarios.waitStreamOpening();
        Conversations.assertNoConversationBySubject(CONVERSATION1_SUBJECT);

        EXPECT("Conversation of author hidden for user can be deleted by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION2_SUBJECT);
        Conversations.assertMessageInCurrentConversation(Pod1.ana, CONVERSATION2_TEXT);
        Conversations.deleteCurrentConversation();
        Scenarios.waitStreamOpening();
        Conversations.assertNoConversationBySubject(CONVERSATION2_SUBJECT);

    }

    @Test
    public void testNewConversationFromContactSite() {

        GIVEN("Setup relations among users of pod1");
        Pod1.ensureRelations();

        GIVEN("Message from Ana to Rob is added from scratch");
        Conversations.ensureAddConversationFromScratch(Pod1.ana, Pod1.rob, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

        WHEN("Message is sent from contact site to searched mutual user");
        Menu.search(Pod1.rob.fullName);
        Contact.sendMessageToContact(CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

        THEN("Conversation is shown for author");
        Conversations.assertInInboxBySubject(CONVERSATION1_SUBJECT);

        EXPECT("It is not possible to send message to not mutual user");
        Menu.search(Pod1.eve.fullName);
        Contact.assertNoMessaging();

        EXPECT("Message sent from author to user is shown for user");
        Diaspora.ensureSignInAs(Pod1.rob);
        Menu.openConversations();
        Conversations.selectConversationBySubject(CONVERSATION1_SUBJECT);
        Conversations.assertCurrentConversation(Pod1.ana, CONVERSATION1_SUBJECT, CONVERSATION1_TEXT);

    }

}

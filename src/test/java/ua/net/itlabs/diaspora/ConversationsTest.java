package ua.net.itlabs.diaspora;

import steps.Relation;
import org.junit.BeforeClass;
import org.junit.Test;
import pages.Contact;
import pages.Conversations;
import pages.Diaspora;
import pages.Menu;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.*;
import static ua.net.itlabs.testDatas.Users.*;
import static core.Gherkin.*;

public class ConversationsTest extends BaseTest {

    @BeforeClass
    public static void givenSetupRelation() {

        GIVEN("Setup mutual relation between users in some different aspects");
        Relation.forUser(Pod1.eve).toUser(Pod1.ana, WORK).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.eve, FRIENDS).notToUsers(Pod1.rob).ensure();
        Relation.forUser(Pod1.rob).notToUsers(Pod1.ana).ensure();

    }

    @Test
    public void testNewConversation() {
        clearUniqueData();

        WHEN("Conversation to user is added by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.eve, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        THEN("Conversation is shown for user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));
        Menu.ensureLogOut();

    }

    @Test
    public void testReply() {

        GIVEN("Conversation to user is added by author");
        clearUniqueData();
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.sendNewConversationTo(Pod1.eve, the("subject"), the("text"));
        Conversations.assertInInboxBySubject(the("subject"));//this check for wait moment when stream will be loaded
        Menu.ensureLogOut();

        EXPECT("User can reply on this conversation");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));//this check for wait moment when message will be loaded
        Conversations.replyToCurrentConversation(the("reply"));
        Conversations.assertMessageInCurrentConversation(Pod1.eve, the("reply"));
        Menu.ensureLogOut();

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
        Menu.ensureLogOut();

        EXPECT("Conversation hidden for author is shown for user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject1"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject1"), the("text1"));

        EXPECT("Conversation hidden for author can be deleted by user");
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject1"));

        EXPECT("Conversation from author shown for user can be hidden by user");
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject2"), the("text2"));
        Conversations.hideCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.ensureLogOut();

        EXPECT("Conversation of author hidden for user can be deleted by author");
        Diaspora.ensureSignInAs(Pod1.ana);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject2"));
        Conversations.assertMessageInCurrentConversation(Pod1.ana, the("text2"));
        Conversations.deleteCurrentConversation();
        Conversations.assertNoConversationBySubject(the("subject2"));
        Menu.ensureLogOut();

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
        Menu.ensureLogOut();

        EXPECT("Message sent from author to user is shown for user");
        Diaspora.ensureSignInAs(Pod1.eve);
        Menu.openConversations();
        Conversations.selectConversationBySubject(the("subject"));
        Conversations.assertCurrentConversation(Pod1.ana, the("subject"), the("text"));
        Menu.ensureLogOut();

    }

}

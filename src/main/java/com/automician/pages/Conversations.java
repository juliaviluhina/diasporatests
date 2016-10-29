package com.automician.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.automician.datastructures.PodUser;
import org.openqa.selenium.StaleElementReferenceException;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.confirm;
import static com.automician.core.AdditionalAPI.scrollToAndHover;
import static com.automician.core.AdditionalAPI.isVisible;
import static com.automician.core.conditions.CustomCondition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.automician.steps.Scenarios.*;

public class Conversations {

    public static SelenideElement inbox = $("#conversation_inbox");
    public static SelenideElement currentConversation = $(".stream_container");

    private static ElementsCollection conversations = inbox.findAll(".conversation");
    private static SelenideElement hideButton = $(".hide_conversation");
    private static SelenideElement deleteButton = $(".delete_conversation");

    @Step
    public static void sendNewConversationTo(PodUser toUser, String subject, String text) {

        SelenideElement startNewConversation = $("#left_pane_header .btn");
        SelenideElement toUserContainer = $("#as-selections-contact_ids");
        ElementsCollection toUserVariants = $$("#as-results-contact_ids li");
        SelenideElement sendConversation = $("#new_conversation .btn");

        startNewConversation.click();

        toUserContainer.click();
        toUserContainer.find("#contact_ids").sendKeys(toUser.userName);
        toUserVariants.find(exactText(toUser.fullName)).click();
        toUserContainer.shouldHave(text(toUser.fullName));

        $("#conversation_subject").setValue(subject);

        $("#conversation_text").setValue(text);

        sendConversation.click();

    }

    @Step
    public static void selectConversationBySubject(String subject) {
        conversation(subject).click();
    }

    @Step
    public static void replyToCurrentConversation(String text) {
        $("#message_text").setValue(text);
        $("[value='Reply']").click();
    }

    @Step
    public static void hideCurrentConversation() {
        clickButton(hideButton);
    }

    @Step
    public static void deleteCurrentConversation() {
        clickButton(deleteButton);
    }


    @Step
    public static void assertInInboxBySubject(String subject) {
        inbox.findAll(".subject").find(exactText(subject)).shouldBe(visible);
    }

    @Step
    public static void assertCurrentConversation(PodUser from, String subject, String text) {

        assertCurrentSubject(subject);

        SelenideElement firstMessage = currentConversation.find("#conversation_show #first_unread");
        firstMessage.find(".ltr").shouldHave(exactText(text));
        firstMessage.find(".author").shouldHave(exactText(from.fullName));

    }

    @Step
    public static void assertCurrentSubject(String subject) {
        SelenideElement currentSubject = currentConversation.find(".conversation_participants h3");
        currentSubject.shouldHave(exactText(subject));
    }

    @Step
    public static void assertMessageInCurrentConversation(PodUser from, String text) {
        ElementsCollection messages = currentConversation.findAll(".stream_element");
        messages.find(textBeginAndContain(from.fullName, text)).shouldBe(visible);
    }

    @Step
    public static void assertNoConversationBySubject(String subject) {
        conversation(subject).shouldNotBe(present);
    }

    @Step
    public static void ensureNoConversation(PodUser podUser, String subject) {
        Diaspora.ensureSignInAs(podUser);
        Menu.openConversations();
        while (true) { //cycle is used for deletion all entities which have the same property
            SelenideElement conversation = conversation(subject);

            if (isVisible(conversation)) {
                conversation.click();
                assertCurrentSubject(subject);
                hideOrDeleteCurrentConversation();
            } else
                break;
        }
    }

    @Step
    public static void ensureAddConversationFromScratch(PodUser fromUser, PodUser toUser, String subject, String text) {
        ensureNoConversation(toUser, subject);
        ensureNoConversation(fromUser, subject);
        sendNewConversationTo(toUser, subject, text);
        assertInInboxBySubject(subject);//this check for wait moment when stream will be loaded
    }

    @Step
    public static void clearAll() {

        Menu.openConversations();
        waitStreamOpening();


        int count = 0;

        try {
            while (true) { //cycle is used for deletion all entities
                SelenideElement conversation = conversations.first();
                if (!conversation.is(present))
                    break;
                conversation.click();

                if (hideOrDeleteCurrentConversation()) {
                    count++;
                }
            }
        } catch (StaleElementReferenceException e) {
            //in this case is valid exception because of dynamic refresh list of conversation
        } catch (IndexOutOfBoundsException e) {
            //in this case is valid exception because of dynamic refresh list of conversation
        }

        if (count > 0)
            clearAll();

    }

    private static boolean hideOrDeleteCurrentConversation() {
        if (hideButton.is(visible)) {
            clickButton(hideButton);
            return true;
        } else if (deleteButton.is(visible)) {
            clickButton(deleteButton);
            return true;
        }
        return false;
    }

    private static SelenideElement conversation(String subject) {
        return conversations.find(text(subject));
    }

    //this method is added because button can be hidden under the header
    private static void clickButton(SelenideElement button) {
        button.shouldBe(visible);
        scrollToAndHover(button);
        button.click();
        confirm(null);
    }

}

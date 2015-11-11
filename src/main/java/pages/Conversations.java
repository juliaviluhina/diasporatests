package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.confirm;
import static core.conditions.CustomCondition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Conversations {

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
        $(".hide_conversation").click();
        confirm(null);
    }

    @Step
    public static void deleteCurrentConversation() {
        $(".delete_conversation").click();
        confirm(null);
    }


    @Step
    public static void assertInInboxBySubject(String subject) {
        inbox.findAll(".subject").find(exactText(subject)).shouldBe(visible);
    }

    @Step
    public static void assertCurrentConversation(PodUser from, String subject, String text) {

        SelenideElement currentSubject = currentConversation.find(".conversation_participants h3");
        currentSubject.shouldHave(exactText(subject));

        SelenideElement firstMessage = currentConversation.find("#conversation_show #first_unread");
        firstMessage.find(".ltr").shouldHave(exactText(text));
        firstMessage.find(".author").shouldHave(exactText(from.fullName));

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


    public static SelenideElement inbox = $("#conversation_inbox");
    public static SelenideElement currentConversation = $(".stream_container");

    private static SelenideElement conversation(String subject) {
        return inbox.findAll(".conversation").find(text(subject));
    }

}

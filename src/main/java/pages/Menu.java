package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Menu {

    public static SelenideElement userMenu = $("#user_menu");
    public static SelenideElement subMenuIndicator = $(".user-menu-more-indicator");

    @Step
    public static void logOut() {
        subMenuIndicator.click();
        userMenu.find("[data-method='delete']").click();
    }

    @Step
    public static void search(String text) {
        $("#q").setValue(text);
        $$(".ac_results").shouldHave(texts(text));
        $("#q").pressEnter();

        Contact.ensureSearchedContact(text);
    }

    @Step
    public static void openStream() {
        $(".header-nav [href='/stream']").click();
    }

    @Step
    public static void openContacts(){
        subMenuIndicator.click();
        $("[href='/contacts']").click();
    }

    public static void assertLoggedUser(PodUser user) {
        userMenu.find(".user-name").shouldHave(exactText(user.fullName));
    }

    public static void ensureNewSignIn() {
        if ($("header").findAll(".dark-header").size() == 0) {
            return;
        }
        logOut();
    }

}

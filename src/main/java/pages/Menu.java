package pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.AdditionalAPI;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static core.conditions.CustomCondition.textBegin;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.STANDART_ASPECTS;

public class Menu{

    public static SelenideElement userMenuHeader = $(".user-menu-trigger");//$(".user-menu-more-indicator");
    public static ElementsCollection userMenuItems = $$(".user-menu-item a");
    private static SelenideElement accountHeader = $("header .dark-header");

    @Step
    public static void logOut() {
        openMenu();
        userMenuItems.find(exactText("Log out")).click();
    }

    @Step
    public static void search(String searchText) {
        $("#q").setValue(searchText);
        $$(".ac_results").find(text(searchText)).shouldBe(visible);
        $("#q").pressEnter();

        Contact.ensureSearchedContact(searchText);
    }

    @Step
    public static void openConversations() {
        $("#nav_badges [href='/conversations']").click();
    }

    @Step
    public static void openStream() {
        $(".header-nav [href='/stream']").click();
    }

    @Step
    public static void openContacts() {
        openMenu();
        userMenuItems.find(exactText("Contacts")).click();
    }

    @Step
    public static void ensureLoggedOut() {
        if (accountHeader.is(present)) {
            logOut();
        }
    }

    @Step
    public static void assertLoggedOut() {
        accountHeader.shouldNotBe(present);
    }

    //method added because of problem with opening user menu when stream is not loaded
    private static void openMenu() {

        long startTime = currentTimeMillis();
        Boolean result = FALSE;

        do {
            userMenuHeader.click();
            if (userMenuItems.filter(exactText("Log out")).size() == 1) {
                result = TRUE;
            }
        } while ((!result) || (startTime + Configuration.timeout < currentTimeMillis()));
    }

}

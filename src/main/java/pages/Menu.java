package pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.AdditionalAPI;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.STANDART_ASPECTS;

public class Menu{

    public static SelenideElement userMenuHeader = $(".user-menu-trigger");//$(".user-menu-more-indicator");
    public static ElementsCollection userMenuItems = $$(".user-menu-item a");

    private static String darkHeaderLocator = "header .dark-header";

    @Step
    public static void logOut() {
        openMenu();
        doLogOut();
    }

    @Step
    public static void search(String text) {
        $("#q").setValue(text);
        $$(".ac_results").shouldHave(texts(text));
        $("#q").pressEnter();

        Contact.ensureSearchedContact(text);
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
        //userMenuHeader.click();
        openMenu();
        userMenuItems.find(exactText("Contacts")).click();
    }

    @Step
    public static void ensureLoggedOut() {
        if ($$(darkHeaderLocator).size() != 0) {
            logOut();
        }
    }

    @Step
    public static void assertLoggedOut() {
        $$(darkHeaderLocator).shouldBe(empty);
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

    //method added because of problem with click on LogOut when stream is not loaded
    private static void doLogOut() {

        long startTime = currentTimeMillis();
        Boolean result = FALSE;

        do {
            userMenuItems.find(exactText("Log out")).click();
            if ($$(darkHeaderLocator).size() == 0) {
                result = TRUE;
            }
        } while ((!result) || (startTime + Configuration.timeout < currentTimeMillis()));
    }


}

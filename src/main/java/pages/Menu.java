package pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.STANDART_ASPECTS;

public class Menu {

    public static SelenideElement userMenuHeader = $(".user-menu-trigger");//$(".user-menu-more-indicator");
    public static ElementsCollection userMenuItems = $$(".user-menu-item a");

    private static String darkHeaderLocator = "header .dark-header";

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

    @Step
    public static void logOut() {
        //userMenuHeader.click();
        openMenu();
        userMenuItems.find(exactText("Log out")).click();
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
    public static void openContacts(){
        //userMenuHeader.click();
        openMenu();
        userMenuItems.find(exactText("Contacts")).click();
    }

//often fail in jenkins
//    @Step
//    public static void assertLoggedUser(PodUser user) {
//        userMenuHeader.find(".user-name").shouldHave(exactText(user.fullName));
//    }

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



}

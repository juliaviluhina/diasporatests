package com.automician.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.automician.datastructures.PodUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static com.automician.core.AdditionalAPI.*;
import static com.automician.steps.Scenarios.*;

public class Menu {

    public static SelenideElement userMenu = $("#user_menu");
    public static ElementsCollection userMenuItems = $$(".dropdown-menu a");
    public static SelenideElement search = $("#q");

    @Step
    public static void openStream() {
        $(".navbar-left [href='/stream']").click();
    }

    @Step
    public static void openConversations() {
        $("#nav_badges [href='/conversations']").click();
    }

    @Step
    public static void openContacts() {
        openMenu();
        userMenuItems.find(exactText("Contacts")).click();
    }

    @Step
    public static void search(String searchText) {
        waitStreamOpening();//if menu is opened then search is possible too
        search.setValue(searchText);
        $$("#header-search-form .tt-menu .name").find(text(searchText)).shouldBe(visible);
        search.pressEnter();
        Contact.ensureSearchedContact(searchText);
    }

    @Step
    public static void assertLoggedOut() {
        Diaspora.userName.shouldBe(visible);
    }

    @Step
    public static void ensureLogOut() {
        Diaspora.ensureLogOut();
    }

    //method added because of problem with opening user menu when stream is not loaded
    public static void openMenu() {
        assertThat(userMenuOpened(), timeout2x());
    }

    private static ExpectedCondition<Boolean> userMenuOpened() {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                userMenu.click();

                if (!userMenuItems.find(exactText("Log out")).is(visible)) {
                    return FALSE;
                }
                return TRUE;
            }

            @Override
            public String toString() {
                return "Error opening user menu";
            }

        });
    }

    public static void assertLoggedUser(PodUser user) {
        userMenu.$(".user-name").shouldHave(exactText(user.fullName));
    }
}
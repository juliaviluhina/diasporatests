package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static core.AdditionalAPI.*;

public class Menu {

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
//        $("#q").setValue(searchText);
//        $$(".ac_results").find(text(searchText)).shouldBe(visible);
//        $("#q").pressEnter();
        $("#q").setValue(searchText);
        assertThat(searched(searchText));
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
        assertThat(userMenuOpened());
    }


    private static ExpectedCondition<Boolean> userMenuOpened() {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                userMenuHeader.click();

                if (! userMenuItems.find(exactText("Log out")).is(visible)){
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

    private static ExpectedCondition<Boolean> searched(final String searchText) {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {

                if (! $$(".ac_results").find(text(searchText)).is(visible)){
                    return FALSE;
                }
                return TRUE;
            }

            @Override
            public String toString() {
                return "Error searching "+searchText;
            }

        });
    }

}

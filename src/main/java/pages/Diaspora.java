package pages;

import com.codeborne.selenide.SelenideElement;
import core.WebDriversManager;
import datastructures.PodUser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;


import core.WebDriversManager.StateAfterPreparing;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static core.AdditionalAPI.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class Diaspora {

    public static SelenideElement userName = $("#user_username");

    static {
        webDriversManager = new WebDriversManager(6);
    }

    @Step
    public static void signInAs(PodUser user) {
        if (isSeparateSigningInMode()) {
            signInAsAtSeparateWebDriver(user);
        } else {
            ensureSignInAs(user);
        }
    }

    @Step
    public static void logOut() {
        Menu.openMenu();
        Menu.userMenuItems.find(exactText("Log out")).click();
    }

    @Step
    public static void ensureSignInAs(PodUser user) {
        if (isLoggedOut()) {
            ensureLogOut();
        }
        if (isSeparateSigningInMode()) {
            ensureSignInAsAtSeparateWebDriver(user);
        } else {
            assertThat(authenticationIsOpened(user), timeout2x());
            signIn(user);
        }
    }

    @Step
    public static void ensureLogOut() {
        if (isSeparateSigningInMode())
            webDriversManager.hideCurrentBrowser();
         else
            if (!isLoggedOut())
                logOut();
    }

    private static WebDriversManager webDriversManager;

    private static ExpectedCondition<Boolean> authenticationIsOpened(final PodUser user) {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                open(user.podLink + "/users/sign_in");
                if (!userName.is(visible)) {
                    Menu.ensureLogOut();
                    return FALSE;
                }
                return TRUE;
            }

            @Override
            public String toString() {
                return "Error opening authentication";
            }

        });
    }

    private static Boolean isSeparateSigningInMode() {
        return (System.getProperty("signingInMode").equals("separate"));
    }

    private static void signIn(PodUser user) {
        userName.setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }

    private static void openUserAccount(PodUser user) {
        open(user.podLink + "/users/sign_in");
        signIn(user);
    }

    private static void ensureSignInAsAtSeparateWebDriver(PodUser user) {
        if (webDriversManager.prepareWebDriverForKey(user.fullName) == StateAfterPreparing.IS_CREATED)
            openUserAccount(user);
        else
            Menu.openStream();
    }

    private static void signInAsAtSeparateWebDriver(PodUser user) {
        webDriversManager.prepareDisposableWebDriver();
        openUserAccount(user);
    }

    private static Boolean isLoggedOut() {
        return Diaspora.userName.is(visible);
    }

}

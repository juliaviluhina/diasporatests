package pages;

import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static core.AdditionalAPI.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Diaspora {

    @Step
    public static void ensureSignInAs(PodUser user) {

        if (isSeparateSigningInMode()) {
            ensureSignInAsAtSeparateWebDriver(user);
        }
        else {
            ensureSignInAsAtOneWebDriver(user);
        }

    }

    @Step
    public static void signInAs(PodUser user) {

        if (isSeparateSigningInMode()) {
            signInAsAtSeparateWebDriver(user);
        }
        else {
            ensureSignInAsAtOneWebDriver(user);
        }

    }

    public static Boolean isSeparateSigningInMode() {
        return (System.getProperty("signingInMode").equals("separate"));
    }

    public static void closeWebDrivers() {
        for (WebDriver webDriver:userWebDrivers.values()) {
            webDriver.close();
        }
        userWebDrivers.clear();
        if (webDriverForAuthenticationTest != null) {
            webDriverForAuthenticationTest.close();
            webDriverForAuthenticationTest = null;
        }
    }

    public static void hideCurrentUserBrowser() {
        if (currentUser != null) {
            userWebDrivers.get(currentUser).manage().window().setPosition(new Point(-2000, 0));
            currentUser = null;
        }
    }

    @Step
    public static void logOut() {
        Menu.openMenu();
        Menu.userMenuItems.find(exactText("Log out")).click();
    }

    public static SelenideElement userName = $("#user_username");

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

    private static void ensureSignInAsAtOneWebDriver(PodUser user) {
        //open(user.podLink + "/users/sign_in");
        assertThat(authenticationIsOpened(user), timeout2x());
        userName.setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }

    private static void ensureSignInAsAtSeparateWebDriver(PodUser user) {
        WebDriver currentWebDriver;
        if (currentUser != null) {
            hideCurrentUserBrowser();
        }
        currentUser = user;
        if (!userWebDrivers.containsKey(user)) {
            if (userWebDrivers.size() == 0) {
                currentWebDriver = getWebDriver();
            }
            else {
                currentWebDriver = createWebDriver();
            }
            userWebDrivers.put(user, currentWebDriver);
            setWebDriver(currentWebDriver);

            setUserNameAndPassword(user);

        }
        else
        {   currentWebDriver = userWebDrivers.get(user);
            setWebDriver(currentWebDriver);
            currentWebDriver.manage().window().setPosition(new Point(0, 0));
            currentWebDriver.manage().window().maximize();
            currentWebDriver.switchTo().window(currentWebDriver.getWindowHandle());//without this string on Linux does not work
            Menu.openStream();
        }
    }


    private static void signInAsAtSeparateWebDriver(PodUser user) {
        if (webDriverForAuthenticationTest != null) {
           webDriverForAuthenticationTest.close();
        }

        webDriverForAuthenticationTest = createWebDriver();
        setWebDriver(webDriverForAuthenticationTest);

        setUserNameAndPassword(user);
    }

    private static void setUserNameAndPassword(PodUser user) {
        open(user.podLink + "/users/sign_in");

        userName.setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }


    private static Map<PodUser, WebDriver> userWebDrivers = new HashMap<PodUser, WebDriver>();
    private static PodUser currentUser = null;
    private static WebDriver webDriverForAuthenticationTest = null;

}

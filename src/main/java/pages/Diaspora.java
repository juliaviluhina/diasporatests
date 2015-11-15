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

import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Condition.text;
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
    public static void signInAs(PodUser user) {
        if (!userWebDrivers.containsKey(user)) {
            if (userWebDrivers.size() == 0) {
                currentWebDriver = getWebDriver();
            }
            else {
                currentWebDriver = createWebDriver();
            }
            userWebDrivers.put(user, currentWebDriver);
            setWebDriver(currentWebDriver);

            open(user.podLink + "/users/sign_in");

            userName.setValue(user.userName);
            $("#user_password").setValue(user.password);
            $(By.name("commit")).click();

        }
        else
        {   currentWebDriver = userWebDrivers.get(user);
            setWebDriver(currentWebDriver);
            //currentWebDriver.switchTo().window(user.windowHandle);
            currentWebDriver.manage().window().setPosition(new Point(0, 0));
            currentWebDriver.manage().window().maximize();
            Menu.openStream();
        }
    }


    public static SelenideElement userName = $("#user_username");

    private static ExpectedCondition<Boolean> authenticationIsOpened(final PodUser user) {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                open(user.podLink + "/users/sign_in");
                if (!userName.is(visible)) {
                    Menu.logOut();
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

    public static void closeWebDrivers() {
        for (WebDriver webDriver:userWebDrivers.values()) {
            webDriver.close();
        }
    }

    private static Map<PodUser, WebDriver> userWebDrivers = new HashMap<PodUser, WebDriver>();
    public static WebDriver currentWebDriver = null;

}

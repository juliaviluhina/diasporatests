package pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import datastructures.PodUser;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static core.AdditionalAPI.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Diaspora {

    private static UserThreadManager userThreadManager;
    private static PodUser currentUser;

    static {
        userThreadManager = new UserThreadManager();
    }

    @Step
    public static void ensureSignInAs(PodUser user) {
        if (currentUser != null) {
            ensureLogOut();
        }
        currentUser = user;
        if (isSeparateSigningInMode()) {
            ensureSignInAsAtSeparateWebDriver(user);
        } else {
            ensureSignInAsAtOneWebDriver(user);
        }
    }

    @Step
    public static void signInAs(PodUser user) {
        if (isSeparateSigningInMode()) {
            //webDriverManager.signInAsAtSeparateWebDriver(user);
        } else {
            ensureSignInAsAtOneWebDriver(user);
        }
    }

    @Step
    public static void ensureLogOut() {
        if (isSeparateSigningInMode()) {
            hideCurrentUserBrowser();
        } else {
            logOut();
        }
        currentUser = null;
    }

    @Step
    public static void logOut() {
        Menu.openMenu();
        Menu.userMenuItems.find(exactText("Log out")).click();
    }

    public static Boolean isSeparateSigningInMode() {
        return (System.getProperty("signingInMode").equals("separate"));
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

    public static void setUserNameAndPassword(PodUser user) {
        open(user.podLink + "/users/sign_in");

        userName.setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }

    private static class UserThreadManager implements Runnable {

        private Map<PodUser, UserThread> userThreads = new HashMap<PodUser, UserThread>();
        private PodUser currentUser = null;
        private PodUser prevUser = null;
        private Thread baseThread = null;

        private static class UserThread extends Thread {
            public PodUser podUser;
            public WebDriver webDriver;
            public Boolean isActive;

            public UserThread(UserThreadManager target, PodUser podUser) {
                super(target, podUser.fullName);
                this.podUser = podUser;
                webDriver = null;
                isActive = FALSE;
            }
        }

        private UserThread addThread(PodUser podUser) {
            if (userThreads.size() == 0) {
                baseThread = Thread.currentThread();
            }
            UserThread userThread = new UserThread(this, podUser);
            userThread.start();
            userThreads.put(podUser, userThread);
            return userThread;
        }

        public void EnsureSignInAs(PodUser podUser) {
            if (podUser.equals(currentUser)) return;

            ensureLogOut();

            if (!userThreads.containsKey(podUser)) {
                addThread(podUser);
            } else {
            }
            currentUser = podUser;

        }

        public void EnsureLogOut() {
            if (!userThreads.containsKey(currentUser)) {
                return;
            }
            prevUser = currentUser;
            currentUser = null;
        }

        public void run() {

            WebDriver webDriver;
            UserThread currentThread = (UserThread) Thread.currentThread();

            while (baseThread.isAlive()) {
                synchronized (currentThread) {
                    if (currentThread.podUser.equals(prevUser) && currentThread.isActive) {
                        currentThread.isActive = false;
                        currentThread.webDriver.manage().window().setPosition(new Point(-2000, 0));
                    }

                    if (currentThread.podUser.equals(currentUser) && !currentThread.isActive) {
                        currentThread.isActive = true;
                        if (currentThread.webDriver == null) {
                            currentThread.webDriver = getWebDriver();
                            setUserNameAndPassword(currentUser);
                            NavBar.assertLoggedUser(currentUser);
                            currentThread.isActive = TRUE;
                        } else {
                            webDriver = currentThread.webDriver;
                            webDriver.manage().window().setPosition(new Point(0, 0));
                            webDriver.manage().window().maximize();
                            webDriver.switchTo().window(webDriver.getWindowHandle());//without this string on Linux does not work
                            Menu.openStream();
                        }
                    }
                }
            }
        }

    }


    public static void ensureSignInAsAtSeparateWebDriver(PodUser user) {
        userThreadManager.EnsureSignInAs(user);
    }

    public static void hideCurrentUserBrowser() {
        userThreadManager.EnsureLogOut();
    }


}

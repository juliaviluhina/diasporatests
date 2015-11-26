package core;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static core.helpers.UniqueDataHelper.deleteUniqueData;
import static core.helpers.UniqueDataHelper.the;

public class WebDriversManager {

    public enum StateAfterPreparing {IS_CREATED, IS_OPENED};

    public StateAfterPreparing prepareWebDriverForKey(String key) {
        WebDriver currentWebDriver;
        if (!currentKey.isEmpty()) {
            hideCurrentBrowser();
        }
        currentKey = key;
        if (!webDrivers.containsKey(key)) {
            if (webDrivers.size() == 0) {
                currentWebDriver = getWebDriver();
            } else {
                currentWebDriver = createWebDriver();
            }
            Runtime.getRuntime().addShutdownHook(new WebDriversFinalCleanupThread(currentWebDriver));
            webDrivers.put(key, currentWebDriver);
            setWebDriver(currentWebDriver);
            return StateAfterPreparing.IS_CREATED;
        } else {
            currentWebDriver = webDrivers.get(key);
            setWebDriver(currentWebDriver);
            currentWebDriver.manage().window().setPosition(new Point(0, 0));
            currentWebDriver.manage().window().maximize();
            currentWebDriver.switchTo().window(currentWebDriver.getWindowHandle());//without this string on Linux does not work
            return StateAfterPreparing.IS_OPENED;
        }
    }

    public StateAfterPreparing prepareDisposableWebDriver() {
        deleteUniqueData("DisposableWebDriver");
        return prepareWebDriverForKey(the("DisposableWebDriver"));
    }

    public void hideCurrentBrowser() {
        if (!currentKey.isEmpty()) {
            webDrivers.get(currentKey).manage().window().setPosition(new Point(-2000, 0));
            currentKey = "";
        }
    }

    private String currentKey = "";
    private Map<String, WebDriver> webDrivers = new HashMap<String, WebDriver>();

    private static WebDriver createWebDriver() {
        try {
            Method method = WebDriverThreadLocalContainer.class.getDeclaredMethod("createDriver");
            method.setAccessible(true);
            WebDriver webDriver = (WebDriver) method.invoke(webdriverContainer);
            return webDriver;
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private static class WebDriversFinalCleanupThread extends Thread {
        private final WebDriver webDriver;

        public WebDriversFinalCleanupThread(WebDriver webDriver) {
            this.webDriver = webDriver;
        }

        public void run() {
            webDriver.close();
        }
    }

}

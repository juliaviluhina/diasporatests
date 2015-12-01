package core;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static core.helpers.UniqueDataHelper.deleteUniqueData;
import static core.helpers.UniqueDataHelper.the;

public class WebDriversManager {

    public enum StateAfterPreparing {IS_CREATED, IS_OPENED}

    public WebDriversManager() {
        currentThread = Thread.currentThread();
    }

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
            markForAutoClose(currentKey, currentWebDriver);
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

    private static final Logger log = Logger.getLogger(WebDriversManager.class.getName());
    private String currentKey = "";
    private Map<String, WebDriver> webDrivers = new HashMap<String, WebDriver>();
    protected final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);
    Thread currentThread = null;

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

    protected WebDriver markForAutoClose(String key, WebDriver webDriver) {
        if (!this.cleanupThreadStarted.get()) {
            synchronized (this) {
                if (!this.cleanupThreadStarted.get()) {
                    (new UnusedWebDriversCleanupThread()).start();
                    this.cleanupThreadStarted.set(true);
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(new WebDriversFinalCleanupThread(key, webDriver));
        return webDriver;
    }


    private class WebDriversFinalCleanupThread extends Thread {
        private final WebDriver webDriver;
        private final String key;

        public WebDriversFinalCleanupThread(String key, WebDriver webDriver) {
            this.webDriver = webDriver;
            this.key = key;
            this.setName("WebDriver killer for key "+key+"(WebDriverManager)");
        }

        public void run() {
            synchronized (this) {
                webDriver.close();
                webDrivers.remove(key);
                log.info("WebDriverManager closed webdriver for "+key);
            }
        }
    }

    protected class UnusedWebDriversCleanupThread extends Thread {
        public UnusedWebDriversCleanupThread() {
            this.setDaemon(true);
            this.setName("WebDrivers killer (WebDriverManager)");
        }

        public void run() {
            while (true) {
                closeUnusedWebDrivers();

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var2) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        protected void closeUnusedWebDrivers() {
            synchronized (this) {
                if (!currentThread.isAlive()) {
                    for (String key : webDrivers.keySet()) {
                        webDrivers.get(key).close();
                        webDrivers.remove(key);
                        log.info("WebDriverManager finally closed webdriver for "+key);
                    }
                }
            }
        }
    }

}

package core;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.Killable;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static core.helpers.UniqueDataHelper.deleteUniqueData;
import static core.helpers.UniqueDataHelper.the;
import static com.codeborne.selenide.Configuration.*;
import static java.util.logging.Level.*;

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
            webDrivers.put(key, currentWebDriver);
            markForAutoClose(currentKey, currentWebDriver);
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
    private Map<String, WebDriver> webDrivers = new ConcurrentHashMap<String, WebDriver>();
    Thread currentThread = null;
    protected final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);

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

    private WebDriver markForAutoClose(String key, WebDriver webDriver) {
        Runtime.getRuntime().addShutdownHook(new WebDriversFinalCleanupThread(key, webDriver));
        if(!this.cleanupThreadStarted.get()) {
            synchronized(this) {
                if(!this.cleanupThreadStarted.get()) {
                    (new UnusedWebDriversCleanupThread()).start();
                    this.cleanupThreadStarted.set(true);
                }
            }
        }

        return webDriver;
    }

    private class UnusedWebDriversCleanupThread extends Thread {
        public UnusedWebDriversCleanupThread() {
            this.setDaemon(true);
            this.setName("Webdrivers killer thread");
        }

        public void run() {
            while(true) {
                if(!currentThread.isAlive()) {
                    log.info("Thread " + currentThread.getId() + " is dead. Let\'s close webdrivers.");
                    for (String key:webDrivers.keySet()) {
                        closeWebDriver(webDrivers.get(key), key);
                    }
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var2) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }


    private class WebDriversFinalCleanupThread extends Thread {
        private final WebDriver webDriver;
        private final String key;

        public WebDriversFinalCleanupThread(String key, WebDriver webDriver) {
            this.webDriver = webDriver;
            this.key = key;
            this.setName("WebDriver killer for key " + key + "(WebDriverManager)");
        }

        public void run() {
            closeWebDriver(webDriver, key);
        }
    }

    private void closeWebDriver(WebDriver webDriver, String key) {

        if (webDriver != null) {
            log.info("WebDriversManager's closing webDriver for key: " + key + " -> " + webDriver);

            long start = System.currentTimeMillis();

            Thread t = new Thread(new CloseBrowser(webDriver, key));
            t.setDaemon(true);
            t.start();

            try {
                t.join(closeBrowserTimeoutMs);
            } catch (InterruptedException e) {
                log.log(FINE, "Failed to close webDriver in " + closeBrowserTimeoutMs + " milliseconds", e);
            }

            long duration = System.currentTimeMillis() - start;
            if (duration >= closeBrowserTimeoutMs) {
                log.severe("Failed to close webDriver in " + closeBrowserTimeoutMs + " milliseconds");
            } else if (duration > 200) {
                log.info("Closed webDriver in " + duration + " ms");
            } else {
                log.fine("Closed webDriver in " + duration + " ms");
            }
        }
    }

    private class CloseBrowser implements Runnable {
        private final WebDriver webdriver;
        private final String key;

        private CloseBrowser(WebDriver webdriver, String key) {
            this.webdriver = webdriver;
            this.key = key;
        }

        public void run() {
            try {
                log.info("Trying to close the browser " + webdriver + " ...");
                webdriver.quit();
            } catch (UnreachableBrowserException e) {
                // It happens for Firefox. It's ok: browser is already closed.
                log.log(FINE, "Browser is unreachable", e);
            } catch (WebDriverException cannotCloseBrowser) {
                log.severe("Cannot close browser normally");
            } finally {
                killBrowser(webdriver);
                webDrivers.remove(key);
            }
        }

        protected void killBrowser(WebDriver webdriver) {
            if (webdriver instanceof Killable) {
                try {
                    ((Killable) webdriver).kill();
                } catch (Exception e) {
                    log.log(SEVERE, "Failed to kill browser " + webdriver + ':', e);
                }
            }
        }

    }
}
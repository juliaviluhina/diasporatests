package concurrency;

import datastructures.PodUser;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import pages.Diaspora;
import pages.Menu;
import pages.NavBar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class UserThreadManager {

    private UserSwitcher userSwitcher = new UserSwitcher();

    public void ensureSinIn(PodUser podUser) {
        userSwitcher.setActive_(podUser);
    }

    public void ensureLogOut() {
       userSwitcher.setPassive_();
    }

    private class UserSwitcher {

        private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(0);
        private PausableThreadPoolExecutor taskExecutor = new PausableThreadPoolExecutor(5,5,10, TimeUnit.MILLISECONDS, workQueue);
        private Map<PodUser, UserThread> userThreads = new HashMap<PodUser, UserThread>();
        private Thread baseThread = null;

        private void addThread(PodUser podUser) {
            Thread thread;
            if (userThreads.size() == 0)
                thread = Thread.currentThread();
            else
                thread = new Thread();

            taskExecutor.execute(thread);
            userThreads.put(podUser, new UserThread(podUser, thread));
        }

        public void setActive_(PodUser podUser) {

            setPassive_();
            if (!userThreads.containsKey(podUser))
                addThread(podUser);
            UserThread currentThread = userThreads.get(podUser);
            try {
                if (currentThread.webDriver == null) {
                    currentThread.webDriver = getWebDriver();
                    Diaspora.setUserNameAndPassword(podUser);
                    NavBar.assertLoggedUser(podUser);
                } else {
                    currentThread.webDriver.manage().window().setPosition(new Point(0, 0));
                    currentThread.webDriver.manage().window().maximize();
                    currentThread.webDriver.switchTo().window(currentThread.webDriver.getWindowHandle());//without this string on Linux does not work
                    Menu.openStream();
                }
            } finally {
                currentThread.isActive = true;
            }
        }

        public void setPassive_() {
            if (userThreads.size() != 0 )
                waitInThread();
            for (UserThread userThread : userThreads.values())
                if (!userThread.isActive) {
                    userThread.webDriver.manage().window().setPosition(new Point(-2000, 0));
                    userThread.isActive = false;
                }
        }

    }

    private class UserThread {
        public Thread thread;
        public PodUser podUser;
        public WebDriver webDriver;
        public Boolean isActive;

        public UserThread(PodUser podUser, Thread thread) {
            this.thread = thread;
            this.podUser = podUser;
            webDriver = null;
            isActive = FALSE;
        }
    }

}


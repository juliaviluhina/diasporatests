package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.AfterClass;
import pages.*;

import java.io.File;
import java.io.IOException;

import static core.AdditionalAPI.*;
import static core.helpers.UniqueDataHelper.*;

public abstract class BaseTest {

    static {
        Configuration.timeout = 15000;
        clearUniqueData();
        countTestClasses = (System.getProperty("test") == null) ? 11 : 1;
    }

    @After
    public void tearDown() throws IOException {
//        byte[] lastSelenideScreenshot = lastSelenideScreenshot();
//        if (lastSelenideScreenshot != null) {
//            screenshot(lastSelenideScreenshot);
//        }
        File lastSelenideScreenshot = Screenshots.takeScreenShotAsFile();
        if (lastSelenideScreenshot != null) {
            screenshot(Files.toByteArray(lastSelenideScreenshot));
        }

    }

    @AfterClass
    public static void CloseWebDrivers(){
        countTestClassesLoaded++;
        if (countTestClassesLoaded == countTestClasses) {
            Diaspora.closeWebDrivers();
        }
    }

    private static int countTestClassesLoaded = 0;
    private static int countTestClasses;

}

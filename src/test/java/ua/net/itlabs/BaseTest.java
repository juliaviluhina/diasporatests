package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import pages.*;

import java.io.IOException;

import static core.AdditionalAPI.*;
import static core.helpers.UniqueDataHelper.*;

public abstract class BaseTest {

    static {
        Configuration.timeout = 15000;
        clearUniqueData();
    }

    @After
    public void tearDown() throws IOException {
        byte[] lastSelenideScreenshot = lastSelenideScreenshot();
        if (lastSelenideScreenshot != null) {
            screenshot(lastSelenideScreenshot);
        }
    }

    @AfterClass
    public static void CloseWebDrivers(){
        countTestClasses++;
        if (countTestClasses == 11) {
            Diaspora.closeWebDrivers();
        }
    }

    private static int countTestClasses = 0;

}

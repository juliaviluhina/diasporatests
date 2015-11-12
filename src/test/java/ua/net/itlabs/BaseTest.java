package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import pages.*;

import java.io.IOException;

import static core.AdditionalAPI.*;
import static core.helpers.UniqueDataHelper.*;

public abstract class BaseTest {

    static {
        Configuration.timeout = 30000;
        clearUniqueData();
    }

    @After
    public void tearDown() throws IOException {
        byte[] lastSelenideScreenshot = lastSelenideScreenshot();
        if (lastSelenideScreenshot != null) {
            screenshot(lastSelenideScreenshot);
        }
    }

}

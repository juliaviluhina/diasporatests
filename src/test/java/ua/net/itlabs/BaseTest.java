package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import pages.*;

import java.io.IOException;

import static core.AdditionalAPI.*;

public abstract class BaseTest {

    static {
        Configuration.timeout = 90000;
    }

    @After
    public void tearDown() throws IOException {
        byte[] lastSelenideScreenshot = lastSelenideScreenshot();
        if (lastSelenideScreenshot != null) {
            screenshot(lastSelenideScreenshot);
        }
        Menu.ensureLoggedOut();
    }

}

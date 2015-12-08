package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.AfterClass;
import pages.*;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
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
        File lastSelenideScreenshot = Screenshots.takeScreenShotAsFile();
        if (lastSelenideScreenshot != null) {
            screenshot(Files.toByteArray(lastSelenideScreenshot));
        }
    }

    @Attachment(type = "image/png")
    public static byte[] screenshot(byte[] dataForScreenshot) {
        return dataForScreenshot;
    }

}

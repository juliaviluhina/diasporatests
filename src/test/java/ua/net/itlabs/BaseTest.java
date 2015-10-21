package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.google.common.io.Files;
import core.AdditionalAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import pages.*;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static core.helpers.UniqueDataHelper.clearThe;
import static ua.net.itlabs.testDatas.Users.*;
import static core.steps.Scenarios.*;


public class BaseTest extends AdditionalAPI{
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 90000;
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(ANA_P1);
            clearUserData(ROB_P1);
            clearUserData(EVE_P1);
            clearUserData(RON_P1);
            clearUserData(BOB_P2);
            clearUserData(SAM_P2);
        }
    }

    @Before
    public void ActionsBeforeTest()  {
        clearThe();
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

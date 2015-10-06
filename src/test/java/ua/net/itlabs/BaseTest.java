package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.google.common.io.Files;
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


public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 120000;
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(ANA_P1);
            clearUserData(BOB_P2);
            clearUserData(ROB_P1);
            clearUserData(SAM_P2);
            clearUserData(EVE_P1);
        }
    }

    @Before
    public void ActionsBeforeTest() {
        clearThe();
        Menu.ensureLoggedOut();
    }

    @After
    public void tearDown() throws IOException {
        screenshot();
    }

    @Attachment(type = "image/png")
    public byte[] screenshot() throws IOException {
        Field allScreenshotsField = null;
        try {
            allScreenshotsField = ScreenShotLaboratory.class.getDeclaredField("allScreenshots");
            allScreenshotsField.setAccessible(true);
            List<String> allScreenshots = (List<String>) allScreenshotsField.get(Screenshots.screenshots);
            return Files.toByteArray(new File(allScreenshots.get(allScreenshots.size() - 1)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }

}

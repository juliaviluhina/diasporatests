package ua.net.itlabs;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import pages.*;

import java.io.IOException;

import static core.helpers.UniqueDataHelper.clearThe;
import static ua.net.itlabs.testDatas.Users.*;
import static core.steps.Scenarios.*;


public class BaseTest {
    @BeforeClass
    public static void clearDataBeforeTests() {
        Configuration.timeout = 120000;
        if (System.getProperty("withClearedDataOnStart").equals("true")) {
            //System.out.println("clearing data before tests");
            clearUserData(ANA);
            clearUserData(BOB);
            clearUserData(ROB);
            clearUserData(SAM);
            clearUserData(EVE);
        }
    }

    @Before
    public void ActionsBeforeTest() {
        clearThe();
    }

    @After
    public void tearDown() throws IOException {
        Menu.ensureLoggedOut();
    }



}

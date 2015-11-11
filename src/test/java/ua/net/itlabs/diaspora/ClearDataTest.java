package ua.net.itlabs.diaspora;

import org.junit.BeforeClass;
import org.junit.Test;
import ua.net.itlabs.BaseTest;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static steps.Scenarios.clearUserData;
import static ua.net.itlabs.testDatas.Users.*;


public class ClearDataTest extends BaseTest {

    @Test
    public void testClearData() {
        clearUserData(Pod1.ana);
        clearUserData(Pod1.rob);
        clearUserData(Pod1.eve);
        clearUserData(Pod2.bob);
        clearUserData(Pod2.sam);
    }
}

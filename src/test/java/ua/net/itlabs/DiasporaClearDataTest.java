package ua.net.itlabs;

import org.junit.BeforeClass;
import org.junit.Test;

import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.steps.Scenarios.clearUserData;
import static ua.net.itlabs.testDatas.Users.*;


public class DiasporaClearDataTest extends BaseTest {

    @BeforeClass
    public static void buildGivenForTests() {
        clearUniqueData();
    }

    @Test
    public void testClearData() {
        clearUserData(Pod1.ana);
        clearUserData(Pod1.rob);
        clearUserData(Pod1.eve);
        clearUserData(Pod1.ron);
        clearUserData(Pod2.bob);
        clearUserData(Pod2.sam);
    }
}

package ua.net.itlabs;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import ua.net.itlabs.categories.AdditionalOperations;
import ua.net.itlabs.categories.ClearData;

import static core.steps.Scenarios.clearUserData;
import static ua.net.itlabs.testDatas.Users.*;
import static ua.net.itlabs.testDatas.Users.BOB_P2;
import static ua.net.itlabs.testDatas.Users.SAM_P2;

@Category(ClearData.class)
public class DiasporaClearDataTest extends BaseTest {

    @Test
    public void testClearData() {
        clearUserData(ANA_P1);
        clearUserData(ROB_P1);
        clearUserData(EVE_P1);
        clearUserData(RON_P1);
        clearUserData(BOB_P2);
        clearUserData(SAM_P2);
    }
}

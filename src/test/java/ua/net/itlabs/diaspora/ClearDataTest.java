package ua.net.itlabs.diaspora;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import steps.Relation;
import ua.net.itlabs.BaseTest;

import static steps.Scenarios.clearUserData;
import static ua.net.itlabs.testDatas.Users.*;

@Category(ua.net.itlabs.categories.ClearData.class)
public class ClearDataTest extends BaseTest {

    @Test
    public void testClearData() {

        //ignoring is needed for clearing streams from alien deleted posts
        //because of problem with federation deletion all posts is not enough
        Relation.forUser(Pod1.ana).ignoreUsers(Pod1.eve, Pod1.rob, Pod2.sam, Pod2.bob).ensure();
        Relation.forUser(Pod1.eve).ignoreUsers(Pod1.ana, Pod1.rob, Pod2.sam, Pod2.bob).ensure();
        Relation.forUser(Pod1.rob).ignoreUsers(Pod1.eve, Pod1.ana, Pod2.sam, Pod2.bob).ensure();
        Relation.forUser(Pod2.bob).ignoreUsers(Pod1.ana, Pod1.eve, Pod1.rob, Pod2.sam).ensure();
        Relation.forUser(Pod2.sam).ignoreUsers(Pod1.ana, Pod1.eve, Pod1.rob, Pod2.bob).ensure();

        clearUserData(Pod1.ana);
        clearUserData(Pod1.rob);
        clearUserData(Pod1.eve);
        clearUserData(Pod2.bob);
        clearUserData(Pod2.sam);

    }

}

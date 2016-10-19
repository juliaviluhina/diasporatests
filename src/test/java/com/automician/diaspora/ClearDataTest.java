package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.categories.ClearData;
import com.automician.testDatas.Users;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import steps.Relation;

import static steps.Scenarios.clearUserData;

@Category(ClearData.class)
public class ClearDataTest extends BaseTest {

    @Test
    public void testClearData() {

        //ignoring is needed for clearing streams from alien deleted posts
        //because of problem with federation deletion all posts is not enough
        Relation.forUser(Users.Pod1.ana).ignoreUsers(Users.Pod1.eve, Users.Pod1.rob, Users.Pod2.sam, Users.Pod2.bob).ensure();
        Relation.forUser(Users.Pod1.eve).ignoreUsers(Users.Pod1.ana, Users.Pod1.rob, Users.Pod2.sam, Users.Pod2.bob).ensure();
        Relation.forUser(Users.Pod1.rob).ignoreUsers(Users.Pod1.eve, Users.Pod1.ana, Users.Pod2.sam, Users.Pod2.bob).ensure();
        Relation.forUser(Users.Pod2.bob).ignoreUsers(Users.Pod1.ana, Users.Pod1.eve, Users.Pod1.rob, Users.Pod2.sam).ensure();
        Relation.forUser(Users.Pod2.sam).ignoreUsers(Users.Pod1.ana, Users.Pod1.eve, Users.Pod1.rob, Users.Pod2.bob).ensure();

        clearUserData(Users.Pod1.ana);
        clearUserData(Users.Pod1.rob);
        clearUserData(Users.Pod1.eve);
        clearUserData(Users.Pod2.bob);
        clearUserData(Users.Pod2.sam);

    }

}

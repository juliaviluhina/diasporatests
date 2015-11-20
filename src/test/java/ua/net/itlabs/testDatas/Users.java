package ua.net.itlabs.testDatas;

import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;
import steps.Relation;

import static core.Gherkin.*;
import static pages.Aspects.*;

public class Users {

    protected static class Pod {

        public static String podLink;

        public static PodUser newUser(String userName, String password, String email) {
            return new PodUser(userName, password, podLink, email);
        }

    }

    public static class Pod1 extends Pod {

        public static PodUser ana;
        public static PodUser rob;
        public static PodUser eve;

        static {

            if (getDataSet().equals("set1")) {

                podLink = "https://diaspora.koehn.com";
                ana = newUser("ana_tjvi", "tjvitjvi", "ana.tjvi@gmail.com");
                rob = newUser("rob_tjvi", "tjvitjvi", "rob.tjvi@gmail.com");
                eve = newUser("eve_tjvi", "tjvitjvi", "eve.tjvi@gmail.com");

            } else {

                podLink = "https://framasphere.org";
                ana = newUser("ana1_tjvi", "tjvitjvi", "h   ur12328@adiaw.com");
                rob = newUser("rob1_tjvi", "tjvitjvi", "epc27203@adiaw.com");
                eve = newUser("eve1_tjvi", "tjvitjvi", "tsw00991@adiaw.com");

            }

        }

    }

    public static class Pod2 extends Pod {

        public static PodUser bob;
        public static PodUser sam;

        static {

            if (getDataSet().equals("set1")) {

                podLink = "https://nerdpol.ch";
                bob = newUser("bob_tjvi", "tjvitjvi", "bob.tjvi@gmail.com");
                sam = newUser("sam_tjvi", "tjvitjvi", "sam.tjvi@gmail.com");

            } else {

                podLink = "https://pod.geraspora.de";
                bob = newUser("bob1_tjvi", "tjvitjvi", "fwl11173@adiaw.com");
                sam = newUser("sam1_tjvi", "tjvitjvi", "zdd05833@adiaw.com");

            }

        }

    }

    public static String getDataSet() {
        return (System.getProperty("dataset"));
    }

    @Step
    public static void ensureRelationsForUsersOfPod1() {

        GIVEN("Eve is not linked with Ana and Rob");
        GIVEN("Rob is linked with Ana as Friend and is not linked with Eve");
        GIVEN("Ana is linked with Rob as Friend and is not linked with Eve");
        Relation.forUser(Pod1.eve).notToUsers(Pod1.ana, Pod1.rob).ensure();
        Relation.forUser(Pod1.rob).toUser(Pod1.ana, FRIENDS).notToUsers(Pod1.eve).ensure();
        Relation.forUser(Pod1.ana).toUser(Pod1.rob, FRIENDS).notToUsers(Pod1.eve).ensure();

    }

}

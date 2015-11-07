package ua.net.itlabs.testDatas;

import datastructures.PodUser;

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
        //public static PodUser ron; //currently this user of is not used in tests, it can be used

        static {

            if (getDataSet().equals("set1")) {

                podLink = "https://diaspora.koehn.com";
                ana = newUser("ana_tjvi", "tjvitjvi", "ana.tjvi@gmail.com");
                rob = newUser("rob_tjvi", "tjvitjvi", "rob.tjvi@gmail.com");
                eve = newUser("eve_tjvi", "tjvitjvi", "eve.tjvi@gmail.com");
                //ron = newUser("ron_tjvi", "tjvitjvi", "ron.tjvi@gmail.com");

            } else {

                podLink = "https://framasphere.org";
                ana = newUser("ana1_tjvi", "tjvitjvi", "h   ur12328@adiaw.com");
                rob = newUser("rob1_tjvi", "tjvitjvi", "epc27203@adiaw.com");
                eve = newUser("eve1_tjvi", "tjvitjvi", "tsw00991@adiaw.com");
                //ron = newUser("ron1_tjvi", "tjvitjvi", "smj41103@adiaw.com");

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

    //currently user of this pod is not used in tests
    //it can be used in future
    //take into account - this user does not have any posts. LogOut - modal dialog is appeared.
    public static class Pod3 extends Pod {

        public static PodUser dave;

        static {

            if (getDataSet().equals("set1")) {

                podLink = "https://diasporabrazil.org";
                dave = newUser("dave_tjvi", "tjvitjvi", "dave.tjvi@gmail.com");

            } else {

                podLink = "https://pod.readme.is";
                dave = newUser("dave1_tjvi", "tjvitjvi", "dave1.tjvi@gmail.com");

            }

        }

    }

    public static String getDataSet() {
        return (System.getProperty("dataset"));
    }

}

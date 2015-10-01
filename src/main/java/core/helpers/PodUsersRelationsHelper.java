package core.helpers;

import datastructures.PodUser;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;

public class PodUsersRelationsHelper {
    private PodUser podUser;
    private PodUser[] linkedUsers;
    private PodUser[] unlinkedUsers;
    private String[] aspects;
    private String followedTag;

    @Step
    public static void setupLinksFor(PodUser podUser, PodUser linkedUser, String followedTag, PodUser unlinkedUser, String... diasporaAspects) {
        new PodUsersRelationsHelper(podUser, linkedUser, followedTag, unlinkedUser, diasporaAspects);
    }

    @Step
    public static void setupLinksFor(PodUser podUser, String followedTag, PodUser... unlinkedUsers) {
        new PodUsersRelationsHelper(podUser, followedTag, unlinkedUsers);
    }

    @Step
    public static void setupLinksFor(PodUser podUser, PodUser linkedUser, String diasporaAspect, String followedTag, PodUser... unlinkedUsers) {
        new PodUsersRelationsHelper(podUser, linkedUser, diasporaAspect, followedTag, unlinkedUsers);
    }

    public PodUsersRelationsHelper(PodUser podUser, PodUser linkedUser, String followedTag, PodUser unlinkedUser, String... diasporaAspects) {
        this.podUser = podUser;
        this.followedTag = followedTag;

        this.linkedUsers = new PodUser[1];
        this.linkedUsers[0] = linkedUser;

        this.aspects = new String[diasporaAspects.length];
        int i = 0;
        for (String diasporaAspect : diasporaAspects) {
            this.aspects[i] = diasporaAspect;
            i++;
        }

        this.unlinkedUsers = new PodUser[1];
        this.unlinkedUsers[0] = unlinkedUser;
        createRelations();
    }

    public PodUsersRelationsHelper(PodUser podUser, String followedTag, PodUser... unlinkedUsers) {
        this.podUser = podUser;
        this.followedTag = followedTag;

        this.linkedUsers = new PodUser[0];

        this.aspects = new String[0];

        this.unlinkedUsers = new PodUser[unlinkedUsers.length];
        int i = 0;
        for (PodUser unlinkedUser : unlinkedUsers) {
            this.unlinkedUsers[i] = unlinkedUser;
            i++;
        }
        createRelations();
    }

    public PodUsersRelationsHelper(PodUser podUser, PodUser linkedUser, String diasporaAspect, String followedTag, PodUser... unlinkedUsers) {
        this.podUser = podUser;
        this.followedTag = followedTag;

        this.linkedUsers = new PodUser[1];
        this.linkedUsers[0] = linkedUser;

        this.aspects = new String[0];
        this.aspects[0] = diasporaAspect;

        this.unlinkedUsers = new PodUser[unlinkedUsers.length];
        int i = 0;
        for (PodUser unlinkedUser : unlinkedUsers) {
            this.unlinkedUsers[i] = unlinkedUser;
            i++;
        }
        createRelations();
    }

    private void createRelations() {
        Diaspora.signInAs(podUser);
        Menu.assertLoggedUser(podUser);
        //users have aspects relation with linkedUsers
        for (PodUser linkedUser : linkedUsers) {
            Menu.search(linkedUser.fullName);
            Contact.ensureAspectsForContact(aspects);
        }
        //user have not any relation with unlinkedUser1
        for (PodUser unlinkedUser : unlinkedUsers) {
            Menu.search(unlinkedUser.fullName);
            Contact.ensureNoAspectsForContact();
        }
        //Addition followed tag
        if (!followedTag.isEmpty()) {
            Menu.openStream();
            NavBar.openTags();
            Tags.add(followedTag);
            Tags.assertExist(followedTag);
        }
        Menu.logOut();
    }

}

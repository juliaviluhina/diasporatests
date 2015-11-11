package core.steps;

import datastructures.PodUser;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.dismiss;
import static java.lang.Boolean.*;

public class Relation {

    private final PodUser podUser;

    private final List<LinkWithUser> linkWithUsers;
    private final List<PodUser> unlinkedUsers;
    private final List<String> followedTags;
    private Boolean doLogOut;
    private Boolean clearTags;

    private static class LinkWithUser {
        public PodUser linkedUser;
        public String[] aspects;

        public LinkWithUser(PodUser linkedUser, String... aspects) {
            this.linkedUser = linkedUser;
            this.aspects = new String[aspects.length];
            int i = 0;
            for (String aspect : aspects) {
                this.aspects[i] = aspect;
                i++;
            }
        }
    }

    public static Builder forUser(PodUser podUser) {
        return new Builder(podUser);
    }

    public static class Builder {

        private final PodUser podUser;

        private List<LinkWithUser> linkWithUsers;
        private List<PodUser> unlinkedUsers;
        private List<String> followedTags;
        private Boolean doLogOut;
        private Boolean clearTags;

        private Builder(PodUser podUser) {
            this.podUser = podUser;

            linkWithUsers = new ArrayList<LinkWithUser>();
            unlinkedUsers = new ArrayList<PodUser>();
            followedTags = new ArrayList<String>();
            doLogOut = TRUE;
            clearTags = FALSE;
        }

        public Builder doNotLogOut() {
            doLogOut = FALSE;
            return this;
        }

        public Builder clearTags() {
            clearTags = TRUE;
            return this;
        }


        public Builder toUser(PodUser linkedUser, String... aspects) {
            linkWithUsers.add(new LinkWithUser(linkedUser, aspects));
            return this;
        }

        public Builder withTags(String... tags) {
            for (String tag : tags) {
                followedTags.add(tag);
            }
            return this;
        }

        public Builder notToUsers(PodUser... users) {
            for (PodUser user : users) {
                unlinkedUsers.add(user);
            }
            return this;
        }

        @Step
        public Relation ensure() {
            return new Relation(this).createRelations();
        }
    }

    private Relation(Builder builder) {

        this.podUser = builder.podUser;

        this.linkWithUsers = builder.linkWithUsers;
        this.unlinkedUsers = builder.unlinkedUsers;
        this.followedTags = builder.followedTags;
        this.doLogOut = builder.doLogOut;
        this.clearTags = builder.clearTags;

    }

    public Relation createRelations() {
        Menu.ensureLoggedOut();
        Diaspora.signInAs(podUser);
        NavBar.assertLoggedUser(podUser);
        if (clearTags) {
            NavBar.openTags();
            Tags.deleteAll();
        }
        for (LinkWithUser linkWithUser : linkWithUsers) {
            Menu.search(linkWithUser.linkedUser.fullName);
            Contact.ensureAspectsForContact(linkWithUser.aspects);
        }
        for (PodUser unlinkedUser : unlinkedUsers) {
            Menu.search(unlinkedUser.fullName);
            Contact.ensureNoAspectsForContact();
        }
        for (String followedTag : followedTags) {
            if (!followedTag.isEmpty()) {
                Menu.openStream();
                NavBar.openTags();
                Tags.add(followedTag);
                Tags.assertExist(followedTag);
            }
        }
        if (doLogOut) {
            Menu.logOut();
        }

        return this;
    }
}

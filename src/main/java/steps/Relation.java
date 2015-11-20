package steps;

import datastructures.PodUser;
import pages.*;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.*;

import static com.codeborne.selenide.Selenide.$;
import static java.lang.Boolean.*;

public class Relation {

    private final PodUser podUser;
    private RelationProperties relationProperties;
    private Boolean doLogOut;

    private Relation(Builder builder) {
        this.podUser = builder.podUser;

        this.relationProperties = builder.relationProperties;
        this.doLogOut = builder.doLogOut;
    }

    public static Builder forUser(PodUser podUser) {
        return new Builder(podUser);
    }

    public Relation createRelations() {
        Diaspora.ensureSignInAs(podUser);
        NavBar.assertLoggedUser(podUser);
        if (relationProperties.clearTags) {
            NavBar.openTags();
            Tags.deleteAll();
        }
        for (LinkWithUser linkWithUser : relationProperties.linkWithUsers) {
            Menu.search(linkWithUser.linkedUser.fullName);
            Contact.ensureAspectsForContact(linkWithUser.aspects);
        }
        for (PodUser unlinkedUser : relationProperties.unlinkedUsers) {
            Menu.search(unlinkedUser.fullName);
            Contact.ensureNoAspectsForContact();
        }
        for (String followedTag : relationProperties.followedTags) {
            if (!followedTag.isEmpty()) {
                Menu.openStream();
                NavBar.openTags();
                Tags.add(followedTag);
                Tags.assertExist(followedTag);
            }
        }
        if (doLogOut) {
            Menu.ensureLogOut();
        }

        return this;
    }


    public static class Builder {

        private final PodUser podUser;
        private RelationProperties relationProperties;
        private Boolean doLogOut;

        private static Map<PodUser, RelationProperties> usersRelations;

        static {
            usersRelations = new HashMap<PodUser, RelationProperties>();
        }

        private Builder(PodUser podUser) {
            this.podUser = podUser;
            relationProperties = new RelationProperties();
            doLogOut = TRUE;
        }

        public Builder doNotLogOut() {
            doLogOut = FALSE;
            return this;
        }

        public Builder clearTags() {
            relationProperties.clearTags = TRUE;
            return this;
        }


        public Builder toUser(PodUser linkedUser, String... aspects) {
            relationProperties.linkWithUsers.add(new LinkWithUser(linkedUser, aspects));
            return this;
        }

        public Builder withTags(String... tags) {
            for (String tag : tags) {
                relationProperties.followedTags.add(tag);
            }
            return this;
        }

        public Builder notToUsers(PodUser... users) {
            for (PodUser user : users) {
                relationProperties.unlinkedUsers.add(user);
            }
            return this;
        }

        @Step
        public Relation ensure() {
            RelationProperties currentRelation = usersRelations.get(podUser);
            if (currentRelation != null) {
                if (currentRelation.equals(relationProperties)) {
                    //relations set up earlier
                    return currentRelation.relation;
                }
            }

            relationProperties.relation = new Relation(this).createRelations();
            usersRelations.put(podUser, relationProperties);

            return relationProperties.relation;

        }

        public void isChanged() {
            usersRelations.remove(podUser);
        }
    }

    private static class RelationProperties {

        private LinkWithUsers linkWithUsers;
        private UnlinkedUsers unlinkedUsers;
        private FollowedTags followedTags;
        private Boolean clearTags;

        public Relation relation;

        public RelationProperties() {
            linkWithUsers = new LinkWithUsers();
            unlinkedUsers = new UnlinkedUsers();
            followedTags = new FollowedTags();
            clearTags = FALSE;

            relation = null;
        }

        public boolean equals(RelationProperties relationProperties) {
            return (clearTags == relationProperties.clearTags) &&
                    (linkWithUsers.equals(relationProperties.linkWithUsers)) &&
                    (unlinkedUsers.equals(relationProperties.unlinkedUsers)) &&
                    (followedTags.equals(relationProperties.followedTags));
        }

    }

    private static class LinkWithUsers extends ArrayList<LinkWithUser> {

        public boolean equals(LinkWithUsers toValues) {
            if (this.size() != toValues.size()) {
                return false;
            }
            for (LinkWithUser toValue : toValues) {
                boolean found = false;
                for (LinkWithUser value : this) {
                    if (value.equals(toValue)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }
    }

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

        public boolean equals(LinkWithUser toValue) {
            if (!linkedUser.fullName.equals(toValue.linkedUser.fullName)) {
                return false;
            }
            if (aspects.length != toValue.aspects.length) {
                return false;
            }
            List<String> toValueAspects = Arrays.asList(toValue.aspects);
            for (String aspect : aspects) {
                if (!toValueAspects.contains(aspect)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class UnlinkedUsers extends ArrayList<PodUser> {

        public boolean equals(UnlinkedUsers toValues) {
            if (this.size() != toValues.size()) {
                return false;
            }
            for (PodUser toValue : toValues) {
                boolean found = false;
                for (PodUser value : this) {
                    if (value.fullName.equals(toValue.fullName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

    }

    private static class FollowedTags extends ArrayList<String> {

        public boolean equals(FollowedTags toValues) {
            if (this.size() != toValues.size()) {
                return false;
            }
            for (String toValue : toValues) {
                if (!contains(toValue)) {
                    return false;
                }
            }
            return true;
        }

    }

}

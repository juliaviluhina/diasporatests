package steps;

        import datastructures.PodUser;
        import pages.*;
        import ru.yandex.qatools.allure.annotations.Step;

public class Scenarios {

    @Step
    public static void clearUserData(PodUser user) {
        Diaspora.signInAs(user);

        NavBar.openStream();
        Feed.deleteAllPosts(user);

        NavBar.openTags();
        Tags.deleteAll();

        Menu.openContacts();
        Contacts.deleteAllUserAspects();

        Menu.logOut();
    }

    @Step
    public static void waitStreamOpening(){
        //user menu can be opened only if stream is opened - indirect check
        Menu.openMenu();
        Menu.search.click();//to close usermenu
    }
}

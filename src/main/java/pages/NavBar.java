package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.$;

public class NavBar {

    private static SelenideElement container = $("#leftNavBar");

    @Step
    public static void openStream() {
        container.find("[href='/stream']").click();
    }

    @Step
    public static void openMyActivity() {
        container.find("[href='/activity']").click();
    }

    @Step
    public static void openTags() {
        container.find("[href='/followed_tags']").click();
    }

    @Step
    public static void openMyAspects() {
        container.find("[href='/aspects']").click();
    }

    @Step
    public static void openMentions() {
        container.find("[href='/mentions']").click();
    }

    //bacause of slow loading stream this check is better
    @Step
    public static void assertLoggedUser(PodUser user) {
        $("#home_user_badge").shouldHave(Condition.exactText(user.userName));
    }

}

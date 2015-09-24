package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Menu {

    public static SelenideElement userMenu = $("#user_menu");

    @Step
    public static void logOut() {
        $(".user-menu-more-indicator").click();
        userMenu.find("[data-method='delete']").click();
    }

    @Step
    public static void search(String text) {
        $("#q").setValue(text);
        $$(".ac_results").shouldHave(texts(text));
        $("#q").pressEnter();
    }

    @Step
    public static void openStream(){
        $(".header-nav [href='/stream']").click();
    }

    public static void assertLoggedUser(PodUser user){
        userMenu.find(".user-name").shouldHave(exactText(user.fullName));
    }

}

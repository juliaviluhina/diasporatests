package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static core.conditions.CustomCollectionCondition.textsBegin;

public class NavBar {

    public static SelenideElement navBar =  $("#leftNavBar");
    public static SelenideElement tagsHeader = $("[href='/followed_tags']");

    @Step
    public static void openTags() {
        tagsHeader.click();
    }

    @Step
    public static void openStream(){
        navBar.find("[href='/stream']").click();
    }

    @Step
    public static void openMyActivity(){
        navBar.find("[href='/activity']").click();
    }

    @Step
    public static void openMyAspects(){
        navBar.find("[href='/aspects']").click();
        navBar.find("[href='/aspects']").click();
    }

}

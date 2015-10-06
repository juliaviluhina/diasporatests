package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.$;

public class NavBar {

    private static SelenideElement container =  $("#leftNavBar");

    @Step
    public static void openTags() {
        container.find("[href='/followed_tags']").click();
    }

    @Step
    public static void openStream(){
        container.find("[href='/stream']").click();
    }

    @Step
    public static void openMyActivity(){
        container.find("[href='/activity']").click();
    }

    @Step
    public static void openMyAspects(){
        container.find("[href='/aspects']").click();
        container.find("[href='/aspects']").click();
    }

    @Step
    public static void should(Condition... conditions) {
        container.should(conditions);
    }

}

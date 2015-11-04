package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static core.AdditionalAPI.hover;

public class Aspects {

    public static final String FAMILY = "Family";
    public static final String FRIENDS = "Friends";
    public static final String WORK = "Work";
    public static final String ACQUAINTANCES = "Acquaintances";
    public static final String[] STANDART_ASPECTS = {FAMILY, FRIENDS, WORK, ACQUAINTANCES};


    public static ElementsCollection aspectsNavBar = $$("#aspects_list li a");
    public static ElementsCollection aspectContainersNavBar = $$("#aspects_list li");

    @Step
    public static void toggleAll() {
        $(".toggle_selector").click();
    }

    @Step
    public static void toggleAspect(String text) {
        aspectsNavBar.find(text(text)).click();
    }

    @Step
    public static void add(String aspect) {
        aspectsNavBar.find(text("Add an aspect")).click();
        Contacts.addAspectInDialog(aspect);
    }

    @Step
    public static void switchToEditMode(String aspect) {
        SelenideElement currentAspect = aspectContainersNavBar.find(exactText(aspect));
        hover(currentAspect);
        currentAspect.find(".modify_aspect").click();
    }

    @Step
    public static void assertAspectInNavBar(String aspect) {
        aspectsNavBar.find(text(aspect)).shouldBe(visible);
    }

    @Step
    public static void assertNoAspectInNavBar(String aspect) {
        aspectsNavBar.filter(text(aspect)).shouldBe(empty);
    }

    @Step
    public static void assertToggleAllText(String text) {
        $(".toggle_selector").shouldHave(text(text));
    }

}
package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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
    public static void switchToEditMode(String aspect){
        SelenideElement currentAspect = aspectContainersNavBar.find(text(aspect));
        Coordinates coordinates = aspectsNavBar.find(text(aspect)).getCoordinates();
        coordinates.inViewPort();
        currentAspect.hover();
        currentAspect.find(".modify_aspect").click();
    }

    public static void assertAspectIsShownInNavBar(String aspect) {
        aspectsNavBar.filter(text(aspect)).shouldHave(size(1));
    }

    public static void assertAspectIsNotShownInNavBar(String aspect) {
        aspectsNavBar.filter(text(aspect)).shouldHave(size(0));
    }

    public static void assertToggleAllText(String text) {
        $(".toggle_selector").shouldHave(text(text));
    }

}
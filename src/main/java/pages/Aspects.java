package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.internal.Coordinates;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static java.lang.Boolean.*;
import static core.conditions.CustomCollectionCondition.*;

public class Aspects {

    public static SelenideElement manageContact = $(".btn.dropdown-toggle");
    public static ElementsCollection aspects = $$(".aspect_selector");
    public static ElementsCollection aspectsNavBar = $$("#aspects_list li a");
    public static ElementsCollection aspectContainersNavBar = $$("#aspects_list li");
    public static final String[] STANDART_ASPECTS = {"Family", "Friends", "Work", "Acquaintances"};

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
        $("#newAspectModalLabel").shouldHave(text("Add a new aspect"));
        $("#aspect_name").setValue(aspect);
        $(".creation").click();
    }

    @Step
    public static void switchToEditMode(String aspect){
        SelenideElement currentAspect = aspectContainersNavBar.find(text(aspect));
        Coordinates coordinates = aspectsNavBar.find(text(aspect)).getCoordinates();
        coordinates.inViewPort();
        currentAspect.hover();
        currentAspect.find(".modify_aspect").click();
    }

    @Step
    public static void rename(String oldName, String newName) {
        $("#change_aspect_name").click();
        $("#aspect_name_form #aspect_name").setValue(newName);
        $(By.name("commit")).click();
    }

    @Step
    public static void delete() {
        $("#delete_aspect").click();
        confirm(null);
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

    public static void assertAspectsAreNotUsed() {
        manageContact.shouldHave(text("Add contact"));
    }

    protected static Boolean aspectIsUsed(SelenideElement aspect) {
        return aspect.getAttribute("class").contains("selected");
    }

    public static void ensureNoAspectsForContact() {
        if (manageContact.getText().equals("Add contact")) {
            return;
        }
        manageContact.click();
        aspects.shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspects.getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspects.get(i));
        }
        $("#diaspora_handle").click();
        for (int i = 0; i < aspects.size(); i++) {
            if (beUsed[i]) {
                manageContact.click();
                aspects.get(i).click();
                $("#diaspora_handle").click();
            }
        }
    }

    public static void ensureAspectsForContact(String... diasporaAspects) {
        if (diasporaAspects.length == 1) {
            if (manageContact.getText().equals(diasporaAspects[0])) {
                return;
            }
        }
        manageContact.click();
        aspects.shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspects.getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        Boolean[] shouldBeUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspects.get(i));
            shouldBeUsed[i] = FALSE;
        }
        $("#diaspora_handle").click();
        for (String diasporaAspect : diasporaAspects) {
            for (int i=0; i<aspectTexts.length; i++) {
                if (aspectTexts[i].contains(diasporaAspect)) {
                    shouldBeUsed[i] = TRUE;
                    break;
                }
            }
        }

        for (int i = 0; i < aspects.size(); i++) {
            if (beUsed[i] != shouldBeUsed[i]) {
                manageContact.click();
                aspects.get(i).click();
                $("#diaspora_handle").click();
            }
        }
    }

}
package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.DiasporaAspect;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static java.lang.Boolean.*;
import static core.conditions.CustomCollectionCondition.*;

public class Aspects {

    public static SelenideElement manageContact = $(".btn.dropdown-toggle");
    public static ElementsCollection aspects = $$(".aspect_selector");
    public static ElementsCollection aspectsNavBar = $$("#aspects_list a");
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
        confirm(aspect);
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
        for (String aspectText : aspectTexts) {
            SelenideElement aspect = aspects.find(text(aspectText));
            if (!aspectIsUsed(aspect)) {
                continue;
            }
            aspect.click();
        }
        $("#diaspora_handle").click();
    }

    public static void ensureAspectsForContact(DiasporaAspect... diasporaAspects) {
        if (diasporaAspects.length == 1) {
            if (manageContact.getText().equals(diasporaAspects[0].name)) {
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
        for (DiasporaAspect diasporaAspect : diasporaAspects) {
            shouldBeUsed[diasporaAspect.number] = TRUE;
        }

        for (int i = 0; i < aspects.size(); i++) {
            if (beUsed[i] != shouldBeUsed[i]) {
                aspects.get(i).click();
            }
        }
        $("#diaspora_handle").click();
    }

}
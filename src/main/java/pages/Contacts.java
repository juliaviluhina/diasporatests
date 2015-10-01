package pages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static java.lang.Boolean.*;
import static core.conditions.CustomCollectionCondition.textsBegin;
import static core.conditions.CustomCondition.textBegin;
import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.STANDART_ASPECTS;


public class Contacts {

    public static ElementsCollection aspects = $$(".aspect a");
    public static SelenideElement manageContact = $(".btn.dropdown-toggle");
    public static ElementsCollection aspectsOfContact = $$(".aspect_selector");

    @Step
    public static void addAspect(String aspect) {
        $("[data-target='#newAspectModal']").click();
        addAspectInDialog(aspect);
    }

    public static void addAspectInDialog(String aspect) {
        $("#newAspectModalLabel").shouldHave(text("Add a new aspect"));
        $("#aspect_name").setValue(aspect);
        $(".creation").click();
    }

    @Step
    public static void selectAspect(String aspect) {
        aspects.filter(exactText("0\n" + aspect)).get(0).click();
        $(".header #aspect_name").shouldHave(exactText(aspect));
    }

    public static void deleteAspect() {
        $("#delete_aspect").click();
        confirm(null);
    }

    @Step
    public static void rename(String oldName, String newName) {
        $("#change_aspect_name").click();
        $("#aspect_name_form #aspect_name").setValue(newName);
        $(By.name("commit")).click();
    }

    public static void assertAspectIsShown(String aspect) {
        aspects.filter(exactText("0\n" + aspect)).shouldHave(size(1));
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
        aspectsOfContact.shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspectsOfContact.getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspectsOfContact.get(i));
        }
        $("#diaspora_handle").click();
        for (int i = 0; i < aspectsOfContact.size(); i++) {
            if (beUsed[i]) {
                manageContact.click();
                aspectsOfContact.get(i).click();
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
        aspectsOfContact.shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspectsOfContact.getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        Boolean[] shouldBeUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspectsOfContact.get(i));
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

        for (int i = 0; i < aspectsOfContact.size(); i++) {
            if (beUsed[i] != shouldBeUsed[i]) {
                manageContact.click();
                aspectsOfContact.get(i).click();
                $("#diaspora_handle").click();
            }
        }
    }

    private static Boolean isStandartAspect(String aspect) {
        for (String standartAspect:STANDART_ASPECTS){
            if (standartAspect.equals(aspect)) {return TRUE;}
        }
        return FALSE;
    }

    public static void deleteAllUserAspects() {
        clearThe();
        addAspect(the("ServAsp"));
        int countDeleted = 0;
        aspects.filter(exactText("0\n" + the("ServAsp"))).shouldBe(size(1));
        String[] aspectNames = aspects.filter(textBegin("0\n")).getTexts();
        for (String aspectName : aspectNames) {
            if (aspectName.equals("")) {continue;}
            aspectName = aspectName.substring(2);
            if (isStandartAspect(aspectName)) {continue;}
            selectAspect(aspectName);
            deleteAspect();
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteAllUserAspects();
        }
    }

}

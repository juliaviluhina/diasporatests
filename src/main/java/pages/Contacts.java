package pages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.conditions.CustomCondition.textEnd;
import static java.lang.Boolean.*;
import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static pages.Aspects.STANDART_ASPECTS;
import static pages.Contact.contact;

public class Contacts {

    public static ElementsCollection aspects = $$(".aspect a");

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
        aspects.filter(textEnd("\n" + aspect)).get(0).click();
        $(".header #aspect_name").shouldHave(exactText(aspect));
    }

    @Step
    public static void deleteAspect() {
        $("#delete_aspect").click();
        confirm(null);
    }

    @Step
    public static void rename(String newName) {
        $("#change_aspect_name").click();
        $("#aspect_name_form #aspect_name").setValue(newName);
        $(By.name("commit")).click();
    }

    @Step
    public static void addLinkedContactForAspect(String aspect, PodUser podUser) {
        selectAspect(aspect);
        contact(podUser).find(".contact_add-to-aspect").click();
    }

    @Step
    public static void deleteLinkedContactForAspect(String aspect, PodUser podUser) {
        selectAspect(aspect);
        contact(podUser).find(".contact_remove-from-aspect").click();
    }

    @Step
    public static void openAllContacts() {
        $(".all_contacts a").click();
    }

    public static int countContactsInAspect(String aspect) {
        return Integer.parseInt(aspects.filter(textEnd("\n" + aspect)).get(0).find(".badge").getText());
    }

    public static void assertCountContactsInAspect(String aspect, int countContacts) {
        aspects.filter(textEnd("\n" + aspect)).get(0).find(".badge").shouldHave(exactText(Integer.toString(countContacts)));
    }

    public static void assertAspectIsShown(String aspect) {
        aspects.filter(textEnd("\n" + aspect)).shouldHave(size(1));
    }

    public static void assertAspectIsNotShown(String aspect) {
        aspects.filter(textEnd("\n" + aspect)).shouldBe(empty);
    }

    private static Boolean isStandartAspect(String aspect) {
        for (String standartAspect : STANDART_ASPECTS) {
            if (standartAspect.equals(aspect)) {
                return TRUE;
            }
        }
        return FALSE;
    }

    public static void deleteAllUserAspects() {
        clearThe();
        addAspect(the("ServAsp"));
        int countDeleted = 0;
        aspects.filter(textEnd("\n" + the("ServAsp"))).shouldBe(size(1));
        String[] aspectNames = aspects.filter(text("\n")).getTexts();
        for (String aspectName : aspectNames) {
            if (aspectName.equals("")) {
                continue;
            }
            aspectName = aspectName.substring(2);
            if (isStandartAspect(aspectName)) {
                continue;
            }
            selectAspect(aspectName);
            deleteAspect();
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteAllUserAspects();
        }
    }

}

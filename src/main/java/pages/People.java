package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.DiasporaAspect;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class People {

    public static SelenideElement manageContact = $(".btn.dropdown-toggle");
    public static ElementsCollection aspects = $$(".aspect_selector");

    @Step
    public static String toggleAspect(DiasporaAspect diasporaAspect) {
        //aspects.get(diasporaAspect.number).hover();
        SelenideElement togglingAspect = aspects.get(diasporaAspect.number);
        togglingAspect.click();
        return togglingAspect.getText();
    }

    public static void assertAspectIsUsed(String text) {
        manageContact.shouldHave(text(text));
    }

    public static void assertAspectsAreNotUsed() {
        assertAspectIsUsed("Add contact");
    }

    public static void assertPerson(String fullName) {
        $("#diaspora_handle").shouldHave(Condition.exactText(fullName));
    }

    protected static Boolean aspectIsUsed(SelenideElement aspect) {
        return aspect.getAttribute("class").contains("selected");
    }

    public static void ensureNoAspectsForContact() {
        if (manageContact.getText().equals("Add contact")) {
            return;
        }
        manageContact.click();
        for (int i = 0; i < aspects.size(); i++) {
            SelenideElement aspect = aspects.get(i);
            if (!aspectIsUsed(aspect)) {
                continue;
            }
            aspect.click();
        }
        $("#diaspora_handle").click();
    }

    public static void ensureAspectForContact(DiasporaAspect diasporaAspect) {
        if (manageContact.getText().equals(diasporaAspect.name)) {
            return;
        }
        manageContact.click();
        for (int i = 0; i < aspects.size(); i++) {
            SelenideElement aspect = aspects.get(i);
            if (i == diasporaAspect.number) {
                if (!aspectIsUsed(aspect)) {
                    aspect.click();
                }
            } else {
                if (aspectIsUsed(aspect)) {
                    aspect.click();
                }
            }
        }
        $("#diaspora_handle").click();
    }

}

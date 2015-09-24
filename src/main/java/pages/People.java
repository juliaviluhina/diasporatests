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
    public static void clickAspect(DiasporaAspect diasporaAspect) {
        aspects.get(diasporaAspect.number).hover();
        statusOfAspect(aspects.get(diasporaAspect.number)).click();
    }

    public static void assertAspectIsUsed(DiasporaAspect diasporaAspect) {
        aspects.filter(cssClass("selected")).shouldHave(texts(diasporaAspect.name));
    }

    public static void assertPerson(String fullName) {
        $("#diaspora_handle").shouldHave(Condition.exactText(fullName));
    }

    protected static SelenideElement statusOfAspect(SelenideElement aspect) {
        return aspect.find(".status_indicator");
    }

    protected static Boolean aspectIsUsed(SelenideElement aspect) {
        return aspect.getAttribute("class").contains("selected");
    }

    public static void ensureAddContact() {
        if (manageContact.getText().equals("Add contact")) {
            return;
        }
        manageContact.click();
        for (int i = 0; i < aspects.size(); i++) {
            SelenideElement aspect = aspects.get(i);
            if (!aspectIsUsed(aspect)) {
                continue;
            }
            statusOfAspect(aspect).click();
        }
        manageContact.click();
        aspects.filter(cssClass("selected")).shouldBe(empty);
    }

}

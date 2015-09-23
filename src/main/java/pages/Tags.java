package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static core.helpers.UniqueDataHelper.the;

public class Tags {

    public static SelenideElement newTag = $("#tags");
    public static ElementsCollection tags = $$("#tags_list .selectable");

    @Step
    public static void deleteAll() {
        add(the("#tagForCleaning"));
        assertExist(the("#tagForCleaning"));
        try {
            while (tags.size() > 0) {
                String tagName = tags.get(0).getText();
                delete(tagName);
                assertNotExist(tagName);
            }
        } catch (IndexOutOfBoundsException e) {}
    }

    @Step
    public static void add(String tagName) {
        newTag.setValue(tagName);
        $$("#as-results-tags li").shouldHave(exactTexts(tagName));
        newTag.pressEnter();
    }

    @Step
    public static void delete(String tagName) {
        tags.find(exactText(tagName)).hover();
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
    }

    public static void assertNotExist(String tagName) {
        tags.filter(exactText(tagName)).shouldBe(empty);
    }

    public static void assertExist(String tagName) {
        tags.filter(exactText(tagName)).shouldHave(size(1));
    }

    public static void assertNthIs(int nth, String tagName) {
        tags.get(nth).shouldHave(exactText(tagName));
    }

}

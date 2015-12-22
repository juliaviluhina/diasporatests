package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.exactTexts;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static core.AdditionalAPI.isVisible;
import static core.AdditionalAPI.scrollToAndHover;
import static core.conditions.CustomCondition.textBegin;
import static core.helpers.UniqueDataHelper.*;
import static steps.Scenarios.*;

public class Tags {

    public static SelenideElement newTag = $("#tags");
    public static ElementsCollection tags = $$("#tags_list  [data-template='tag_following']");

    @Step
    public static void add(String tagName) {
        newTag.setValue(tagName);
        $$("#as-results-tags li").shouldHave(exactTexts(tagName));
        newTag.pressEnter();
    }

    @Step
    public static void add(String... tagNames) {
        for (String tagName : tagNames) {
            add(tagName);
        }
    }

    @Step
    public static void delete(String tagName) {
        scrollToAndHover(tag(tagName));
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
    }

    @Step
    public static void filter(String tagName) {
        tag(tagName).click();
    }

    @Step
    public static void assertNotExist(String tagName) {
        tag(tagName).shouldNotBe(present);
    }

    @Step
    public static void assertExist(String tagName) {
        tag(tagName).shouldBe(visible);
    }

    @Step
    public static void assertTags(String... tagNames) {
        tags.shouldHave(exactTexts(tagNames));
    }

    @Step
    public static void ensureTag(String tagName) {
        NavBar.openTags();
        if (!isVisible(tag(tagName))) {
            add(tagName);
        }

    }

    @Step
    public static void ensureNoTag(String tagName) {
        NavBar.openTags();
        if (isVisible(tag(tagName))) {
            delete(tagName);
        }
    }

    @Step
    public static void ensureNoTags() {
        NavBar.openTags();
        deleteAll();
    }

    private static SelenideElement tag(String tagName) {
        return tags.find(exactText(tagName));
    }

    @Step
    public static void deleteAll() {

        //when tags are more than one page - without this code does not work
        NavBar.openTags();
        waitStreamOpening();

        ElementsCollection userTags = tags.filter(textBegin("#"));
        int countDeleted = 0;

        for (SelenideElement userTag : userTags) {
            delete(userTag);
            countDeleted++;
        }

        if (countDeleted > 1) {
            deleteAll();
        }

    }

    @Step
    private static void delete(SelenideElement tag) {
        scrollToAndHover(tag);
        tag.find(".delete_tag_following").click();
        confirm(null);
    }

}

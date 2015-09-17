package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static core.conditions.CustomCollectionConditions.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static core.helpers.Helpers.listToArray;

public class Stream {

    public static SelenideElement tagsHeader = $("[href=\"/followed_tags\"]");
    public static SelenideElement newTag = $("#tags");
    public static SelenideElement userMenu = $("#user_menu");

    public static ElementsCollection tags = $$("#tags_list .selectable");

    private static List<String> expectedTagNames;
    private static List<String> tagsForCleaning;

    public static String[] expectedTagNames() { return listToArray(expectedTagNames); }

    public static void addTagsForCleaning(String ... tagNames) {
        tagsForCleaning = new ArrayList<String>(Arrays.asList(tagNames));
    }

    public static void cleanAddedData(){
        for (String tagName:tagsForCleaning){
            deleteTag(tagName);
        }
        tagsForCleaning.clear();
    }

    @Step
    public static void expandTags() {
        tagsHeader.click();
        tags.shouldBe(textsLoaded);
        expectedTagNames = new ArrayList<String>(Arrays.asList(tags.getTexts()));
    }

    @Step
    public static void addTag(String tagName) {
        newTag.setValue(tagName);
        $$("#as-results-tags li").shouldHave(exactTexts(tagName));
        newTag.pressEnter();
        expectedTagNames.add(0, tagName);
    }

    @Step
    public static void deleteTag(String tagName) {
        tags.find(exactText(tagName)).hover();
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
        expectedTagNames.remove(tagName);
        tags.shouldBe(textsLoaded);
    }

    @Step
    public static void logOut() {
        $(".user-menu-more-indicator").click();
        userMenu.find("[data-method=\"delete\"]").click();
    }

    public static void assertTagsHeaderIsVisible() {
        tagsHeader.shouldBe(visible);
    }

    public static void assertTagsInOrder(String... tagNames) {
        tags.shouldHave(exactTexts(tagNames));
    }

    public static void assertTags(String... tagNames) {
        tags.shouldBe(textsLoaded);
        tags.shouldHave(exactTextsInAnyOrder(tagNames));
    }

    public static void assertTags() {
        assertTags(listToArray(expectedTagNames));
    }

}

package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.CustomElementsCollection;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.confirm;

import static core.conditions.CustomCollectionConditions.exactTextsInAnyOrder;
import static core.CustomElementsCollection.$$; //option 1
//import static com.codeborne.selenide.Selenide.$$; //option 2
import static core.helpers.Helpers.listToArray;

public class Tags {

    public static SelenideElement tagsHeader = $("[href='/followed_tags']");
    public static SelenideElement newTag = $("#tags");

    public static CustomElementsCollection tags = $$("#tags_list .selectable");//option 1
    //public static ElementsCollection tags = $$("#tags_list .selectable"); //option 2

    private static List<String> expectedTagNames;

    public static String[] expectedTagNames() {
        return listToArray(expectedTagNames);
    }

    @Step
    public static void deleteTags() {
        expandTags();
        String[] tagNames = expectedTagNames();
        for (String tagName : tagNames) {
            deleteTag(tagName);
        }
    }

    @Step
    public static void expandTags() {
        tagsHeader.click();
        $("#aspect_stream_header").shouldHave(exactText(tagsHeader.getText()));
        expectedTagNames = new ArrayList<String>(Arrays.asList(tags.getTexts()));
    }

    @Step
    public static void addTag(String tagName) {
        newTag.setValue(tagName);
        $$("#as-results-tags li").shouldHave(exactTexts(tagName));
        newTag.pressEnter();
        expectedTagNames.add(0, tagName);
        tags.filter(exactText(tagName)).shouldHave(size(1));
    }

    @Step
    public static void deleteTag(String tagName) {
        tags.find(exactText(tagName)).hover();
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
        expectedTagNames.remove(tagName);
        tags.filter(exactText(tagName)).shouldBe(empty);
    }

    public static void assertTagsInOrder(String... tagNames) {
        tags.shouldHave(exactTexts(tagNames));
    }

    public static void assertTags(String... tagNames) {
        tags.shouldHave(exactTextsInAnyOrder(tagNames));
    }

    public static void assertTags() {
        assertTags(listToArray(expectedTagNames));
    }

}

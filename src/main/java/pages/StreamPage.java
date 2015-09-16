package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.empty;
import static core.conditions.CustomCollectionConditions.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;

public class StreamPage {

    public SelenideElement tagsHeader = $("[href=\"/followed_tags\"]");
    public SelenideElement newTag = $("#tags");
    public SelenideElement userMenu = $("#user_menu");

    public ElementsCollection tags = $$("#tags_list .selectable");

    private List<String> expectedTagNames;


    public String[] expectedTagNames() {
        return expectedTagNames.toArray(new String[expectedTagNames.size()]);
    }

    @Step
    public void expandTags() {
        tagsHeader.click();
        newTag.shouldBe(visible); //without its check next checks works not always
        tags.shouldBe(textsLoaded);
        expectedTagNames = new ArrayList<String>(Arrays.asList(tags.getTexts()));
    }

    @Step
    public void addTag(String tagName) {
        newTag.setValue(tagName);
        $$("#as-results-tags li").shouldHave(exactTexts(tagName));//without its check next checks works not always
        newTag.pressEnter();
        expectedTagNames.add(0, tagName);
    }

    @Step
    public void deleteTag(String tagName) {
        tags.find(exactText(tagName)).hover();
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
        expectedTagNames.remove(tagName);
        tags.filter(exactText(tagName)).shouldBe(empty); //without its check next checks works not always
    }

    @Step
    public void logOut() {
        $(".user-menu-more-indicator").click();
        userMenu.find("[data-method=\"delete\"]").click();
    }

    public void assertTagsHeaderIsVisible() {
        tagsHeader.shouldBe(visible);
    }

    public void assertTagsInOrder(String... tagNames) {
        tags.shouldHave(exactTexts(tagNames));
    }

    public void assertTags(String... tagNames) {
        tags.shouldBe(textsLoaded);
        tags.shouldHave(exactTextsInAnyOrder(tagNames));
    }

    public void assertTags() {
        assertTags(expectedTagNames());
    }
}

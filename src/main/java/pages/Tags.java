package pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static core.AdditionalAPI.hover;
import static core.conditions.CustomCondition.textBegin;
import static core.helpers.UniqueDataHelper.deleteUniqueValue;
import static core.helpers.UniqueDataHelper.the;

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
        hover(tags.find(exactText(tagName)));
        $("#unfollow_" + tagName.substring(1)).click();
        confirm(null);
    }

    @Step
    public static void filter(String tagName) {
        tags.find(exactText(tagName)).click();
    }

    @Step
    public static void assertNotExist(String tagName) {
        tags.find(exactText(tagName)).shouldNotBe(present);
    }

    @Step
    public static void assertExist(String tagName) {
        tags.find(exactText(tagName)).shouldBe(visible);
    }

    @Step
    public static void assertTags(String... tagNames) {
        tags.shouldHave(exactTexts(tagNames));
    }

    @Step
    public static void ensureTag(String tagName) {
        NavBar.openTags();
        if (tags.find(exactText(tagName)).is(visible)) {
            return;
        }
        add(tagName);
    }

    @Step
    public static void ensureNoTags() {
        NavBar.openTags();
        deleteAll();
    }


    @Step
    public static void deleteAll() {
        add(the("#stag"));
        assertExist(the("#stag"));

        //when tags are more than one page - without this code does not work
        NavBar.openTags();

        ElementsCollection userTags = tags.filter(textBegin("#"));
        userTags.find(exactText(the("#stag"))).shouldBe(visible); //for wait - tags collection is loaded
        int countDeleted = 0;

        for (SelenideElement userTag : userTags) {
            delete(userTag);
            countDeleted++;
        }

        if (countDeleted > 1) {
            deleteUniqueValue(the("#stag"));
            deleteAll();
        }

    }

    @Step
    private static void delete(SelenideElement tag) {
        hover(tag);
        tag.find(".delete_tag_following").click();
        confirm(null);
    }

}

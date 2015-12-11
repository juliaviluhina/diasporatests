package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.exactTexts;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static core.AdditionalAPI.scrollToAndHover;
import static core.conditions.CustomCondition.textBegin;
import static core.helpers.UniqueDataHelper.*;
import static steps.Scenarios.*;

public class Tags {

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
        scrollToAndHover(tags.find(exactText(tagName)));
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


//    @Step
//    public static void deleteAll() {
//        deleteUniqueData("#stag");
//        add(the("#stag"));
//
//        //when tags are more than one page - without this code does not work
//        NavBar.openTags();
//
//        ElementsCollection userTags = tags.filter(textBegin("#"));
//        userTags.find(exactText(the("#stag"))).shouldBe(visible); //for wait - tags collection is loaded
//        int countDeleted = 0;
//
//        for (SelenideElement userTag : userTags) {
//            delete(userTag);
//            countDeleted++;
//        }
//
//        if (countDeleted > 1) {
//            deleteAll();
//        }
//
//    }

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

    public static SelenideElement newTag = $("#tags");
    public static ElementsCollection tags = $$("#tags_list  [data-template='tag_following']");

    @Step
    private static void delete(SelenideElement tag) {
        scrollToAndHover(tag);
        tag.find(".delete_tag_following").click();
        confirm(null);
    }

}

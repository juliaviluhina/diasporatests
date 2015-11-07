package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.AdditionalAPI.scrollToAndHover;
import static core.helpers.UniqueDataHelper.clearUniqueData;
import static core.helpers.UniqueDataHelper.deleteUniqueValue;
import static core.helpers.UniqueDataHelper.the;
import static core.conditions.CustomCondition.*;

public class Feed {

    private static SelenideElement aspect = $(".aspect_dropdown");
    public static SelenideElement newPostText = $("#status_message_fake_text");
    public static SelenideElement setAspect = $(".aspect_dropdown .btn");
    public static SelenideElement share = $("#submit");

    // optimized speed via using xpath - see post() and comment()
    //public static ElementsCollection posts = $$(".stream_element");

    @Step
    public static void addPublicPost(String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensurePublicPostingMode();
        share.click();
    }

    @Step
    public static void addPublicPostWithMentionAbout(PodUser podUser, String text) {
        newPostText.click();

        newPostText.sendKeys(text + " @" + podUser.userName);
        ElementsCollection dropdownMenuItemsForMention = $$(".mentions-autocomplete-list li");
        dropdownMenuItemsForMention.find(exactText(podUser.fullName)).click();

        ensurePublicPostingMode();
        share.click();
    }


    @Step
    public static void addPrivatePost(String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensurePrivatePostingMode();
        share.click();
    }

    @Step
    public static void addAllAspectsPost(String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensureAllAspectsPostingMode();
        share.click();
    }

    @Step
    public static void addAspectPost(String diasporaAspect, String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensureAspectPostingMode(diasporaAspect);
        share.click();
    }

    @Step
    public static void deletePost(PodUser from, String postText) {
        deletePost(post(from, postText));
    }

    @Step
    public static void hidePost(PodUser from, String postText) {
        SelenideElement post = post(from, postText);
        scrollToAndHover(post);
        post.find(".hide_post").click();
        confirm(null);
    }

    @Step
    public static void ignoreAuthorOfPost(PodUser author, String postText) {
        SelenideElement post = post(author, postText);
        scrollToAndHover(post);
        post.find(".block_user").click();
        confirm(null);
    }

    @Step
    public static void toggleLike(PodUser from, String post) {
        post(from, post).find(".like").click();
    }

    @Step
    public static void addComment(PodUser from, String postText, String comment) {
        SelenideElement post = post(from, postText);
        post.find(".focus_comment_textarea").click();
        post.find(".comment_box").setValue(comment);
        post.find(".new_comment").find(By.name("commit")).click();
    }

    @Step
    public static void reshare(PodUser from, String post) {
        post(from, post).find(".reshare").click();
        confirm(null);
    }

    @Step
    public static void deleteComment(PodUser fromPost, String postText, PodUser fromComment, String commentText) {
        SelenideElement comment = comment(fromPost, postText, fromComment, commentText);
        scrollToAndHover(comment);
        comment.find(".delete").click();
        confirm(null);
    }

    @Step
    public static void assertAspectForNewPost(String aspectName) {
        newPostText.click();

        setAspect.click();
        aspect.findAll(".aspect_selector").find(text(aspectName)).shouldBe(visible);

        newPostText.click();
    }

    @Step
    public static void assertNoAspectForNewPost(String aspectName) {
        newPostText.click();

        setAspect.click();
        aspect.findAll(".aspect_selector").filter(text(aspectName)).shouldBe(empty);

        newPostText.click();
    }

    @Step
    public static void assertComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        comment(fromPost, post, fromComment, comment).shouldBe(visible);
    }

    @Step
    public static void assertCommentCanNotBeDeleted(PodUser fromPost, String postText, PodUser fromComment, String commentText) {
        SelenideElement comment = comment(fromPost, postText, fromComment, commentText);
        scrollToAndHover(comment);
        comment.find(".delete").shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeDeleted(PodUser fromPost, String postText) {
        SelenideElement post = post(fromPost, postText);
        scrollToAndHover(post);
        post.findAll(".remove_post").shouldBe(empty);
    }

    @Step
    public static void assertNoComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        comment(fromPost, post, fromComment, comment).shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeReshared(PodUser from, String postText) {
        //posts.filter(textBeginAndContain(from.fullName, postText)).filter(cssClass("reshare")).shouldBe(empty);
        post(from, postText).find(".reshare").shouldNotBe(present);
    }

    @Step
    public static void assertLikes(PodUser from, String post, int countLikes) {
        post(from, post).find(".expand_likes").shouldHave(text(Integer.toString(countLikes)));
    }

    @Step
    public static void assertNoLikes(PodUser from, String post) {
        post(from, post).findAll(".expand_likes").shouldBe(empty);
    }

    @Step
    public static void assertPostFrom(PodUser from, String postText) {
        post(from, postText).shouldBe(visible);
    }

    @Step
    public static void assertNoPostFrom(PodUser from, String postText) {
        //posts.filter(textBeginAndContain(from.fullName, postText)).shouldBe(empty);
        post(from, postText).shouldNotBe(present);
    }

    @Step
    private static SelenideElement comment(PodUser fromPost, String postText, PodUser fromComment, String commentText) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return comment(post(fromPost, postText), fromComment, commentText);
        return $(By.xpath(String.format("//div[contains(@class, 'comment media')][contains(., '%s') ][descendant::*[contains(@class, 'author-name') and contains(text(), '%s')]][ancestor::*[contains(.,'%s') and descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]]",
                commentText, fromComment, postText, fromPost )));

    }


    @Step
    private static SelenideElement post(PodUser from, String post) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return posts.find(textBeginAndContain(from.fullName, post));
        return $(By.xpath(String.format("//*[contains(@class, 'stream_element')][contains(., '%s')][descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]", post, from.fullName)));
    }

    @Step
    private static void deletePost(SelenideElement post) {
        scrollToAndHover(post);
        post.find(".remove_post").click();
        confirm(null);
    }

    @Step
    public static void ensurePublicPostingMode() {
        if (aspect.getText().contains("Public")) {
            return;
        }
        setAspect.click();
        aspect.find(".public").click();
        setAspect.shouldHave(Condition.text("Public"));
    }


    @Step
    public static void ensurePrivatePostingMode() {
        if (aspect.getText().contains("Select aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
        setAspect.click();
        ElementsCollection aspects = aspect.findAll(".aspect_selector");
        aspects.get(0).click();
        String[] selectedAspectstext = aspects.filter(cssClass("selected")).getTexts();
        for (String selectedAspectTest : selectedAspectstext) {
            aspects.find(exactText(selectedAspectTest)).click();
        }
    }

    @Step
    public static void ensureAllAspectsPostingMode() {
        if (aspect.getText().contains("All aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
    }

    @Step
    public static void ensureAspectPostingMode(String diasporaAspect) {
        ensureAllAspectsPostingMode();
        setAspect.click();
        SelenideElement selectingAspect = aspect.findAll(".aspect_selector").find(text(diasporaAspect));
        selectingAspect.click();
        setAspect.shouldHave(Condition.text(selectingAspect.getText()));
        setAspect.click();
    }

    @Step
    public static void deleteAllPosts(PodUser from) {
        clearUniqueData();
        addPublicPost(the("servicepost"));
        assertPostFrom(from, the("servicepost"));
        int countDeleted = 0;
        ElementsCollection userPosts = $$(".stream_element").filter(textBegin(from.fullName));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteUniqueValue(the("servicepost"));
            deleteAllPosts(from);
        }
    }

}

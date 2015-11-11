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
    public static void deletePost(PodUser author, String text) {
        deletePost(post(author, text));
    }

    @Step
    public static void hidePost(PodUser author, String text) {
        SelenideElement post = post(author, text);
        scrollToAndHover(post);
        post.find(".hide_post").click();
        confirm(null);
    }

    @Step
    public static void ignoreAuthorOfPost(PodUser author, String text) {
        SelenideElement post = post(author, text);
        scrollToAndHover(post);
        post.find(".block_user").click();
        confirm(null);
    }

    @Step
    public static void toggleLikePost(PodUser author, String text) {
        post(author, text).find(".like").click();
    }

    @Step
    public static void addComment(PodUser postAuthor, String postText, String commentText) {
        SelenideElement post = post(postAuthor, postText);
        post.find(".focus_comment_textarea").click();
        post.find(".comment_box").setValue(commentText);
        post.find(".new_comment").find(By.name("commit")).click();
    }

    @Step
    public static void resharePost(PodUser author, String text) {
        post(author, text).find(".reshare").click();
        confirm(null);
    }

    @Step
    public static void deleteComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        SelenideElement comment = comment(postAuthor, postText, commentAuthor, commentText);
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
    public static void assertComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        comment(postAuthor, postText, commentAuthor, commentText).shouldBe(visible);
    }

    @Step
    public static void assertCommentCanNotBeDeleted(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        SelenideElement comment = comment(postAuthor, postText, commentAuthor, commentText);
        scrollToAndHover(comment);
        comment.find(".delete").shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeDeleted(PodUser author, String text) {
        SelenideElement post = post(author, text);
        scrollToAndHover(post);
        post.findAll(".remove_post").shouldBe(empty);
    }

    @Step
    public static void assertNoComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        comment(postAuthor, postText, commentAuthor, commentText).shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeReshared(PodUser author, String text) {
        post(author, text).find(".reshare").shouldNotBe(present);
    }

    @Step
    public static void assertLikes(PodUser postAuthor, String postText, int countLikes) {
        post(postAuthor, postText).find(".expand_likes").shouldHave(text(Integer.toString(countLikes)));
    }

    @Step
    public static void assertNoLikes(PodUser postAuthor, String postText) {
        post(postAuthor, postText).findAll(".expand_likes").shouldBe(empty);
    }

    @Step
    public static void assertPost(PodUser author, String text) {
        post(author, text).shouldBe(visible);
    }

    @Step
    public static void assertNoPost(PodUser author, String text) {
        post(author, text).shouldNotBe(present);
    }

    @Step
    private static SelenideElement comment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return comment(post(postAuthor, postText), commentAuthor, commentText);
        return $(By.xpath(String.format("//div[contains(@class, 'comment media')][contains(., '%s') ][descendant::*[contains(@class, 'author-name') and contains(text(), '%s')]][ancestor::*[contains(.,'%s') and descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]]",
                commentText, commentAuthor, postText, postAuthor )));

    }


    @Step
    private static SelenideElement post(PodUser author, String text) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return posts.find(textBeginAndContain(author.fullName, text));
        return $(By.xpath(String.format("//*[contains(@class, 'stream_element')][contains(., '%s')][descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]", text, author.fullName)));
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
    public static void deleteAllPosts(PodUser author) {
        clearUniqueData();
        addPublicPost(the("servicepost"));
        assertPost(author, the("servicepost"));
        int countDeleted = 0;
        ElementsCollection userPosts = $$(".stream_element").filter(textBegin(author.fullName));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteUniqueValue(the("servicepost"));
            deleteAllPosts(author);
        }
    }

}

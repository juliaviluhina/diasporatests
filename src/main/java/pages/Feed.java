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
import static core.conditions.CustomCondition.*;
import static core.helpers.UniqueDataHelper.*;
import static steps.Scenarios.*;

public class Feed {

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
    private static void deletePost(SelenideElement post) {
        scrollToAndHover(post);
        removePostButton(post).click();
        confirm(null);
    }

    @Step
    public static void hidePost(PodUser author, String text) {
        SelenideElement post = post(author, text);
        scrollToAndHover(post);
        post.find(".hide_post").click();
        confirm(null);
    }

    @Step
    public static void toggleLikePost(PodUser author, String text) {
        post(author, text).find(".like").click();
    }

    @Step
    public static void resharePost(PodUser author, String text) {
        resharePostButton(author, text).click();
        confirm(null);
    }

    @Step
    public static void addComment(PodUser postAuthor, String postText, String commentText) {
        SelenideElement post = post(postAuthor, postText);
        post.find(".focus_comment_textarea").click();
        post.find(".comment_box").setValue(commentText);
        post.find(".new_comment").find(By.name("commit")).click();
    }

    @Step
    public static void deleteComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        SelenideElement comment = comment(postAuthor, postText, commentAuthor, commentText);
        scrollToAndHover(comment);
        deleteCommentButton(comment).click();
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
    public static void assertPost(PodUser author, String text) {
        post(author, text).shouldBe(visible);
    }

    @Step
    public static void assertNoPost(PodUser author, String text) {
        post(author, text).shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeDeleted(PodUser author, String text) {
        SelenideElement post = post(author, text);
        scrollToAndHover(post);
        removePostButton(post).shouldNotBe(present);
    }

    @Step
    public static void assertPostCanNotBeReshared(PodUser author, String text) {
        resharePostButton(author, text).shouldNotBe(present);
    }

    @Step
    public static void assertLikes(PodUser postAuthor, String postText, int countLikes) {
        likesCounterForPost(postAuthor, postText).shouldHave(text(Integer.toString(countLikes)));
    }

    @Step
    public static void assertNoLikes(PodUser postAuthor, String postText) {
        likesCounterForPost(postAuthor, postText).shouldNotBe(present);
    }

    @Step
    public static void assertComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        comment(postAuthor, postText, commentAuthor, commentText).shouldBe(visible);
    }

    @Step
    public static void assertNoComment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        comment(postAuthor, postText, commentAuthor, commentText).shouldNotBe(present);
    }

    @Step
    public static void assertCommentCanNotBeDeleted(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        SelenideElement comment = comment(postAuthor, postText, commentAuthor, commentText);
        scrollToAndHover(comment);
        deleteCommentButton(comment).shouldNotBe(present);
    }

    @Step
    public static void assertAspectForNewPost(String aspectName) {
        newPostText.click();

        setAspect.click();
        aspects().find(text(aspectName)).shouldBe(visible);

        newPostText.click();
    }

    @Step
    public static void assertNoAspectForNewPost(String aspectName) {
        newPostText.click();

        setAspect.click();
        aspects().filter(text(aspectName)).shouldBe(empty);

        newPostText.click();
    }

    @Step
    public static void ensureAspectPost(PodUser author, String diasporaAspect, String text) {
        waitStreamOpening();
        if (post(author, text).is(visible)) {
            return;
        }
        addAspectPost(diasporaAspect, text);
    }

    @Step
    public static void ensurePublicPost(PodUser author, String text) {
        waitStreamOpening();
        if (post(author, text).is(visible)) {
            return;
        }
        addPublicPost(text);
    }

    @Step
    public static void ensureCommentForPost(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        waitStreamOpening();
        if (comment(postAuthor, postText, commentAuthor, commentText).is(visible)) {
            return;
        }
        addComment(postAuthor, postText, commentText);
    }


    @Step
    public static void deleteAllPosts(PodUser author) {
        clearUniqueData();
        addPublicPost(newThe("servicepost"));
        assertPost(author, the("servicepost"));
        int countDeleted = 0;
        ElementsCollection userPosts = $$(".stream_element").filter(textBegin(author.fullName));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteAllPosts(author);
        }
    }


    private static SelenideElement aspect = $(".aspect_dropdown");
    public static SelenideElement newPostText = $("#status_message_fake_text");
    public static SelenideElement setAspect = $(".aspect_dropdown .btn");
    public static SelenideElement share = $("#submit");

    // optimized speed via using xpath - see post() and comment()
    //public static ElementsCollection posts = $$(".stream_element");

    @Step
    private static SelenideElement post(PodUser author, String text) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return posts.find(textBeginAndContain(author.fullName, text));
        return $(By.xpath(String.format("//*[contains(@class, 'stream_element')][contains(., '%s')][descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]", text, author.fullName)));
    }

    @Step
    private static SelenideElement comment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return comment(post(postAuthor, postText), commentAuthor, commentText);
        return $(By.xpath(String.format("//div[contains(@class, 'comment media')][contains(., '%s') ][descendant::*[contains(@class, 'author-name') and contains(text(), '%s')]][ancestor::*[contains(.,'%s') and descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]]",
                commentText, commentAuthor, postText, postAuthor)));

    }

    private static ElementsCollection aspects() {
        return aspect.findAll(".aspect_selector");
    }

    private static SelenideElement likesCounterForPost(PodUser postAuthor, String postText) {
        return post(postAuthor, postText).find(".expand_likes");
    }

    private static SelenideElement removePostButton(SelenideElement post) {
        return post.find(".remove_post");
    }

    private static SelenideElement resharePostButton(PodUser postAuthor, String postText) {
        return post(postAuthor, postText).find(".reshare");
    }

    private static SelenideElement deleteCommentButton(SelenideElement comment) {
        return comment.find(".delete");
    }

    @Step
    private static void ensurePublicPostingMode() {
        if (aspect.getText().contains("Public")) {
            return;
        }
        setAspect.click();
        aspect.find(".public").click();
        setAspect.shouldHave(Condition.text("Public"));
    }

    @Step
    private static void ensurePrivatePostingMode() {
        if (aspect.getText().contains("Select aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
        setAspect.click();
        aspects().get(0).click();
        String[] selectedAspectstext = aspects().filter(cssClass("selected")).getTexts();
        for (String selectedAspectTest : selectedAspectstext) {
            aspects().find(exactText(selectedAspectTest)).click();
        }
    }

    @Step
    private static void ensureAllAspectsPostingMode() {
        if (aspect.getText().contains("All aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
    }

    @Step
    private static void ensureAspectPostingMode(String diasporaAspect) {
        ensureAllAspectsPostingMode();
        setAspect.click();
        SelenideElement selectingAspect = aspects().find(text(diasporaAspect));
        selectingAspect.click();
        setAspect.shouldHave(Condition.text(selectingAspect.getText()));
        setAspect.click();
    }

}

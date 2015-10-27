package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.internal.Coordinates;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.helpers.UniqueDataHelper.clearThe;
import static core.helpers.UniqueDataHelper.the;
import static core.conditions.CustomCondition.*;

public class Feed {

    protected static SelenideElement aspect = $(".aspect_dropdown");
    public static SelenideElement newPostText = $("#status_message_fake_text");
    public static SelenideElement setAspect = $(".aspect_dropdown .btn");
    public static SelenideElement share = $("#submit");

    public static ElementsCollection posts = $$(".stream_element");

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
        dropdownMenuItemsForMention.filter(exactText(podUser.fullName)).shouldHave(size(1)).get(0).click();

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
        deletePost(assertPostFrom(from, postText));
    }

    @Step
    public static void hidePost(PodUser from, String postText) {
        SelenideElement post = assertPostFrom(from, postText);
        hoverPost(post);
        post.find(".hide_post").click();
        confirm(null);
    }

    @Step
    public static void ignoreAuthorOfPost(PodUser author, String postText) {
        SelenideElement post = assertPostFrom(author, postText);
        hoverPost(post);
        post.find(".block_user").click();
        confirm(null);
    }

    @Step
    public static void toggleLike(PodUser from, String post) {
        assertPostFrom(from, post).find(".like").click();
    }

    @Step
    public static void addComment(PodUser from, String post, String comment) {
        SelenideElement currentPost = assertPostFrom(from, post);
        currentPost.find(".focus_comment_textarea").click();
        currentPost.find(".comment_box").setValue(comment);
        currentPost.find(".new_comment").find(By.name("commit")).click();
    }

    @Step
    public static void reshare(PodUser from, String post) {
        assertPostFrom(from, post).find(".reshare").click();
        confirm(null);
    }

    @Step
    public static void deleteComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        SelenideElement currentComment = commentsByFilter(fromPost, post, fromComment, comment).get(0);
        Coordinates coordinates = currentComment.getCoordinates();
        coordinates.inViewPort();
        currentComment.hover();
        currentComment.find(".delete").click();
        confirm(null);
    }

    @Step
    public static void assertComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        commentsByFilter(fromPost, post, fromComment, comment).shouldHave(size(1));
    }

    @Step
    public static void assertCommentCanNotBeDeleted(PodUser fromPost, String post, PodUser fromComment, String comment) {
        SelenideElement currentComment = commentsByFilter(fromPost, post, fromComment, comment).shouldHave(size(1)).get(0);
        Coordinates coordinates = currentComment.getCoordinates();
        coordinates.inViewPort();
        currentComment.hover();
        currentComment.findAll(".delete").shouldBe(empty);
    }

    @Step
    public static void assertPostCanNotBeDeleted(PodUser fromPost, String post) {
        SelenideElement currentPost = assertPostFrom(fromPost, post);
        Coordinates coordinates = currentPost.getCoordinates();
        coordinates.inViewPort();
        currentPost.find(".post-content").hover();
        currentPost.findAll(".remove_post").shouldBe(empty);
    }

    @Step
    public static void assertNoComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        commentsByFilter(fromPost, post, fromComment, comment).shouldBe(empty);
    }

    @Step
    public static void assertPostCanNotBeReshared(PodUser from, String post) {
        posts.filter(textBeginAndContain(from.fullName, post)).filter(cssClass("reshare")).shouldBe(empty);
    }

    @Step
    public static void assertLikes(PodUser from, String post, int countLikes) {
        assertPostFrom(from, post).find(".expand_likes").shouldHave(text(Integer.toString(countLikes)));
    }

    @Step
    public static void assertNoLikes(PodUser from, String post) {
        assertPostFrom(from, post).findAll(".expand_likes").shouldBe(empty);
    }

    @Step
    public static void assertNthPostIs(int nth, PodUser from, String post) {
        posts.get(nth).shouldHave(textBegin(from.fullName)).shouldHave(text(post));
    }

    @Step
    protected static ElementsCollection commentsByFilter(PodUser fromPost, String post, PodUser fromComment, String comment) {
        SelenideElement currentPost = assertPostFrom(fromPost, post);
        ElementsCollection comments = currentPost.findAll(".comment");
        return comments.filter(textBeginAndContain(fromComment.fullName, comment));
    }

    @Step
    public static SelenideElement assertPostFrom(PodUser from, String post) {
        return posts.filter(textBeginAndContain(from.fullName, post)).shouldHave(size(1)).get(0);
    }

    @Step
    public static void assertCountPosts(int count) {
        posts.shouldHave(size(count));
    }

    @Step
    public static void assertNoPostFrom(PodUser from, String post) {
        posts.filter(textBeginAndContain(from.fullName, post)).shouldBe(empty);
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
        for (String selectedAspectTest:selectedAspectstext) {
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
        clearThe();
        addPublicPost(the("servicepost"));
        assertNthPostIs(0, from, the("servicepost"));
        int countDeleted = 0;
        ElementsCollection userPosts = posts.filter(textBegin(from.fullName));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteAllPosts(from);
        }
    }

    private static void deletePost(SelenideElement post) {
        hoverPost(post);
        post.find(".remove_post").click();
        confirm(null);
    }

    private static void hoverPost(SelenideElement post) {
        Coordinates coordinates = post.getCoordinates();
        coordinates.inViewPort();
        post.find(".post-content").hover();
    }

}

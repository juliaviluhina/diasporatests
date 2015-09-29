package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.DiasporaAspect;
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
    public static void addAspectPost(DiasporaAspect diasporaAspect, String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensureAspectPostingMode(diasporaAspect);
        share.click();
    }

    @Step
    public static void deletePost(PodUser from, String postText) {
        deletePost(assertPostIsShown(from, postText));
    }

    public static void deletePost(SelenideElement post) {
        Coordinates coordinates = post.getCoordinates();
        coordinates.inViewPort();
        post.find(".post-content").hover();
        post.find(".remove_post").click();
        confirm(null);
    }

    @Step
    public static void toggleLike(PodUser from, String post) {
        assertPostIsShown(from, post).find(".like").click();
    }

    @Step
    public static void addComment(PodUser from, String post, String comment) {
        SelenideElement currentPost = assertPostIsShown(from, post);
        currentPost.find(".focus_comment_textarea").click();
        currentPost.find(".comment_box").setValue(comment);
        currentPost.find(".new_comment").find(By.name("commit")).click();
    }

    @Step
    public static void reshare(PodUser from, String post) {
        assertPostIsShown(from, post).find(".reshare").click();
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

    public static void assertComment(PodUser fromPost, String post, PodUser fromComment, String comment) {
        commentsByFilter(fromPost, post, fromComment, comment).shouldHave(size(1));
    }

    public static void assertCommentCanNotBeDeleted(PodUser fromPost, String post, PodUser fromComment, String comment) {
        SelenideElement currentComment = commentsByFilter(fromPost, post, fromComment, comment).shouldHave(size(1)).get(0);
        Coordinates coordinates = currentComment.getCoordinates();
        coordinates.inViewPort();
        currentComment.hover();
        currentComment.findAll(".delete").shouldBe(empty);
    }

    public static void assertPostCanNotBeDeleted(PodUser fromPost, String post) {
        SelenideElement currentPost = assertPostIsShown(fromPost, post);
        Coordinates coordinates = currentPost.getCoordinates();
        coordinates.inViewPort();
        currentPost.find(".post-content").hover();
        currentPost.findAll(".remove_post").shouldBe(empty);
    }

    public static void assertCommentIsNotExist(PodUser fromPost, String post, PodUser fromComment, String comment) {
        commentsByFilter(fromPost, post, fromComment, comment).shouldBe(empty);
    }

    public static void assertReshareIsImpossible(PodUser from, String post) {
        postsByFilter(from, post).filter(cssClass("reshare")).shouldBe(empty);
    }

    public static void assertLikes(PodUser from, String post, int countLikes) {
        assertPostIsShown(from, post).find(".expand_likes").shouldHave(text(Integer.toString(countLikes)));
    }

    public static void assertNthPostIs(int nth, PodUser from, String post) {
        posts.get(nth).shouldHave(textBegin(from.fullName)).shouldHave(text(post));
    }

    protected static ElementsCollection postsByFilter(PodUser from, String post) {
        //return  posts.filter(textBegin(from.fullName)).filter(text(post));
        return posts.filter(textBeginAndContain(from.fullName, post));
    }

    protected static ElementsCollection commentsByFilter(PodUser fromPost, String post, PodUser fromComment, String comment) {
        SelenideElement currentPost = assertPostIsShown(fromPost, post);
        ElementsCollection comments = currentPost.findAll(".comment");
        return comments.filter(textBeginAndContain(fromComment.fullName, comment));
    }

    public static SelenideElement assertPostIsShown(PodUser from, String post) {
        return postsByFilter(from, post).shouldHave(size(1)).get(0);
    }

    public static void assertPostIsNotShown(PodUser from, String post) {
        postsByFilter(from, post).shouldBe(empty);
    }

    public static void ensurePublicPostingMode() {
        if (aspect.getText().contains("Public")) {
            return;
        }
        setAspect.click();
        aspect.find(".public").click();
        setAspect.shouldHave(Condition.text("Public"));
    }

    protected static Boolean aspectIsUsed(SelenideElement aspect) {
        return aspect.getAttribute("class").contains("selected");
    }

    public static void ensurePrivatePostingMode() {
        if (aspect.getText().contains("Select aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
        setAspect.click();
        ElementsCollection aspects = aspect.findAll(".aspect_selector");
        aspects.get(0).click();
        for (SelenideElement aspect:aspects) {
            if (aspectIsUsed(aspect)) {aspect.click();}
        }
    }

    public static void ensureAllAspectsPostingMode() {
        if (aspect.getText().contains("All aspects")) {
            return;
        }
        setAspect.click();
        aspect.find(".all_aspects").click();
    }

    public static void ensureAspectPostingMode(DiasporaAspect diasporaAspect) {
        if (!aspect.getText().contains("All aspects")) {
            setAspect.click();
            aspect.find(".all_aspects").click();
        }
        setAspect.click();
        SelenideElement selectingAspect = aspect.findAll(".aspect_selector").get(diasporaAspect.number);
        selectingAspect.click();
        setAspect.shouldHave(Condition.text(selectingAspect.getText()));
        setAspect.click();
    }

    public static void deleteAllPosts(PodUser from) {
        addPublicPost(the("servicepost"));
        assertNthPostIs(0, from, the("servicepost"));
        ElementsCollection userPosts = posts.filter(textBegin(from.fullName));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
        }
        addPublicPost(the("servicepost"));
        assertNthPostIs(0, from, the("servicepost"));
        ElementsCollection userPosts1 = posts.filter(textBegin(from.fullName));
        if (userPosts1.size() == 1) {
            deletePost(userPosts1.get(0));
        } else {
            deleteAllPosts(from);
        }
    }


}

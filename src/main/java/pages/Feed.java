package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.AdditionalAPI.*;
import static core.conditions.CustomCondition.*;
import static core.helpers.UniqueDataHelper.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static steps.Scenarios.*;

public class Feed {

    public static SelenideElement newPostText = $("#status_message_fake_text");
    public static SelenideElement setAspect = $(".aspect_dropdown .btn");
    public static SelenideElement share = $("#submit");
    public static ElementsCollection posts = $$(".stream_element");

    private static SelenideElement aspect = $(".aspect_dropdown");

    @Step
    public static void addPublicPost(String text) {
        ensureAddPostMode();
        newPostText.click();
        newPostText.setValue(text);

        ensurePublicPostingMode();
        share.click();
    }

    @Step
    public static void addPublicPostWithMentionAbout(PodUser podUser, String text) {
        ensureAddPostMode();
        newPostText.click();
        newPostText.sendKeys(text + " @" + podUser.userName);
        ElementsCollection dropdownMenuItemsForMention = $$(".mentions-autocomplete-list li");
        dropdownMenuItemsForMention.find(exactText(podUser.fullName)).click();

        ensurePublicPostingMode();
        share.click();
    }


    @Step
    public static void addPrivatePost(String text) {
        ensureAddPostMode();

        newPostText.click();
        newPostText.setValue(text);

        ensurePrivatePostingMode();
        share.click();
    }

    @Step
    public static void addAllAspectsPost(String text) {
        ensureAddPostMode();

        newPostText.click();
        newPostText.setValue(text);

        ensureAllAspectsPostingMode();
        share.click();
    }

    @Step
    public static void addAspectPost(String diasporaAspect, String text) {
        ensureAddPostMode();

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
        likeUnlike(post(author, text)).click();
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
    public static void assertLike(PodUser postAuthor, String postText, PodUser likedUser) {
        SelenideElement post = post(postAuthor, postText);
        likes(post).click();
        likeFromUser(post, likedUser).shouldBe(visible);
    }

    @Step
    public static void assertNoLike(PodUser postAuthor, String postText, PodUser likedUser) {
        SelenideElement post = post(postAuthor, postText);
        post.shouldBe(visible);
        if (likes(post).is(visible)) {
            likes(post).click();
            likeFromUser(post, likedUser).shouldNotBe(present);
        }
    }

    @Step
    public static void assertLikeOfCurrentUser(PodUser postAuthor, String postText) {
        likeUnlike(post(postAuthor, postText)).shouldBe(exactText("Unlike"));
    }

    @Step
    public static void assertNoLikeOfCurrentUser(PodUser postAuthor, String postText) {
        likeUnlike(post(postAuthor, postText)).shouldBe(exactText("Like"));
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
        assertPost(author, text);
    }

    @Step
    public static void ensureAspectPostIsNotHidden(PodUser author, String diasporaAspect, String text) {
        //addition post from scratch is the only known way when post will be shown
        waitStreamOpening();
        SelenideElement post = post(author, text);
        if (post.is(visible)) {
            deletePost(post);
        }
        addAspectPost(diasporaAspect, text);
        assertPost(author, text);
    }

    @Step
    public static void ensurePublicPostIsNotHidden(PodUser author, String text) {
        //addition post from scratch is the only known way when post will be shown
        waitStreamOpening();
        SelenideElement post = post(author, text);
        if (post.is(visible)) {
            deletePost(post);
        }
        addPublicPost(text);
        assertPost(author, text);
    }

    @Step
    public static void ensurePublicPost(PodUser author, String text) {
        waitStreamOpening();
        if (post(author, text).is(visible)) {
            return;
        }
        addPublicPost(text);
        assertPost(author, text);
    }

    @Step
    public static void ensurePublicPostWithMention(PodUser author, PodUser about, String text) {
        waitStreamOpening();
        if (post(author, text).is(visible)) {
            return;
        }
        addPublicPostWithMentionAbout(about, text);
        assertPost(author, text);
    }

    @Step
    public static void ensureNoPost(PodUser author, String text) {
        waitStreamOpening();
        if (post(author, text).is(visible)) {
            deletePost(author, text);
            waitStreamOpening();//without this wait next check is unstable
            assertNoPost(author, text);
        }
    }

    @Step
    public static void ensureCommentForPost(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        waitStreamOpening();
        if (comment(postAuthor, postText, commentAuthor, commentText).is(visible)) {
            return;
        }
        addComment(postAuthor, postText, commentText);
        assertComment(postAuthor, postText, commentAuthor, commentText);
    }

    @Step
    public static void ensureNoCommentForPost(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        waitStreamOpening();
        if (comment(postAuthor, postText, commentAuthor, commentText).is(visible)) {
            deleteComment(postAuthor, postText, commentAuthor, commentText);
            assertNoComment(postAuthor, postText, commentAuthor, commentText);
            return;
        }
    }

    @Step
    public static void ensureResharePublicPost(PodUser postAuthor, String postText, PodUser reshareAuthor) {
        Menu.openStream();
        waitStreamOpening();
        if (post(reshareAuthor, postText).is(visible)) {
            return;
        }
        Menu.search(postAuthor.fullName);
        resharePost(postAuthor, postText);
        Menu.openStream();
        assertPost(reshareAuthor, postText);
    }

    @Step
    public static void ensureLike(PodUser postAuthor, String postText) {
        SelenideElement likeUnlike = likeUnlike(post(postAuthor, postText));
        if (likeUnlike.getText().equals("Like"))
            likeUnlike.click();
        likeUnlike.shouldBe(exactText("Unlike"));
    }

    @Step
    public static void ensureNoLike(PodUser postAuthor, String postText) {
        SelenideElement likeUnlike = likeUnlike(post(postAuthor, postText));
        if (likeUnlike.getText().equals("Unlike"))
            likeUnlike.click();
        likeUnlike.shouldBe(exactText("Like"));
    }

//    @Step
//    public static void deleteAllPosts(PodUser author) {
//        deleteUniqueData("servicepost");
//        addPublicPost(the("servicepost"));
//        assertPost(author, the("servicepost"));
//        int countDeleted = 0;
//        ElementsCollection userPosts = $$(".stream_element").filter(textBegin(author.fullName));
//        for (SelenideElement userPost : userPosts) {
//            deletePost(userPost);
//            countDeleted++;
//        }
//        if (countDeleted > 1) {
//            deleteAllPosts(author);
//        }
//    }

    @Step
    public static void deleteAllPosts(PodUser author, String text) {

        Menu.openStream();
        waitStreamOpening();
        int countDeleted = 0;
        ElementsCollection userPosts = $$(".stream_element").filter(textBeginAndContain(author.fullName, text));
        for (SelenideElement userPost : userPosts) {
            deletePost(userPost);
            countDeleted++;
        }
        if (countDeleted > 1) {
            deleteAllPosts(author, text);
        }
    }

    @Step
    private static SelenideElement post(PodUser author, String text) {
        //return posts.find(textBeginAndContain(author.fullName, text));
        // optimized speed via using ugly but efficient xpath
        // waiting for fix in Selenide, to switch back to "readable" solution
        //return posts.find(textBeginAndContain(author.fullName, text));
        return $(By.xpath(String.format("//*[contains(@class, 'stream_element')][contains(., '%s')][descendant::*[contains(@class, 'author-name')][1][contains(text(), '%s')]]", text, author.fullName)));

    }

    @Step
    private static SelenideElement comment(PodUser postAuthor, String postText, PodUser commentAuthor, String commentText) {
        return post(postAuthor, postText).findAll(".comment").find(textBeginAndContain(commentAuthor.fullName, commentText));
    }

    private static SelenideElement likeUnlike(SelenideElement post) {
        return post.find(".like");
    }

    private static SelenideElement likes(SelenideElement post) {
        return post.find(".expand_likes");
    }

    private static SelenideElement likeFromUser(SelenideElement post, PodUser likedUser) {
        return post.find(String.format(".avatar.micro[alt='%s']", likedUser.fullName));
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

    //method added because of problem with appearing buttons to add post when stream is not loaded
    public static void ensureAddPostMode() {
        scrollToAndHover(newPostText);
        assertThat(buttonsForNewPostAppear(), timeout2x());
    }

    private static ExpectedCondition<Boolean> buttonsForNewPostAppear() {
        return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                newPostText.click();

                if (!share.is(visible)) {
                    return FALSE;
                }
                return TRUE;
            }

            @Override
            public String toString() {
                return "Error appearing buttons for new post";
            }

        });
    }

}

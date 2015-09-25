package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.DiasporaAspect;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.helpers.UniqueDataHelper.the;

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
    public static void addAspectPost(DiasporaAspect diasporaAspect, String text) {
        newPostText.click();
        newPostText.setValue(text);

        ensureAspectPostingMode(diasporaAspect);
        share.click();
    }

    @Step
    public static void deletePost(PodUser from, String postText) {
        SelenideElement post = posts.filter(text(from.fullName)).find(text(postText));
        post.hover();
        post.find(".remove_post").click();
        confirm(null);
    }

    public static void assertNthPostIs(int nth, PodUser from, String post) {
        posts.get(nth).shouldHave(text(from.fullName)).shouldHave(text(post));
    }

    public static void assertPostIsShown(PodUser from, String post) {
        posts.filter(text(from.fullName)).filter(text(post)).shouldHave(size(1));
    }

    public static void assertPostIsNotShown(PodUser from, String post) {
        posts.filter(text(from.fullName)).filter(text(post)).shouldBe(empty);
    }

    public static void ensurePublicPostingMode() {
        if (aspect.getText().contains("Public")) {
            return;
        }
        setAspect.click();
        aspect.find(".public").click();
        setAspect.shouldHave(Condition.text("Public"));
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
        try {
            while (posts.size() > 0) {
                String post = posts.get(0).find(".markdown-content").getText();
                deletePost(from, post);
                assertPostIsNotShown(from, post);
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

}

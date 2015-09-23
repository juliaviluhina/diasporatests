package pages;

import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.$;

public class NavBar {

    public static SelenideElement tagsHeader = $("[href='/followed_tags']");

    @Step
    public static void expandTags() {
        tagsHeader.click();
    }
}

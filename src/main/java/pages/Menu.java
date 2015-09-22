package pages;

import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.$;

public class Menu {

    public static SelenideElement userMenu = $("#user_menu");

    @Step
    public static void logOut() {
        $(".user-menu-more-indicator").click();
        userMenu.find("[data-method='delete']").click();
    }

}

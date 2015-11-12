package pages;

import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class Diaspora {

    @Step
    public static void signInAs(PodUser user) {
        open(user.podLink + "/users/sign_in");
        userName.setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }

    public static SelenideElement userName = $("#user_username");

}

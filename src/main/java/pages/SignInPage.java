package pages;

import core.DiasporaAccountInformation;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SignInPage {

    @Step
    public void signIn(DiasporaAccountInformation user){
        open(user.podLink+"/users/sign_in");
        $("#user_username").setValue(user.userName);
        $("#user_password").setValue(user.password);
        $(By.name("commit")).click();
    }

}

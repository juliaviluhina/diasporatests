package ua.net.itlabs.diaspora;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.Test;
import pages.Contact;
import pages.Contacts;
import pages.Diaspora;
import pages.Menu;
import ua.net.itlabs.BaseTest;
import ua.net.itlabs.testDatas.Users;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static core.Gherkin.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static ua.net.itlabs.testDatas.Users.*;


public class SearchUserTest extends BaseTest {

    static {
        Configuration.browser = "chrome";
    }

    @Test
    public void testOpenUsersMatchingPage() {
        GIVEN("User is signed in");
        Diaspora.signInAs(Pod1.ana);

        WHEN("Set searched user name and press enter");
        $("#q").setValue(Pod1.eve.fullName).pressEnter();

        THEN("Search page is opened");
        $("#people_search .term").shouldHave(exactText(Pod1.eve.fullName));
        assertThat(url(), startsWith(Pod1.podLink + "/people?q=" + Pod1.eve.userName));

        //https://diaspora.koehn.com/people?q=eve_tjvi%40diaspora.koehn.com
    }

    @Test
    public void testOpenSearchedUserPage() {
        GIVEN("User is signed in");
        Diaspora.signInAs(Users.Pod1.ana);

        WHEN("Set searched user name");
        $("#q").setValue(Pod1.eve.fullName);
        AND("select appropriate item from dropdown menu");
        $$("#header-search-form .search-suggestion-person").find(text(Pod1.eve.fullName)).click();

        THEN("User is found");
        $("#author_info #name").shouldHave(Condition.exactText(Pod1.eve.fullName));
        assertThat(url(), startsWith(Pod1.podLink + "/people/"));
        //System.out.println(url());//https://diaspora.koehn.com/people/b0e6425048c4013376f9746d049d3c70
    }
}

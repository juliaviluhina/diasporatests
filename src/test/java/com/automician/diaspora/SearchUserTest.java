package com.automician.diaspora;

import com.automician.BaseTest;
import com.automician.testDatas.Users;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.Test;
import pages.Diaspora;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static core.Gherkin.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;


public class SearchUserTest extends BaseTest {

    static {
        Configuration.browser = "chrome";
    }

    @Test
    public void testOpenUsersMatchingPage() {
        GIVEN("User is signed in");
        Diaspora.signInAs(Users.Pod1.ana);

        WHEN("Set searched user name and press enter");
        $("#q").setValue(Users.Pod1.eve.fullName).pressEnter();

        THEN("Search page is opened");
        $("#people_search .term").shouldHave(exactText(Users.Pod1.eve.fullName));
        assertThat(url(), startsWith(Users.Pod1.podLink + "/people?q=" + Users.Pod1.eve.userName));

        //https://diaspora.koehn.com/people?q=eve_tjvi%40diaspora.koehn.com
    }

    @Test
    public void testOpenSearchedUserPage() {
        GIVEN("User is signed in");
        Diaspora.signInAs(Users.Pod1.ana);

        WHEN("Set searched user name");
        $("#q").setValue(Users.Pod1.eve.fullName);
        AND("select appropriate item from dropdown menu");
        $$("#header-search-form .search-suggestion-person").find(text(Users.Pod1.eve.fullName)).click();

        THEN("User is found");
        $("#author_info #name").shouldHave(Condition.exactText(Users.Pod1.eve.fullName));
        assertThat(url(), startsWith(Users.Pod1.podLink + "/people/"));
        //System.out.println(url());//https://diaspora.koehn.com/people/b0e6425048c4013376f9746d049d3c70
    }
}

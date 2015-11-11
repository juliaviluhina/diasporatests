package pages;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static core.AdditionalAPI.assertThat;
import static core.AdditionalAPI.elementExceptionsCatcher;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static pages.Aspects.FRIENDS;
import static pages.Aspects.STANDART_ASPECTS;

public class Contact {

    public static SelenideElement contactHeader = $(".profile_header");
    public static String manageContactLocator = ".btn.dropdown-toggle";

    public static SelenideElement contact(PodUser podUser) {
        return $$(".stream_element").find(text(podUser.fullName));
    }

    protected static SelenideElement manageContact(SelenideElement contact) {
        return contact.find(manageContactLocator);
    }

    @Step
    public static void stopIgnoring() {
        $(stopIgnoringLocator).click();
    }

    @Step
    public static void startIgnoring() {
        $("#block_user_button").click();
        confirm(null);
    }

    @Step
    public static void sendMessageToContact(String subject, String text) {
        $("#message_button").click();
        $("#conversation_subject").setValue(subject);
        $("#conversation_text").setValue(text);
        $("[value='Send']").click();
    }

    @Step
    public static void assertNoMessaging() {
        $$("#message_button").shouldBe(CollectionCondition.empty);
    }

    @Step
    public static void ensureSearchedContact(String fullName) {
        //even id search result site is shown - clicking in avatar load Contact site
        //purpose fot this ensure - after search 2 variants site can be loaded, for test contact site is needed
        SelenideElement avatar = $("[alt='" + fullName + "']");
        avatar.click();
    }

    @Step
    public static void ensureNoAspectsForContact() {
        ensureNoAspectsForContact(contactHeader);
    }

    @Step
    public static void ensureNoAspectsForContact(SelenideElement contact) {
        new AspectManager(manageContact(contact), contact).ensureNoAspects();
    }

    @Step
    public static void ensureAspectsForContact(String... diasporaAspects) {
        ensureAspectsForContact(contactHeader, diasporaAspects);
    }

    @Step
    public static void ensureAspectsForContact(SelenideElement contact, String... diasporaAspects) {
        new AspectManager(manageContact(contact), contact).ensureAspects(diasporaAspects);
    }

    @Step
    public static void ensureNoIgnoreMode(SelenideElement contact) {
        new AspectManager(manageContact(contact), contact).ensureNoIgnoreMode();
    }

    @Step
    public static void ensureNoIgnoreMode() {
        ensureNoIgnoreMode(contactHeader);
    }

    private static String stopIgnoringLocator = "#unblock_user_button";

    private static class AspectManager {
        private SelenideElement btnManageAspect;
        private SelenideElement aspectsContainer;

        private String[] aspectTexts;
        private String[] selectedAspectTexts;
        private Boolean[] beUsed;
        private Boolean[] shouldBeUsed;

        public AspectManager(SelenideElement btnManageAspect, SelenideElement aspectsContainer) {
            this.btnManageAspect = btnManageAspect;
            this.aspectsContainer = aspectsContainer;
        }

        private ElementsCollection aspects() {
            return aspectsContainer.findAll(".aspect_selector");
        }

        private ElementsCollection selectedAspects() {
            return aspects().filter(cssClass("selected"));
        }

        @Step
        private void openMenuAspects() {
            assertThat(aspectsMenuOpened());
        }

        @Step
        private void fixStartState() {
            openMenuAspects();
            aspectTexts = aspects().getTexts();
            selectedAspectTexts = selectedAspects().getTexts();
            beUsed = new Boolean[aspectTexts.length];
            shouldBeUsed = new Boolean[aspectTexts.length];
            for (int i = 0; i < aspectTexts.length; i++) {
                beUsed[i] = FALSE;
                shouldBeUsed[i] = FALSE;
                for (int j = 0; j < selectedAspectTexts.length; j++) {
                    if (aspectTexts[i].equals(selectedAspectTexts[j])) {
                        beUsed[i] = TRUE;
                        break;
                    }
                }
            }
            aspectsContainer.click();
        }

        @Step
        public void ensureNoIgnoreMode() {
            if ($$(stopIgnoringLocator).size() != 0) {
                stopIgnoring();
            }
        }

        @Step
        public void ensureNoAspects() {
            ensureNoIgnoreMode();

            if (btnManageAspect.getText().equals("Add contact")) {
                return;
            }

            fixStartState();
            for (int i = 0; i < aspectTexts.length; i++) {
                if (beUsed[i]) {
                    openMenuAspects();
                    aspects().get(i).click();
                    aspectsContainer.click();
                }
            }
        }

        @Step
        public void ensureAspects(String... diasporaAspects) {
            ensureNoIgnoreMode();

            if (diasporaAspects.length == 1) {
                if (btnManageAspect.getText().equals(diasporaAspects[0])) {
                    return;
                }
            }

            fixStartState();
            for (String diasporaAspect : diasporaAspects) {
                for (int i = 0; i < aspectTexts.length; i++) {
                    if (aspectTexts[i].contains(diasporaAspect)) {
                        shouldBeUsed[i] = TRUE;
                        break;
                    }
                }
            }

            for (int i = 0; i < beUsed.length; i++) {
                if (beUsed[i] != shouldBeUsed[i]) {
                    openMenuAspects();
                    aspects().get(i).click();
                    aspectsContainer.click();
                }
            }
        }

        private ExpectedCondition<Boolean> aspectsMenuOpened() {
            return elementExceptionsCatcher(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver webDriver) {
                    btnManageAspect.click();

                    if (aspects().find(exactText(FRIENDS)).is(visible)) {
                        if (aspects().size() >= STANDART_ASPECTS.length) {
                            return TRUE;
                        }
                    }
                    return FALSE;
                }

                @Override
                public String toString() {
                    return "Error opening contact's aspects menu";
                }

            });
        }

    }

}

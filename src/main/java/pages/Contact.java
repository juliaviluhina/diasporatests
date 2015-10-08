package pages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static core.conditions.CustomCollectionCondition.textsBegin;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static pages.Aspects.STANDART_ASPECTS;

public class Contact {

    public static SelenideElement contactHeader = $(".profile_header");
    public static String manageContactLocator = ".btn.dropdown-toggle";

    public static SelenideElement contact(PodUser podUser) {
        return $$(".stream_element").filter(text(podUser.fullName)).get(0);
    }

    protected static SelenideElement manageContact(SelenideElement contact) {
        return contact.find(manageContactLocator);
    }

    @Step
    public static void ensureSearchedContact(String fullName) {
        //even id search result site is shown - clicking in avatar load Contact site
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
            return aspectsContainer.findAll(".aspect_selector.selected");
        }

        @Step
        private void fixStartState() {
            btnManageAspect.click();
            aspects().shouldHave(textsBegin(STANDART_ASPECTS));
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
        public void ensureNoAspects() {

            if (btnManageAspect.getText().equals("Add contact")) {
                return;
            }
            fixStartState();
            for (int i = 0; i < aspectTexts.length; i++) {
                if (beUsed[i]) {
                    btnManageAspect.click();
                    aspects().get(i).click();
                    aspectsContainer.click();
                }
            }
        }

        @Step
        public void ensureAspects(String... diasporaAspects) {

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
                    btnManageAspect.click();
                    aspects().get(i).click();
                    aspectsContainer.click();
                }
            }
        }

    }

}

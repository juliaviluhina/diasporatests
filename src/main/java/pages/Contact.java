package pages;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import datastructures.PodUser;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static core.conditions.CustomCollectionCondition.textsBegin;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static pages.Aspects.STANDART_ASPECTS;
import static pages.Aspects.aspectIsUsed;

public class Contact {

    public static SelenideElement contactHeader = $(".profile_header");
    public static String manageContactLocator = ".btn.dropdown-toggle";

    public static SelenideElement contact(PodUser podUser) {
        return $$(".stream_element").filter(text(podUser.fullName)).get(0);
    }

    protected static ElementsCollection aspectsOfContact(SelenideElement contact) {
        return contact.findAll(".aspect_selector");
    }

    protected static SelenideElement manageContact(SelenideElement contact) {
        return contact.find(manageContactLocator);
    }

    //search results can be  shown in two variants
    public static void ensureSearchedContact(String fullName) {
        if ($$("#search_title").filter(text(fullName)).size() > 0) {
            $$(".stream_element").find(text(fullName)).find(".avatar").click();
        }
        //$("#diaspora_handle").shouldHave(text(fullName));
        $$("#diaspora_handle").filter(text(fullName)).shouldHave(size(1));
    }


    public static void ensureNoAspectsForContact() {
        ensureNoAspectsForContact(contactHeader);
    }

    public static void ensureNoAspectsForContact(SelenideElement contact) {
        if (manageContact(contact).getText().equals("Add contact")) {
            return;
        }
        manageContact(contact).click();
        aspectsOfContact(contact).shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspectsOfContact(contact).getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspectsOfContact(contact).get(i));
        }
        contact.click();
        for (int i = 0; i < aspectTexts.length; i++) {
            if (beUsed[i]) {
                manageContact(contact).click();
                aspectsOfContact(contact).get(i).click();
                contact.click();
            }
        }
    }


    public static void ensureAspectsForContact(String... diasporaAspects) {
        ensureAspectsForContact(contactHeader, diasporaAspects);
    }

    public static void ensureAspectsForContact(SelenideElement contact, String... diasporaAspects) {
        if (diasporaAspects.length == 1) {
            if (manageContact(contact).getText().equals(diasporaAspects[0])) {
                return;
            }
        }
        manageContact(contact).click();
        aspectsOfContact(contact).shouldHave(textsBegin(STANDART_ASPECTS));
        String[] aspectTexts = aspectsOfContact(contact).getTexts();
        Boolean[] beUsed = new Boolean[aspectTexts.length];
        Boolean[] shouldBeUsed = new Boolean[aspectTexts.length];
        for (int i = 0; i < aspectTexts.length; i++) {
            beUsed[i] = aspectIsUsed(aspectsOfContact(contact).get(i));
            shouldBeUsed[i] = FALSE;
        }
        contact.click();
        for (String diasporaAspect : diasporaAspects) {
            for (int i=0; i<aspectTexts.length; i++) {
                if (aspectTexts[i].contains(diasporaAspect)) {
                    shouldBeUsed[i] = TRUE;
                    break;
                }
            }
        }

        for (int i = 0; i < beUsed.length; i++) {
            if (beUsed[i] != shouldBeUsed[i]) {
                manageContact(contact).click();
                aspectsOfContact(contact).get(i).click();
                contact.click();
            }
        }
    }

}

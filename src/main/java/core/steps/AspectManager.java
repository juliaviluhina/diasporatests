package core.steps;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.yandex.qatools.allure.annotations.Step;

import static core.conditions.CustomCollectionCondition.textsBegin;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static pages.Aspects.STANDART_ASPECTS;

public class AspectManager {
    private static SelenideElement btnManageAspect;
    private static SelenideElement aspectsContainer;

    private static String[] aspectTexts;
    private static String[] selectedAspectTexts;
    private static Boolean[] beUsed;
    private static Boolean[] shouldBeUsed;

    private static ElementsCollection aspects() {
        return aspectsContainer.findAll(".aspect_selector");
    }

    private static ElementsCollection selectedAspects() {
        return aspectsContainer.findAll(".aspect_selector.selected");
    }

    @Step
    private static void fixStartState() {
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
    }


    @Step
    public static void ensureNoAspects(SelenideElement btnManageAspect_, SelenideElement aspectsContainer_) {
        btnManageAspect = btnManageAspect_;
        aspectsContainer = aspectsContainer_;

        if (btnManageAspect.getText().equals("Add contact")) {
            return;
        }
        fixStartState();
        aspectsContainer.click();
        for (int i = 0; i < aspectTexts.length; i++) {
            if (beUsed[i]) {
                btnManageAspect.click();
                aspects().get(i).click();
                aspectsContainer.click();
            }
        }
    }

    @Step
    public static void ensureAspects(SelenideElement btnManageAspect_, SelenideElement aspectsContainer_, String... diasporaAspects) {
        btnManageAspect = btnManageAspect_;
        aspectsContainer = aspectsContainer_;

        if (diasporaAspects.length == 1) {
            if (btnManageAspect.getText().equals(diasporaAspects[0])) {
                return;
            }
        }
        fixStartState();
        aspectsContainer.click();
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

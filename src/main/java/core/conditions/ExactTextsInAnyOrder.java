package core.conditions;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExactTextsInAnyOrder extends CollectionCondition {

    protected final String[] expectedTexts;

    public ExactTextsInAnyOrder(String... expectedTexts) {
        this.expectedTexts = expectedTexts;
    }

    public boolean apply(List<WebElement> elements) {

        String[] actualTexts = new String[elements.size()];
        boolean result = true;

        if (elements.size() != expectedTexts.length) {
            return false;
        }

        if (expectedTexts.length == 0) {
            return true;
        }

        int i = 0;

        for (WebElement element : elements) {
            actualTexts[i] = element.getText();
            i++;
        }

        List<String> actualTextList = new ArrayList<String>(Arrays.asList(actualTexts));
        for (String expectedText : expectedTexts) {
            result = result && actualTextList.contains(expectedText);
        }
        return result;
    }

    public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
        if (elements == null || elements.isEmpty()) {
            ElementNotFound elementNotFound = new ElementNotFound(collection, expectedTexts, lastError);
            elementNotFound.timeoutMs = timeoutMs;
            throw elementNotFound;
        } else {
            throw new TextsMismatch(collection, ElementsCollection.getTexts(elements), expectedTexts, timeoutMs);
        }
    }

    public String toString() {
        return "Exact texts " + Arrays.toString(this.expectedTexts);
    }
}


package core;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class CustomCollectionConditions {

    public static CollectionCondition exactTextsInAnyOrder(final String... expectedTexts) {
        return new CollectionCondition() {

            String[] actualTexts;

            public boolean apply(List<WebElement> elements) {

                actualTexts = new String[elements.size()];
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

            @Override
            public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
                if (elements == null || elements.isEmpty()) {
                    ElementNotFound elementNotFound = new ElementNotFound(collection, expectedTexts, lastError);
                    elementNotFound.timeoutMs = timeoutMs;
                    throw elementNotFound;
                } else {
                    throw new TextsMismatch(collection, ElementsCollection.getTexts(elements), expectedTexts, timeoutMs);
                }
            }

            @Override
            public String toString() {
                return String.format("Expected text \nis: %s\n while actual text is: %s\n",
                        Arrays.toString(expectedTexts), Arrays.toString(actualTexts));
            }
        };
    }

    public static CollectionCondition textsLoaded() {
        return new CollectionCondition() {

            public List<String> actualTexts = new ArrayList<String>();

            public boolean apply(List<WebElement> elements) {

                boolean result = false;

                long startTime = currentTimeMillis();

                do {
                    result = true;
                    if (actualTexts.size() == elements.size()) {
                        int i = 0;
                        for (WebElement element : elements) {
                            result = result && (Html.text.equals(element.getText(), actualTexts.get(i)));
                            i++;
                            if (!result) { break;}
                        }
                    }
                    else { result = false; }
                    if (!result) {
                        actualTexts.clear();
                        for (WebElement element : elements) {
                            actualTexts.add(element.getText());
                        }
                    }
                } while (((startTime + Configuration.timeout) < currentTimeMillis()) && !result);

                return result;
            }

            @Override
            public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
                if (elements == null || elements.isEmpty()) {
                    ElementNotFound elementNotFound = new ElementNotFound(collection, null, lastError);
                    elementNotFound.timeoutMs = timeoutMs;
                    throw elementNotFound;
                } else {
                    throw new CollectionNotLoaded(collection, lastError);
                }
            }

            @Override
            public String toString() {
                return String.format("Loaded text \nis: %s\n", actualTexts.toString());
            }
        };
    }


    /* not needed
    public static CollectionCondition containTexts(final String... expectedTexts) {
        return new CollectionCondition() {

            String[] actualTexts;

            public boolean apply(List<WebElement> elements) {

                actualTexts = new String[elements.size()];
                boolean result = false;

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
                    result = result || actualTextList.contains(expectedText);
                }
                return result;
            }

            @Override
            public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
                if (elements == null || elements.isEmpty()) {
                    ElementNotFound elementNotFound = new ElementNotFound(collection, expectedTexts, lastError);
                    elementNotFound.timeoutMs = timeoutMs;
                    throw elementNotFound;
                } else {
                    throw new TextsMismatch(collection, ElementsCollection.getTexts(elements), expectedTexts, timeoutMs);
                }
            }

            @Override
            public String toString() {
                return String.format("Expected text %s\nis not contain in actual text\n while actual text is: %s\n",
                        Arrays.toString(expectedTexts), Arrays.toString(actualTexts));
            }
        };
    }*/

}

package core;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import core.ex.CollectionNotLoaded;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class CustomElementsCollection {

    public static CustomElementsCollection $$(String cssSelector) {
        return new CustomElementsCollection(cssSelector);
    }

    public CustomElementsCollection(String cssSelector) {
        elementsCollection = com.codeborne.selenide.Selenide.$$(cssSelector);
    }

    public ElementsCollection shouldBe(CollectionCondition... conditions) {
        return elementsCollection.shouldBe(textsLoaded).shouldBe(conditions);
    }

    public ElementsCollection shouldHave(CollectionCondition... conditions) {
        return elementsCollection.shouldBe(textsLoaded).shouldHave(conditions);
    }

    public String[] getTexts() {
        return elementsCollection.shouldBe(textsLoaded).getTexts();
    }

    public SelenideElement find(Condition condition) {
        return filter(condition).get(0);
    }

    public ElementsCollection filter(Condition condition) {
        return elementsCollection.shouldBe(textsLoaded).filter(condition);
    }

    protected ElementsCollection elementsCollection;

    protected CollectionCondition textsLoaded = new CollectionCondition() {
        public boolean apply(List<WebElement> elements) {

            List<String> actualTexts = new ArrayList<String>();
            List<String> expectedTexts = new ArrayList<String>(); //for debugging

            boolean result = false;

            long startTime = currentTimeMillis();

            do {
                expectedTexts.clear();

                result = true;
                if (actualTexts.size() == elements.size()) {
                    int i = 0;
                    for (WebElement element : elements) {
                        expectedTexts.add(element.getText());
                        result = result && (Html.text.equals(element.getText(), actualTexts.get(i)));
                        i++;
                        if (!result) {
                            break;
                        }
                    }
                } else {
                    result = false;
                }
                if (!result) {
                    actualTexts.clear();
                    for (WebElement element : elements) {
                        actualTexts.add(element.getText());
                    }
                }
            } while ((!result) || (startTime + Configuration.timeout < currentTimeMillis()));

            return result;
        }

        public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
            if (elements == null || elements.isEmpty()) {
                ElementNotFound elementNotFound = new ElementNotFound(collection, null, lastError);
                elementNotFound.timeoutMs = timeoutMs;
                throw elementNotFound;
            } else {
                throw new CollectionNotLoaded(collection, ElementsCollection.getTexts(elements), lastError);
            }
        }

        public String toString() {
            return "Texts not loaded completely ";
        }
    };

}

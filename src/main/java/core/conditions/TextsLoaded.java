package core.conditions;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import core.ex.CollectionNotLoaded;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class TextsLoaded extends CollectionCondition {

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
        } while (!result); //it is commented through thus check diaspora tags couldn't be loaded ((startTime + Configuration.timeout) < currentTimeMillis()) &&

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
        return "Texts loaded completely ";
    }

}

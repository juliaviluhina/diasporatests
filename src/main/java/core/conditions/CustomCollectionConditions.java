package core.conditions;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import core.ex.CollectionNotLoaded;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class CustomCollectionConditions {

    public static CollectionCondition exactTextsInAnyOrder(final String... expectedTexts) {
        return new ExactTextsInAnyOrder(expectedTexts);
    }

}

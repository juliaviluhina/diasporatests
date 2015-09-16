package core.ex;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

public class CollectionNotLoaded extends UIAssertionError {

    public CollectionNotLoaded (WebElementsCollection collection, String[] actualTexts, Throwable lastError) {
        super("\nCollection not loaded completely, loaded:" + Arrays.toString(actualTexts) + "\nCollection: " + collection.description(),lastError);
    }

}

package core;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.WebElementsCollection;

public class CollectionNotLoaded extends UIAssertionError {
    public CollectionNotLoaded (WebElementsCollection collection, Throwable lastError) {
        super("Collection not loaded completely {" + collection.description() + "}", lastError);
    }

}

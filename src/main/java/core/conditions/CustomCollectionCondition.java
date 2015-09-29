package core.conditions;

import com.codeborne.selenide.CollectionCondition;

public class CustomCollectionCondition {

    public static CollectionCondition textsBegin(String... expectedTexts){
        return new TextsBegin(expectedTexts);
    }

}

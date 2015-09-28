package core.conditions;

import com.codeborne.selenide.Condition;

public class CustomCondition {

    public static Condition textBegin(String fromText) {
        return new TextBegin(fromText);
    }

    public static Condition textBeginAndContain(String fromText, String containText) {
        return new TextBeginAndContain(fromText, containText);
    }
}

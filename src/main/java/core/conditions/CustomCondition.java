package core.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.conditions.Text;
import org.openqa.selenium.WebElement;

public class CustomCondition {

    public static Condition textBegin(final String fromText) {

        return new Condition("Text begin") {

            public boolean apply(WebElement element) {
                String elementText = element.getText();
                if (elementText.length() < fromText.length()) {
                    return false;
                }
                return elementText.substring(0, fromText.length()).equals(fromText);
            }

            public String toString() {
                return this.name + " \'" + fromText + '\'';
            }

        };
    }

    public static Condition textBeginAndContain(final String fromText, final String containText) {
        return new Condition("Text begin and contain") {

            public boolean apply(WebElement element) {
                String elementText = element.getText();
                if (elementText.length() < fromText.length()) {
                    return false;
                }
                if (!elementText.substring(0, fromText.length()).equals(fromText)) {
                    return false;
                }
                return elementText.contains(containText);
            }

            public String toString() {
                return this.name + " \' " + fromText + " | " + containText + '\'';
            }

        };
    }

    public static Condition textEnd(final String toText) {
        return new Condition("Text end") {

            public boolean apply(WebElement element) {
                String elementText = element.getText();
                if (elementText.length() < toText.length()) {
                    return false;
                }
                String fromText = elementText.replaceFirst(toText, "");
                return elementText.equals(fromText + toText);
            }

            public String toString() {
                return this.name + " \'" + toText + '\'';
            }

        };
    }

}

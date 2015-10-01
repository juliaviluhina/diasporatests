package core.conditions;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.WebElement;

public class TextEnd extends Condition {

    protected final String toText;

    public TextEnd(String toText) {
        super("text end");
        this.toText = toText;
    }

    public boolean apply(WebElement element) {
        String elementText = element.getText();
        if (elementText.length()<toText.length()) {return false;}
        String fromText = elementText.replaceFirst(toText, "");
        return elementText.equals(fromText+toText);
    }

    public String toString() {
        return this.name + " \'" + this.toText + '\'';
    }

}

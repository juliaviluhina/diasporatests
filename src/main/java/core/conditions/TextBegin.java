package core.conditions;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.WebElement;

public class TextBegin extends Condition {

    protected final String fromText;

    public TextBegin(String fromText) {
        super("text begin");
        this.fromText = fromText;
    }

    public boolean apply(WebElement element) {
        String elementText = element.getText();
        if (elementText.length()<fromText.length()) {return false;}
        return elementText.substring(0,fromText.length()).equals(fromText);
    }

    public String toString() {
        return this.name + " \'" + this.fromText + '\'';
    }

}

package core.conditions;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.WebElement;

public class TextBeginAndContain extends Condition {

    protected final String fromText;
    protected final String containText;

    public TextBeginAndContain(String fromText, String containText) {
        super("text begin and contain");
        this.fromText = fromText;
        this.containText = containText;
    }

    public boolean apply(WebElement element) {
        String elementText = element.getText();
        if (elementText.length()<fromText.length()) {return false;}
        if (!elementText.substring(0,fromText.length()).equals(fromText)) {return false;}
        return elementText.contains(containText);
    }

    public String toString() {
        return this.name + " \' " + this.fromText+" | " +this.containText+ '\'';
    }

}

package core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.common.base.Function;


import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class AdditionalAPI {

    protected static <V> V waitUntil(Function<? super WebDriver, V> condition, int timeout) {
        return (new WebDriverWait(getWebDriver(), timeout)).until(condition);
    }

    public static <V> V assertThat(Function<? super WebDriver, V> condition, long timeout) {
        return waitUntil(condition, (int) (timeout / 1000));
    }

    public static <V> V assertThat(Function<? super WebDriver, V> condition) {
        return assertThat(condition, Configuration.timeout);
    }

    public static <V> ExpectedCondition<V> elementExceptionsCatcher(final Function<? super WebDriver, V> condition) {
        return new ExpectedCondition<V>() {
            public V apply(WebDriver input) {
                try {
                    return condition.apply(input);
                } catch (StaleElementReferenceException e) {
                    return null;
                } catch (ElementNotVisibleException e) {
                    return null;
                }
            }

            public String toString() {
                return condition.toString();
            }
        };
    }

    public static void scrollWithOffset(SelenideElement element, int x, int y) {

        String code = "window.scroll(" + (element.getLocation().x + x) + "," + (element.getLocation().y + y) + ");";

        ((JavascriptExecutor)getWebDriver()).executeScript(code, element, x, y);

    }

    public static SelenideElement scrollToAndHover(SelenideElement element) {
        //element.scrollTo(); scrollTo works not always. especially unstable for tags
        //element.getCoordinates().inViewPort(); this variant doesn't help in situations when something is shown under the header
        scrollWithOffset(element, 0, -150); //this variant is used to move element from under the header
        return element.hover();
    }

    public static long timeout2x() {
        return Configuration.timeout * 2;
    }

    public static long timeout3x() {
        return Configuration.timeout * 3;
    }
}

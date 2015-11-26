package core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import com.google.common.io.Files;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Attachment;
import com.google.common.base.Function;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;


public class AdditionalAPI {

    @Attachment(type = "image/png")
    public static byte[] screenshot(byte[] dataForScreenshot) {
        return dataForScreenshot;
    }

    public static byte[] lastSelenideScreenshot() {
        Field allScreenshotsField = null;
        try {
            allScreenshotsField = ScreenShotLaboratory.class.getDeclaredField("allScreenshots");
            allScreenshotsField.setAccessible(true);
            List<String> allScreenshots = (List<String>) allScreenshotsField.get(Screenshots.screenshots);
            int allScreenshotsSize = allScreenshots.size();
            if (allScreenshotsSize > 0) {
                return Files.toByteArray(new File(allScreenshots.get(allScreenshotsSize - 1)));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

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

    public static SelenideElement scrollToAndHover(SelenideElement element) {
        //element.scrollTo(); scrollTo works not always. especially unstable for tags
        element.getCoordinates().inViewPort();
        return element.hover();
    }

    public static long timeout2x() {
        return Configuration.timeout * 2;
    }

    public static long timeout3x() {
        return Configuration.timeout * 3;
    }
}

package ua.net.itlabs;


import com.codeborne.selenide.Screenshots;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.BeforeClass;
import ua.net.itlabs.pages.SignInPage;
import ua.net.itlabs.pages.StreamPage;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;


public class AtDiasporaWithReportedScreenshotsTest {
    public static DiasporaAccountInformation userAna;
    public static DiasporaAccountInformation userBob;
    public static DiasporaAccountInformation userDave;

    public SignInPage signInPage = new SignInPage();
    public StreamPage streamPage = new StreamPage();

    @BeforeClass
    public static void SetGivenInformationForTests() {
        userAna = new DiasporaAccountInformation("ana_tjvi", "tjvitjvi", "https://diaspora.koehn.com", "ana.tjvi@gmail.com");
        userBob = new DiasporaAccountInformation("bob_tjvi", "tjvitjvi", "https://nerdpol.ch", "bob.tjvi@gmail.com");
        userDave = new DiasporaAccountInformation("dave_tjvi", "tjvitjvi", "https://diasporabrazil.org", "dave.tjvi@gmail.com");
    }

    @After
    public void postScreensAfterTest() throws IOException {
        screenshot();
        streamPage.logOut();
    }

    @Attachment(type = "image/png")
    public byte[] screenshot() throws IOException {
        File screenshot = Screenshots.getScreenShotAsFile();
        return Files.toByteArray(screenshot);
    }

    public String newUniqueTagName() {
        return newUniqueTagName("tag");
    }

    public String newUniqueTagName(String prefix) {
        return "#"+ prefix + currentTimeMillis();
    }

}

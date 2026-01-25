package core;

import com.microsoft.playwright.*;
import org.testng.annotations.*;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    // config defaults
    protected String baseUrl = System.getProperty("baseUrl", "https://saucedemo.com/");
    protected boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

    @BeforeClass
    public void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeMethod
    public void createContextAndPage() {
        // Defensive guard: if BeforeClass didn't run, fail loudly
        if (browser == null)
            throw new IllegalStateException("Browser not initialized. Ensure @BeforeClass launchBrowser() is executed before this method.");

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720));

        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        try {
            // If the listener didn't stop tracing (test passed), stop it quietlyu
            context.tracing().stop();
        } catch (Exception ignored) {}

        if (context != null) context.close();
    }

    // Utility for later (screenshots on failure, etc.)
    protected void ensureDir(String dirName) {
        try {
            Files.createDirectories(Path.of(dirName));
        } catch (Exception ignored) {
            // TODO Handle more cleanly
        }
    }
}
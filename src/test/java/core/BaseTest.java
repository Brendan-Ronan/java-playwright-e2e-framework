package core;

import com.microsoft.playwright.*;
import org.testng.ITestResult;
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

    @BeforeClass(alwaysRun = true)
    public void launchBrowser() {
        System.out.println(">> BeforeClass launchBrowser() START");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless));

        System.out.println(">> BeforeClass launchBrowser() DONE. browser=" + (browser != null));
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeMethod(alwaysRun = true)
    public void createContextAndPage() {
        System.out.println(">> BeforeMethod createContextAndPage() START. browser=" + (browser != null));
        // Defensive guard: if BeforeClass didn't run, fail loudly
        if (browser == null)
            throw new IllegalStateException("Browser not initialized. Ensure @BeforeClass launchBrowser() is executed before this method.");

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720));

        page = context.newPage();

        System.out.println(">> BeforeMethod createContextAndPage() DONE. page=" + (page != null));

        // Start tracing for this test
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(ITestResult result) {
        try {
            // For passing tests, stop tracing without saving a file
            if (context != null) {
                if (!result.isSuccess()) {
                    ensureDir("artifacts");
                    String testName = result.getTestClass().getName() + "." +
                            result.getMethod().getMethodName();

                    // Screenshot
                    if (page != null) {
                        page.screenshot(new Page.ScreenshotOptions()
                                .setPath(Path.of("artifacts", testName + ".png"))
                                .setFullPage(true));
                    }

                    // Trace
                    context.tracing().stop(new Tracing.StopOptions()
                            .setPath(Path.of("artifacts", testName + ".zip")));
                } else {
                    context.tracing().stop();
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (context != null) context.close();
        }
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
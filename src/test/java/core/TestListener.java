package core;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.nio.file.Files;

public class TestListener implements ITestListener {

    private void ensureDir(String dir) {
        try {
            Files.createDirectories(Path.of(dir));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create directory: " + dir, e);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) return;

        BaseTest test = (BaseTest) instance;

        ensureDir("artifacts/screenshots");
        ensureDir("artifacts/traces");

        String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        String safeName = testName.replaceAll("[^a-zA-Z0-9]", "_");

        // Screenshot
        try {
            test.page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                    .setPath(Path.of("artifacts/screenshots/", safeName + ".png"))
                    .setFullPage(true));
        } catch (Exception ignored) {}

        // Trace
        try {
            test.context.tracing().stop(new com.microsoft.playwright.Tracing.StopOptions()
                    .setPath(Path.of("artifacts/traces/", safeName + ".zip")));
        } catch (Exception ignored) {}

    }

    @Override
    public void onTestStart(ITestResult result) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) return;

        BaseTest test = (BaseTest) instance;

        // Start tracing at test start
        try {
            test.context.tracing().start(new com.microsoft.playwright.Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        } catch (Exception ignored) {}
    }

    @Override
    public void onFinish(ITestContext context) {

    }

}

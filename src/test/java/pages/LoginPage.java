package pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    // Variables
    private final Page page;

    // Constructor
    public LoginPage(Page page) {
        this.page = page;
    }

    // Methods
    public void goTo(String baseUrl) {
        page.navigate(baseUrl);
    }

    public void login(String username, String password) {
        page.locator("[data-test='username']").fill(username);
        page.locator("[data-test='password']").fill(password);
        page.locator("[data-test='login-button']").click();
    }

    public String getErrorMessage() {
        return page.locator("[data-test='error']").innerText();
    }
}

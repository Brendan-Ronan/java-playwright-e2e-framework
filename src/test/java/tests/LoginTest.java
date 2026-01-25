package tests;

import core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke"})
    public void userLogIn() {
        // Arrange
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);

        // Act
        loginPage.goTo(baseUrl);
        loginPage.login("standard_user", "secret_sauce");

        // Assert
        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page visible after login.");
    }

    @Test(groups = {"smoke"})
    public void invalidPasswordError() {
        // Arrange
        LoginPage loginPage = new LoginPage(page);

        // Act
        loginPage.goTo(baseUrl);
        loginPage.login("standard_user", "wrong_password");

        // Assert
        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.toLowerCase().contains("username and password"),
                "Expected an error message about username/password. Actual: " + error);
    }

    @Test(groups = {"smoke"})
    public void addItemToCartShowsBadge() {
        // Arrange
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);

        // Act
        loginPage.goTo(baseUrl);
        loginPage.login("standard_user", "secret_sauce");

        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page visible after login");

        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.cartBadgeCount(), "1");
    }
}
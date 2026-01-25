package pages;

import com.microsoft.playwright.Page;

public class InventoryPage {
    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.url().contains("inventory") && page.locator(".inventory_list").isVisible();
    }

    public void addBackpackToCart() {
        page.locator("#add-to-cart-sauce-labs-backpack").click();
    }

    public String cartBadgeCount() {
        return page.locator(".shopping_cart_badge").innerText();
    }
}

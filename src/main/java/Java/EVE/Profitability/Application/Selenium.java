package Java.EVE.Profitability.Application;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Selenium {

    public static void main(String[] args) throws InterruptedException {
        String location = "Jita";
        FirefoxDriver driver = openDriver();
        openNewSite(driver,"https://evemarketer.com/");
        Item item = new Item("paladin");
        searchEveMarketerForItem(driver,item.getName());
        item.setId(getIDFromURL(driver,item));
        item.setPrice(findCheapestPriceInLocation(driver,location));
        item.setRequiredMaterials(getRequiredMaterialsFromEveRef(driver,item));
        System.out.println(item.getName() + " " + item.getId() + " " + item.getPrice() + " " + item.getRequiredMaterials());
        closeDriver(driver);
    }

    private static int getIDFromURL(FirefoxDriver driver, Item item) {
        openNewSite(driver,"https://evemarketer.com/");
        searchEveMarketerForItem(driver, item.getName());
        String url = driver.getCurrentUrl();
        String[] splitURL = url.split("types/");
        return Integer.parseInt(splitURL[1]);
    }


    private static String getRequiredMaterialsFromEveRef(FirefoxDriver driver,Item item){
        int id = item.getId();
        openNewSite(driver, "https://everef.net/type/"+id);
        WebElement table = new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div/div[5]/div[5]/div/div/div[14]/div/div/div/table[2]/tbody")));
        String[] requiredMaterialsArray = table.getText().split("\n");
        String requiredMaterials = "";
        for (String part:requiredMaterialsArray) {
            requiredMaterials += part;
            requiredMaterials += ", ";
        }
        return requiredMaterials;
    }

    private static FirefoxDriver openDriver() {
        System.setProperty("webdriver.gecko.driver","C:\\Users\\Leo\\Downloads\\geckodriver-v0.29.1-win64\\geckodriver.exe");
        return new FirefoxDriver();
    }

    private static void closeDriver(FirefoxDriver driver) {
        driver.close();
    }

    private static void openNewSite(FirefoxDriver driver, String input){
        driver.get(input);
    }

    private static String findCheapestPriceInLocation(FirefoxDriver driver, String location) {
        int row = 1;
        String xpath = "/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[4]/span";
        WebElement element = new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        if(!element.getText().contains(capitalizeString(location))){
            while (!driver.findElementByXPath(xpath).getText().contains(capitalizeString(location))) {
                row += 1;
                xpath = "/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[4]/span";
            }
        }
        return driver.findElementByXPath("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[3]").getText();
    }

    private static void searchEveMarketerForItem(FirefoxDriver driver, String input) {
        WebElement search = driver.findElementByXPath("/html/body/div/div[2]/div[1]/div/div/div[1]/div[1]/div/input");
        search.clear();
        search.sendKeys(capitalizeString(input));
        search.sendKeys(Keys.RETURN);
        WebElement firstResult = new WebDriverWait(driver, 2).until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div[2]/div[1]/div/div/div[1]/div[3]/ul/li[1]/h5/a")));
        firstResult.click();
    }

    private static String capitalizeString(String input){
        return input.substring(0,1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }
}
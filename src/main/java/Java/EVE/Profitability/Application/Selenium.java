package Java.EVE.Profitability.Application;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;
import java.util.Locale;

public class Selenium {

    public static void main(String[] args) throws InterruptedException {
        String location = "Jita";
        FirefoxDriver driver = openDriver();
        openNewSite(driver,"https://evemarketer.com/");
        Item item = new Item("heron");
        searchEveMarketerForItem(driver,item.getName());
        item.setId(getIDFromURL(driver,item));
        item.setPrice(getPriceFromEveMarketer(driver,location));
        item.setRequiredMaterials(getRequiredMaterialsFromEveRef(driver,item));
        item.setBuildCost(getBuildCostFromRequiredMaterials(driver,item,location));
        System.out.println(item.getName() + " " + item.getPrice() + " ISK   Build cost: " + item.getBuildCost() + " ISK   Profit for building: " + profitFromBuilding(item) + " ISK   Profit %: " + profitPercentageFromBuilding(item) + "%");
        closeDriver(driver);
    }

    private static int getIDFromURL(FirefoxDriver driver, Item item) {
        openNewSite(driver,"https://evemarketer.com/");
        searchEveMarketerForItem(driver, item.getName());
        String url = driver.getCurrentUrl();
        String[] splitURL = url.split("types/");
        return Integer.parseInt(splitURL[1]);
    }

    private static String getPriceFromEveMarketer(FirefoxDriver driver, String location) {
        int row = 1;
        String xpath = "/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[4]/span";
        WebElement element = new WebDriverWait(driver, 3).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        if(!element.getText().contains(capitalizeString(location))){
            while (!driver.findElementByXPath(xpath).getText().contains(capitalizeString(location))) {
                row += 1;
                xpath = "/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[4]/span";
            }
        }
        String[] price = driver.findElementByXPath("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr[" + row + "]/td[3]").getText().split("\s");
        return price[0];
    }

    private static String getRequiredMaterialsFromEveRef(FirefoxDriver driver,Item item){
        int id = item.getId();
        openNewSite(driver, "https://everef.net/type/"+id);
        WebElement blueprint = new WebDriverWait(driver, 3).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Blueprint")));
        blueprint.click();
        String requiredMaterials = "";
        WebElement tableHead = new WebDriverWait(driver,3).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[text()='Input']")));
        WebElement wrapper3 = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode;", tableHead);
        WebElement wrapper2 = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode;", wrapper3);
        WebElement wrapper1 = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode;", wrapper2);
        WebElement table = wrapper1.findElement(By.xpath("./child::tbody"));
        String[] requiredMaterialsArray = table.getText().split("\n");
        for (String part:requiredMaterialsArray) {
            requiredMaterials += part;
            requiredMaterials += ";";
        }
        return requiredMaterials;
    }

    private static String getBuildCostFromRequiredMaterials(FirefoxDriver driver, Item item, String location){
        String[] requiredMaterials = item.getRequiredMaterials().split(";");
        int totalBuildCost = 0;
        for (String materialAndQuantity:requiredMaterials) {
            String removedCommas = materialAndQuantity.replaceAll(",","");
            String[] dividedTextAndInt = removedCommas.split("(?<=\\D)(?=\\d)");
            searchEveMarketerForItem(driver, dividedTextAndInt[0]);
            //System.out.println("item: " + dividedTextAndInt[0] + " amount: " + Double.parseDouble(dividedTextAndInt[1]) + " cost per: " + Double.parseDouble(getPriceFromEveMarketer(driver, location).replaceAll(",","")) + " total cost: " + Double.parseDouble(getPriceFromEveMarketer(driver, location).replaceAll(",",""))*Double.parseDouble(dividedTextAndInt[1]));
            totalBuildCost += Double.parseDouble(getPriceFromEveMarketer(driver, location).replaceAll(",",""))*Double.parseDouble(dividedTextAndInt[1]);
            //System.out.println(totalBuildCost);
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(totalBuildCost);
    }

    private static String profitFromBuilding(Item item){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(Double.parseDouble(item.getPrice().replaceAll(",",""))-Double.parseDouble(item.getBuildCost().replaceAll(",","")));
    }

    private static String profitPercentageFromBuilding(Item item){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        Double price = Double.parseDouble(item.getPrice().replaceAll(",",""));
        Double buildcost = Double.parseDouble(item.getBuildCost().replaceAll(",",""));
        return formatter.format(((price-buildcost)/price)*100);
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

    private static void searchEveMarketerForItem(FirefoxDriver driver, String input) {
        openNewSite(driver,"https://evemarketer.com/");
        WebElement search = driver.findElementByXPath("/html/body/div/div[2]/div[1]/div/div/div[1]/div[1]/div/input");
        search.clear();
        search.sendKeys(capitalizeString(input));
        search.sendKeys(Keys.RETURN);
        WebElement firstResult = new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/div[2]/div[1]/div/div/div[1]/div[3]/ul/li[1]/h5/a")));
        firstResult.click();
    }

    private static String capitalizeString(String input){
        return input.substring(0,1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }
}
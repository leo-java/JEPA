package Java.EVE.Profitability.Application;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Item {
    private String name;
    private int id;
    private String price;
    private String requiredMaterials;
    private String buildCost;

    public Item(String name) {
        this.name = capitalizeString(name);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getRequiredMaterials() {
        return requiredMaterials;
    }

    public String getBuildCost() {
        return buildCost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRequiredMaterials(String requiredMaterials) {
        this.requiredMaterials = requiredMaterials;
    }

    public void setBuildCost(String buildCost){
        this.buildCost = buildCost;
    }

    private static String capitalizeString(String input){
        return input.substring(0,1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }
}

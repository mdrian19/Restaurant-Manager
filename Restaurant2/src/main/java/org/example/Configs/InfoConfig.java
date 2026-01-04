package org.example.Configs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class InfoConfig {

    private static double tax = 0.09;
    private static String restaurantName = "Restaurant";
    private static double toppingPrice = 2.5;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = InfoConfig.class.getClassLoader().getResourceAsStream("config.json");

            if (is == null) {
                System.out.println("Missing json file. Using default settings.");
            }
            else{
                JsonNode root = mapper.readTree(is);
                tax = root.get("tax").asDouble();
                restaurantName = root.get("restaurantName").asText();
                toppingPrice = root.get("toppingPrice").asDouble();
            }
        } catch (Exception e) {
            System.out.println("Error reading config file (invalid or corrupted). Using default settings.");
        }
    }

    public static double getTax() {
        return tax;
    }

    public static String getRestaurantName() {
        return restaurantName;
    }

    public static double getToppingPrice() {
        return toppingPrice;
    }
}


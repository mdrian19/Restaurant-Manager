package org.example.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.Entity.*;
import org.example.Repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductService {
    private final ProductRepository productRepository = new ProductRepository();

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public Optional<Product> findByName(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        return getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.trim().toLowerCase()))
                .findFirst();
    }

    public List<Product> filterProducts(List<Product> inputList, boolean vegetarian, String category, double maxPrice) {
        return inputList.stream()
                .filter(p -> {
                    if (!vegetarian) return true;
                    return p.isVegetarian();
                })
                .filter(p -> {
                    if (category == null || category.equals("Toate")) return true;
                    boolean fitsCategory = false;
                    switch(category){
                        case "Aperitiv" -> fitsCategory = p.getCategory() == Product.Category.APPETIZER;
                        case "Fel principal" -> fitsCategory = p.getCategory() == Product.Category.MAIN_COURSE;
                        case "Desert" -> fitsCategory = p.getCategory() == Product.Category.DESSERT;
                        case "Bautura racoritoare" -> fitsCategory = p.getCategory() == Product.Category.SOFT_DRINK;
                        case "Bautura alcoolica" -> fitsCategory = p.getCategory() == Product.Category.ALCOHOLIC_DRINK;
                    }
                    return fitsCategory;
                })
                .filter(p -> maxPrice <= 0 || p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public void addProduct(Product p) { productRepository.addProduct(p); }
    public void deleteProduct(Product p) { productRepository.deleteProduct(p); }

    public void seedDatabaseIfEmpty() {
        List<Product> existing = productRepository.getAllProducts();
        if (!existing.isEmpty()) return;

        System.out.println("Empty database detected. Importing initial menu from JSON...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("menu.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            List<Product> products = parseJson(root);

            for (Product p : products) {
                productRepository.addProduct(p);
            }
            System.out.println("Initial import finished: " + products.size() + " products.");
        } catch (Exception e) {
            System.out.println("Error importing initial menu: " + e.getMessage());
        }
    }

    public void exportMenuToJson(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Product> productsToExport = productRepository.getAllProducts();

            Map<String, List<Product>> wrapper = new HashMap<>();
            wrapper.put("products", productsToExport);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, wrapper);
        } catch (IOException e) {
            System.out.println("Error exporting to json: " + e.getMessage());
        }
    }

    public void importMenuFromJson(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(file);
            List<Product> importedProducts = parseJson(root);

            for (Product p : importedProducts) {
                productRepository.addProduct(p);
            }
        } catch (IOException e) {
            System.out.println("Error importing from json: " + e.getMessage());
        }
    }

    private List<Product> parseJson(JsonNode root) {
        List<Product> productsFromJSON = new ArrayList<>();
        if (root == null || !root.has("products") || !root.get("products").isArray()) {
            return productsFromJSON;
        }

        for (JsonNode node : root.get("products")) {
            try {
                String type = node.get("type").asText();
                switch (type) {
                    case "Drink" -> productsFromJSON.add(new Drink(
                            node.get("name").asText(),
                            node.get("price").asDouble(),
                            Product.Category.valueOf(node.get("category").asText()),
                            node.get("capacity").asInt()));
                    case "Food" -> productsFromJSON.add(new Food(
                            node.get("name").asText(),
                            node.get("price").asDouble(),
                            Product.Category.valueOf(node.get("category").asText()),
                            node.get("grams").asInt(),
                            node.get("vegetarian").asBoolean()));
                    case "Pizza" -> {
                        Pizza.Builder.Dough dough = Pizza.Builder.Dough.valueOf(node.get("dough").asText());
                        Pizza.Builder.Sauce sauce = Pizza.Builder.Sauce.valueOf(node.get("sauce").asText());
                        Vector<Pizza.Builder.Topping> toppings = new Vector<>();
                        for (JsonNode t : node.get("toppings")) {
                            toppings.add(Pizza.Builder.Topping.valueOf(t.asText()));
                        }
                        productsFromJSON.add(new Pizza(
                                node.get("name").asText(),
                                node.get("price").asDouble(),
                                Product.Category.valueOf(node.get("category").asText()),
                                node.get("grams").asInt(),
                                node.get("vegetarian").asBoolean(),
                                dough, sauce, toppings));
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid product, skipping entry.");
            }
        }
        return productsFromJSON;
    }

    public enum Query {
        ALL,
        VEGETARIAN_ALPHABETICAL,
        AVERAGE_PRICE_DESSERTS,
        MORE_THAN_100_RON
    }

    private Query query;

    private Stream<Product> compressProductsMap() {
        return getAllProducts().stream();
    }

    Optional<String> solveQuery(Query query) {
        Optional<String> result = Optional.empty();
        switch (query) {
            case ALL:
                result = Optional.of(this.toString());
                break;
            case VEGETARIAN_ALPHABETICAL:
                String vegList = compressProductsMap()
                        .filter(p -> p instanceof Food f && f.isVegetarian())
                        .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                        .map(Product::toString)
                        .collect(Collectors.joining("\n"));

                result = Optional.of(vegList.isEmpty() ? "Nu exista produse vegetariene." : vegList);
                break;

            case AVERAGE_PRICE_DESSERTS:
                OptionalDouble avg = compressProductsMap()
                        .filter(p -> p.getCategory() == Product.Category.DESSERT)
                        .mapToDouble(Product::getPrice)
                        .average();

                result = Optional.of(
                        avg.isPresent()
                                ? String.format("Pretul mediu al deserturilor este: %.2f RON", avg.getAsDouble())
                                : "Nu exista deserturi in meniu.");
                break;

            case MORE_THAN_100_RON:
                String expensive = compressProductsMap()
                        .filter(p -> p.getPrice() > 100.0)
                        .map(Product::toString)
                        .collect(Collectors.joining("\n"));

                result = Optional.of(
                        expensive.isEmpty()
                                ? "Nu exista produse peste 100 RON."
                                : "Produse cu pret mai mare de 100 RON:\n" + expensive);
                break;
        }
        return result;
    }
}
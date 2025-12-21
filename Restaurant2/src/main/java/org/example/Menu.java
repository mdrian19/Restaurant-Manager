package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.Entity.*;
import org.example.Repository.ProductRepository;
// import org.example.Offers.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Menu {
    private final Map<Product.Category, ObservableList<Product>> products = new HashMap<>();
    private final ProductRepository repo = new ProductRepository();

    public Menu() {
        for (Product.Category category : Product.Category.values()) {
            products.put(category, FXCollections.observableArrayList());
        }

        List<Product> productsFromDB = repo.getAllProducts();

        if (productsFromDB.isEmpty()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("menu.json")) {
                if (is == null) {
                    System.out.println("Error: menu.json file is missing. Program can't continue.");
                    System.exit(1);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(is);
                productsFromDB = loadFromJson(root);

                for (Product p : productsFromDB) {
                    try {
                        repo.addProduct(p);
                    } catch (Exception e) {
                        System.out.println("Error: unable to save products to database.");
                        System.exit(1);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: unable to read menu.json file.");
                System.exit(1);
            }
        }

        refreshUI(productsFromDB);
    }

    private void refreshUI(List<Product> productList) {
        for (Product p : productList) {
            if (p.getCategory() != null)
                products.get(p.getCategory()).add(p);
        }
    }

    public void exportDataToJSON(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Product> productsToExport = repo.getAllProducts();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, productsToExport);
        } catch (IOException e) {
            System.out.println("Error: unable to export data to JSON file.");
        }
    }

    public void importDataFromJSON(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(file);
            List<Product> importedProducts = loadFromJson(root);

            if (!importedProducts.isEmpty()) {
                for (Product p : importedProducts) {
                    repo.addProduct(p);
                }
                refreshUI(importedProducts);
            }
        } catch (IOException e) {
            System.out.println("Error: unable to import data from JSON file.");
        }
    }

    private List<Product> loadFromJson(JsonNode root) {
        List<Product> productsFromJSON = new ArrayList<>();
        if (root == null || !root.has("products") || !root.get("products").isArray()) {
            System.out.println("Error: invalid JSON structure for products.");
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
                                dough,
                                sauce,
                                toppings));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: invalid product entry in JSON file. Skipping entry.");
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

    public ObservableList<Product> getAllProductsObservable() {
        ObservableList<Product> allProducts = FXCollections.observableArrayList();
        for (ObservableList<Product> list : products.values()) {
            allProducts.addAll(list);
        }
        return allProducts;
    }

    public ObservableList<Product> getProductsByCategory(Product.Category category) {
        return products.getOrDefault(category, FXCollections.observableArrayList());
    }

    private Stream<Product> compressProductsMap() {
        return products.values().stream().flatMap(Collection::stream);
    }

    Optional<String> solveQuery(Query query) {
        Optional<String> result = Optional.empty();
        switch (query) {
            case ALL:
                result = Optional.of(this.toString());
                break;
            case VEGETARIAN_ALPHABETICAL:
                String vegList = compressProductsMap()
                        .filter(p -> p instanceof Food f && f.getVegetarian())
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

    public Optional<String> findProductByName(String input) {
        if (input == null || input.isBlank()) {
            return Optional.of("Input invalid.");
        }

        String normalized = input.trim().toLowerCase();

        return compressProductsMap()
                .filter(p -> p.getName().toLowerCase().equals(normalized))
                .findFirst()
                .map(p -> "Produsul \"" + input + "\" a fost gasit in meniu." + "\n" + p.toString())
                .or(() -> Optional.of("Produsul \"" + input + "\" nu a fost gasit."));
    }

    @Override
    public String toString() {
        StringBuilder menuString = new StringBuilder();
        menuString.append("--- Meniul Restaurantului \"La Andrei\" ---\n");

        // var offer = OfferManager.getCurrentOffer();
        // if (!(offer instanceof RegularPrice)) {
        // menuString.append("Oferta curenta: ").append(offer).append("\n\n");
        // }

        int index = 1;
        for (Product.Category category : Product.Category.values()) {
            menuString.append("\n[").append(category).append("]\n");

            List<Product> list = products.get(category);
            if (list != null && !list.isEmpty()) {
                for (Product p : list) {
                    menuString.append(index++).append(". ").append(p).append("\n");
                }
            } else {
                menuString.append("  (Nimic în aceasta categorie)\n");
            }
        }
        menuString.append("\n-----------------------------------------\n");
        return menuString.toString();
    }

}

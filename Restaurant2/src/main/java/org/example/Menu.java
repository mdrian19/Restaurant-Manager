package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// import org.example.Offers.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Menu {
    private final Map<Product.Category, ObservableList<Product>> products;

    public Menu() {
        JsonNode root = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("menu.json");

            if (is == null) {
                System.out.println("Error: menu.json file is missing. Program can't continue.");
                System.exit(1);
            }

            root = mapper.readTree(is);

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            System.out.println("Error: menu.json file is not valid JSON.");
            System.exit(1);

        } catch (IOException e) {
            System.out.println("Error: unable to read menu.json file.");
            System.exit(1);
        }

        products = new HashMap<>();
        for (Product.Category category : Product.Category.values()) {
            products.put(category, FXCollections.observableArrayList());
        }

        Vector<Product> ungroupedProducts = new Vector<>();
        assert root != null;
        for (JsonNode node : root.get("products")) {
            String type = node.get("type").asText();

            switch (type) {
                case "Drink" -> ungroupedProducts.add(new Drink(
                        node.get("name").asText(),
                        node.get("price").asDouble(),
                        Product.Category.valueOf(node.get("category").asText()),
                        node.get("capacity").asInt()
                ));

                case "Food" -> ungroupedProducts.add(new Food(
                        node.get("name").asText(),
                        node.get("price").asDouble(),
                        Product.Category.valueOf(node.get("category").asText()),
                        node.get("grams").asInt(),
                        node.get("vegetarian").asBoolean()
                ));

                case "Pizza" -> {
                    Pizza.Builder.Dough dough =
                            Pizza.Builder.Dough.valueOf(node.get("dough").asText());

                    Pizza.Builder.Sauce sauce =
                            Pizza.Builder.Sauce.valueOf(node.get("sauce").asText());

                    Vector<Pizza.Builder.Topping> toppings = new Vector<>();
                    for (JsonNode t : node.get("toppings")) {
                        toppings.add(Pizza.Builder.Topping.valueOf(t.asText()));
                    }

                    ungroupedProducts.add(new Pizza(
                            node.get("name").asText(),
                            node.get("price").asDouble(),
                            Product.Category.valueOf(node.get("category").asText()),
                            node.get("grams").asInt(),
                            node.get("vegetarian").asBoolean(),
                            dough,
                            sauce,
                            toppings
                    ));
                }
            }
        }

        for (Product p : ungroupedProducts) {
            products.get(p.getCategory()).add(p);
        }
    }

    public enum Query{
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

    Optional<String> solveQuery(Query query){
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
                                : "Nu exista deserturi in meniu."
                );
                break;

            case MORE_THAN_100_RON:
                String expensive = compressProductsMap()
                        .filter(p -> p.getPrice() > 100.0)
                        .map(Product::toString)
                        .collect(Collectors.joining("\n"));

                result = Optional.of(
                        expensive.isEmpty()
                                ? "Nu exista produse peste 100 RON."
                                : "Produse cu pret mai mare de 100 RON:\n" + expensive
                );
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

//        var offer = OfferManager.getCurrentOffer();
//        if (!(offer instanceof RegularPrice)) {
//            menuString.append("Oferta curenta: ").append(offer).append("\n\n");
//        }

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


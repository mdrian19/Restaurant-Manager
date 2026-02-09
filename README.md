# Restaurant Manager - Java

This project implements a complete management of restaurant "La Andrei" in Java. It includes dashboards for: Guests, Staff and Admin.

Guests have the option to see available items to order in the restaurant. They can filter products by price, category or whether they are vegetarian, or they can search for a specific product, but they cannot make any changes to those products and can't make orders of their own. The Guest interface appears first when the interface is running.

The guest interface has a Login button, which sends us to the Login page, and then one of the Staff or Admin dashboards.

Staff (or waiters) are the ones that make the orders based on guests' requests. They can add items to the cart and the total price (with offers or not) is calculated in real-time. Staff can also see their OWN order history.

Admin - the boss of the company. He can add or delete staff and see a general history of all their orders, they can add or delete items from the inventory, they can change active offers and they can import or export items to/from JSON files. He has the most power in this company and handles all information about staff and their password, products and all their specifications, and active offers.

Tbe database is stored in Postgres.

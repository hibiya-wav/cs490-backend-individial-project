package ds73.cs490backendmilestone2.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    private final JdbcTemplate template;

    @Autowired
    public QueryService(JdbcTemplate template) {
        this.template = template;
    }

    public List<Map<String, Object>> viewTopFilms5() {
        String sqlQuery = """
                select film.film_id, category.name, film.title, count(inventory.film_id) as rented
                from film inner join film_category
                on film.film_id = film_category.film_id
                inner join category
                on category.category_id = film_category.category_id
                inner join inventory
                on film.film_id = inventory.film_id
                inner join rental
                on inventory.inventory_id = rental.inventory_id
                group by film.film_id, category.name, film.title
                order by rented DESC
                LIMIT 5;""";
        return template.queryForList(sqlQuery);
    }

    public List<Map<String, Object>> viewFilms() {
        String sqlQuery = "select * from film";
        return template.queryForList(sqlQuery);
    }

    public List<Map<String, Object>> viewCustomer() {
        String sqlQuery = "select * from customer";
        return template.queryForList(sqlQuery);
    }

    public Map<String, Object> viewSpecificCustomerInfo(int customerId) {
        String sqlQuery = "select * from customer where customer_id = ?";
        return template.queryForMap(sqlQuery, customerId);
    }

    public List<Map<String, Object>> viewTopActors5() {
        String sqlQuery = """
                SELECT actor.actor_id, actor.first_name, actor.last_name, COUNT(film_actor.actor_id) AS movies
                FROM actor
                INNER JOIN film_actor ON actor.actor_id = film_actor.actor_id
                GROUP BY actor.actor_id, actor.first_name, actor.last_name
                ORDER BY movies desc
                Limit 5;
                """;
        return template.queryForList(sqlQuery);
    }

    public Map<String, Object> top5FilmsDetailsId(int filmId) {
        String sqlQuery = "Select * from film where film_id = ?";
        return template.queryForMap(sqlQuery, filmId);
    }

    public List<Map<String, Object>> top5ActorDetailsId(int actorId) {
        String sqlQuery = """
                SELECT actor.first_name, actor.last_name, film.title, COUNT(*) as rental_count
                FROM film
                INNER JOIN film_actor ON film.film_id = film_actor.film_id
                INNER JOIN actor ON film_actor.actor_id = actor.actor_id
                INNER JOIN inventory ON film.film_id = inventory.film_id
                INNER JOIN rental ON inventory.inventory_id = rental.inventory_id
                WHERE actor.actor_id = ?
                GROUP BY actor.first_name, actor.last_name, film.title
                ORDER BY rental_count DESC;
                """;
        return template.queryForList(sqlQuery, actorId);
    }

    public List<Map<String, Object>> allFilmsActors(String firstName, String lastName) {

        String sqlQuery = """
                SELECT film.title, film.description, film.release_year, film.rental_rate, film.rating
                FROM film
                JOIN film_actor ON film.film_id = film_actor.film_id
                JOIN actor ON film_actor.actor_id = actor.actor_id
                WHERE actor.first_name = ? and actor.last_name = ?;
                """;
        return template.queryForList(sqlQuery, firstName, lastName);
    }

    public List<Map<String, Object>> allFilmsGenre(String genreName) {
        String sqlQuery = """
                SELECT film.title, film.description, film.release_year, film.rental_rate, film.rating
                FROM film
                JOIN film_category ON film.film_id = film_category.film_id
                JOIN category ON film_category.category_id = category.category_id
                WHERE category.name = ?;
                                """;
        return template.queryForList(sqlQuery, genreName);
    }

    public List<Map<String, Object>> allCustomerInformation(String customerID) {
        String sqlQuery = """
                SELECT customer.customer_id, customer.first_name, customer.last_name, customer.email, address.address, city.city, country.country, rental.rental_id, rental.rental_date, rental.return_date, film.title AS film_title
                FROM customer
                INNER JOIN address ON customer.address_id = address.address_id
                INNER JOIN city ON address.city_id = city.city_id
                INNER JOIN country ON city.country_id = country.country_id
                LEFT JOIN rental ON customer.customer_id = rental.customer_id
                LEFT JOIN inventory ON rental.inventory_id = inventory.inventory_id
                LEFT JOIN film ON inventory.film_id = film.film_id
                WHERE customer.customer_id = ?
                ORDER BY rental.rental_date DESC
                """;
        return template.queryForList(sqlQuery, customerID);
    }

    public void deleteCustomer(String customerId) {
        String deleteRentalSql = "DELETE FROM rental WHERE customer_id = ?";
        String deletePaymentSql = "DELETE FROM payment WHERE customer_id = ?";
        String deleteCustomerSql = "DELETE FROM customer WHERE customer_id = ?";

        template.update(deleteRentalSql, customerId);
        template.update(deletePaymentSql, customerId);
        template.update(deleteCustomerSql, customerId);
    }

    public void addCustomer(String firstName, String lastName, String email, String address_id) {
        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

        String sqlQuery = "INSERT INTO customer (store_id, first_name, last_name, email, address_id, create_date, last_update, active) VALUES (1, ?, ?, ?, ?, ?, ?, 1)";
        template.update(sqlQuery, firstName, lastName, email, address_id, currentTimestamp, currentTimestamp);
    }

    public void saveEditedCustomer(int customerId, String firstName, String lastName, String email) {
        StringBuilder sqlQuery = new StringBuilder("UPDATE customer SET ");
        List<Object> params = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            sqlQuery.append("first_name = ?, ");
            params.add(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            sqlQuery.append("last_name = ?, ");
            params.add(lastName);
        }

        if (email != null && !email.isEmpty()) {
            sqlQuery.append("email = ?, ");
            params.add(email);
        }

        if (!params.isEmpty()) {
            sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());
        } else {
            return;
        }

        sqlQuery.append(" WHERE customer_id = ?");
        params.add(customerId);

        try {
            template.update(sqlQuery.toString(), params.toArray());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error updating customer details", e);
        }
    }
}

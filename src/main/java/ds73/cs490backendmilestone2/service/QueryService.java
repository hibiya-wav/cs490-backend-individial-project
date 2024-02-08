package ds73.cs490backendmilestone2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

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

    public Map<String, Object> viewSpecificCustomer(int customerId) {
        String sqlQuery = "select * from customer where customer_id = ? and active = 1";
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

//    public List<Map<String, Object>> allFilmsActors(String firstName, String lastName){
//
//        String sqlQuery = """
//                SELECT film.title, film.description, film.release_year, film.rental_rate, film.rating
//                FROM film
//                JOIN film_actor ON film.film_id = film_actor.film_id
//                JOIN actor ON film_actor.actor_id = actor.actor_id
//                WHERE actor.first_name = ? and actor.last_name = ?;
//                """;
//        return template.queryForList(sqlQuery, firstName, lastName);
//    }

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

}
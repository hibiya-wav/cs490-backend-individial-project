package ds73.cs490backendmilestone2.controller;

import ds73.cs490backendmilestone2.service.QueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/queries")

public class QueryController {
    private final QueryService sqlQueryService;

    public QueryController(QueryService serviceCall) {
        this.sqlQueryService = serviceCall;
    }

    @GetMapping("/top5films")
    public List<Map<String, Object>> viewTopFilms5() {
        return sqlQueryService.viewTopFilms5();
    }

    @GetMapping("/top5films/{filmId}")
    public Map<String, Object> top5FilmsDetailsId(@PathVariable int filmId) {
        return sqlQueryService.top5FilmsDetailsId(filmId);
    }

    @GetMapping("/top5actors")
    public List<Map<String, Object>> viewTopActors5() {
        return sqlQueryService.viewTopActors5();
    }

    @GetMapping("/top5actors/{actorId}")
    public List<Map<String, Object>> top5ActorDetailsId(@PathVariable int actorId) {
        return sqlQueryService.top5ActorDetailsId(actorId);
    }

    @GetMapping("/films")
    public List<Map<String, Object>> viewFilms() {
        return sqlQueryService.viewFilms();
    }

//    @GetMapping("/films/actors/{actorName}")
//    public List<Map<String, Object>> allFilmsActors(@PathVariable String actorName){
//        String[] inputs = actorName.trim().toUpperCase().split(" ");
//        return sqlQueryService.allFilmsActors(inputs[0], inputs[1]);
//    }


    @GetMapping("/films/actors/{actorName}")
    public List<Map<String, Object>> allFilmsActors(@PathVariable String actorName) {
        String[] inputs = actorName.trim().toUpperCase().split("\\s+");
        return sqlQueryService.allFilmsActors(inputs[0], inputs[1]);
    }

    @GetMapping("/films/genre/{genreName}")
    public List<Map<String, Object>> allFilmsGenre(@PathVariable String genreName) {
        String[] inputs = genreName.trim().toUpperCase().split("\\s+");
        return sqlQueryService.allFilmsGenre(inputs[0]);
    }

    @GetMapping("/customerinfo")
    public List<Map<String, Object>> viewCustomer() {
        return sqlQueryService.viewCustomer();
    }

    @GetMapping("/customerinfo/{customerId}")
    public Map<String, Object> viewSpecificCustomer(@PathVariable int customerId) {
        return sqlQueryService.viewSpecificCustomer(customerId);
    }
}

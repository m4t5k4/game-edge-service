package com.example.gameedgeservice.controller;

import com.example.gameedgeservice.model.FilledGameReview;
import com.example.gameedgeservice.model.Game;
import com.example.gameedgeservice.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FilledGameReviewController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${REVIEW_SERVICE_BASEURL:localhost:8052}")
    private String reviewServiceBaseUrl;

    @Value("${GAME_INFO_SERVICE_BASEURL:localhost:8051}")
    private String gameInfoServiceBaseUrl;

    @GetMapping("/rankings/user/{userId}")
    public List<FilledGameReview> getRankingsByUserId(@PathVariable Integer userId) {
        List<FilledGameReview> returnList = new ArrayList<>();

        ResponseEntity<List<Review>> responseEntityReviews =
                restTemplate.exchange("http://" + reviewServiceBaseUrl + "/reviews/user/{userId}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
                        }, userId);
        List<Review> reviews = responseEntityReviews.getBody();

        for (Review review:
            reviews) {
            Game game =
                    restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                            Game.class, review.getAppId());
            returnList.add(new FilledGameReview(game, review));
        }
        return returnList;
    }

    @GetMapping("rankings/game/developer/{developer}")
    public List<FilledGameReview> getRankingsByDeveloper(@PathVariable String developer) {
        List<FilledGameReview> returnList = new ArrayList<>();

        ResponseEntity<List<Game>> responseEntityGames =
                restTemplate.exchange("http://" + gameInfoServiceBaseUrl + "/games/developer/{developer}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Game>>() {
                        }, developer);

        List<Game> games = responseEntityGames.getBody();

        for (Game game:
            games) {
            ResponseEntity<List<Review>> responseEntityReviews =
                    restTemplate.exchange("http://" + reviewServiceBaseUrl + "reviews/{appId}",
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
                            }, game.getAppId());
            returnList.add(new FilledGameReview(game,responseEntityReviews.getBody()));
        }
        return returnList;
    }

    @GetMapping("/rankings/game/{appId}")
    public FilledGameReview getRankingsByAppId(@PathVariable Integer appId) {

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        ResponseEntity<List<Review>> responseEntityReviews =
                restTemplate.exchange("http://" + reviewServiceBaseUrl + "/reviews/{appId}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
                        }, appId);

        return new FilledGameReview(game, responseEntityReviews.getBody());
    }

    @GetMapping("rankings/{userId}/game/{appId}")
    public FilledGameReview getRankingsByUserIdAndAppId(@PathVariable Integer userId, @PathVariable Integer appId) {

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        Review review =
                restTemplate.getForObject("http://"+reviewServiceBaseUrl+"/reviews/user/" + userId + "/game/" + appId,
                        Review.class);

        return new FilledGameReview(game, review);
    }

    @PostMapping("/rankings")
    public FilledGameReview addRanking(@RequestParam Integer userId, @RequestParam Integer appId, @RequestParam Integer score) {

        Review review =
                restTemplate.postForObject("http://" + reviewServiceBaseUrl + "/reviews",
                        new Review(userId,appId,score),Review.class);

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        return new FilledGameReview(game, review);
    }

    @PutMapping("/rankings")
    public FilledGameReview updateRanking(@RequestParam Integer userId, @RequestParam Integer appId, @RequestParam Integer score) {

        Review review =
                restTemplate.getForObject("http://" + reviewServiceBaseUrl + "/reviews/user/" + userId + "/game/" + appId,
                        Review.class);
        review.setScoreNumber(score);

        ResponseEntity<Review> responseEntityReview =
                restTemplate.exchange("http://" + reviewServiceBaseUrl + "/reviews",
                        HttpMethod.PUT, new HttpEntity<>(review), Review.class);

        Review retrievedReview = responseEntityReview.getBody();

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        return new FilledGameReview(game, retrievedReview);
    }

    @DeleteMapping("/rankings/{userId}/game/{appId}")
    public ResponseEntity deleteRanking(@PathVariable Integer userId, @PathVariable Integer appId) {
        restTemplate.delete("http://"+reviewServiceBaseUrl+"/reviews/user/"+userId+"/game/"+appId);
        return ResponseEntity.ok().build();
    }
}

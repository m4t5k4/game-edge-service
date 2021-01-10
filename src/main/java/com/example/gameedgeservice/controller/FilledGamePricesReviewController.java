package com.example.gameedgeservice.controller;

import com.example.gameedgeservice.model.FilledGamePricesReview;
import com.example.gameedgeservice.model.Game;
import com.example.gameedgeservice.model.Prices;
import com.example.gameedgeservice.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class FilledGamePricesReviewController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${GAME_SERVICE_BASEURL:localhost:8051}")
    private String gameInfoServiceBaseUrl;

    @Value("${PRICES_SERVICE_BASEURL:localhost:8053}")
    private String pricesServiceBaseUrl;

    @Value("${REVIEW_SERVICE_BASEURL:localhost:8052}")
    private String reviewServiceBaseUrl;

    @GetMapping("/details/{appId}")
    public FilledGamePricesReview getDetailsByAppId(@PathVariable Integer appId) {

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        Prices prices =
                restTemplate.getForObject("http://"+pricesServiceBaseUrl+"/prices/{appId}",
                        Prices.class, appId);

        ResponseEntity<List<Review>> responseEntityReviews =
                restTemplate.exchange("http://" + reviewServiceBaseUrl + "/reviews/{appId}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
                        }, appId);

        return new FilledGamePricesReview(game, prices, responseEntityReviews.getBody());
    }
}

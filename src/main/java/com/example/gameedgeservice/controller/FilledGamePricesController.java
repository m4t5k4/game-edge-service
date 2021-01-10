package com.example.gameedgeservice.controller;

import com.example.gameedgeservice.model.FilledGamePrices;
import com.example.gameedgeservice.model.Game;
import com.example.gameedgeservice.model.Prices;
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
public class FilledGamePricesController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${GAME_SERVICE_BASEURL:localhost:8051}")
    private String gameInfoServiceBaseUrl;

    @Value("${PRICES_SERVICE_BASEURL:localhost:8053}")
    private String pricesServiceBaseUrl;

    @GetMapping("prices/game/{appId}")
    public FilledGamePrices getPricesByAppId(@PathVariable Integer appId) {

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);

        Prices prices =
                restTemplate.getForObject("http://"+pricesServiceBaseUrl+"/prices/{appId}",
                        Prices.class, appId);
        prices.id = null;

        return new FilledGamePrices(game,prices);
    }

    @GetMapping("prices/game/developer/{developer}")
    public List<FilledGamePrices> getPricesByDeveloper(@PathVariable String developer) {

        ResponseEntity<List<Game>> responseEntityGames =
                restTemplate.exchange("http://" + gameInfoServiceBaseUrl + "/games/developer/{developer}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Game>>() {
                        }, developer);
        List<Game> games = responseEntityGames.getBody();

        List<FilledGamePrices> returnList = new ArrayList<>();
        for (Game game:
            games) {
            Prices prices =
                    restTemplate.getForObject("http://"+pricesServiceBaseUrl+"/prices/{appId}",
                            Prices.class, game.getAppId());
            prices.id=null;
            returnList.add(new FilledGamePrices(game,prices));
        }

        return returnList;
    }

    @PostMapping("/prices")
    public FilledGamePrices addPrices(
            @RequestParam Integer appId,
            @RequestParam double euro,
            @RequestParam double dollar,
            @RequestParam double pound,
            @RequestParam double peso) {

        Game game = restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                Game.class, appId);

        Prices prices =
                restTemplate.postForObject("http://"+pricesServiceBaseUrl+"/prices",
                        new Prices(appId,euro,dollar,pound,peso), Prices.class);
        prices.id = null;
        return new FilledGamePrices(game, prices);
    }

    @PutMapping("/prices")
    public FilledGamePrices updatePrices(@RequestParam Integer appId,
                                         @RequestParam double euro,
                                         @RequestParam double dollar,
                                         @RequestParam double pound,
                                         @RequestParam double peso) {

        Prices prices =
                restTemplate.getForObject("http://"+pricesServiceBaseUrl+"/prices/{appId}",
                        Prices.class, appId);
        prices.setEuro(euro);
        prices.setDollar(dollar);
        prices.setPound(pound);
        prices.setPeso(peso);

        ResponseEntity<Prices> responseEntityPrices =
                restTemplate.exchange("http://" + pricesServiceBaseUrl + "/prices",
                        HttpMethod.PUT, new HttpEntity<>(prices), Prices.class);

        Prices retrievedPrices = responseEntityPrices.getBody();

        Game game =
                restTemplate.getForObject("http://"+gameInfoServiceBaseUrl+"/games/{appId}",
                        Game.class, appId);
        retrievedPrices.id = null;
        return new FilledGamePrices(game, retrievedPrices);
    }

    @DeleteMapping("prices/{appId}")
    public ResponseEntity deletePrices(@PathVariable Integer appId) {
        restTemplate.delete("http://"+pricesServiceBaseUrl+"/prices/"+appId);
        return ResponseEntity.ok().build();
    }
}

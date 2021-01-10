package com.example.gameedgeservice;

import com.example.gameedgeservice.model.Game;
import com.example.gameedgeservice.model.Prices;
import com.example.gameedgeservice.model.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilledGamePricesControllerUnitTests {

    @Value("${GAME_INFO_SERVICE_BASEURL:localhost:8051}")
    private String gameInfoServiceBaseUrl;

    @Value("${PRICES_SERVICE_BASEURL:localhost:8053}")
    private String pricesServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private Game game1 = new Game(620,"Portal 2","Valve",new Date(2011,4,19));
    private Game game2 = new Game(1145360,"Hades","Spuergiant Games",new Date(2020,9,17));

    private Prices prices1 = new Prices(620,1.63,1.99,1.43,22.79);
    private Prices prices2 = new Prices(1145360, 16.79, 19.99, 15.59, 187.19);

    List<Game> gamesByValve = Arrays.asList(game1);

    @BeforeEach
    public void initializeMockServer() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenGetPricesByAppId_thenReturnFilledGamePricesJson() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+pricesServiceBaseUrl+"/prices/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(prices1))
                );

        mockMvc.perform(get("/prices/game/{appId}",620))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.prices.appId", is(620)))
                .andExpect(jsonPath("$.prices.euro", is(1.63)));
    }

    @Test
    public void whenGetPricesByDeveloper_thenReturnFilledGamePricesJson() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/developer/Valve")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(gamesByValve))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + pricesServiceBaseUrl + "/prices/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(prices1))
                );

        mockMvc.perform(get("/prices/game/developer/{developer}","Valve"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$[0].appId", is(620)))
                .andExpect(jsonPath("$[0].prices.appId", is(620)))
                .andExpect(jsonPath("$[0].prices.euro", is(1.63)));
    }

    @Test
    public void whenAddPrices_thenReturnFilledGamePricesJson() throws Exception {

        Prices prices = new Prices(620,9.99,8.88,7.77,6.66);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + gameInfoServiceBaseUrl + "/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + pricesServiceBaseUrl + "/prices")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(prices))
                );

        mockMvc.perform(post("/prices")
                .param("appId", prices.getAppId().toString())
                .param("euro", String.valueOf(prices.getEuro()))
                .param("dollar", String.valueOf(prices.getDollar()))
                .param("pound", String.valueOf(prices.getPound()))
                .param("peso", String.valueOf(prices.getPeso()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.prices.appId", is(620)))
                .andExpect(jsonPath("$.prices.euro", is(9.99)));
    }

    @Test
    public void whenUpdatePrices_thenReturnFilledGamePricesJson() throws Exception {

        Prices prices = new Prices(620,9.99,8.88,7.77,6.66);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + pricesServiceBaseUrl + "/prices/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(prices1))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + pricesServiceBaseUrl + "/prices")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(prices))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + gameInfoServiceBaseUrl + "/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockMvc.perform(put("/prices")
                .param("appId", prices.getAppId().toString())
                .param("euro", String.valueOf(prices.getEuro()))
                .param("dollar", String.valueOf(prices.getDollar()))
                .param("pound", String.valueOf(prices.getPound()))
                .param("peso", String.valueOf(prices.getPeso()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.prices.euro", is(9.99)))
                .andExpect(jsonPath("$.prices.appId", is(620)));
    }

    @Test
    public void whenDeletePrices_thenReturnStatusOk() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + pricesServiceBaseUrl + "/prices/999")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/prices/{appId}", 999))
                .andExpect(status().isOk());
    }
}

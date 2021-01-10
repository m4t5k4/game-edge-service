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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilledGamePricesReviewControllerUnitTests {

    @Value("${REVIEW_SERVICE_BASEURL:localhost:8052}")
    private String reviewServiceBaseUrl;

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
    private Prices prices1 = new Prices(620,1.63,1.99,1.43,22.79);
    private Review review1 = new Review(001,620,3);
    private List<Review> reviewsForGame1 = Arrays.asList(review1);

    @BeforeEach
    public void initializeMockServer() { mockServer = MockRestServiceServer.createServer(restTemplate);}

    @Test
    public void whenGetDetailsByAppId_thenReturnFilledGamePricesReviewJson() throws Exception {

        //game1
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

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+reviewServiceBaseUrl+"/reviews/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewsForGame1))
                );

        mockMvc.perform(get("/details/{appId}",620))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(3)))
                .andExpect(jsonPath("$.prices.euro", is(1.63)))
                .andExpect(jsonPath("$.prices.dollar", is(1.99)))
                .andExpect(jsonPath("$.prices.pound", is(1.43)))
                .andExpect(jsonPath("$.prices.peso", is(22.79)));
    }
}

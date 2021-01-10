package com.example.gameedgeservice;

import com.example.gameedgeservice.model.Game;
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
public class FilledGameReviewControllerUnitTests {

    @Value("${REVIEW_SERVICE_BASEURL:localhost:8052}")
    private String reviewServiceBaseUrl;

    @Value("${GAME_INFO_SERVICE_BASEURL:localhost:8051}")
    private String gameInfoServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private Game game1 = new Game(620,"Portal 2","Valve",new Date(2011,4,19));
    private Game game2 = new Game(1145360,"Hades","Spuergiant Games",new Date(2020,9,17));

    private Review review1 = new Review(001,620,3);
    private Review review2 = new Review(001,1145360,3);

    private List<Review> reviewsFromUser1 = Arrays.asList(review1,review2);
    private List<Review> reviewsForGame1 = Arrays.asList(review1);
    private List<Game> gamesByValve = Arrays.asList(game1);

    @BeforeEach
    public void initializeMockServer() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenGetRankingsByUserId_thenReturnFilledGameReviewsJson() throws Exception {

        //reviewsUser1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+reviewServiceBaseUrl+"/reviews/user/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewsFromUser1))
                );

        //game1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(game1))
                );

        //game2
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/1145360")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(game2))
                );

        mockMvc.perform(get("/rankings/user/{userId}",1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$[0].appId", is(620)))
                .andExpect(jsonPath("$[0].userScores[0].userId", is(1)))
                .andExpect(jsonPath("$[0].userScores[0].scoreNumber", is(3)))
                .andExpect(jsonPath("$[1].gameTitle", is("Hades")))
                .andExpect(jsonPath("$[1].appId", is(1145360)))
                .andExpect(jsonPath("$[1].userScores[0].userId", is(1)))
                .andExpect(jsonPath("$[1].userScores[0].scoreNumber", is(3)));
    }

    @Test
    public void whenGetRankingsByDeveloper_thenReturnFilledGameReviewsJson() throws Exception {

        //games
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/developer/Valve")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(gamesByValve))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewsForGame1))
                );

        mockMvc.perform(get("/rankings/game/developer/{developer}","Valve"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$[0].appId", is(620)))
                .andExpect(jsonPath("$[0].userScores[0].userId", is(1)))
                .andExpect(jsonPath("$[0].userScores[0].scoreNumber", is(3)));
    }

    @Test
    public void whenGetRankingsByAppId_thenReturnFilledGameReviewJson() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://"+gameInfoServiceBaseUrl+"/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewsForGame1))
                );

        mockMvc.perform(get("/rankings/game/{appId}", 620))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(3)));
    }

    @Test
    public void whenGetRankingsByUserIdAndAppId_thenReturnFilledGameReviewJson() throws Exception {

        // game1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + gameInfoServiceBaseUrl + "/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        // review1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/1/game/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(review1))
                );

        mockMvc.perform(get("/rankings/{userId}/game/{appId}", 1, 620))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(3)));

    }

    @Test
    public void whenAddRanking_thenReturnFilledGameReviewJson() throws Exception {

        Review review3 = new Review(3,620,4);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(review3))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + gameInfoServiceBaseUrl + "/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockMvc.perform(post("/rankings")
                .param("userId", review3.getUserId().toString())
                .param("appId", review3.getAppId().toString())
                .param("score", review3.getScoreNumber().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.userScores[0].userId", is(3)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(4)));
    }

    @Test
    public void whenUpdateRanking_thenReturnFilledGameReviewJson() throws Exception {

        Review updatedReviewUser1Game1 = new Review(1, 620, 5);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/1/game/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(review1))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updatedReviewUser1Game1))
                );

        // GET Book 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + gameInfoServiceBaseUrl + "/games/620")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(game1))
                );

        mockMvc.perform(put("/rankings")
                .param("userId", updatedReviewUser1Game1.getUserId().toString())
                .param("appId", updatedReviewUser1Game1.getAppId().toString())
                .param("score", updatedReviewUser1Game1.getScoreNumber().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTitle", is("Portal 2")))
                .andExpect(jsonPath("$.appId", is(620)))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(5)));

    }

    @Test
    public void whenDeleteRanking_thenReturnStatusOk() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/999/game/999")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/rankings/{userId}/game/{appId}", 999, 999))
                .andExpect(status().isOk());
    }

}

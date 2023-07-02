package org.example;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.example.model.Review;
import org.example.security.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReviewRestTest extends CassandraTest {
    public static final String API_KEY_HEADER = "X-API-KEY";
    @Autowired
    private SecurityProperties securityProperties;

    @LocalServerPort
    private int port;

    private RequestSpecification req;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        req = given();
    }

    @Test
    public void testPost() {
        Review request = new Review("qwe", 3.3, 4);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(201);

        Review response = req.get("/review/qwe")
                .then()
                .statusCode(200)
                .extract().body().as(Review.class);

        assertEquals(request, response);
    }

    @Test
    public void testPostUnauth() {
        Review request = new Review("qwe", 3.3, 4);

        req.body(request).contentType(JSON)
                .post("/review")
                .then()
                .statusCode(401);
    }

    @Test
    public void testPostAlreadyExists() {
        Review request = new Review("qwe", 3.6, 7);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(201);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(409);
    }

    @Test
    public void testPut() {
        Review request = new Review("qwe", 2.123, 578);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(201);

        request.setAverageReviewScore(2.6);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .put("/review/qwe")
                .then()
                .statusCode(200);

        Review response = req.get("/review/qwe")
                .then()
                .statusCode(200)
                .extract().body().as(Review.class);

        assertEquals(request, response);
    }

    @Test
    public void testPutNotFound() {
        Review request = new Review("qwe", 2.123, 578);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(201);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .put("/review/qweq")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPutUnauth() {
        Review request = new Review("qwe", 3.3, 4);

        req.body(request).contentType(JSON)
                .put("/review/qwe")
                .then()
                .statusCode(401);
    }

    @Test
    public void testDelete() {
        Review request = new Review("qwe", 4.56, 11);

        req.body(request).contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .post("/review")
                .then()
                .statusCode(201);

        req.contentType(JSON).header(API_KEY_HEADER, securityProperties.getApiKey())
                .delete("/review/qwe")
                .then()
                .statusCode(200);

        req.get("/review/qwe").then().statusCode(404);
    }

    @Test
    public void testDeleteUnauth() {
        req.contentType(JSON)
                .delete("/review/qwe")
                .then()
                .statusCode(401);
    }
}

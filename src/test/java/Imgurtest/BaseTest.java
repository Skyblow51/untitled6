package Imgurtest;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;


import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BaseTest {
    static ResponseSpecification positiveResponseSpecification;
    static ResponseSpecification requestSpecificationWithAuth;

    static Properties properties = new Properties();
    static String token;
    static String username;


    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters( new AllureRestAssured() );
        RestAssured.baseURI = "https:/api.imgur.com/3";
        getProperties();
        token = properties.getProperty( "token" );
        username = properties.getProperty( "username" );

        positiveResponseSpecification = new ResponseSpecBuilder()
                .expectBody( "status", equalTo( 200))
                .expectBody( "success", is( true))
                .expectContentType( ContentType.JSON )
                .expectStatusCode( 200 )
                .build();

        requestSpecificationWithAuth = new ResponseSpecBuilder()
                .expectHeader("Authorization", token)
                .build();

        RestAssured.responseSpecification = positiveResponseSpecification;
    }


        private static void getProperties () {
            try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
                properties.load(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




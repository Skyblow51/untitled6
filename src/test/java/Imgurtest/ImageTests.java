package Imgurtest;


import io.restassured.path.json.JsonPath;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ImageTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/Test IMG.png";
    private final String PATH_TO_VIDEO = "src/test/resources/video.mp4";
    static String encodeFile;
    String uploadedImageId;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodeFile = Base64.getEncoder().encodeToString( byteArray );
    }


    //TEST1
    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers( "Authorization", token )
                .multiPart( "image", encodeFile )
                .formParam( "title", "ImageTitle" )
                .expect()
                .body( "success", is( true ) )
                .body( "data.id", is( notNullValue() ) )
                .when()
                .post( "https://api.imgur.com/3/image" )
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString( "data.deletehash" );
    }

    //TEST2
    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers( "Authorization", token )
                .multiPart( "image", new File( PATH_TO_IMAGE ) )
                .expect()
                .statusCode( 200 )
                .when()
                .post( "https://api.imgur.com/3/upload" )
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString( "data.deletehash" );
    }

    //TEST3
    @Test
    void FavoriteAnImageTest() {
        given()
                .headers( "Authorization", token )
                .expect()
                .statusCode( 200 )
                .when()
                .post( "https://api.imgur.com/3/image" + uploadedImageId + "/favorite" );
    }

    //TEST4
    @Test
    public void getImageByIdTest() {
        given()
                .headers( "Authorization", token )
                .expect()
                .statusCode( 200 )
                .when()
                .get( "https://api.imgur.com/3/image/" + uploadedImageId )
                .then()
                .body( "success", is( true ) )
                .body( "data.id", is( uploadedImageId ) );
    }

    //TEST5
    @Test
    public void getImageCountTest() {
        JsonPath json = given()
                .headers( "Authorization", token )
                .expect()
                .statusCode( 200 )
                .body( "success", is( true ) )
                .body( "data", is( notNullValue() ) )
                .when()
                .get( "https://api.imgur.com/3/account/" + username + "/images/count" )
                .jsonPath();

        System.out.println( json.prettify() );
    }

    //TEST6
    @Test
    public void getAllImagesTest() {
        given()
                .headers( "Authorization", token )
                .expect()
                .statusCode( 200 )
                .body( "success", is( true ) )
                .body( "data.id", is( notNullValue() ) )
                .when()
                .get( "https://api.imgur.com/3/account/me/images" );
    }

    //TEST7

    @Test
    public void uploadEmptyImageTest () {
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload");
    }

    //YEST8
    @Test
    public void getAllImagesWithoutHeadersTest() {
        given()
                .expect()
                .statusCode( 401 )
                .body( "success", is( false ) )
                .body( "data.error", is( "Authentication required" ) )
                .when()
                .get( "https://api.imgur.com/3/account/me/images" );
    }

    //TEST9
    @Test
    public void deleteImageTest () {
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("data", is(true))
                .body("success", is(true))
                .when()
                .delete("https://api.imgur.com/3/image/" + uploadedImageId);
    }



    public byte[] getFileContent() {
        byte[] byreArray = new byte[0];
        try {
            byreArray = FileUtils.readFileToByteArray( new File( PATH_TO_IMAGE ) );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return byreArray;

    }

}















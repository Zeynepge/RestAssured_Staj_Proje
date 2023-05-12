package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Nationalities_Muaz {

    Faker fake1=new Faker();

    RequestSpecification reqSpec;


    String nationalitiesName;
    String nationalitiesId;

    @BeforeClass
    public void Setup(){

        baseURI="https://test.mersys.io";

        Map<String,String > userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()

                ;

        reqSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createNationalities(){

        Map<String, String> nationalities=new HashMap<>();

        nationalitiesName=fake1.address().firstName();

        nationalities.put("name", nationalitiesName);

        nationalitiesId=
                given()
                        .spec(reqSpec)
                        .body(nationalities)
                        .log().body()

                        .when()
                        .post("/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegative(){

       Map<String, String> nationalities=new HashMap<>();

       nationalities.put("name", nationalitiesName);

        given()
                .spec(reqSpec)
                .body(nationalities)
                .log().body()

                .when()
                .post("/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400);
    }

    @Test(dependsOnMethods = "createNationalities")
    public void updateNationalites(){
        Map<String, String> nationalities=new HashMap<>();

        nationalitiesName=fake1.address().firstName();

        nationalities.put("id",nationalitiesId);
        nationalities.put("name",nationalitiesName);


        given()
                .spec(reqSpec)

                .body(nationalities)

                .when()
                .put("/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(nationalitiesId))
                .body("name",equalTo(nationalitiesName));
    }
    @Test(dependsOnMethods = "updateNationalites")
    public void deleteNationalities(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/nationality/"+nationalitiesId)

                .then()
                .log().body()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/nationality/"+nationalitiesId)

                .then()
                .log().body()
                .statusCode(400)
        ;

    }







}

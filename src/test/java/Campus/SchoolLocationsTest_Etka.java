package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SchoolLocationsTest_Etka {
    Faker faker2 = new Faker();
    RequestSpecification reqSpec;

    String locationsName;
    String locationsShortName;
    String locationType;
    int capacity;
    String locationsID;


    @BeforeClass
    public void Setup() {

        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createSchoolLocations() {

        Map<String, Object> schoolLocations = new HashMap<>();

        locationsName = faker2.name().firstName();
        locationsShortName = faker2.name().lastName();
        locationType = "CLASS";
        capacity = faker2.number().numberBetween(1, 100);

        schoolLocations.put("name", locationsName);
        schoolLocations.put("shortName", locationsShortName);
        schoolLocations.put("type", locationType);
        schoolLocations.put("capacity", capacity);
        schoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");

        locationsID =
                given()
                        .spec(reqSpec)
                        .body(schoolLocations)
                        .log().body()

                        .when()
                        .post("/school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        System.out.println("locationsID = " + locationsID);
    }

    @Test(dependsOnMethods = "createSchoolLocations")
    public void createSchoolLocationsNegative() {
        Map<String, Object> schoolLocations = new HashMap<>();

        schoolLocations.put("name", locationsName);
        schoolLocations.put("shortName", locationsShortName);
        schoolLocations.put("type", locationType);
        schoolLocations.put("capacity", capacity);
        schoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(schoolLocations)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "createSchoolLocations")
    public void updateSchoolLocations() {

        Map<String, Object> schoolLocations = new HashMap<>();

        locationsName = faker2.name().firstName();
        locationsShortName = faker2.name().lastName();
        locationType = "CLASS";
        capacity = 10;

        schoolLocations.put("id", locationsID);
        schoolLocations.put("name", locationsName);
        schoolLocations.put("shortName", locationsShortName);
        schoolLocations.put("type", locationType);
        schoolLocations.put("capacity", capacity);
        schoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");


        given()
                .spec(reqSpec)

                .body(schoolLocations)

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(locationsID))
                .body("name", equalTo(locationsName))
                .body("shortName", equalTo(locationsShortName))
                .body("type", equalTo(locationType))
                .body("capacity", equalTo(capacity))
                .body("school", equalTo("6390f3207a3bcb6a7ac977f9"))
        ;
    }

    @Test(dependsOnMethods = "updateSchoolLocations")
    public void deleteSchoolLocations() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/location/" + locationsID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteSchoolLocations")
    public void deleteSchoolLocationsNegative() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/location/" + locationsID)

                .then()
                .log().body()
                .statusCode(400)
        ;

    }


}



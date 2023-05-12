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

public class Attestations_Aynur {

    Faker fakerx=new Faker();
    RequestSpecification reqSpec;

    String Name;
    String translateName;
    String ID;


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
    public void createAttestations() {

        Map<String, String> attestations = new HashMap<>();

        Name = fakerx.address().firstName();
        translateName =fakerx.address().lastName();

        attestations.put("name", Name);
        attestations.put("translateName ",translateName);

     ID=
                given()
                        .spec(reqSpec)
                        .body(attestations)
                        .log().body()

                        .when()
                        .post("/school-service/api/attestation")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
    }
    @Test(dependsOnMethods = "createAttestations")
    public void createAttestationsNegative(){

        Map<String, String> attestations = new HashMap<>();

        attestations.put("name", Name);
        attestations.put("translateName ",translateName);

        given()
                .spec(reqSpec)
                .body(attestations)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "createAttestations")
    public void updateAttestations(){

        Map<String, String> attestations = new HashMap<>();

        Name = fakerx.address().firstName();

        attestations.put("id",ID);
        attestations.put("name", Name);

        given()
                .spec(reqSpec)
                .body(attestations)

                .when()
                .put("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(ID))
                .body("name",equalTo(Name))

        ;
    }
    @Test(dependsOnMethods = "updateAttestations")
    public void deleteAttestations(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/attestation/"+ID)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deleteAttestations")
    public void deleteAttestationsNegative(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/attestation/"+ID)

                .then()
                .log().body()
                .statusCode(400)
        ;

    }





}

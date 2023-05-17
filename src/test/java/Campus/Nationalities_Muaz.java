package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public void Setup() throws IOException {

        baseURI="https://test.mersys.io";

        String path="src/test/java/Campus/Excel_Data/Campus_Data.xlsx";

        FileInputStream inputStream=new FileInputStream(path);
        Workbook workbook= WorkbookFactory.create(inputStream);
        Sheet sheet=workbook.getSheetAt(0);

        String userName=String.valueOf(sheet.getRow(1).getCell(0));
        String password=String.valueOf(sheet.getRow(1).getCell(1));

        Map<String,String > userCredential=new HashMap<>();
        userCredential.put("username",userName);
        userCredential.put("password",password);
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

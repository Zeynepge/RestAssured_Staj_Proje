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

public class Positions_Hatice {
    Faker faker4 = new Faker();
    RequestSpecification reqSpec;

    String epositionsName;
    String epositionsShortName;
    String epositionsId;


    @BeforeClass
    public void Setup() throws IOException {

        baseURI = "https://test.mersys.io";

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
    public void createEmployeePositions() {

        Map<String, Object> ePositions = new HashMap<>();

        epositionsName = faker4.address().firstName();
        epositionsShortName=faker4.name().lastName();

        ePositions.put("name", epositionsName);
        ePositions.put("shortName",epositionsShortName);
        ePositions.put("tenantId","6390ef53f697997914ec20c2");
        //ePositions.put("active",true);
        epositionsId =
                given()
                        .spec(reqSpec)
                        .body(ePositions)
                        .log().body()

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

    }

    @Test(dependsOnMethods = "createEmployeePositions")
    public void createEmployeePositionsNegative() {

        Map<String, Object> ePositions = new HashMap<>();
        ePositions.put("name", epositionsName);
        ePositions.put("shortName",epositionsShortName);
        ePositions.put("tenantId","6390ef53f697997914ec20c2");

        given()
                .spec(reqSpec)
                .body(ePositions)
                .log().body()

                .when()
                .post("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }


    @Test(dependsOnMethods = "createEmployeePositions")
    public void updateEmployeePositions() {

        Map<String, Object> ePositions = new HashMap<>();

        epositionsName = faker4.address().firstName();
        epositionsShortName=faker4.name().lastName();

        ePositions.put("id", epositionsId);
        ePositions.put("name", epositionsName);
        ePositions.put("shortName",epositionsShortName);
        ePositions.put("tenantId","6390ef53f697997914ec20c2");

        given()
                .spec(reqSpec)

                .body(ePositions)

                .when()
                .put("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(epositionsId))
                .body("name", equalTo(epositionsName))
                .body("shortName",equalTo(epositionsShortName))
                .body("tenantId",equalTo("6390ef53f697997914ec20c2"))
        ;
    }

    @Test(dependsOnMethods = "updateEmployeePositions")
    public void deleteEmployeePositions() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/employee-position/" + epositionsId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteEmployeePositions")
    public void deleteEmployeePositionsNegative() {

        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/employee-position/" + epositionsId)

                .then()
                .log().body()
                .statusCode(400)

        ;
    }
}


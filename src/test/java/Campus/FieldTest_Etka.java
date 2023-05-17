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

public class FieldTest_Etka {
    Faker faker2 = new Faker();
    RequestSpecification reqSpec;

    String fieldName;
    String fieldCode;
    String fieldType;
    String fieldID;


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
    public void createFields() {

        Map<String, String> fields = new HashMap<>();

        fieldName = faker2.address().firstName();
        fieldCode = faker2.number().digits(5);
        fieldType = "DATE";

        fields.put("name", fieldName);
        fields.put("code", fieldCode);
        fields.put("type", fieldType);
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        fieldID =
                given()
                        .spec(reqSpec)
                        .body(fields)
                        .log().body()

                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createFields")
    public void createFieldsNegative() {
        Map<String, String> fields = new HashMap<>();

        fields.put("name", fieldName);
        fields.put("code", fieldCode);
        fields.put("type", fieldType);
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(fields)
                .log().body()

                .when()
                .post("/school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "createFields")
    public void updateFields() {

        Map<String, String> fields = new HashMap<>();

        fieldName = faker2.address().firstName();
        fieldCode = faker2.number().digits(5);
        fieldType = "DATE";

        fields.put("id", fieldID);
        fields.put("name", fieldName);
        fields.put("code", fieldCode);
        fields.put("type", fieldType);
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)

                .body(fields)

                .when()
                .put("/school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(fieldID))
                .body("name", equalTo(fieldName))
                .body("code", equalTo(fieldCode))
                .body("type", equalTo(fieldType))
                .body("schoolId", equalTo("6390f3207a3bcb6a7ac977f9"))
        ;
    }

    @Test(dependsOnMethods = "updateFields")
    public void deleteFields() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/entity-field/" + fieldID)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteFields")
    public void deleteFieldsNegative() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/entity-field/" + fieldID)

                .then()
                .log().body()
                .statusCode(400)
        ;

    }


}


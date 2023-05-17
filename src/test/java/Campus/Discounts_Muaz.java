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

public class Discounts_Muaz {

    Faker fake1 = new Faker();

    RequestSpecification reqSpec;

    String description;
    String integrationCode;
    int priority;
    String discountId;

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
    public void createDiscount() {

        Map<String, Object> discount = new HashMap<>();

        description = fake1.address().fullAddress();
        integrationCode = fake1.number().digits(5);
        priority = fake1.number().numberBetween(1211,54324);
        discount.put("description",description);
        discount.put("code",integrationCode);
        discount.put("priority",priority);

        discountId=
                given()
                        .spec(reqSpec)
                        .body(discount)
                        .log().body()

                        .when()
                        .post("/school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


    }

    @Test(dependsOnMethods = "createDiscount")
    public void createDiscountNegative(){
        Map<String, Object> discount = new HashMap<>();
        discount.put("description",description);
        discount.put("code",integrationCode);
        discount.put("priority",priority);

        given()
                .spec(reqSpec)
                .body(discount)
                .log().body()

                .when()
                .post("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "createDiscount")
    public void updateDiscount(){
        Map<String, Object> discount = new HashMap<>();

        description = fake1.address().firstName();
        integrationCode = fake1.address().firstName();
        priority = fake1.number().numberBetween(548,21211);

        discount.put("id",discountId);
        discount.put("description",description);
        discount.put("code",integrationCode);
        discount.put("priority",priority);


        given()
                .spec(reqSpec)

                .body(discount)

                .when()
                .put("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(discountId))
                .body("description",equalTo(description))
                .body("priority",equalTo(priority))
                .body("code",equalTo(integrationCode));
    }

    @Test(dependsOnMethods = "updateDiscount")
    public void deleteDiscount(){

        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/discounts/"+discountId)

                .then()
                .log().body()
                .statusCode(200);

    }
    @Test(dependsOnMethods = "deleteDiscount")
    public void deleteDiscountNegative(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/discounts/"+discountId)

                .then()
                .log().body()
                .statusCode(400);
    }

}

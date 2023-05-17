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

public class Attestations_Aynur {

    Faker fakerx=new Faker();
    RequestSpecification reqSpec;

    String Name;
    String translateName;
    String ID;


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
    public void createAttestations() {

        Map<String, String> attestations = new HashMap<>();

        Name = fakerx.address().firstName();
        translateName =fakerx.address().lastName();

        attestations.put("name", Name);
       // attestations.put("translateName ",translateName);
        // bu satir fazlalik

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

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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SubjectCategories_Aynur {

    Faker fakery=new Faker();
    RequestSpecification reqSpec;

    String Name;
    String translateName;
    String Code;
    String subID;


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
                //.addHeader("Autorization","Bearer....")
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }
    @Test
    public void createSubjectCategories() {

        Map<String, String> subjectCategories = new HashMap<>();

        Name = fakery.address().firstName();
        translateName =fakery.address().lastName();
        Code=fakery.number().digits(5);

        subjectCategories .put("name", Name);
        subjectCategories .put("translateName ",translateName);
        subjectCategories.put("code", Code);

        subID=
                given()
                        .spec(reqSpec)
                        .body(subjectCategories )
                        .log().body()

                        .when()
                        .post("/school-service/api/subject-categories")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createSubjectCategories")
    public void createSubjectCategoriesNegative(){

        Map<String, String> subjectCategories = new HashMap<>();

        subjectCategories.put("name", Name);
        subjectCategories.put("translateName ",translateName);
        subjectCategories.put("code", Code);

        given()
                .spec(reqSpec)
                .body(subjectCategories)
                .log().body()

                .when()
                .post("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "createSubjectCategories")
    public void updateSubjectCategories(){

        Map<String, String> subjectCategories = new HashMap<>();

        Name = fakery.address().firstName();
        Code=fakery.number().digits(23456);
        subjectCategories.put("id",subID);
        subjectCategories.put("name", Name);
        subjectCategories.put("translateName ",translateName);
        subjectCategories.put("code", Code);

        given()
                .spec(reqSpec)
                .body(subjectCategories)

                .when()
                .put("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(subID))
                .body("name",equalTo(Name))
                .body("code",equalTo(Code))
               // .body("translateName ",equalTo(translateName))
        ;
    }

    @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategories(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/subject-categories/"+subID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteSubjectCategories")
    public void deleteSubjectCategoriesNegative(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/subject-categories/"+subID)

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
}

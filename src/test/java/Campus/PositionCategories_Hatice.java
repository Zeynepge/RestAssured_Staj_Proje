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

public class PositionCategories_Hatice {

    Faker faker3=new Faker();
    RequestSpecification reqSpec;

    String  positionCategoryName;
    String positionCategoriesId;


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
    public void createPositionCategories(){

        Map<String, String> positionCategory=new HashMap<>();

        positionCategoryName=faker3.address().firstName();

        positionCategory.put("name",positionCategoryName);


        positionCategoriesId=
        given()
                .spec(reqSpec)
                .body(positionCategory)
                .log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
        ;
    }
    @Test(dependsOnMethods = "createPositionCategories")
    public void createPositionCategoriesNegative(){

        Map<String, String> positionCategory=new HashMap<>();
        positionCategory.put("name",positionCategoryName);

               given()
                        .spec(reqSpec)
                        .body(positionCategory)
                        .log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .log().body()
                        .statusCode(400)

        ;
    }


    @Test(dependsOnMethods = "createPositionCategories")
    public void updatePositionCategories() {

        Map<String, String> positionCategory = new HashMap<>();

        positionCategoryName = faker3.address().firstName();

        positionCategory.put("name", positionCategoryName);
        positionCategory.put("id",positionCategoriesId);

        given()
                .spec(reqSpec)

                .body(positionCategory)

                .when()
                .put("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", equalTo(positionCategoryName))
                .body("id",equalTo(positionCategoriesId))
        ;
    }
    @Test(dependsOnMethods = "updatePositionCategories")
    public void deletePositionCategories(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/position-category/" +positionCategoriesId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deletePositionCategories")
    public void deletePositionCategoriesNegative() {
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/position-category/" + positionCategoriesId)

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    }


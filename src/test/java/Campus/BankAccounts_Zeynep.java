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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
public class BankAccounts_Zeynep{
    Faker faker1=new Faker();
    RequestSpecification reqSpec;

    String bankAccountName;
    String bankAccountIban;
    String integrationCode;
    String currency;
    String bankID;


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
    public void createBankAccount() {

        Map<String, String> bankAccount = new HashMap<>();

        bankAccountName = faker1.address().firstName();
        bankAccountIban = faker1.number().digits(5);
        currency = "KZT";
        integrationCode =faker1.number().digits(5);

        bankAccount.put("name", bankAccountName);
        bankAccount.put("iban", bankAccountIban);
        bankAccount.put("currency", currency);
        bankAccount.put("integrationCode", integrationCode);
        bankAccount.put("schoolId","6390f3207a3bcb6a7ac977f9");

        bankID=
                given()
                        .spec(reqSpec)
                        .body(bankAccount)
                        .log().body()

                        .when()
                        .post("/school-service/api/bank-accounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void createBankAccountNegative(){
        Map<String, String> bankAccount = new HashMap<>();

        bankAccount.put("name", bankAccountName);
        bankAccount.put("iban", bankAccountIban);
        bankAccount.put("currency", currency);
        bankAccount.put("integrationCode", integrationCode);
        bankAccount.put("schoolId","6390f3207a3bcb6a7ac977f9");



        given()
                .spec(reqSpec)
                .body(bankAccount)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "createBankAccount")
    public void updateBankAccount(){

        Map<String, String> bankAccount = new HashMap<>();

        bankAccountName = faker1.address().firstName();
        bankAccountIban = faker1.number().digits(5);
        currency = "KZT";
        integrationCode =faker1.number().digits(5);

        bankAccount.put("id",bankID);
        bankAccount.put("name", bankAccountName);
        bankAccount.put("iban", bankAccountIban);
        bankAccount.put("currency", currency);
        bankAccount.put("integrationCode", integrationCode);
        bankAccount.put("schoolId","6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)

                .body(bankAccount)

                .when()
                .put("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(bankID))
                .body("name",equalTo(bankAccountName))
                .body("iban",equalTo(bankAccountIban))
                .body("currency",equalTo(currency))
                .body("integrationCode",equalTo(integrationCode))
                .body("schoolId",equalTo("6390f3207a3bcb6a7ac977f9"))
        ;
    }
    @Test(dependsOnMethods = "updateBankAccount")
    public void deleteBankAccount(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/bank-accounts/"+bankID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteBankAccount")
    public void deleteBankAccountNegative(){
        given()
                .spec(reqSpec)

                .when()

                .delete("/school-service/api/bank-accounts/"+bankID)

                .then()
                .log().body()
                .statusCode(400)
        ;

    }





}

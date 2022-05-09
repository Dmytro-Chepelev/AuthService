package net.absoft;

import static org.testng.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest {

  @Test(
          groups = { "positive" }
  )
  public void testSuccessfulAuthentication() {
    Response response = new AuthenticationService().authenticate("user1@test.com", "password1");
    SoftAssert softAssert = new SoftAssert();
      softAssert.assertEquals(response.getCode(), 200, "Response code should be 200");
      softAssert.assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
    softAssert.assertAll();
  }

  @Test(
          groups = { "negative" }
  )
  public void testAuthenticationWithWrongPassword() {
    Response response = new AuthenticationService()
        .authenticate("user1@test.com", "wrong_password1");
    assertEquals(response.getCode(), 401, "Response code should be 401");
    assertEquals(response.getMessage(), "Invalid email or password",
        "Response message should be \"Invalid email or password\"");
  }

  @Test(
          groups = { "negative" }
  )
  public void testAuthenticationWithEmptyEmail() {
    Response response = new AuthenticationService().authenticate("", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Email should not be empty string",
        "Response message should be \"Email should not be empty string\"");
  }

  @Test(
          groups = { "negative" }
  )
  public void testAuthenticationWithInvalidEmail() {
    Response response = new AuthenticationService().authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
  }

  @Test(
          groups = { "negative" }
  )
  public void testAuthenticationWithEmptyPassword() {
    Response response = new AuthenticationService().authenticate("user1@test.com", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }

  @Test(
          groups = { "negative" },
          dataProvider = "failAuthentications"
  )
  public void testFailAuthentications(String testName, String email, String password, Integer errorCode, String errorMessage) {
    Response response = new AuthenticationService().authenticate(email, password);
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), errorCode.intValue(), "Wrong error code:");
    sa.assertEquals(response.getMessage(), errorMessage,"Wrong error message:");
    sa.assertAll("Unexpected response by '" + testName + "' test");
  }

  @DataProvider(
          name = "failAuthentications",
          parallel = true
  )
  public Object[][] dataProviderFailAuthentications () {
    return new Object[][]{
            // String testName, String email, String password, Integer errorCode, String errorMessage
            {"empty email",     "",               "password1",        400, "Email should not be empty string"},
            {"invalid email",   "user1",          "password1",        400, "Invalid email"},
            {"empty password",  "user1@test.com", "",                 400, "Password should not be empty string"},
            {"wrong password",  "user1@test.com", "wrong_password1",  401, "Invalid email or password"}
    };
  }
}

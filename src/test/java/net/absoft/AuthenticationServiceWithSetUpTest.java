package net.absoft;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AuthenticationServiceWithSetUpTest {

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
}

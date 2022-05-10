package net.absoft;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/*

Следует обратить внимание, что одновременная реализация в текущем заданий пунктов
- Налаштуйте параллельний запуск тестів
- Додайте setup методи
ведет к образованию межпоточных гонок, так как единственное осмысленное применение setup в этом задании
это порождение нового экземпляра AuthenticationService для каждого теста (а все тесты запускаются парллельно).

Для демонстрации этого поведения системы в каждом методе-тесте выводится хеш объекта-сервиса,
который генерируется в setUp() методе. При параллельном исполнении тестов видно, что значение
instanceService идентично во всех потоках, так как из-за гонки потоков значение поля экземпляра было переписано
последним запущенным тестом. А далее все тесты используют это значение, т.е. обращаются к одному и тому же
экземпляру AuthenticationService. (CPU AMD Ryzen 5 2600 = 6 core / 12 thread)

Так как сам класс AuthenticationService по совпадению является потокобезопасным,
то ситуация гонки не оказывает влияния на успешное прохождение тестов, хотя по факту нарушает их смысл.

Для избежания конкурентного доступа к объекту-сервису я использовал ThreadLocal

При последовательном исполнении тестов-методов гонка потоков отсутствует и каждый тест получает уникальный объет-сервис.


<suite name="setupDemo-suite" parallel="methods">

instance object: @428a6ba1    thread object: @57cec0ab
instance object: @428a6ba1    thread object: @3edd2084
instance object: @428a6ba1    thread object: @428a6ba1
instance object: @428a6ba1    thread object: @123f951f


<suite name="setupDemo-suite" >

instance object: @76577407    thread object: @76577407
instance object: @6e3d1998    thread object: @6e3d1998
instance object: @32d7f78e    thread object: @32d7f78e
instance object: @47b98ce8    thread object: @47b98ce8
 */

public class AuthenticationServiceWithSetUpTest {

    private AuthenticationService instanceService;
    private final ThreadLocal<AuthenticationService> threadService = new ThreadLocal<>();

    @BeforeMethod(groups = "setupDemo")
    public void setUp() {
        AuthenticationService localService = new AuthenticationService();
        instanceService = localService;
        threadService.set(localService);
    }

    private void printHashCode() {
        System.out.printf(
                "instance object: @%x    thread object: @%x%n",
                instanceService.hashCode(),
                threadService.get().hashCode()
        );
    }

    @Test(groups = "setupDemo")
    public void testAuthenticationWithWrongPassword() {
        printHashCode();
        Response response = threadService.get().authenticate("user1@test.com", "wrong_password1");
        assertEquals(response.getCode(), 401, "Response code should be 401");
        assertEquals(response.getMessage(), "Invalid email or password",
                "Response message should be \"Invalid email or password\"");
    }

    @Test(groups = "setupDemo")
    public void testAuthenticationWithEmptyEmail() {
        printHashCode();
        Response response = threadService.get().authenticate("", "password1");
        assertEquals(response.getCode(), 400, "Response code should be 400");
        assertEquals(response.getMessage(), "Email should not be empty string",
                "Response message should be \"Email should not be empty string\"");
    }

    @Test(groups = "setupDemo")
    public void testAuthenticationWithInvalidEmail() {
        printHashCode();
        Response response = threadService.get().authenticate("user1", "password1");
        assertEquals(response.getCode(), 400, "Response code should be 400");
        assertEquals(response.getMessage(), "Invalid email",
                "Response message should be \"Invalid email\"");
    }

    @Test(groups = "setupDemo")
    public void testAuthenticationWithEmptyPassword() {
        printHashCode();
        Response response = threadService.get().authenticate("user1@test.com", "");
        assertEquals(response.getCode(), 400, "Response code should be 400");
        assertEquals(response.getMessage(), "Password should not be empty string",
                "Response message should be \"Password should not be empty string\"");
    }
}

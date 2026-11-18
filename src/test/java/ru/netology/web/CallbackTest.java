package ru.netology.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CallbackTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);

        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    @Test
        //Проверка успешной отправки заявки с использованием поиска по классам
    void shouldTestV1() {
        List<WebElement> elements = driver.findElements(By.className("input__control"));
        elements.get(0).sendKeys("Василий");
        elements.get(1).sendKeys("+79270000000");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(
                By.cssSelector("[data-test-id=order-success]")
        ).getText();
        assertEquals(
                "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                text.trim()
        );
    }

    @Test
        // Проверка успешной отправки заявки с использованием data-test-id
    void shouldTestV2() {
        WebElement form = driver.findElement(By.tagName("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Василий");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79270000000");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button.button")).click();
        String text = driver.findElement(
                By.cssSelector("[data-test-id=order-success]")
        ).getText();
        assertEquals(
                "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                text.trim()
        );
    }

    @Test
        // --- оба поля пустые
    void shouldShowErrorIfAllFieldsAreEmpty() {
        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();
        WebElement error = driver.findElement(By.className("input__sub"));

        assertEquals("Поле обязательно для заполнения", error.getText());
    }

    // Проверка ошибки, если телефон не заполнен
    @Test
    void shouldShowErrorIfPhoneIsEmpty() {
        driver.findElement(By.name("name")).sendKeys("Иванов Иван");
        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        WebElement error = driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"));

        assertEquals("Поле обязательно для заполнения", error.getText());
    }

    // Проверка имени с цифрами
    @Test
    void shouldShowErrorIfNameContainsDigits() {
        driver.findElement(By.name("name")).sendKeys("Ив123");
        driver.findElement(By.name("phone")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        WebElement name = driver.findElement(By.name("name"));

        System.out.println("INPUT: " + name.getAttribute("outerHTML"));
        System.out.println("GRANDPARENT: " + name.findElement(By.xpath("../..")).getAttribute("outerHTML"));
    }

    // Проверка имени с латинскими буквами
    @Test
    void shouldShowErrorIfNameContainsLatinLetters() {
        driver.findElement(By.name("name")).sendKeys("Ivan");
        driver.findElement(By.name("phone")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        WebElement name = driver.findElement(By.name("name"));

        System.out.println("INPUT: " + name.getAttribute("outerHTML"));
        System.out.println("GRANDPARENT: " + name.findElement(By.xpath("../..")).getAttribute("outerHTML"));
    }

    // Проверка имени со специальными символами
    @Test
    void shouldShowErrorIfNameContainsSpecialCharacters() {
        driver.findElement(By.name("name")).sendKeys("Иван@Иванов");
        driver.findElement(By.name("phone")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        WebElement name = driver.findElement(By.name("name"));

        System.out.println("INPUT: " + name.getAttribute("outerHTML"));
        System.out.println("GRANDPARENT: " + name.findElement(By.xpath("../..")).getAttribute("outerHTML"));
    }

    // Проверка телефона без знака +
    @Test
    void shouldShowErrorIfPhoneDoesNotStartWithPlus() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка телефона с латинскими буквами
    @Test
    void shouldShowErrorIfPhoneContainsLatinLetters() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("abcdefghijk");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка телефона с кириллическими буквами
    @Test
    void shouldShowErrorIfPhoneContainsCyrillicLetters() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("абвгдежзийк");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка телефона с избыточным количеством цифр
    @Test
    void shouldShowErrorIfPhoneHasMoreThanElevenDigits() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+799912345678");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка телефона с недостаточным количеством цифр
    @Test
    void shouldShowErrorIfPhoneHasLessThanElevenDigits() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+7999123456");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка телефона со специальными символами
    @Test
    void shouldShowErrorIfPlusIsNotAtTheBeginningOfPhone() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+7$991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();

        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        assertTrue(driver.findElement(By.name("phone"))
                .findElement(By.xpath("../.."))
                .findElement(By.className("input__sub"))
                .getText().contains("Телефон указан неверно"));
    }

    // Проверка отображения ошибки, если не установлено согласие
    @Test
    void shouldShowErrorIfAgreementIsNotChecked() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+79991234567");
        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        boolean isInvalid = driver.findElement(By.cssSelector("[data-test-id='agreement']"))
                .getAttribute("class").contains("input_invalid");

        assertTrue(isInvalid);
    }

    // Проверка успешной отправки заявки
    @Test
    void shouldShowSuccessMessageAfterClickContinue() {
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.xpath("//button[contains(., 'Продолжить')]")).click();

        boolean isSuccessMessageDisplayed = driver.findElement(By.cssSelector("[data-test-id='order-success']"))
                .isDisplayed();

        assertTrue(isSuccessMessageDisplayed);
    }
}

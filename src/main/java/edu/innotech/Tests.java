package edu.innotech;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


public class Tests {
    private WireMockServer wireMockServer;
    private Student stud;

    @BeforeEach
    public void SetUp() {
        // Запускаем WireMock сервер
        wireMockServer = new WireMockServer(5352);
        wireMockServer.start();

        // Создаем студента
        stud = new Student("vasia");
    }

    @AfterEach
    public void tearDown() {
        // Останавливаем WireMock сервер
        wireMockServer.stop();
    }

    @Test

    public void testAddValidGrade() throws Exception {
            // Настраиваем заглушку для проверки оценки
            wireMockServer.stubFor(get(urlEqualTo("/checkGrade?grade=5"))
                    .willReturn(aResponse().withBody("true")));

            // Добавляем оценку
            stud.addGrade(5);

            // Проверяем, что оценка добавлена
            List<Integer> grades = stud.getGrades();
            assertEquals(1, grades.size());
            assertEquals(5, grades.get(0));
        }

        @Test
        public void testAddInvalidGrade () {
            // Настраиваем заглушку для проверки оценки
            wireMockServer.stubFor(get(urlEqualTo("/checkGrade?grade=11"))
                    .willReturn(aResponse().withBody("false")));

            // Проверяем, что при добавлении неправильной оценки выбрасывается исключение
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                stud.addGrade(11);
            });

            String expectedMessage = "11 is wrong grade";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            // Проверяем, что оценка не была добавлена
            List<Integer> grades = stud.getGrades();
            assertTrue(grades.isEmpty());
        }
    }
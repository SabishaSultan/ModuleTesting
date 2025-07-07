package Main.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class test {

    // Тесты для GET /student/{id}
    @Test
    public void getStudentWithIdAndName_WhenExists() {
        int id = 1;
        given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(id))
                .body("name", Matchers.is(notNullValue()))
                .body("marks", Matchers.is(notNullValue()));
    }

    // Тест, когда студент не найден
    @Test
    public void getStudentWithIdAndName_WhenNotExists() {

        given()
                .baseUri("http://localhost:8080/student/-1")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(404)
                .body("error", is("Not found"));
    }

    // Тесты для POST /student
    @Test
    public void addStudent_Returns201_WhenStudentIsCreated() {
        int id = 99;
        String st = "Student{id:99, name:Misha, marks:[3, 4]}";
        given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .body(st)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", Matchers.is(notNullValue())); // Проверяем, что возвращается ID нового студента
    }

    // Тест для успешного обновления существующего студента
    @Test
    public void addStudent_Returns201_WhenStudentIsUpdated() {
        String studentJson = "Student{id:1, name:Vasia, marks:[5, 4]}";

        // Предполагаем, что студент с ID 1 уже существует
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201)
                .body("Id", equalTo(1)); // Проверяем, что возвращается тот же ID
    }

    // Тест для обработки случая, когда имя не указано
    @Test
    public void addStudent_Returns400_WhenNameIsMissing() {
        String studentJson = "Student{id:2, name: , marks:[4, 4]}";
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post()
                .then()
                .statusCode(400); // Ожидаем статус 400 Bad Request
    }

    // Тест для обработки случая с некорректным ID (например, отрицательное число)
    @Test
    public void addStudent_Returns400_WhenIdIsNegative() {
        String studentJson = "Student{id:-1, name:Vasia, marks:[5, 4]}";
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post()
                .then()
                .statusCode(400); // Ожидаем статус 400 Bad Request
    }

    // Тест для обработки случая с пустым массивом оценок
    @Test
    public void addStudent_Returns201_WhenMarksAreEmpty() {
        String studentJson = "Student{id:1, name:Vasia, marks:[ ]}";
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("Id", is(notNullValue())); // Проверяем, что возвращается ID нового студента
    }

    // Тесты для DELETE /student/{id}
    @Test
    public void deleteStudent_RemovesStudent_WhenExists() {
        given()
                .baseUri("http://localhost:8080/student/1")
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .statusCode(200);
    }

    @Test
    public void deleteStudent_Returns404_WhenNotExists() {
        given()
                .baseUri("http://localhost:8080/student/-1")
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .statusCode(404);
    }

    // Тесты для GET /topStudent
    @Test
    public void getTopStudent_Returns200_AndEmptyBody_WhenNoStudents() {
        Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        assertEquals("", response.getBody().asString());
    }

    @Test
    public void getTopStudent_Returns200_AndEmptyBody_WhenNoGrades() {
        given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(isEmptyOrNullString());
    }
    //Тест на успешный ответ с одним студентом, имеющим максимальную среднюю оценку
    @Test
    public void getTopStudent_ReturnsOneStudent_WhenHasMaxAverageGrade() {
        Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .extract().response();

        Map<String, Object> student = response.jsonPath().getMap("$");
        assertNotNull(student);
        assertTrue((Integer) student.get("Id") > 0);
        assertNotNull(student.get("Vasia"));

        List<Integer> marks = (List<Integer>) student.get("marks");
        assertNotNull(marks);
        assertFalse(marks.isEmpty());
    }

    //Тест на успешный ответ с несколькими студентами, имеющими одинаковую максимальную среднюю оценку и одинаковое количество оценок
    @Test
    public void testGetTopStudent_MultipleEqualStudents() {
        Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .extract().response();

        List<Map<String, Object>> students = response.jsonPath().getList("$");
        assertFalse(students.isEmpty());

        // Проверяем, что все студенты имеют одинаковую максимальную среднюю оценку
        int maxMarksCount = ((List<Integer>) students.get(0).get("marks")).size();
        for (Map<String, Object> student : students) {
            List<Integer> marks = (List<Integer>) student.get("marks");
            assertEquals(maxMarksCount, marks.size());

            assertTrue((Integer) student.get("Id") > 0);
            assertNotNull(student.get("Sasha"));
            assertFalse(marks.isEmpty());
        }
    }
//Тест на успешный ответ с несколькими студентами, имеющими одинаковую максимальную среднюю оценку, но разное количество оценок
    @Test
    public void getTopStudent_ReturnsMultipleStudents_WhenEqualMaxAverageGrade() {
        Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .extract().response();

        List<Map<String, Object>> students = response.jsonPath().getList("$");
        assertFalse(students.isEmpty());

        // Находим студента с наибольшим количеством оценок среди тех, кто имеет максимальную среднюю оценку
        Map<String, Object> topStudent = null;
        int maxMarksCount = -1;

        for (Map<String, Object> student : students) {
            List<Integer> marks = (List<Integer>) student.get("marks");
            int marksCount = marks.size();

            if (marksCount > maxMarksCount) {
                maxMarksCount = marksCount;
                topStudent = student;
            }
        }

        assertNotNull(topStudent);
        assertTrue((Integer) topStudent.get("Id") > 0);
        assertNotNull(topStudent.get("Petia"));

        List<Integer> marks = (List<Integer>) topStudent.get("marks");
        assertFalse(marks.isEmpty());
    }
}
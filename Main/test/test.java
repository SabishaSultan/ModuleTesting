package Main.test;

import Main.edu.innotech.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
//import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        String st = "{id:10, name:Misha, marks:[3, 4, 5]}";
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
        String studentJson = "{id:1, name:Vasia, marks:[5, 4, 2]}";

        // Предполагаем, что студент с ID 1 уже существует
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("Id", equalTo(1)); // Проверяем, что возвращается тот же ID
    }

    // добавляет студента в базу, если ID null, то возвращается назначенный ID, код 201.
    @Test
    public void postStudentAssignsNewIdWhenIdIsNull() {
        String newStudentJson = "{id: null, name: Dilya, marks: [5,4,2]}";

        int statusCode = given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(newStudentJson)
                .when()
                .post()
                .then()
                .extract().statusCode();

        assertEquals(201, statusCode);
    }

    // Тест для обработки случая, когда имя не указано
    @Test
    public void addStudent_Returns400_WhenNameIsMissing() {
        String studentJson = "{id:2, name: , marks:[4, 4, 3]}";
        given()
                .baseUri("http://localhost:8080/student/")
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post()
                .then()
                .statusCode(400); // Ожидаем статус 400 Bad Request
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
    //код 200 и пустое тело, если студентов в базе нет
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

    //код 200 и пустое тело, если ни у кого из студентов в базе нет оценок
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
    public void getTopStudent_ReturnsOneStudent_WhenHasMaxAverageGrade() throws Exception {
        Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.asString();
        System.out.println("Ответ: " + responseBody);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> students = objectMapper.readValue(responseBody, List.class);
        assertNotNull(students);
        assertFalse(students.isEmpty());

        Map<String, Object> student = students.get(0);
        assertNotNull(student);

        assertTrue((Integer) student.get("id") > 0);
        assertEquals("Kolya", student.get("name"));

        List<Integer> marks = (List<Integer>) student.get("marks");
        assertNotNull(marks);
        assertFalse(marks.isEmpty());
    }

    //Тест на успешный ответ с несколькими студентами, имеющими одинаковую максимальную среднюю оценку и одинаковое количество оценок

//Тест на успешный ответ с несколькими студентами, имеющими одинаковую максимальную среднюю оценку, но разное количество оценок
@Test
    public void getTopStudent_ReturnsMultipleStudents_WhenEqualMaxAverageGrade() throws Exception {
       Response response = given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .extract().response();

    ObjectMapper objectMapper = new ObjectMapper();

    // Десериализуем JSON-ответ в список студентов
    List<Student> students = objectMapper.readValue(response.asString(), new TypeReference<List<Student>>() {});

    assertFalse(students.isEmpty());
        // Находим студента с наибольшим количеством оценок среди тех, кто имеет максимальную среднюю оценку
    Student topStudent = null;
    int maxMarksCount = -1;

    for (Student student : students) {
        List<Integer> marks = student.getMarks();
        int marksCount = marks.size();

        if (marksCount > maxMarksCount) {
            maxMarksCount = marksCount;
            topStudent = student;
        }
    }

    assertNotNull(topStudent);
    assertTrue(topStudent.getId() > 0);
    assertNotNull(topStudent.getName());

    List<Integer> marks = topStudent.getMarks();
    assertFalse(marks.isEmpty());
}
}
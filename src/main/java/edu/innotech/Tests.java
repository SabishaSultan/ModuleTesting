package edu.innotech;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class Tests {
    @Test
    @Tag("smoke")
    @DisplayName("Проверка записи оценок")
    public void testAddGrade() {

        Student stud = new Student("vasia");
        List<Integer> grades = stud.getGrades();
        stud.addGrade(3);
        stud.addGrade(4);
        stud.addGrade(5);
        Assertions.assertEquals(2, grades.size());
        Assertions.assertTrue(grades.contains(3));
        Assertions.assertTrue(grades.contains(4));
        Assertions.assertTrue(grades.contains(5));

        System.out.println("Тест \"Проверка записи оценок\"");
    }

    @Test
    @Tag("critical")
    @DisplayName("Проверка возвращения копии списка оценок")
    public void testGetGradesReturnsCopy() {
        Student stud = new Student("vasia");
        stud.addGrade(3);

        List<Integer> grades = stud.getGrades();

        // Проверяем, что можно получить оценки
        Assertions.assertEquals(1, grades.size());

        // Изменяем копию и проверяем, что оригинальный список не изменился
        grades.add(5);

        Assertions.assertEquals(1, stud.getGrades().size());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Проверка невалидных оценок")
    public void marksNotInRange() {
        List<Integer> lst = Arrays.asList(0, 1, 6, 7);
        Student stud = new Student("vasia");
        Assertions.assertThrows(IllegalArgumentException.class, () -> stud.addGrade(lst.get(0)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> stud.addGrade(lst.get(1)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> stud.addGrade(lst.get(2)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> stud.addGrade(lst.get(3)));
    }
    @Test
    @Tag("critical")
    @DisplayName("Обработка исключений при добавлении неправильных оценок.")
    public void testAddInvalidGrade() {
        Student stud = new Student("vasia");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stud.addGrade(6); // Неправильная оценка
        });

        Assertions.assertEquals("6 is wrong grade", exception.getMessage());
    }

    @Test
    @Tag("critical")
    @DisplayName("Правильность методов equals и hashCode.")
    public void testEqualsAndHashCode() {
        Student stud1 = new Student("vasia");
        Student stud2 = new Student("sasha");

        stud1.addGrade(3);
        stud2.addGrade(4);

        Assertions.assertEquals(stud1, stud2);
        Assertions.assertEquals(stud1.hashCode(), stud2.hashCode());

        stud2.addGrade(4);

        Assertions.assertNotEquals(stud1, stud2);
    }

    @Test
    @Tag("critical")
    @DisplayName("Корректное представление объекта в строковом формате")
    public void testToString() {
        Student stud = new Student("vasia");
        stud.addGrade(3);

        String expectedString = "Student{name='vasia', marks=[3]}";

        Assertions.assertEquals(expectedString, stud.toString());
    }
}


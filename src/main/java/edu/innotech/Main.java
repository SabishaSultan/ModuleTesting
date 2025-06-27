package edu.innotech;
import org.testng.annotations.Test;

public class Main {
    public static void main (String [] args) {
        Student stud = new Student("vasia");
        stud.addGrade(3);
        stud.getGrades().add(105);
        System.out.println(stud);
       // Method[] mets= Test.class.getDeclaredMethods();
       // mets[0].isAnnotationPresent(Test.class);
    }
}

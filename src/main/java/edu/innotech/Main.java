package edu.innotech;
import org.testng.annotations.Test;

public class Main {
    public static void main (String [] args) {
        Tests tests = new Tests();
        tests.testAddGrade();
        tests.marksNotInRange();

       // Method[] mets= Test.class.getDeclaredMethods();
       // mets[0].isAnnotationPresent(Test.class);
    }
}

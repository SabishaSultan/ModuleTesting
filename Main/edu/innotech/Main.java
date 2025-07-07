package Main.edu.innotech;

public class Main {
    public static void main (String [] args) {
        Student stud = new Student("vasia");
        stud.addMarks(3);
        stud.getMarks().add(105);
        System.out.println(stud);
       // Method[] mets= Test.class.getDeclaredMethods();
       // mets[0].isAnnotationPresent(Test.class);
    }
}

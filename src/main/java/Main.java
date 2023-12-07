import java.util.Scanner;

public class Main {
    public Main() {
    }

    public static void main(String[] var0) {
        System.out.println("Enter size of matrix: ");
        Scanner var1 = new Scanner(System.in);
        int var2 = var1.nextInt();
        Matrix var3 = new Matrix(var2);
        var3.generate3diagonalMatrix();
        var3.show();
        var3.getSolution();
        System.out.println();
        System.out.println("Press any key");
        var1.next();
    }
}
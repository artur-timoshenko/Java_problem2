import java.util.Random;

public class Matrix {
    Double[][] matrix;    //coefficients of the system of linear equations
    Double[] freeMembers;   //free members of the system of linear equations
    private int size;
    private Double[] solution;
    private Double[] alpha;   //auxiliary coefficients for Thompson's algorithm   (right)
    private Double[] beta;    //auxiliary coefficients for Thompson's algorithm   (right)
    private Double[] gamma;   //auxiliary coefficients for Thompson's algorithm   (left)
    private Double[] omega;   //auxiliary coefficients for Thompson's algorithm   (left)

    Matrix(int size) {
        this.size = size;
    }


    public void generate3diagonalMatrix() {    //generate a three-diagonal matrix
        matrix = new Double[size][size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = i - 1; j < i + 2; j++) {
                if (j < 0 || j == size) continue;
                matrix[i][j] = (rand.nextInt(10) + 1) * 1.;
            }
        }

        for (int i = 1; i < size - 1; i++) {                    //make the diagonal advantage
            double tmp = matrix[i][i - 1] + matrix[i][i + 1] + 1.;
            matrix[i][i] = tmp;
        }
        matrix[0][0] = matrix[0][1] + 1.;
        matrix[size - 1][size - 1] = matrix[size - 1][size - 2] + 1.;

        freeMembers = new Double[size];        //generate free members
        for (int i = 0; i < size; i++)
            freeMembers[i] = (rand.nextInt(10) + 1) * 1.;
    }

    public void show() {            //print matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(matrix[i][j] + "    ");
            System.out.println(" |  " + freeMembers[i]);
        }
    }

    public void getSolution() {
        final int p = size / 2;            //divide our matrix on the two parts
        //search auxiliary coefficients for Thompson's algorithm  (left part of matrix)
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                leftCoeff(p);
            }
        });
        thread1.start();
        //search auxiliary coefficients for Thompson's algorithm  (right part of matrix)
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                rightCoeff(p);
            }
        });
        thread2.start();

        while (thread1.isAlive() || thread2.isAlive()) {
        }  //wait until the threads finish their work

        solution = new Double[size];
        //compute the X(p), p=[n/2]
        solution[p] = (alpha[p + 1] * omega[p + 1] + beta[p + 1]) / (1. - alpha[p + 1] * gamma[p + 1]);

        //compute X(i), 0<=i<p
        thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                rightSolution(p);
            }
        });
        thread2.start();
        //compute X(i), p<i<size
        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                leftSolution(p);
            }
        });
        thread1.start();

        for (int i = 0; i < size; i++)    //print solution
            System.out.print(solution[i] + " ");
    }
    public Double[] retrieveSolution() {
        return this.solution;
    }

    public void leftCoeff(int n) {
        gamma = new Double[size];   //auxiliary coefficients
        omega = new Double[size];   //auxiliary coefficients

        gamma[size - 1] = (-1.) * matrix[size - 1][size - 2] / matrix[size - 1][size - 1];   //-An/Cn
        omega[size - 1] = freeMembers[size - 1] / matrix[size - 1][size - 1];            //Fn/Cn

        for (int i = size - 2; i > n; i--) {
            // -Ai/ ( Ci+Bi*GAMMAi+1 )
            gamma[i] = (-1.) * matrix[i][i - 1] / (matrix[i][i] + matrix[i][i + 1] * gamma[i + 1]);
            //(Fi-Bi*OMEGAi+1)/(Ci+Bi*GAMMAi+1)
            omega[i] = (freeMembers[i] - matrix[i][i + 1] * omega[i + 1]) / (matrix[i][i] + matrix[i][i + 1] * gamma[i + 1]);
        }
    }

    public void leftSolution(int n) {
        for (int i = n + 1; i < size; i++) {
            solution[i] = gamma[i] * solution[i - 1] + omega[i];  //	Xi=GAMMAi*Xi-1 + BETAi
        }
    }

    public void rightCoeff(int n) {
        alpha = new Double[n + 2];  //auxiliary coefficients
        beta = new Double[n + 2];   //auxiliary coefficients

        alpha[1] = (-1.) * matrix[0][1] / matrix[0][0];  //-B0/C0
        beta[1] = freeMembers[0] / matrix[0][0];       //F0/C0

        for (int i = 2; i <= n + 1; i++) {
            alpha[i] = (-1.) * matrix[i - 1][i] /    // -B(i-1)/(A(i-1)*alpha(i-1)+C(i-1))
                    (matrix[i - 1][i - 2] * alpha[i - 1] + matrix[i - 1][i - 1]);
            //(F(i-1)-A(i-1) *beta(i-1))/(A(i-1)*alpha(i-1)+C(i-1))
            beta[i] = (freeMembers[i - 1] - matrix[i - 1][i - 2] * beta[i - 1]) /
                    (matrix[i - 1][i - 2] * alpha[i - 1] + matrix[i - 1][i - 1]);
        }
    }


    public void rightSolution(int n) {
        solution[n - 1] = (freeMembers[n - 1] - matrix[n - 1][n - 2] * beta[n - 1]) /     //Xn=(Fn-An*BETAn)/(An*ALPHAn+Cn)
                (matrix[n - 1][n - 1] + matrix[n - 1][n - 2] * alpha[n - 1]);
        for (int i = n - 2; i >= 0; i--) {
            solution[i] = alpha[i + 1] * solution[i + 1] + beta[i + 1];   //	Xi=ALPHAi+1*Xi+1 + BETAi+1
        }
    }

}

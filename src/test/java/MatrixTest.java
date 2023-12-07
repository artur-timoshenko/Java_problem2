import org.junit.Test;
import static org.junit.Assert.*;

public class MatrixTest {

    @Test
    public void testMatrixSolution() {
        Matrix matrix = new Matrix(3);
        Double[][] testMatrix = {
                {3.0, 2.0, 0.0},
                {1.0, 4.0, 1.0},
                {0.0, 3.0, 2.0}
        };
        Double[] testFreeMembers = {5.0, 11.0, 8.0};


        matrix.matrix = testMatrix;
        matrix.freeMembers = testFreeMembers;


        matrix.getSolution();


        Double[] expectedSolution = {3.0, 4.0, 2.0};


        assertArrayEquals(expectedSolution, matrix.retrieveSolution());
    }
}

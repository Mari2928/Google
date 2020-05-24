import java.util.*;
/**
 * This program receives integer N as an input, create size N of three 2D arrays A, B, and C 
 * and store some values. Then, it calculates the product of matrices A*B and store the result into matrix C.
 * @author ashigam
 * Please change the initial value in args on line 12 to change the size of matrices.
 */
public class STEP_W2_HW1 {
	public static void main(String args[])
	  {
		// input the size N
		args = new String[]{"20"};
		
		if (args.length != 1) {
			System.out.println("usage: java Matrix N");
			return;
		}
		int n = Integer.parseInt(args[0]);

	    double[][] a = new double[n][n]; // Matrix A
	    double[][] b = new double[n][n]; // Matrix B
	    double[][] c = new double[n][n]; // Matrix C
	      
	    // Initialize the matrices to some values.    
	    int i, j;
	    for (i = 0; i < n; i++) {
	    	for (j = 0; j < n; j++) {
	    		a[i][j] = i * n + j;
		        b[i][j] = j * n + i;
		        c[i][j] = 0;	     
		        }
	    }
	    long begin = System.currentTimeMillis();	 
	    
	    /**************************************/
	    /* Write code to calculate C = A * B. */
	    /**************************************/
	    
	    // to test calculation (comment out by default)
//	    double[][] a = new double[][]{{1,2},{3,4}}; // Matrix A
//	    double[][] b = new double[][]{{5,6},{7,8}}; // Matrix B
//	    double[][] c = new double[2][2]; // Matrix C
	    
	    // go through matrix C
	    for(int row= 0; row < c.length; row++) {
	    	for(int col = 0; col < c[0].length; col++) {
	    		
	    		// store matrix A * matrix B in each element of C
	    		for(int colA = 0; colA < a[0].length; colA++)  {
	    			int rowB = colA;
	    			c[row][col] += a[row][colA]*b[rowB][col];  		
	    		}    		 		
	    	}	    		
	    }
//	    // to test calculation (comment out by default)    
//	    for(int row= 0; row < c.length; row++) {
//	    	for(int col = 0; col < c[0].length; col++) {
//	    		System.out.print(c[row][col]);	    		
//	    	}  		
//	    	System.out.println();
//	    }  
	    /**************************************/

	    long end = System.currentTimeMillis();
	    System.out.printf("time: %.6f sec\n", (end - begin) / 1000.0);

	    // Print C for debugging. Comment out the print before measuring the execution time.
	    double sum = 0;
	    for (i = 0; i < n; i++) {
	    	for (j = 0; j < n; j++) {
		        sum += c[i][j];		        
		        //System.out.printf("c[%d][%d]=%f\n", i, j, c[i][j]);
		      }	      
	    }
	    // Print out the sum of all values in C.
	    // This should be 450 for N=3, 3680 for N=4, and 18250 for N=5.
	    System.out.printf("sum: %.6f\n", sum);	    
		    		    
	  }	
}
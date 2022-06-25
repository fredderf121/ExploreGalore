package com.fred.exploregalore.utils;

import org.ejml.simple.SimpleMatrix;

public class SimpleMatrixUtils {

    /**
     * Taken from <a href="https://stackoverflow.com/a/62849708/8402160">https://stackoverflow.com/a/62849708/8402160</a>
     */
    public static double[][] toDoubleArray(SimpleMatrix matrix) {
        double[][] array = new double[matrix.numRows()][matrix.numCols()];
        for (int r = 0; r < matrix.numRows(); r++) {
            for (int c = 0; c < matrix.numCols(); c++) {
                array[r][c] = matrix.get(r, c);
            }
        }
        return array;
    }
}

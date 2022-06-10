package com.fred.exploregalore.math;

import com.fred.exploregalore.learning_notes.removedCode.PathBuilderRemoved;
import net.minecraft.core.BlockPos;

public class PathBuilderTest {

    public static final int NUM_ITERATIONS = 10;

    public static void linearPath2DTest(int n) {
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k < n; k++) {
                    PathBuilder pathBuilder = new PathBuilder().linearPath2D(new BlockPos(0, 0, 0), new BlockPos(i, j, k));
                }
            }
        }
    }

    public static void linearPath2DDepreciatedTest(int n) {
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k < n; k++) {
                    PathBuilderRemoved pathBuilder = new PathBuilderRemoved().linearPath2D(new BlockPos(0, 0, 0), new BlockPos(i, j, k), true);
                }
            }
        }
    }

    public static void linearPath3DTest(int n) {
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k < n; k++) {
                    PathBuilder pathBuilder = new PathBuilder().linearPath3D(new BlockPos(0, 0, 0), new BlockPos(i, j, k));
                }
            }
        }
    }

    public static void linearPath3DDepreciatedTest(int n) {
        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k < n; k++) {
                    PathBuilderRemoved pathBuilder = new PathBuilderRemoved().linearPath3D(new BlockPos(0, 0, 0), new BlockPos(i, j, k), true);
                }
            }
        }
    }

    public static void compare2DAlgorithms() {
        for (int i = 0; i < 100; i++) {
            linearPath2DTest(10);
            linearPath2DDepreciatedTest(10);
        }

        long startTime1 = System.nanoTime();
        linearPath2DTest(100);
        long endTime1 = System.nanoTime();

        long startTime2 = System.nanoTime();
        linearPath2DDepreciatedTest(100);
        long endTime2 = System.nanoTime();

        System.out.println("Non deprecated: " + (endTime1 - startTime1) + " Depreciated: " + (endTime2 - startTime2));

    }

    public static void compare3DAlgorithms() {
        // Non deprecated: 17528502100 Depreciated: 25884297400
        for (int i = 0; i < 100; i++) {
            linearPath3DTest(10);
            linearPath3DDepreciatedTest(10);
        }

        long startTime1 = System.nanoTime();
        linearPath3DTest(100);
        long endTime1 = System.nanoTime();

        long startTime2 = System.nanoTime();
        linearPath3DDepreciatedTest(100);
        long endTime2 = System.nanoTime();

        System.out.println("Non deprecated: " + (endTime1 - startTime1) + " Depreciated: " + (endTime2 - startTime2));

    }

    public static void main(String[] args) {
        compare3DAlgorithms();
   }
}

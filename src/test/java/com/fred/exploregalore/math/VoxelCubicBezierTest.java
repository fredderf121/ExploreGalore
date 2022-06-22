package com.fred.exploregalore.math;

import com.fred.exploregalore.math.parametricfunctions.VoxelCubicBezier;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VoxelCubicBezierTest {



    @Test
    public void simpleMatrixToString() {
        System.out.println(new SimpleMatrix(new float[][]{
                {-1, 3, -3, 1},
                {3, -6, 3, 0},
                {-3, 3, 0, 0},
                {1, 0, 0, 0}
        }));
    }

    @Test
    public void simpleMatrixExtractRow() {
        val test = new SimpleMatrix(new float[][]{
                {1, 1},
                {0, 0}
        });
        System.out.println(test.extractVector(true, 0));
    }

    @Test
    public void instantiateIterator() {
        val testCurve = new VoxelCubicBezier(
                new Vec3i(0, 0, 0),
                new Vec3i(5, 0, 0),
                new Vec3i(0, 0, 0),
                new Vec3i(5, 0, 0));

        for (val vec : testCurve) {
            ;
            //System.out.println(vec);
        }

    }
}

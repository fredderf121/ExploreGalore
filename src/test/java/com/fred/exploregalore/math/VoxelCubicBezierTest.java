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
import org.openjdk.nashorn.internal.ir.annotations.Ignore;

import static org.junit.jupiter.api.Assertions.fail;

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
    public void instantiateIteratorSimpleOneDimension() {
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

    @Test
    public void instantiateIteratorComplexThreeDimension() {
        val testCurve = new VoxelCubicBezier(
                new Vec3i(0, 0, 0),
                new Vec3i(100, 200, 100),
                new Vec3i(0, 150, 0),
                new Vec3i(20, 0, 30));

        int counter = 0;
        for (val vec : testCurve) {
            counter++;
            if (counter > 1000) {
                fail("Infinite loop!");
            }
            ;
            //System.out.println(vec);
        }

    }

    @Ignore
    public void minecraftTestCommands() {
        // General test
        // /exploregalore drawbezierpath minecraft:blue_wool ~ ~ ~ ~100 ~200 ~ ~ ~150 ~ ~20 ~ ~

        // Testing 6-connectivity
        // /exploregalore drawbezierpath minecraft:blue_wool ~ ~ ~ ~100 ~200 ~100 ~ ~150 ~ ~20 ~ ~30
    }
}

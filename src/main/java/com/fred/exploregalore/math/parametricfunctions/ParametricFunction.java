package com.fred.exploregalore.math.parametricfunctions;


import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

/**
 * <p>
 * Class representing a parametric function of the form,
 * </p>
 * <p>
 * f(t) = &lt;x(t), y(t), z(t)&gt;
 * </p>
 * <p>
 * Note: The term "Explicit" is used frequently to describe a parametric function. Here,
 * it is defined as a function f(t) such that at least one of x(t), y(t), or z(t) is simply
 * t, OR -t (negative). We include the negative, as the parameter variable "t" is generally ALWAYS increasing.
 * </p>
 * <p>
 * Ex: f(t) = &lt;cos(t), t, e^t&gt;. Here, f(t) is explicit with respect to y.
 * </p>
 */
public class ParametricFunction implements DoubleFunction<Vec3> {

    protected static final double DOUBLE_ERROR_TOLERANCE = 0.01;


    protected final DoubleUnaryOperator xFunc;
    protected final DoubleUnaryOperator yFunc;
    protected final DoubleUnaryOperator zFunc;

    protected final IndependentVariable independentVariable;

    private double startT;
    private double endT;

    public ParametricFunction(
            IndependentVariable independentVariable,
            DoubleUnaryOperator xFunc,
            DoubleUnaryOperator yFunc,
            DoubleUnaryOperator zFunc,
            double startT,
            double endT) {
        this.independentVariable = independentVariable;
        this.xFunc = xFunc;
        this.yFunc = yFunc;
        this.zFunc = zFunc;
        this.startT = startT;
        this.endT = endT;
    }

    public ParametricFunction(
            IndependentVariable independentVariable,
            DoubleUnaryOperator xFunc,
            DoubleUnaryOperator yFunc,
            DoubleUnaryOperator zFunc
    ) {
        this(independentVariable, xFunc, yFunc, zFunc, 0, 0);
    }


    /**
     * <p>
     * Creates a linear parametric function from two points in space, with the option
     * to make it explicit with respect to one variable.
     * </p>
     * <p>
     * This particular method <b>requires</b> that the user provides which variable
     * is the independent one. Other overloaded methods may not.
     * </p>
     * <p>
     * f(0) returns the starting point coordinates. <br>
     * If f(t) is implicit, f(1) returns the ending point coordinates. <br>
     * If f(t) is explicit, f(independentVariableNormalizingFactor) returns the ending
     * point coordinates.
     * </p>
     */
    public static ParametricFunction createLinearParametricFunction(IndependentVariable independentVariable, Vec3 startPoint, Vec3 endPoint) {

        Vec3 distance = endPoint.subtract(startPoint);

        // The result is assumed to be NONZERO
        // Absolute as we want to preserve any negatives in front of the independent variable.
        double independentVariableNormalizingFactor = Math.abs(switch (independentVariable) {
            case X -> distance.x();
            case Y -> distance.y();
            case Z -> distance.z();
            case IMPLICIT -> 1;
        });

        Vec3 coefficients = distance.scale(1 / independentVariableNormalizingFactor);


        return new ParametricFunction(independentVariable,
                t -> coefficients.x() * t + startPoint.x(),
                t -> coefficients.y() * t + startPoint.y(),
                t -> coefficients.z() * t + startPoint.z(),
                0,
                independentVariableNormalizingFactor);


    }

    /**
     * Overridden method that simply takes two points and creates a linear parametric function.
     * The function created is <b>explicit</b>, and is based on the first non-zero component in
     * the direction vector (x, z, or y, in that order - this is the order that Minecraft uses,
     * where height is y rather than the conventional z).
     */
    public static ParametricFunction createLinearParametricFunction(Vec3 startPoint, Vec3 endPoint) {
        Vec3 distance = endPoint.subtract(startPoint);

        // To avoid division by zero in the other overridden method
        if (distance.equals(Vec3.ZERO)) {
            return createLinearParametricFunction(IndependentVariable.IMPLICIT, startPoint, endPoint);
        } else {
            if (distance.x() != 0) {
                return createLinearParametricFunction(IndependentVariable.X, startPoint, endPoint);
            } else if (distance.z() != 0) {
                return createLinearParametricFunction(IndependentVariable.Z, startPoint, endPoint);
            } else {
                return createLinearParametricFunction(IndependentVariable.Y, startPoint, endPoint);
            }
        }
    }

    /**
     * <p>
     *     Create a parametric helix function given the provided parameters.
     * </p>
     * <p>
     *     An example helix function is {@code f(t) = <cos(t), sin(t), t>}.
     * </p>
     * <p>
     *     The helix has the following properties:
     * </p>
     * <ul>
     *     <li>
     *         't' ranges from [0, distance along axis of symmetry from startPoint to endPoint].
     *     </li>
     * </ul>
     * @param startPoint The starting point.
     * @param endPoint The ending point. This is a work in progress, so the current restriction is that the startPoint
     *                 and endPoint MUST only differ in one coordinate axis (x, y, or z). If not, the resulting function
     *                 will have undefined properties.
     * @param radius The radius of the generated helix.
     * @param numSpirals The number of full turns the helix makes from startPoint to endPoint.
     */
    public static ParametricFunction createHelixParametricFunction(Vec3 startPoint, Vec3 endPoint, int radius, int numSpirals) {
        Vec3 pointDifference = endPoint.subtract(startPoint);

        // trigScalingFactor is the 'a' in cos(at), or sin(at)
        double trigScalingFactor = 2 * Math.PI / pointDifference.y() * numSpirals;
        // Only implement y for now, will add others once done debugging
        if (pointDifference.y() != 0 + DOUBLE_ERROR_TOLERANCE) {
            return new ParametricFunction(
                    IndependentVariable.Y,
                    t -> radius * (Math.cos(trigScalingFactor * t) - 1) + startPoint.x(),
                    t -> t + startPoint.y(),
                    t -> radius * Math.sin(trigScalingFactor * t) + startPoint.z(),
                    0,
                    pointDifference.y()
                    );
        }
        return null;
    }


    @Override
    public Vec3 apply(double number) {
        return new Vec3(xFunc.applyAsDouble(number), yFunc.applyAsDouble(number), zFunc.applyAsDouble(number));
    }

    /**
     * Wrapper function for {@link DoubleFunction#apply(double)} so that uses a more familiar
     * {@code f(t)} mathematical syntax.
     */
    public Vec3 f(double t) {
        return apply(t);
    }


    public enum IndependentVariable {
        X, Y, Z, IMPLICIT
    }


    public class ParametricFunctionIterator implements Iterator<Vec3> {

        double t;
        double step;

        // TODO: add parameterized constructors for t and step
        public ParametricFunctionIterator(double step) {
            this.t = startT;
            this.step = step;
        }

        public ParametricFunctionIterator(int numSteps) {
            this((endT - startT) / numSteps);
        }

        @Override
        public boolean hasNext() {
            return t <= endT + DOUBLE_ERROR_TOLERANCE;
        }

        @Override
        public Vec3 next() {
            double oldT = t;
            t += step;
            return apply(oldT);
        }
    }

    public IndependentVariable getIndependentVariable() {
        return independentVariable;
    }

    public double getStartT() {
        return startT;
    }

    public double getEndT() {
        return endT;
    }
}

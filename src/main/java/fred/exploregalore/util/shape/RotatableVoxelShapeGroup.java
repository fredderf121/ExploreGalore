package fred.exploregalore.util.shape;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a group of four VoxelShapes who are identical in shape, but rotated in pi/2 (90) angle intervals (i.e. in the four cardinal
 * directions). The axis of rotation is parallel to the y-axis, and passes through (8, 0, 8) - the center of a
 * 16 x 16 x 16 cube.<br><br>
 * The user passes in a series of cuboids/boxes that are to be constructed into the VoxelShape, and the program performs
 * the rotations and creates the four VoxelShapes.<br>
 * Note that the cuboids passed in should have vertexes that range from (0, 0, 0) to (16, 16, 16) - this class handles
 * the conversion to a percentage when creating the VoxelShape.
 */
public class RotatableVoxelShapeGroup {

    /**
     * The point at which the axis of rotation passes through. Note that we don't provide a <i>direction</i for the
     * vector - this is because the rotation is always done parallel to the y-axis.
     */
    public static final Vec3d ROTATION_AXIS_POINT = new Vec3d(8, 0, 8);


    private final Map<Direction, VoxelShape> DIRECTIONAL_SHAPES;


    /**
     * Converts the boundingBoxes into 4 VoxelShapes, one for each cardinal direction.
     * Note: the North shape is the default shape, and thus experiences no rotation.
     * @param boundingBoxes the boundingBoxes that make up the VoxelShape. Its vertices should range from (0, 0, 0) to
     *                      (16, 16, 16) - as one would find in a .json model file. This class handles the conversion
     *                      to a percentage when creating the final VoxelShapes.
     */
    public RotatableVoxelShapeGroup(Box... boundingBoxes) {
        DIRECTIONAL_SHAPES = new HashMap<>();

        DIRECTIONAL_SHAPES.put(Direction.NORTH, createVoxelShapeWithRotation(0, Direction.Axis.Y, ROTATION_AXIS_POINT, boundingBoxes));
        DIRECTIONAL_SHAPES.put(Direction.SOUTH, createVoxelShapeWithRotation((float) Math.PI, Direction.Axis.Y, ROTATION_AXIS_POINT, boundingBoxes));
        DIRECTIONAL_SHAPES.put(Direction.WEST, createVoxelShapeWithRotation((float) (Math.PI / 2.0), Direction.Axis.Y, ROTATION_AXIS_POINT, boundingBoxes));
        DIRECTIONAL_SHAPES.put(Direction.EAST, createVoxelShapeWithRotation((float) (Math.PI * 3.0 / 2.0), Direction.Axis.Y, ROTATION_AXIS_POINT, boundingBoxes));

    }

    public VoxelShape getShapeFromOrientation(Direction direction) {
        return DIRECTIONAL_SHAPES.get(direction);
    }

    /**
     * Creates a VoxelShape provided an array of boundingBox. A few steps are performed to each boundingBox:
     * <ol>
     *     <li>
     *         Rotates the box according to the specified angle.
     *     </li>
     *     <li>
     *         Converts each box to a VoxelShape
     *     </li>
     *     <li>
     *         Reduces the stream of VoxelShapes by unionising them together to create the final VoxelShape.
     *     </li>
     * </ol>
     *
     * @param angle the angle, counterclockwise, that the boundingBox should be rotated to.
     * @param axis the axis of which the rotation line is parallel to
     * @param rotationLinePoint a point of which the rotation line intersects - note that to form a line, one needs a
     *                          direction vector (which in this case is parallel to an axis), and a point that the
     *                          line intersects.
     * @param boundingBoxes the boxes of which the VoxelShape is to be created from
     * @return the VoxelShape created from the rotated boundingBox.
     */
    private VoxelShape createVoxelShapeWithRotation(float angle, Direction.Axis axis, Vec3d rotationLinePoint, Box... boundingBoxes) {

        return
                Arrays.stream(boundingBoxes)
                        .map(box -> rotateBoxAroundAxis(angle, axis, rotationLinePoint, box)) // rotation by 180 deg ccw
                        .map(box -> VoxelShapes.cuboidUnchecked(box.minX / 16.0D, box.minY / 16.0D, box.minZ / 16.0D, box.maxX / 16.0D, box.maxY / 16.0D, box.maxZ / 16.0D)) // conversion to VoxelShape
                        .reduce(VoxelShapes.empty(), VoxelShapes::union); // combining the VoxelShapes


     /*   return switch (direction) {
            case SOUTH -> Arrays.stream(boundingBoxes)
                    .map(box -> rotateBoxAroundAxis((float) Math.PI, Direction.Axis.Y, rotationLinePoint, box)) // rotation by 180 deg ccw
                    .map(VoxelShapes::cuboid) // conversion to VoxelShape
                    .reduce(VoxelShapes.empty(), VoxelShapes::union); // combining the VoxelShapes

            case EAST -> Arrays.stream(boundingBoxes)
                    .map(box -> rotateBoxAroundAxis((float) (Math.PI / 2), Direction.Axis.Y, rotationLinePoint, box)) // rotation by 90 deg ccw
                    .map(VoxelShapes::cuboid) // conversion to VoxelShape
                    .reduce(VoxelShapes.empty(), VoxelShapes::union); // combining the VoxelShapes

            case WEST -> Arrays.stream(boundingBoxes)
                    .map(box -> rotateBoxAroundAxis((float) (3 / 2 * Math.PI), Direction.Axis.Y, rotationLinePoint, box)) // rotation by 270 deg ccw
                    .map(VoxelShapes::cuboid) // conversion to VoxelShape
                    .reduce(VoxelShapes.empty(), VoxelShapes::union); // combining the VoxelShapes
            // Default is used for NORTH (and for any other directions that technically shouldn't be passed in.
            default -> Arrays.stream(boundingBoxes)
                    .map(VoxelShapes::cuboid) // conversion to VoxelShape (no rotation; NORTH is the default orientation)
                    .reduce(VoxelShapes.empty(), VoxelShapes::union); // combining the VoxelShapes

        };*/
    }


    /**
     * Rotates a cuboid about a rotation line. The rotation line must be parallel to the x or y or z axis.
     *
     * @param axis              the axis that the rotation line is parallel to
     * @param rotationLinePoint the point at which the rotation line passes through
     * @return a box whose points have been rotated about the specified line.
     */
    private Box rotateBoxAroundAxis(float angle, Direction.Axis axis, Vec3d rotationLinePoint, Box box) {
        Vec3d rotatedCorner1 = rotatePointAroundAxis(angle, axis, new Vec3d(box.minX, box.minY, box.minZ), rotationLinePoint);
        Vec3d rotatedCorner2 = rotatePointAroundAxis(angle, axis, new Vec3d(box.maxX, box.maxY, box.maxZ), rotationLinePoint);

        return new Box(rotatedCorner1, rotatedCorner2);
    }

    /**
     * Rotates a point around a given line (that is parallel to an axis) by a specified angle. To accomplish this, a few transformations are done:
     * <ol>
     *     <li>
     *         The point is translated such that the rotation line now lies on the origin.
     *     </li>
     *     <li>
     *         The point is rotated about the specified x, y, or z axis
     *     </li>
     *     <li>
     *         The point is translated 'back', such that the rotation line is now at its original position.
     *     </li>
     * </ol>
     *
     * @param angle             the angle to rotate the point by
     * @param axis              the axis of which the point is to be rotated counterclockwise by
     * @param point             the point to be rotated
     * @param rotationLinePoint a point in which the rotation lines passes through
     * @return the rotated point's coordinates
     */
    private Vec3d rotatePointAroundAxis(float angle, Direction.Axis axis, Vec3d point, Vec3d rotationLinePoint) {
        return switch (axis) {
            case X -> point.subtract(rotationLinePoint)
                    .rotateX(angle)
                    .add(rotationLinePoint);

            case Y -> point.subtract(rotationLinePoint)
                    .rotateY(angle)
                    .add(rotationLinePoint);

            case Z -> point.subtract(rotationLinePoint)
                    .rotateZ(angle)
                    .add(rotationLinePoint);
        };
    }


}

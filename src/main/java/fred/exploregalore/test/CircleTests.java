package fred.exploregalore.test;

import java.lang.reflect.Array;
import java.util.*;

public class CircleTests {
    public static void main(String[] args) {

        List<Set<List<Integer>>> circleSets = new ArrayList<>(8);

        for (int radius = 1; radius <= 8; radius++) {
            circleSets.add(new HashSet<>());

            int[] nextCircleCoor = new int[]{radius, 0};
            // We compute one octant, and then use symmetry to get the rest
            while (nextCircleCoor[1] <= nextCircleCoor[0]) {

                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(nextCircleCoor[0], nextCircleCoor[1])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(-nextCircleCoor[0], nextCircleCoor[1])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(nextCircleCoor[0], -nextCircleCoor[1])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(-nextCircleCoor[0], -nextCircleCoor[1])));

                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(nextCircleCoor[1], nextCircleCoor[0])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(-nextCircleCoor[1], nextCircleCoor[0])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(nextCircleCoor[1], -nextCircleCoor[0])));
                circleSets.get(radius - 1).add(new ArrayList<>(Arrays.asList(-nextCircleCoor[1], -nextCircleCoor[0])));
                nextCircleCoor = computeNextCoorCircleMidpointFirstOctant(nextCircleCoor[0], nextCircleCoor[1], radius);

            }
        }

        circleSets.forEach((circleSet) -> {
            circleSet.forEach(point -> System.out.printf("(%d, %d),", point.get(0), point.get(1)));
            System.out.println();
            System.out.println();
        });

    }

    private static int[] computeNextCoorCircleMidpointFirstOctant(int x, int y, int radius) {
        int possiblePointsComparedError = 2 * ((x * x + y * y - radius * radius) + (2 * y + 1)) + (1 - 2 * x);
        boolean decrementX = possiblePointsComparedError > 0;
        return decrementX ? new int[]{x - 1, y + 1} : new int[]{x, y + 1};
    }
}

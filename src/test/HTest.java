package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jme3.math.Vector2f;

import org.junit.jupiter.api.Test;

import race.H;

public class HTest {

    private final Vector2f o = new Vector2f(0, 0);

    @Test
    public void testLinesIntersect_CoLinear() {
        assertTrue(H.linesIntersect(o, new Vector2f(1, 0), o, new Vector2f(1, 0)).success);
        assertTrue(H.linesIntersect(o, new Vector2f(1, 0), o, new Vector2f(2, 0)).success);
        assertTrue(H.linesIntersect(new Vector2f(-1, 0), new Vector2f(2, 0), o, new Vector2f(1, 0)).success);
        assertTrue(!H.linesIntersect(o, new Vector2f(1, 0), new Vector2f(2, 0), new Vector2f(3, 0)).success);
    }

    @Test
    public void testLinesIntersect_Basic() {
        assertTrue(H.linesIntersect(new Vector2f(-1, -1), new Vector2f(1, 1), new Vector2f(1, -1), new Vector2f(-1, 1)).success);
        assertTrue(H.linesIntersect(new Vector2f(-1, -1), new Vector2f(1, 1), new Vector2f(1, -1), new Vector2f(-1, 1)).success);

        assertTrue(!H.linesIntersect(new Vector2f(-1, 0), new Vector2f(-1, 1), new Vector2f(1, 0), new Vector2f(1, 1)).success);
    }

    @Test
    public void testLinesIntersect_Other() {
        // No intersect
        assertTrue(!H.linesIntersect(new Vector2f(0,0),new Vector2f(2,8),new Vector2f(8,0),new Vector2f(0,20)).success);
        // Intersect
        assertTrue(H.linesIntersect(new Vector2f(0,10),new Vector2f(2,0),new Vector2f(10,0),new Vector2f(0,5)).success);

        // Parallel, vertical
        assertTrue(!H.linesIntersect(new Vector2f(0,0),new Vector2f(0,10),new Vector2f(2,0),new Vector2f(2,10)).success);
        // Parallel, diagonal
        assertTrue(!H.linesIntersect(new Vector2f(0,0),new Vector2f(5,5),new Vector2f(2,0),new Vector2f(7,5)).success);

        // Collinear, overlap
        assertTrue(H.linesIntersect(new Vector2f(0,0),new Vector2f(5,5),new Vector2f(2,2),new Vector2f(7,7)).success);
        // Collinear, no overlap
        assertTrue(!H.linesIntersect(new Vector2f(0,0),new Vector2f(5,5),new Vector2f(7,7),new Vector2f(10,10)).success);
    }
}
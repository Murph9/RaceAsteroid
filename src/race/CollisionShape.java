package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *  Collision shape, has line and circle
 *  @author murph
 */
public class CollisionShape implements EntityComponent
{
    public enum Type {
        Circle,
        Line;
    }

    private Type type;
    private Vector3f dir;
    private float radius;

    public static CollisionShape Line(Vector3f dir) {
        return new CollisionShape(Type.Line, dir, 0);
    }

    public static CollisionShape Circle(float radius) {
        return new CollisionShape(Type.Circle, null, radius);
    }

    private CollisionShape(Type type, Vector3f dir, float radius) {
        this.type = type;
        this.dir = dir;
        this.radius = radius;
    }

    public Type getType() {
        return type;
    }

    public Vector3f getDir() {
        if (type != Type.Line) {
            try {
                throw new Exception("Not valid for type: " + type);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return dir;
    }

    public float getRadius() {
        if (type != Type.Circle) {
            try {
                throw new Exception("Not valid for type: " + type);
            } catch (Exception e) {
                e.printStackTrace();
                return Float.NaN;
            }
        }
        return radius;
    }
}

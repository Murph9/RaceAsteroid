package race.component;

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
    private boolean ghost;

    public static CollisionShape Line(Vector3f dir, boolean ghost) {
        return new CollisionShape(Type.Line, dir, 0, ghost);
    }
    
    public static CollisionShape Circle(float radius) {
        return new CollisionShape(Type.Circle, null, radius, false);
    }

    private CollisionShape(Type type, Vector3f dir, float radius, boolean ghost) {
        this.type = type;
        this.dir = dir;
        this.radius = radius;
        this.ghost = ghost;
    }

    public Type getType() {
        return type;
    }
    public boolean getGhost() {
        return ghost;
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

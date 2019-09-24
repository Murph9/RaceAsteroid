package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;


/**
 *  Direction and magnitude of line.
 *  Start is the position of the object
 *
 *  @author    murph
 */
public class CollisionShape implements EntityComponent
{
	private final static String TYPE_CIRCLE = "circle";
	private final static String TYPE_LINE = "line";
	
	private String type;
	private Vector3f dir;
	private float radius;
	
	public static CollisionShape Line(Vector3f dir) {
		return new CollisionShape(TYPE_LINE, dir, 0);
	}
	public static CollisionShape Circle(float radius) {
		return new CollisionShape(TYPE_CIRCLE, null, radius);
	}
	
	private CollisionShape(String type, Vector3f dir, float radius) {
		this.type = type;
		this.dir = dir;
		this.radius = radius;
	}

    public Vector3f getDir() {
    	if (!type.equals(TYPE_LINE)) {
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
    	if (!type.equals(TYPE_CIRCLE)) {
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

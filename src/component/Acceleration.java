package component;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

import race.H;

/**
 * Represents accelerations. Generic. 
 * @author murph
 */
public class Acceleration implements EntityComponent {

	private Vector3f linear;
    private Vector3f angular; //??
    
    public Acceleration(Vector3f linear) {
        this(linear, new Vector3f());
    }
    
    public Acceleration(Vector3f linear, Vector3f angular) {
        this.linear = linear;
        this.angular = angular;
    }

    public Vector3f getLinear() {
        return linear;
    }

    public Vector3f getAngular() {
        return angular;
    }
    
    @Override
    public String toString() {
        return "Acceleration[linear=" + H.round3f(linear, 3) + ", ang=" + H.round3f(angular, 3) + "]";
    }
}

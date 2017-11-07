package race;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

public class World {

	private static Vector3f boxWorld[] = new Vector3f[]
			{
				new Vector3f(-0.5f, 1f, 0), new Vector3f(0.5f, -2f, 0),
				new Vector3f(-3f, -3f, 0), new Vector3f(0, 6f, 0),
				new Vector3f(-3f, -3f, 0), new Vector3f(6f, 0f, 0),
				new Vector3f(3f, 3f, 0), new Vector3f(-6f, 0f, 0),
				new Vector3f(3f, 3f, 0), new Vector3f(0f, -6f, 0),
			};
	
	public static void generate(EntityData ed) {
		for (int i = 0; i < boxWorld.length; i+=2) {
			EntityId line = ed.createEntity();
	        ed.setComponents(line, 
	        		 new Position(boxWorld[i], new Quaternion()),
	        		 new Velocity(new Vector3f(), new Vector3f()),
	                 CollisionShape.Line(boxWorld[i+1]),
	                 new Mass(10000),
	                 new ModelType(RetroPanicModelFactory.MODEL_WALL));
    	}	
	}
	
	//TODO Make into an appstate probs
}

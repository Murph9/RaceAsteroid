package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;

public interface ContactHandler {
	public void setCollisionState(CollisionState state);

	public void handleContact(Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration);
}

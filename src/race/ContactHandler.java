package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;

/**
 * Called by the CollisionState to handle generated contacts/collisions.
 *
 * @author Paul Speed
 */
public interface ContactHandler {
	public void setCollisionState(CollisionState state);

	public void handleContact(Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration);
}

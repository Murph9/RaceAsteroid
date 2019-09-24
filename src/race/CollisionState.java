package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import component.CollisionShape;
import component.Position;
import component.CollisionShape.Type;

import java.util.Set;

/**
 * Keeps track of the list of colliders and performs collision checks.
 * This watches all entities with Position and CollisionShape components. Any
 * generated contacts are passed to a ContactHandler which can deal with them
 * directly or turn them into contact entities or whatever the game requires. No
 * default contact resolution is performed at all and is 100% up to the
 * ContactHandler callback object. 
 */
public class CollisionState extends BaseAppState {

	private EntityData ed;
	private EntitySet entities;

	private SafeArrayList<Entity> colliders = new SafeArrayList<Entity>(Entity.class);

	private ContactHandler contactHandler;

	public CollisionState() {
	}

	public CollisionState(ContactHandler contactHandler) {
		this.contactHandler = contactHandler;
	}

	public void setContactHandler(ContactHandler handler) {
		if (this.contactHandler != null) {
			this.contactHandler.setCollisionState(null);
		}
		this.contactHandler = handler;
		if (this.contactHandler != null && isInitialized()) {
			this.contactHandler.setCollisionState(this);
		}
	}

	public ContactHandler getContactHandler() {
		return contactHandler;
	}

	protected void addColliders(Set<Entity> set) {
		colliders.addAll(set);
	}

	protected void removeColliders(Set<Entity> set) {
		colliders.removeAll(set);
	}

	protected void generateContacts(Entity e1, Entity e2) {
		if (e1.get(CollisionShape.class).getGhost() || e2.get(CollisionShape.class).getGhost())
			return; // ignore ghost objects

		// only handles collision between circle/circle and circle/line

		CollisionShape cs1 = ed.getComponent(e1.getId(), CollisionShape.class);
		CollisionShape cs2 = ed.getComponent(e2.getId(), CollisionShape.class);
		
		// attempt circle/circle
		if (cs1.getType() == Type.Circle && cs2.getType() == Type.Circle) {
			generateContactsCircles(e1, e2);
		}

		// attempt line/circle
		if (cs1.getType() == Type.Line && cs2.getType() == Type.Circle) {
			generateContactsLineCircle(e1, e2);
		}

		// attempt circle/line
		if (cs1.getType() == Type.Circle && cs2.getType() == Type.Line) {
			generateContactsLineCircle(e2, e1);
		}
	}

	private void generateContactsCircles(Entity e1, Entity e2)
	{
		Position p1 = e1.get(Position.class);
		Position p2 = e2.get(Position.class);
		CollisionShape s1 = e1.get(CollisionShape.class);
		float r1 = s1.getRadius();
		CollisionShape s2 = e2.get(CollisionShape.class);
		float r2 = s2.getRadius();
		float threshold = r1 + r2;
		threshold *= threshold;

		float distSq = p1.getLocation().distanceSquared(p2.getLocation());
		if (distSq > threshold) {
			return; // no collision
		}

		// Find the contact normal.
		Vector3f cn = p2.getLocation().subtract(p1.getLocation());
		float dist = cn.length();
		cn.multLocal(1 / dist); // normalize it

		// Positive if penetrating
		float penetration = (r1 + r2) - dist;

		// Calculate a contact point half-way along the penetration
		Vector3f cp = p1.getLocation().add(cn.mult(r1 - penetration * 0.5f));

		contactHandler.handleContact(e1, e2, cp, cn, penetration);
	}

	private void generateContactsLineCircle(Entity line, Entity circle) {
		//line
		CollisionShape lcs = line.get(CollisionShape.class);
		Vector3f a = line.get(Position.class).getLocation();
		Vector3f b = a.add(lcs.getDir());

		//circle
		CollisionShape ccs = circle.get(CollisionShape.class);
		Vector3f c = circle.get(Position.class).getLocation();
		float r = ccs.getRadius();

		Vector3f ac = c.subtract(a);
		Vector3f ab = b.subtract(a);
		Vector3f ad = ac.project(ab);
		Vector3f d = ad.add(a);
		
		Vector3f cd = d.subtract(c);
		
		boolean isInBetween = FastMath.approximateEquals(ad.length()+d.subtract(b).length(), ab.length()); //checks if the point is between
		if (cd.length() <= r && isInBetween) {
			contactHandler.handleContact(line, circle, cd.normalize().mult(r).subtract(c), cd.normalize().negate(), cd.length());
		}
	}

	protected void generateContacts() {
		if (contactHandler == null)
			return;

		Entity[] array = colliders.getArray();
		for (int i = 0; i < array.length; i++) {
			Entity e1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				Entity e2 = array[j];
				generateContacts(e1, e2);
			}
		}
	}

	@Override
	protected void initialize(Application app) {
		ed = getState(EntityDataState.class).getEntityData();
		entities = ed.getEntities(Position.class, CollisionShape.class);
		
		if (contactHandler != null) {
			contactHandler.setCollisionState(this);
		}
	}

	@Override
	protected void cleanup(Application app) {
		// Release the entity set we grabbed previously
		entities.release();
		entities = null;

		if (contactHandler != null) {
			contactHandler.setCollisionState(null);
		}
	}

	@Override
	protected void onEnable() {
		entities.applyChanges();
		addColliders(entities);
	}

	@Override
	protected void onDisable() {
		removeColliders(entities);
	}

	@Override
	public void update(float tpf) {
		if (entities.applyChanges()) {
			removeColliders(entities.getRemovedEntities());
			addColliders(entities.getAddedEntities());
		}

		generateContacts();
	}
	
}

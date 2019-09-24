package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watches entities with Position and ModelType components and creates/destroys
 * Spatials as needed as well as moving them to the appropriate locations.
 * Spatials are created with a ModelFactory callback object that can be game
 * specific. This class is not Asteroid Panic specific and could be used with
 * any game that maps spatials to entities directly. Note: currently model type
 * changes are not detected.
 *
 * @author Paul Speed
 */
public class ModelState extends BaseAppState {

	static Logger log = LoggerFactory.getLogger(ModelState.class);

	private EntityData ed;
	private EntitySet entities;
	private Map<EntityId, Spatial> models = new HashMap<EntityId, Spatial>();
	private Node modelRoot;
	private ModelFactory factory;

	public ModelState(ModelFactory factory) {
		this.factory = factory;
	}

	public Spatial getSpatial(EntityId entity) {
		return models.get(entity);
	}

	protected Spatial createSpatial(Entity e) {

		return factory.createModel(e);
	}

	protected void addModels(Set<Entity> set) {

		for (Entity e : set) {
			// See if we already have one
			Spatial s = models.get(e.getId());
			if (s != null) {
				log.error("Model already exists for added entity:" + e);
				continue;
			}

			s = createSpatial(e);
			models.put(e.getId(), s);
			updateModelSpatial(e, s);
			modelRoot.attachChild(s);
		}
	}

	protected void removeModels(Set<Entity> set) {

		for (Entity e : set) {
			Spatial s = models.remove(e.getId());
			if (s == null) {
				log.error("Model not found for removed entity:" + e);
				continue;
			}
			s.removeFromParent();
		}
	}

	protected void updateModelSpatial(Entity e, Spatial s) {
		Position p = e.get(Position.class);
		s.setLocalTranslation(p.getLocation());
		s.setLocalRotation(p.getFacing());
		
		Colour c = ed.getComponent(e.getId(), Colour.class);
		if (c != null) {
			if (s instanceof Geometry) {
				Geometry g = (Geometry)s;
				Material m = g.getMaterial();
				m.setColor("Color", c.getColour());
				g.setMaterial(m);
			}
		}
	}

	protected void updateModels(Set<Entity> set) {

		for (Entity e : set) {
			Spatial s = models.get(e.getId());
			if (s == null) {
				log.error("Model not found for updated entity:" + e);
				continue;
			}

			updateModelSpatial(e, s);
		}
	}

	@Override
	protected void initialize(Application app) {

		factory.setState(this);

		// Grab the set of entities we are interested in
		ed = getState(EntityDataState.class).getEntityData();
		entities = ed.getEntities(Position.class, ModelType.class);

		// Create a root for all of the models we create
		modelRoot = new Node("Model Root");
	}

	@Override
	protected void cleanup(Application app) {

		// Release the entity set we grabbed previously
		entities.release();
		entities = null;
	}

	@Override
	protected void onEnable() {
		((Main) getApplication()).getRootNode().attachChild(modelRoot);

		entities.applyChanges();
		addModels(entities);
	}

	@Override
	public void update(float tpf) {
		if (entities.applyChanges()) {
			removeModels(entities.getRemovedEntities());
			addModels(entities.getAddedEntities());
			updateModels(entities.getChangedEntities());
		}
	}

	@Override
	protected void onDisable() {
		modelRoot.removeFromParent();
		removeModels(entities);
	}

}

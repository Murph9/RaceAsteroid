package race;

import java.util.Arrays;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import race.component.CollisionShape;
import race.component.Mass;
import race.component.ModelType;
import race.component.Position;
import race.component.Velocity;

public class World extends BaseAppState {

	private static Vector3f[] staticWorld = new Vector3f[] {
			new Vector3f(-1,2,0), new Vector3f(2,0,0),
			new Vector3f(1,2,0), new Vector3f(1,-1,0),
			new Vector3f(2,1,0), new Vector3f(0,-2,0),
			new Vector3f(2,-1,0), new Vector3f(-1,-1,0),
			
			new Vector3f(1,-2,0), new Vector3f(-2,0,0),
			new Vector3f(-1,-2,0), new Vector3f(-1,1,0),
			new Vector3f(-2,-1,0), new Vector3f(0,2,0),
			new Vector3f(-2,1,0), new Vector3f(1,1,0),
	};
	
	private static Vector3f A = new Vector3f(-2,2,0);
	private static Vector3f B = new Vector3f(2,2,0);
	private static Vector3f C = new Vector3f(2,-2,0);
	private static Vector3f D = new Vector3f(-2,-2,0);
	private static Vector3f E = new Vector3f(-1,3,0);
	private static Vector3f F = new Vector3f(1,3,0);
	private static Vector3f[] initialState = new Vector3f[]
			{
				A, D.subtract(A),
				C, D.subtract(C),
				B, C.subtract(B),
				A, E.subtract(A),
				B, F.subtract(B),
			};
	
	private static Vector3f G = new Vector3f(0,-1,0);
	private static Vector3f H_ = new Vector3f(0,2,0);
	private static Vector3f I = new Vector3f(-1,1,0);
	private static Vector3f J = new Vector3f(1,1,0);
	private static Vector3f[] helpArrow = new Vector3f[] 
			{
				G, H_.subtract(G),
				I, H_.subtract(I),
				J, H_.subtract(J),
			};
	
	public static void generate(EntityData ed) {
		
	}

	private EntityData ed;
	private EntityId ship;
	private EntitySet set;
	
	private WorldSpawnType type;
	
	private static final float SPAWN_RADIUS = 5;
	private Vector3f nextSpawn;
	private PieceType nextSpawnType;
	private static Piece[] pieceList = new Piece[] 
			{
				new Piece(PieceType.UP_2, PieceType.UP_2, 
						new Vector3f[] {
								new Vector3f(-1,0,0), new Vector3f(0,3,0), 
								new Vector3f(1,0,0), new Vector3f(0,3,0)
								}, 
						new Vector3f(0,3,0)),
				new Piece(PieceType.UP_2, PieceType.UP_2, 
						new Vector3f[] {
								new Vector3f(-1,0,0), new Vector3f(1,1,0), 
								new Vector3f(1,0,0), new Vector3f(1,1,0)
								}, 
						new Vector3f(1,1,0)),
				new Piece(PieceType.UP_2, PieceType.UP_2, 
						new Vector3f[] {
								new Vector3f(-1,0,0), new Vector3f(-1,1,0), 
								new Vector3f(1,0,0), new Vector3f(-1,1,0)
								}, 
						new Vector3f(-1,1,0))
			};
	
	enum WorldSpawnType {
		Infinite,
		Static
	}
	public World(EntityId ship, WorldSpawnType type) {
		this.ship = ship;
		this.type = type;
	}

	@Override
	protected void initialize(Application app) {
		ed = getState(EntityDataState.class).getEntityData();
		set = ed.getEntities(ModelType.class);
		
		if (type == WorldSpawnType.Infinite) {
			//spawn initial things
			spawnAsObjects(initialState, RetroPanicModelFactory.MODEL_WALL, new Vector3f());
			spawnAsObjects(helpArrow, RetroPanicModelFactory.MODEL_LINE, new Vector3f());
		}
		
		if (type == WorldSpawnType.Static) {
			spawnAsObjects(staticWorld, RetroPanicModelFactory.MODEL_WALL, new Vector3f());
		}
		
		nextSpawn = E.add(F).mult(0.5f);
		nextSpawnType = PieceType.UP_2;
	}
	
	@Override
	protected void cleanup(Application app) {
		// Release the entity set we grabbed previously
		set.release();
		set = null;
	}

	@Override
	public void update(float tpf) {
		set.applyChanges();
		
		if (type == WorldSpawnType.Static)
			return;
		
		Vector3f pos = ed.getComponent(ship, Position.class).getLocation();
		while (pos.distance(nextSpawn) < SPAWN_RADIUS) {
			
			Piece[] avaliable = Arrays.stream(pieceList).filter( x -> x.startType == this.nextSpawnType).toArray(Piece[]::new); 
			if (avaliable.length == 0)
				break;
			
			//spawn next
			int index = FastMath.nextRandomInt(0, avaliable.length - 1);
			spawn(avaliable[index]);
		}
	}
	
	private void spawn(Piece p) {
		if (p.startType != this.nextSpawnType)
			try {
				throw new Exception("Not the right type: " + p.startType + " and " + this.nextSpawnType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		spawnAsObjects(p.walls, RetroPanicModelFactory.MODEL_WALL, this.nextSpawn);

		this.nextSpawn.addLocal(p.offset);
		this.nextSpawnType = p.endType;
	}
	
	private void spawnAsObjects(Vector3f[] v, String modelType, Vector3f offset) {
		if (v == null || v.length % 2 != 0) {
			System.out.print("What:" + v);
			return;
		}

		for (int i = 0; i < v.length; i+=2) {
			EntityId line = ed.createEntity();
			ed.setComponents(line,
					new Position(offset.add(v[i])),
					new Velocity(new Vector3f()),
					CollisionShape.Line(v[i+1], modelType.equals(RetroPanicModelFactory.MODEL_LINE)),
					new Mass(10000),
					new ModelType(modelType));
		}
	}
	
	@Override
	protected void onDisable() {
	}
	@Override
	protected void onEnable() {
	}
}

enum PieceType {
	UP_2;
}
class Piece {
	Vector3f[] walls;
	PieceType startType;
	PieceType endType;
	Vector3f offset;
	public Piece(PieceType start, PieceType end, Vector3f[] walls, Vector3f offset) {
		this.startType = start;
		this.endType = end;
		this.walls = walls;
		this.offset = offset;
	}
}

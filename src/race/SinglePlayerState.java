package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

import race.World.WorldSpawnType;
import component.ModelType;
import component.Position;
import component.Stun;
import component.Velocity;
import component.Acceleration;
import component.CollisionShape;
import component.Drag;
import component.Mass;


/**
 *  Keeps track of the single-player game state and transitions
 *  through the state machine as game conditions change.
 */
public class SinglePlayerState extends BaseAppState {

    protected enum GameState { LoadLevel, Joining, Playing, Death, EndLevel, GameOver };

    private EntityData ed;
    private EntityId ship;

    private GameState state = GameState.GameOver;

    public SinglePlayerState() {
    }

	protected void setState(GameState state) {
		if (state == this.state) {
            return;
        }
        this.state = state;
        initState();
    }

    protected void initState() {
		switch (state) {
		case LoadLevel:
			setupLevel();
			break;
		case Joining:
			resetShip(true);
			break;
		case Playing:
			break;
		case Death:
		case EndLevel:
		case GameOver:
			break;
		}
    }

	protected void updateState(float tpf) {
		switch (state) {
		case LoadLevel:
			setState(GameState.Joining);
			break;
		case Joining:
			setState(GameState.Playing);
			break;
		case Playing:
			break;
		case Death:
		case EndLevel:
		case GameOver:
			System.out.println("Unknown state: " + state);
			break;
		}
	}

    protected void resetShip(boolean mobile) {
        ed.setComponents(ship,
                         new ModelType(RetroPanicModelFactory.MODEL_SHIP),
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f()),
                         new Acceleration(new Vector3f()),
                         new Mass(mobile ? 0.1 : 0.0),
                         new Stun(0));
        getState(ShipControlState.class).setEnabled(mobile);
        getState(ShipCamera.class).setEnabled(mobile);
        getState(World.class).setEnabled(mobile);
        getState(DebugShipUI.class).setEnabled(mobile);
    }

    protected void setupLevel() {
    }

    @Override
    public void update( float tpf ) {
        updateState(tpf);
    }

    @Override
    protected void initialize(Application app) {

        ed = getState(EntityDataState.class).getEntityData();

        ship = ed.createEntity();
        ed.setComponents(ship,
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f(), new Vector3f()),
                         new Acceleration(new Vector3f(), new Vector3f()),
                         new Drag(0.3f, 0.8f),
                         CollisionShape.Circle(0.1f),
                         new Mass(1),
                         new Stun(0),
                         new ModelType(RetroPanicModelFactory.MODEL_SHIP));
        
        getStateManager().attach(new ShipControlState(ship));
        getState(ShipControlState.class).setEnabled(false);
        getStateManager().attach(new ShipCamera(ship));
        getState(ShipCamera.class).setEnabled(false);
        getStateManager().attach(new World(ship, WorldSpawnType.Infinite));
        getState(World.class).setEnabled(false);
        getStateManager().attach(new DebugShipUI(ship));
        getState(DebugShipUI.class).setEnabled(false);
        
        setState(GameState.LoadLevel);
    }

    @Override
	protected void cleanup(Application app) {
		getStateManager().detach(getState(ShipControlState.class));
		getStateManager().detach(getState(ShipCamera.class));
		getStateManager().detach(getState(World.class));
		getStateManager().detach(getState(DebugShipUI.class));
	}

	@Override
	protected void onEnable() {
	}
	@Override
	protected void onDisable() {
	}
}

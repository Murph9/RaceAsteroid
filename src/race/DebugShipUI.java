package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;

import component.AccelModifier;
import component.Acceleration;
import component.Position;
import component.Stun;
import component.Velocity;

public class DebugShipUI extends BaseAppState {

	private EntityData ed;
	private Container hud;
	private Label values;
	
	private EntityId ship;
	
	public DebugShipUI(EntityId ship) {
		this.ship = ship;
	}
	
	@Override
	protected void initialize(Application app) {
		ed = getState(EntityDataState.class).getEntityData();
		
		hud = new Container(new BorderLayout());
		Container statsPanel = new Container(new SpringGridLayout(Axis.Y, Axis.X,
                FillMode.Even, FillMode.ForcedEven));
		hud.addChild(statsPanel, BorderLayout.Position.North);
		statsPanel.setInsets(new Insets3f(2,5,0,5));
		
		values = statsPanel.addChild(new Label("Score: 0", "retro"));
		
		Camera cam = app.getCamera();
        hud.setPreferredSize(new Vector3f(cam.getWidth(), cam.getHeight(), 1));
        hud.setLocalTranslation(0, cam.getHeight(), 0);
	}
	
	@Override
	public void update(float tpf) {
		//get values set the label text
		Position p = ed.getComponent(ship, Position.class);
		Velocity v = ed.getComponent(ship, Velocity.class);
		Acceleration a = ed.getComponent(ship, Acceleration.class);
		Stun s = ed.getComponent(ship, Stun.class);
		AccelModifier am = ed.getComponent(ship, AccelModifier.class);
		
		values.setText("Position: " + H.round3f(p.getLocation(), 3) + "\n" + "Velocity: " + v.getLinear().length() + "\n"
				+ "Acceleration: " + a.getLinear().length() * am.getValue() + "\n" + s + "\n");
	}
	
	@Override
	protected void cleanup(Application arg0) {
	}

	@Override
	protected void onDisable() {
		hud.removeFromParent();
	}

	@Override
	protected void onEnable() {
		Main main = (Main)getApplication();
		main.getGuiNode().attachChild(hud);
	}
}

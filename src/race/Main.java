package race;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.Styles;

// http://cowboyprogramming.com/2007/01/05/evolve-your-heirachy/
// https://softwareengineering.stackexchange.com/questions/372527/isnt-an-entity-component-system-terrible-for-decoupling-information-hiding

/**
 *  Application entry point.  Sets up the game app states and
 *  initializes the GUI sub-system, styles, and the default
 *  control mappings.
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.flyCam = null;
        app.start();
    }

    public Main() {
        super(new EntityDataState(),
            new PhysicsState(),
            new ThrustPhysicsState(),
            new CollisionState(new RaceContactHandler()),
            new DecayState(),
            new EmitterState(new RaceEmitterFactory()),
            new ModelState(new RaceModelFactory(), new RaceGLModelFactory()),
            new MainMenuState());
    }

    @Override
    public void simpleInitApp() {
        // Initialize the Lemur helper instance
        GuiGlobals.initialize(this);

        // Setup default key mappings
        ShipFunctions.initializeDefaultMappings(GuiGlobals.getInstance().getInputMapper());

        // Setup the "retro" style for our HUD and GUI elements
        Styles styles = GuiGlobals.getInstance().getStyles();
        RaceStyles.initializeStyles(styles);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}

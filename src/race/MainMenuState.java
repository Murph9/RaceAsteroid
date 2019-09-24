package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;


/**
 *  Lays out the main menu controls and handles the clicks.
 *  When enabled/disabled the main menu is shown/hidden.
 *
 *  @author    Paul Speed
 */
public class MainMenuState extends BaseAppState {

    private Container menu;

    public MainMenuState() {
    }

    @Override
    protected void initialize( Application app ) {

        menu = new Container(new SpringGridLayout(), new ElementId(PanicStyles.MENU_ID), "retro");

        menu.addChild(new Label("Race Asteroid", new ElementId(PanicStyles.MENU_TITLE_ID), "retro"));

        Button start = menu.addChild(new Button("Start Game", "retro"));
        start.addClickCommands(new Start());

        Button exit = menu.addChild(new Button("Exit", "retro"));
        exit.addClickCommands(new Exit());

        Camera cam = app.getCamera();
        float menuScale = cam.getHeight()/720f;

        Vector3f pref = menu.getPreferredSize();
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f * menuScale,
                                 cam.getHeight() * 0.75f + pref.y * 0.5f * menuScale,
                                 10);
        menu.setLocalScale(menuScale);
    }

    @Override
    protected void cleanup( Application app ) {
    }

    @Override
    public void update( float tpf ) {
    }

    @Override
    protected void onEnable() {
        Main main = (Main)getApplication();
        main.getGuiNode().attachChild(menu);
    }

    @Override
    protected void onDisable() {
        menu.removeFromParent();
    }

    private class Start implements Command<Button> {
        public void execute( Button source ) {
            getStateManager().attach(new SinglePlayerState());
            setEnabled(false);
        }
    }

    private class Exit implements Command<Button> {
        public void execute( Button source ) {
            getApplication().stop();
        }
    }
}

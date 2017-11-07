/*
 * $Id$
 *
 * Copyright (c) 2013 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package race;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.Styles;

/**
 *  Application entry point.  Sets up the game app states and
 *  initializes the GUI sub-system, styles, and the default
 *  control mappings.
 *
 *  @author Paul Speed
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
               new CollisionState(new PanicContactHandler()),
               new DecayState(),
               new ModelState(new RetroPanicModelFactory()),
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
        PanicStyles.initializeStyles(styles);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}

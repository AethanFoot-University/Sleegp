package uk.ac.bath.csed_group_11.sleegp.gui.Utilities;

import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Views.Resource;

/**
 * Purely a means to an end, a way of implementing any changes an external class wants to make to the stage
 */
public abstract class StageRunnable<Controller>{
    /**
     * Changes to make to the stage
     * @param stage stage to modify
     */
    protected abstract Resource<Controller> setupStage(Stage stage);
}

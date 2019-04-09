package uk.ac.bath.csed_group_11.sleegp.logic.Classification;

import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.data.ProcessedDataContainer;
import uk.ac.bath.csed_group_11.sleegp.logic.util.MathUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClassificationUtils {

    /**
     * Entry point for processing data
     * @param epochContainer
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ProcessedDataContainer convertData(EpochContainer epochContainer) throws IOException, ClassNotFoundException {
        epochContainer.transformCurrentData(MathUtils.movingAverageOnEpochs("lowAlpha", 10));
        List<Epoch> epochs = epochContainer.getEpochList();
        DataSmoother smoother = new DataSmoother(epochs);

        List<Plot> smoothedAlphaData = smoother.smooth("lowAlpha", 75);

        ProcessedDataContainer proc = new ProcessedDataContainer(smoothedAlphaData);
        proc.applyMovingAverage(10);
        proc.mapLevel(0, 100);

        return proc;
    }
}

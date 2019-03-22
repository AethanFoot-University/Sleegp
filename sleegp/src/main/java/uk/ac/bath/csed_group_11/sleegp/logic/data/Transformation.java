package uk.ac.bath.csed_group_11.sleegp.logic.data;

import java.util.List;

/**
 * The type Transformation.
 *
 * @param <t> the type of data set being converted
 * @param <d> the type of outputted data
 */
public abstract class Transformation<t, d> {

    /**
     * Applies a transformation to a data set
     *
     * @param dataSet        the original dataset
     * @param index       the index of the data being iterated through
     * @param finalOutput the final outputted list of data
     */
    public abstract void transform(List<t> dataSet, int index, List<d> finalOutput);
}

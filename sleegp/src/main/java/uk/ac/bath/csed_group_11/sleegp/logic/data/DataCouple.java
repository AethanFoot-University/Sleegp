package uk.ac.bath.csed_group_11.sleegp.logic.data;

import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.Serializable;

public class DataCouple implements Serializable {

    /**
     * This is serializable ID, set to static so that there is not an ID mis-match error when saving files
     */
    static final long serialVersionUID = -6209602756558504168L;

    private EpochContainer rawData = null;
    private ProcessedDataContainer processedData = null;

    public DataCouple(EpochContainer rawData) {
        this.rawData = rawData;
    }

    public DataCouple(EpochContainer rawData, ProcessedDataContainer processedData) {
        this.rawData = rawData;
        this.processedData = processedData;
    }

    public boolean dataHasBeenProcessed(){
        return (processedData ==null);
    }

    public EpochContainer getRawData() {
        return rawData;
    }

    public void setRawData(EpochContainer rawData) {
        this.rawData = rawData;
    }

    public ProcessedDataContainer getProcessedData() {
        return processedData;
    }

    public void setProcessedData(ProcessedDataContainer processedData) {
        this.processedData = processedData;
    }
}

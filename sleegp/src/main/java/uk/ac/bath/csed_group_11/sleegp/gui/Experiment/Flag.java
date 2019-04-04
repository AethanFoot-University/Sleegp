package uk.ac.bath.csed_group_11.sleegp.gui.Experiment;

public class Flag {
    private String flag;
    private long timeMillis;

    public Flag(String flag, long timeMillis) {
        this.flag = flag;
        this.timeMillis = timeMillis;
    }

    public String getFlag() {
        return flag;
    }

    public long getTimeMillis() {
        return timeMillis;
    }
    @Override
    public String toString(){
        return timeMillis +", "+flag;
    }
}

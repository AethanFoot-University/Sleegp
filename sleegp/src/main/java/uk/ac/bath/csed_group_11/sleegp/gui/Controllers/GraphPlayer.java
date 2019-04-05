package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import uk.ac.bath.csed_group_11.sleegp.logic.data.Epoch;
import uk.ac.bath.csed_group_11.sleegp.logic.data.EpochContainer;

public abstract class GraphPlayer {
    private String attribute;

    private LineChart<?,?> chart;
    private EpochContainer rawData;

    private int delay = 100;

    private int window = 50;
    private long timeProgressed =0;
    private long endTime;

    private int lastEpochUpdate = 0;

    private double playBackSpeed = 1.0;


    private long timeLastUpdated;

    private XYChart.Series mainSeries;

    private boolean play = false;

    private boolean disposed = false;

    public GraphPlayer(LineChart<?, ?> chart, EpochContainer rawData, String attribute) {
        this.attribute = attribute;
        this.chart = chart;
        this.rawData = rawData;

        timeLastUpdated = System.currentTimeMillis();

        mainSeries = new XYChart.Series();
        mainSeries.setName(attribute);
        chart.getData().add(mainSeries);

        endTime = rawData.getEpoch(rawData.size()-1).timeElapsed();

        startUpdater();
    }

    private void startUpdater(){
        new Thread(()->{

            while(!disposed){
                update();
                try {Thread.sleep(500);} catch (InterruptedException e){}
            }

        }).start();
    }

    public abstract void sendUpdate(long timeProgressed);

    private void update(){
        if(play) {
            progressTime();

            if (timeProgressed <= endTime) {
                drawLatest();
                sendUpdate(timeProgressed);
            }
        }
        else{
            timeLastUpdated = System.currentTimeMillis();
        }
    }

    private void progressTime(){
        long currentMillis = System.currentTimeMillis();

        timeProgressed += ((currentMillis - timeLastUpdated) *getPlayBackSpeed());
        timeLastUpdated = currentMillis;
    }

    private int currentPivot(){
        return getClosestEpoch(timeProgressed);
    }

    private int getClosestEpoch(long time){
        int index =0;
        for(Epoch epoch : rawData.getEpochList()) {
            if(epoch.timeElapsed()>= time) return index;
            index ++;
        }
        return -1;
    }
    private synchronized void platformLatest(){
        int pivot = currentPivot();
        for(int i = lastEpochUpdate +1; i<= pivot ; i++){
            addEpoch(i);
        }
    }

    private void drawLatest(){

        Platform.runLater(()->{
            platformLatest();
        });
    }

    private void addEpoch(int i ){

            try {
                mainSeries.getData().add(new XYChart.Data((i + ""),
                    rawData.getEpoch(i).getByReference(attribute)));
            }
            catch(Exception e){
                System.out.println("Caught");
            }


        while(mainSeries.getData().size()> window) mainSeries.getData().remove(0);
        lastEpochUpdate = i;
    }

    private void redraw(){
        clearGraph();

        int pivot = currentPivot();
        int start = currentPivot()-window;
            start = (start>0)?start :0;
            ObservableList data = mainSeries.getData();

        int finalStart = start;
        Platform.runLater(()->{
        for(int i = finalStart; i< pivot; i++){
            addEpoch(i);
        }

        });
    }

    private void clearGraph(){
        if(chart.getData().size()>0) chart.getData().remove(0, chart.getData().size()-1);
    }


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        mainSeries.setName(attribute);
        redraw();
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
        redraw();
    }

    public synchronized double getPlayBackSpeed() {
        return playBackSpeed;
    }

    public synchronized void setPlayBackSpeed(double playBackSpeed) {
        this.playBackSpeed = playBackSpeed;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public long getTimeProgressed() {
        return timeProgressed;
    }

    public void setTimeProgressed(long timeProgressed) {
        this.timeProgressed = timeProgressed;
        timeLastUpdated = System.currentTimeMillis();
        redraw();
    }

    public long getEndTime() {
        return endTime;
    }

    public void dispose(){
        disposed = true;
    }
}

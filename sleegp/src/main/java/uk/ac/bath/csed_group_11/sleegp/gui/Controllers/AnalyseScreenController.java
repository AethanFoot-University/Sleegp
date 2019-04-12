package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.cli.SleegpConstants;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.ExperimentManager;
import uk.ac.bath.csed_group_11.sleegp.gui.Experiment.Flag;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.FilePicker;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.ClassificationUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.data.*;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * To be written by: Aethan and Xander
 */
public class AnalyseScreenController implements Initializable {
    private User user = null;

    @FXML
    AnchorPane mainPane;

    @FXML
    private ComboBox<String> processedCombo;
    private ObservableList<String> processedComboData = FXCollections.observableArrayList();

    @FXML
    TableView<TableData> processedTable;

    TableColumn<TableData, String> dateColumn;
    TableColumn<TableData, Double> percentageColumn;
    TableColumn<TableData, Double> timeColumn;

    @FXML
    BarChart<String, Number> barChart;

    @FXML
    LineChart<Number, Number> lineChart;

    @FXML
    TextField goalTextField;

    @FXML
    Label goalLabel;

    @FXML
    LineChart<String, Number> goalChart;
    private boolean tableSetup = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goalTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                    if(event.getCode().toString().equals("ENTER"))
                    {
                        setGoal();
                    }
            }
        });

        if (ExperimentManager.isExperimentMode()) getAnswers();
        try{
            user = User.loadDefaultUser();
        } catch(Exception e){
            recoverFile();
        }
//        try {
//            EpochContainer ec = EpochContainer.loadContainerFromFile(Resource.getFileFromResource(
//                "Test.ec"));
//            user.add(new DataCouple(ec, null));
//            user.saveToFile(new File("test3.usr"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        Platform.runLater(() -> {
            goalLabel.setText(user.getCurrentGoal() + "");
        });
        if (user.size() > 0) {


            if (ExperimentManager.isExperimentMode()) {
                if (ExperimentManager.getVIEW().equals("AnalyseScreen.fxml")) {
                    setupTable();
                    listenForTableWidthChange();
                }
            }
            refreshView();
            listenForComboAction();
        } else {
            new Thread(()->{
                Platform.runLater(()->SceneUtils.displayPopUp("You haven't recorded any data."));
            }).start();
        }
    }

    public void deleteSelected() {
        try {
            int selected = processedTable.getSelectionModel().getSelectedIndex();

            if(selected>=0) if(user.size()>1 ) user.remove(selected); else user.clear();
            saveUser();
             Platform.runLater(()->refreshView());
       } catch (Exception e) {
            SceneUtils.displayOnPopupFXThread("Please select the row you would like to delete.");
        }
    }

    private void recoverFile(){
        SceneUtils.displayOnPopupFXThread("User file is corrupted, repairing...");
        user = new User();
        saveUser();
    }

    private void refreshView(){
        setupGoalChart();
        addToLastWeek();
        comboBoxSetup();
        if (!ExperimentManager.isExperimentMode() &&!tableSetup) {
            setupTable();
            listenForTableWidthChange();
            tableSetup = true;
        }
    }

    public void getAnswers() {
        new Thread(() ->{
            JFrame f = new JFrame();
            String answer = "null";
            while (true) {
                if (answer.equals("null")) {
                    answer = JOptionPane.showInputDialog(f, "What is the time slept for the " +
                        "11/02/2019");
                }
                try {
                    if (Double.parseDouble(answer) >= 3.5 && Double.parseDouble(answer) <= 4) {
                        ExperimentManager.notify("First Answered");
                        break;
                    } else {
                        answer = "null";
                    }
                } catch (NumberFormatException e) {
                    answer = "null";
                }
            }

            while (true) {
                if (answer.equals("null")) {
                    answer = JOptionPane.showInputDialog(f, "What is the date where time slept is 5.89");
                }
                if (answer.equals("22/02/2019")) {
                    ExperimentManager.notify("Second Answered");
                    break;
                } else {
                    answer = "null";
                }
            }

            for (Flag flag : ExperimentManager.getFlagList()) {
                System.out.println(flag.toString());
            }
            ExperimentManager.endExperiment();


        }).start();
    }

    public void saveUser() {
        try {
            user.saveToFile(new File(SleegpConstants.RELATIVE_USER_FILE));
        } catch (FileNotFoundException e) {
            System.out.println("Have you tampered with the user file? You naughty boy.");
        }
    }

    public void setupGoalChart() {
        if(goalChart.getData().size()>0) goalChart.getData().clear();

        if(user.size()>0) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            XYChart.Series<String, Number> seriesGoal = new XYChart.Series<>();
            double totalTime = 0;
            for (DataCouple couple : user) {
                if (couple.getProcessedData() != null) {
                    double percentage = calculatePercentageSlept(couple.getProcessedData());
                    double timeSlept = calculateTimeSlept(percentage,
                        couple.getProcessedData().get(couple.getProcessedData().size() - 1).getTimeElapsed());
                    totalTime += timeSlept;
                    series.getData().add(new XYChart.Data<>(couple.getRawData().getEpoch(0).getTimeStamp(),
                        totalTime));
                }
            }
            seriesGoal.getData().add(new XYChart.Data<>(user.get(0).getRawData().getEpoch(0).getTimeStamp(), user.getCurrentGoal()));
            seriesGoal.getData().add(new XYChart.Data<>(user.get(user.size() - 1).getRawData().getEpoch(0).getTimeStamp(), user.getCurrentGoal()));
            goalChart.getData().addAll(series, seriesGoal);
        }



    }

    public void setupTable() {
        Platform.runLater(()->{
            double inlWidth = processedTable.getScene().getWindow().widthProperty().getValue();

            dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(param -> {
                SimpleObjectProperty<String> property = new SimpleObjectProperty<>();
                property.setValue(param.getValue().getDate());
                return property;
            });
            dateColumn.setPrefWidth(inlWidth * (4.0 / 15.0));
            dateColumn.setMinWidth(100);

            percentageColumn = new TableColumn<>("Percentage Slept");
            percentageColumn.setCellValueFactory(param -> {
                SimpleObjectProperty<Double> property = new SimpleObjectProperty<>();
                property.setValue(param.getValue().getPercentage());
                return property;
            });
            percentageColumn.setPrefWidth(inlWidth * (2.0 / 5.0));
            percentageColumn.setMinWidth(150);

            timeColumn = new TableColumn<>("Time Slept");
            timeColumn.setCellValueFactory(param -> {
                SimpleObjectProperty<Double> property = new SimpleObjectProperty<>();
                property.setValue(param.getValue().getTime());
                return property;
            });
            timeColumn.setPrefWidth(inlWidth * (1.0 / 3.0));
            timeColumn.setMinWidth(125);

            processedTable.getColumns().addAll(dateColumn, percentageColumn, timeColumn);
        });
    }

    public void addToLastWeek() {
        if (ExperimentManager.isExperimentMode()) {
            if (ExperimentManager.getVIEW().equals("AnalyseScreen.fxml")) {
                Platform.runLater(() -> {
                    processedTable.getItems().clear();
                    ObservableList<TableData> data = FXCollections.observableArrayList(
                        new TableData("06/02/2019", 90.74, 9.34),
                        new TableData("07/02/2019", 96.20, 7.53),
                        new TableData("08/02/2019", 86.32, 8.94),
                        new TableData("09/02/2019", 92.99, 5.73),
                        new TableData("10/02/2019", 95.32, 10.32),
                        new TableData("11/02/2019", 67.28, 3.68),
                        new TableData("12/02/2019", 76.63, 6.36),
                        new TableData("13/02/2019", 93.67, 9.78),
                        new TableData("14/02/2019", 69.26, 4.65),
                        new TableData("15/02/2019", 87.39, 11.56),
                        new TableData("16/02/2019", 93.31, 3.97),
                        new TableData("17/02/2019", 82.63, 7.24),
                        new TableData("18/02/2019", 65.92, 8.37),
                        new TableData("19/02/2019", 90.23, 6.95),
                        new TableData("20/02/2019", 94.19, 9.43),
                        new TableData("21/02/2019", 56.80, 2.75),
                        new TableData("22/02/2019", 58.31, 5.89),
                        new TableData("23/02/2019", 91.73, 13.74),
                        new TableData("24/02/2019", 97.54, 4.87),
                        new TableData("25/02/2019", 87.43, 6.71),
                        new TableData("26/02/2019", 73.93, 8.10),
                        new TableData("27/02/2019", 97.13, 5.65),
                        new TableData("28/02/2019", 92.65, 9.06),
                        new TableData("01/03/2019", 84.39, 10.35),
                        new TableData("02/03/2019", 96.95, 4.07),
                        new TableData("03/03/2019", 89.97, 6.48),
                        new TableData("04/03/2019", 79.30, 9.72),
                        new TableData("05/03/2019", 86.54, 10.45)
                        );
                    processedTable.getItems().addAll(data);

                    System.out.println("Table");
                    processedTable.refresh();
                });
            } else {
                Platform.runLater(() -> {
                    XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
                    barChart.getData().clear();
                    barSeries.getData().add(new XYChart.Data<>("06/02/2019", 9.34));
                    barSeries.getData().add(new XYChart.Data<>("07/02/2019", 7.53));
                    barSeries.getData().add(new XYChart.Data<>("08/02/2019", 8.94));
                    barSeries.getData().add(new XYChart.Data<>("09/02/2019", 5.73));
                    barSeries.getData().add(new XYChart.Data<>("10/02/2019", 10.32));
                    barSeries.getData().add(new XYChart.Data<>("11/02/2019", 3.68));
                    barSeries.getData().add(new XYChart.Data<>("12/02/2019", 6.36));
                    barSeries.getData().add(new XYChart.Data<>("13/02/2019", 9.78));
                    barSeries.getData().add(new XYChart.Data<>("14/02/2019", 4.65));
                    barSeries.getData().add(new XYChart.Data<>("15/02/2019", 11.56));
                    barSeries.getData().add(new XYChart.Data<>("16/02/2019", 3.97));
                    barSeries.getData().add(new XYChart.Data<>("17/02/2019", 7.24));
                    barSeries.getData().add(new XYChart.Data<>("18/02/2019", 8.37));
                    barSeries.getData().add(new XYChart.Data<>("19/02/2019", 6.95));
                    barSeries.getData().add(new XYChart.Data<>("20/02/2019", 9.43));
                    barSeries.getData().add(new XYChart.Data<>("21/02/2019", 2.75));
                    barSeries.getData().add(new XYChart.Data<>("22/02/2019", 5.89));
                    barSeries.getData().add(new XYChart.Data<>("23/02/2019", 13.74));
                    barSeries.getData().add(new XYChart.Data<>("24/02/2019", 4.87));
                    barSeries.getData().add(new XYChart.Data<>("25/02/2019", 6.71));
                    barSeries.getData().add(new XYChart.Data<>("26/02/2019", 8.10));
                    barSeries.getData().add(new XYChart.Data<>("27/02/2019", 5.65));
                    barSeries.getData().add(new XYChart.Data<>("28/02/2019", 9.06));
                    barSeries.getData().add(new XYChart.Data<>("01/02/2019", 10.35));
                    barSeries.getData().add(new XYChart.Data<>("02/02/2019", 4.07));
                    barSeries.getData().add(new XYChart.Data<>("03/02/2019", 6.48));
                    barSeries.getData().add(new XYChart.Data<>("04/02/2019", 9.72));
                    barSeries.getData().add(new XYChart.Data<>("05/02/2019", 10.45));

                    barChart.getData().add(barSeries);
                });
            }
        } else {
            Platform.runLater(() -> {
                processedTable.getItems().clear();
                for (DataCouple couple : user) {
                    if (couple.getProcessedData() != null) {
                        double percentage1 =
                            calculatePercentageSlept(couple.getProcessedData());
                        double timeSlept1 = calculateTimeSlept(percentage1,
                            couple.getProcessedData().get(couple.getProcessedData().size() - 1).getTimeElapsed());

                        refreshPoints(couple, timeSlept1);


                        TableData data =
                            new TableData(couple.getRawData().getEpoch(0).getTimeStamp().replace('.', ' '), percentage1, timeSlept1);

                        processedTable.getItems().add(data);
                    }
                }

                System.out.println("Table");
                processedTable.refresh();
            });
        }
    }

    public void refreshPoints(DataCouple couple, double timeSlept1){
        if(!couple.isPointsAwarded()) {
            user.appendHourlyPoints(timeSlept1);
            couple.setPointsAwarded(true);
        }
        saveUser();


        System.out.println("User points: "+user.returnPoints()+", "+user.returnLvl()+", "+user.getPercentageProgress());
    }

    public void listenForTableWidthChange() {
        Platform.runLater(() -> {
            processedTable.getScene().getWindow().widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                    Number newValue) {
                    double width = newValue.doubleValue();
                    Platform.runLater(() -> {
                        dateColumn.setPrefWidth(width * (4.0 / 15.0));
                        percentageColumn.setPrefWidth(width * (2.0 / 5.0));
                        timeColumn.setPrefWidth(width * (1.0 / 3.0));
                    });
                }
            });
        });
    }

    public void comboBoxSetup() {
        if(processedComboData.size() > 0) processedComboData.clear();
        for (DataCouple couple : user) {
            processedComboData.add(couple.getRawData().getEpoch(0).getTimeStamp());
        }

        processedCombo.setItems(processedComboData);
    }

    public void listenForComboAction() {
        processedCombo.setOnAction((event) -> {
            if(lineChart.getData().size()>0)lineChart.getData().clear();
            String selected = processedCombo.getSelectionModel().getSelectedItem();
            System.out.println("ComboBox Action (selected: " + selected + ")");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesPercent = new XYChart.Series<>();

            DataCouple couple = null;
            for (int i = 0; i < user.size(); i++) {
                if (user.get(i).getRawData().getEpoch(0).getTimeStamp().equals(selected)) {
                    couple = user.get(i);
                }
            }
            if (couple !=null &&couple.getProcessedData() != null) {
                for (int i = 0; i < user.get(0).getProcessedData().size(); i += 10) {
                    Plot plot = couple.getProcessedData().get(i);
                    series.getData().add(new XYChart.Data<>(plot.getTimeElapsed(),
                        plot.getLevel()));
                }
                seriesPercent.getData().add(new XYChart.Data<>(0, 60));
                seriesPercent.getData().add(new XYChart.Data<>(user.get(0).getProcessedData().get(user.get(0).getProcessedData().size() - 1).getTimeElapsed(),
                    60));

                lineChart.getData().addAll(series, seriesPercent);
            }
        });
    }

    public double calculatePercentageSlept(ProcessedDataContainer container) {
        int awake = 0;
        int asleep = 0;

        for(Plot p : container) {
            if (p.getLevel() > 60.0) {
                awake += 1;
            } else {
                asleep += 1;
            }
        }

        return 100 * ((double)asleep / (double)(awake + asleep));
    }

    public double calculateTimeSlept(double percentage, double totalTime) {
        double milliseconds = (percentage / 100) * totalTime;
        return milliseconds / (1000 * 60 * 60);
    }

    public void back() {
        SceneUtils.setView((Stage) processedCombo.getScene().getWindow(), "HomeScreen.fxml");
    }

    public void process() {
        System.out.println("Processing");
       new Thread(()->{
           try {
               ProcessedDataContainer processedDataContainer;
               for (int i = 0; i < user.size(); i++) {
                   if (user.get(i).getProcessedData() == null) {
                       processedDataContainer = ClassificationUtils.convertData(user.get(i).getRawData());

                       if(processedDataContainer !=null) {
                           user.get(i).setProcessedData(processedDataContainer);
                       }
                       else{
                            SceneUtils.displayOnPopupFXThread("Terribly sorry. The sleep: "+user.get(i).getRawData().toString()+" was too short to be processed.");
                            try{user.remove((i--)); } catch(Exception e){}
                           Platform.runLater(()->refreshView());

                       }
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           } catch (ClassNotFoundException e) {
               e.printStackTrace();
           }

           //Saving to file
            saveUser();
            System.out.println("usr saved");

            addToLastWeek();
        }).start();
    }

    public void setGoal() {
        try {
            user.setCurrentGoal(Double.parseDouble(goalTextField.getText()));
            goalTextField.clear();
            Platform.runLater(() -> {
                goalLabel.setText(user.getCurrentGoal() + "");
            });
            saveUser();
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
            goalTextField.setText("Please enter a number.");
        }
    }

    public void importCSV(){

            FilePicker filePicker = new FilePicker(".csv", "Comma-Separated Values", "");
            File importLocation = filePicker.getFile(getStage(), FilePicker.OPEN);
        new Thread(()-> {
            if (importLocation != null) {
                try {
                    EpochContainer importedEpoch =
                        new EpochContainer(FileUtils.extractCSV(importLocation.getAbsolutePath()));
                   addEpochContainer(importedEpoch);
                } catch (IOException e) {
                    SceneUtils.displayOnPopupFXThread("File not found!");
                } catch (Exception e) {
                    SceneUtils.displayOnPopupFXThread("Corrupted file!");
                }
            }
        }).start();
    }

    private void addEpochContainer(EpochContainer importedEpoch){
        if(importedEpoch !=null)user.add(new DataCouple(importedEpoch));

        Platform.runLater(()->{
            process();
            saveUser();
            refreshView();
        });
    }

    public void importEC(){

            FilePicker filePicker = new FilePicker(".ec", "Epoch Container", "");
            File importLocation = filePicker.getFile(getStage(), FilePicker.OPEN);
        new Thread(()-> {
            if (importLocation != null) {
                try {
                    EpochContainer importedEpoch =
                        EpochContainer.loadContainerFromFile(importLocation);
                    addEpochContainer(importedEpoch);
                } catch (IOException e) {
                    SceneUtils.displayOnPopupFXThread("File not found!");
                } catch (Exception e) {
                    SceneUtils.displayOnPopupFXThread("Corrupted file!");
                }
            }
        }).start();
    }

    private Stage getStage(){
        return (Stage) processedCombo.getScene().getWindow();
    }

    public class TableData {
        private final SimpleStringProperty date;
        private final SimpleDoubleProperty percentage;
        private final SimpleDoubleProperty time;

        private TableData(String date, double percentage, double time) {
            this.date = new SimpleStringProperty(date);
            this.percentage = new SimpleDoubleProperty(percentage);
            this.time = new SimpleDoubleProperty(time);
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public double getPercentage() {
            return percentage.get();
        }

        public SimpleDoubleProperty percentageProperty() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage.set(percentage);
        }

        public double getTime() {
            return time.get();
        }

        public SimpleDoubleProperty timeProperty() {
            return time;
        }

        public void setTime(double time) {
            this.time.set(time);
        }
    }
}

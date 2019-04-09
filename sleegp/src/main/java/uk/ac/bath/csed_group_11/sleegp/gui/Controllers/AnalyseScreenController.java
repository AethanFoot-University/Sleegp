package uk.ac.bath.csed_group_11.sleegp.gui.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.SceneUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.ClassificationUtils;
import uk.ac.bath.csed_group_11.sleegp.logic.Classification.Plot;
import uk.ac.bath.csed_group_11.sleegp.logic.data.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * To be written by: Aethan and Xander
 */
public class AnalyseScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    private ComboBox<EpochContainer> processedCombo;
    private ObservableList<EpochContainer> processedComboData = FXCollections.observableArrayList();

    @FXML
    TableView<TableData> processedTable;

    TableColumn<TableData, String> dateColumn;
    TableColumn<TableData, String> percentageColumn;
    TableColumn<TableData, String> timeColumn;

    @FXML
    LineChart<Number, Number> lineChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        listenForTableWidthChange();
        Platform.runLater(() ->{

        });

        try {
            System.out.println();
            processedComboData.add(EpochContainer.loadContainerFromFile(Resource.getFileFromResource("Test.ec")));
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        processedCombo.setItems(processedComboData);

        processedCombo.setOnAction((event) -> {
            EpochContainer epochSelected = processedCombo.getSelectionModel().getSelectedItem();
            System.out.println("ComboBox Action (selected: " + epochSelected + ")");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesPercent = new XYChart.Series<>();
            try {
                User user = User.loadUserFromFile(new File("test2.usr"));
                for (int i = 0; i < user.get(0).getProcessedData().size(); i += 10) {
                    Plot plot = user.get(0).getProcessedData().get(i);
                    series.getData().add(new XYChart.Data<>(plot.getTimeElapsed(),
                        plot.getLevel()));
                }
                seriesPercent.getData().add(new XYChart.Data<>(0, 60));
                seriesPercent.getData().add(new XYChart.Data<>(user.get(0).getProcessedData().get(user.get(0).getProcessedData().size() - 1).getTimeElapsed(),
                    60));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            lineChart.getData().addAll(series, seriesPercent);
        });


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
            //dateColumn.setSortable(false);

            percentageColumn = new TableColumn<>("Percentage Slept");
            percentageColumn.setCellValueFactory(param -> {
                SimpleObjectProperty<String> property = new SimpleObjectProperty<>();
                property.setValue(param.getValue().getPercentage());
                return property;
            });
            percentageColumn.setPrefWidth(inlWidth * (2.0 / 5.0));
            percentageColumn.setMinWidth(150);
            //percentageColumn.setSortable(false);

            timeColumn = new TableColumn<>("Time Slept");
            timeColumn.setCellValueFactory(param -> {
                SimpleObjectProperty<String> property = new SimpleObjectProperty<>();
                property.setValue(param.getValue().getTime());
                return property;
            });
            timeColumn.setPrefWidth(inlWidth * (1.0 / 3.0));
            timeColumn.setMinWidth(125);
            //timeColumn.setSortable(false);

            processedTable.getColumns().addAll(dateColumn, percentageColumn, timeColumn);
        });
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

    public void back() {
        SceneUtils.setView((Stage) processedCombo.getScene().getWindow(), "HomeScreen.fxml");
    }

   public void process() {
       new Thread(()->{

        try {


            User user = new User();

            EpochContainer ec;
            ec = EpochContainer.loadContainerFromFile(Resource.getFileFromResource("Test.ec"));
            System.out.println("EC created");

            ProcessedDataContainer processedDataContainer = ClassificationUtils.convertData(ec);
            System.out.println("PC created");

            user.add(new DataCouple(ec, processedDataContainer));
            System.out.println("Couple added");
            //Saving to file
            processedDataContainer.saveToFile(new File("/home/aethan/Sleegp/sleegp/src/main" +
                "/resources/Test.sd"));
            System.out.println("PC saved");
            user.saveToFile(new File("test2.usr"));
            System.out.println("usr saved");

            User user2 = User.loadUserFromFile(new File("test2.usr"));

            Platform.runLater(()->{
//                String[] timeStamp =
//                    user2.get(0).getRawData().getEpoch(0).getTimeStamp().replace('.', ' ').split(
//                        " ");
//                for (int i = 0; i < timeStamp.length; i++) {
//                   timeStamp[i] = timeStamp[i].trim();
//                }
                //String date = timeStamp[1] + "/" + timeStamp[2] + "/" + timeStamp[0];
                TableData data = new TableData(user2.get(0).getRawData().getEpoch(0).getTimeStamp().replace('.', ' '), "93", "8");
                TableData data1 =
                    new TableData(user2.get(0).getRawData().getEpoch(1).getTimeStamp().replace('.', ' '), "94", "10");
                TableData data2 =
                    new TableData(user2.get(0).getRawData().getEpoch(2).getTimeStamp().replace('.', ' '), "95", "9");

                processedTable.getItems().add(data);
                processedTable.getItems().add(data1);
                processedTable.getItems().add(data2);
                System.out.println("Table");
                processedTable.refresh();
            });



        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Unable to load container from file: " + e.toString());
            return;
        }

        }).start();
    }

    public class TableData {
        private final SimpleStringProperty date;
        private final SimpleStringProperty percentage;
        private final SimpleStringProperty time;

        private TableData(String date, String percentage, String time) {
            this.date = new SimpleStringProperty(date);
            this.percentage = new SimpleStringProperty(percentage);
            this.time = new SimpleStringProperty(time);
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

        public String getPercentage() {
            return percentage.get();
        }

        public SimpleStringProperty percentageProperty() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage.set(percentage);
        }

        public String getTime() {
            return time.get();
        }

        public SimpleStringProperty timeProperty() {
            return time;
        }

        public void setTime(String time) {
            this.time.set(time);
        }
    }
}

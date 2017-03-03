package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();

        WordSearchGrid wordSearchGrid = new WordSearchGrid(5);

        TextArea solutionList = new TextArea();
        solutionList.setEditable(false);
        solutionList.setFocusTraversable(false);

        HBox topMenu = new HBox();

        Text sizeLabel = new Text("Size:");
        TextField sizeInputField = new TextField();
        sizeInputField.setPromptText("5");
        sizeInputField.setText("5");
        sizeInputField.setPrefWidth(30);

        Button newSearch = new Button("Create New Word Search");
        newSearch.setOnMouseClicked((MouseEvent mouseEvent) -> {

            int newWSsize = 5;

            try {
                newWSsize = Integer.parseInt(sizeInputField.getText());
                //TODO sizeInputField.setBackground();
            }
            catch (Exception e){ }

            wordSearchGrid.createRandomWordSearch(newWSsize);
            solutionList.clear();
        });

        Button findWords = new Button("Find Words");
        findWords.setOnMouseClicked((MouseEvent mouseEvent) -> {
            solutionList.setText(wordSearchGrid.getSolutionList());
            wordSearchGrid.highlightSolutions();
        });


        topMenu.getChildren().addAll(sizeLabel, sizeInputField, newSearch, findWords);
        topMenu.setSpacing(10);
        topMenu.setStyle("-fx-background-color: #C0C0C0; -fx-border-color: black; -fx-border-width: 1px; -fx-border-style: solid;");
        topMenu.setPadding(new Insets(5));
        topMenu.setAlignment(Pos.CENTER);

        root.setCenter(wordSearchGrid);
        root.setTop(topMenu);
        root.setBottom(solutionList);

        primaryStage.setTitle("Word Search Solver");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

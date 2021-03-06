/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.gui;

import peeters.frank.bridge.deal.*;
import peeters.frank.bridge.filter.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author frankpeeters
 */
public class BridgeBoardSimulator extends Application {

    public static final String TITLE = "Bridge Board Simulator";
    public static final String VERSION = "Version 1.0.6, feb 2020";
    public static final String LOSERS = "Correction rules:\n"
        + "Each ace minus 0.5 losers\n"
        + "Each valuable queen plus 0.5 loser\n"
        + "QTx or QJx is estimated as 2.5 losers\n"
        + "Q in Qxx is estimated as 3 losers.\n"
        + "4441 one extra loser";
    public static final String ABOUT_TEXT
        = TITLE + "\n"
        + VERSION + "\n"
        + "Copyright Frank Peeters\n\n"
        + "If you have any hint to improve this application, then please send an email to:\n"
        + "frankpeeters53@gmail.com\n"
        + "\n"
        + "Some visual controls are decorated with tool tips; pausing the mouse above those controls,\n"
        + "will reveal them.\n"
        + "\n"
        + "Current Features:\n"
        + "1. Simulation of bridge boards with filtering conditions:\n"
        + "   - High Card Points of each direction;\n"
        + "   - Losers of each direction;\n"
        + "   - Suit lengths of each direction;\n"
        + "   - Restriction to a specific distributional pattern;\n"
        + "   - Assigning specific cards to a specific direction.\n"
        + "2. Generating a sample of random boards that satisfy the configured filtering conditions.\n"
        + "3. Editing comments on a specific random board.\n"
        + "4. Saving to a text file containing a report with the configuration settings and random boards\n"
        + "   including all comments.\n"
        + "5. Showing statistics: \n"
        + "   - HCP (H), Losers (L), and Controls (C) of each direction;\n"
        + "   - Estimated probability of the filtering conditions (based on the current sample);\n"
        + "   - Frequency table with the number of controls (NORTH) in the current sample;\n"
        + "   - Frequency table with the number of losers (NORTH) in the current sample.\n"
        + "\n\n"
        + "Changes of " + "Version 1.0.4, sept 2019" + ":\n"
        + "A. Requested by Albert van den Bosch.\n"
        + "The saved text file has been extended with a bare view on the boards of the sample.\n"
        + "The format of the bare view is as follows:\n"
        + "- Each board is presented on one separate line;\n"
        + "- Each hand of a board is extended with a semi colon and a white space;\n"
        + "- The hands of a board are shown in order of North, East, South, West.\n"
        + "- The cards of a hand are shown in order of Spades, Hearts, Diamonds, Clubs.\n"
        + "B.\n"
        + "The list of patterns is extended with the 4, 5, 6, 7, 8, and 9 pattern.\n\n"
        + "Changes of " + VERSION + ":\n"
        + "The Assigned Cards are shown at the generator tab."
        + "\n\n"
        + "Enjoy!\n";
    public static final String BORDER_COLOR = "lightblue";
    public static final String IMAGE_RESOURCE = "file:resources/playingcards.jpg";
    public static final int CONTROL_WIDTH = 84;
    public static final String BUTTON_STYLE = "-fx-border-style: solid inside;"
        + "-fx-pref-width: " + CONTROL_WIDTH + ";"
        + "-fx-border-width: 2;"
        + "-fx-border-insets: 0;"
        + "-fx-border-radius: 2;"
        + "-fx-border-color: SKYBLUE;"
        + "-fx-background-color: lightblue;"
        + "-fx-text-fill: black";
    public static final String TAB_STYLE = "-fx-border-style: solid inside;"
        + "-fx-border-insets: 0 2 0 2;"
        + "-fx-border-width: 2;"
        + "-fx-background-insets: 0 2 0 2;"
        + "-fx-border-radius: 9 9 0 0;"
        + "-fx-background-radius: 9 9 0 0;"
        + "-fx-border-color: lightblue;"
        + "-fx-background-color: ALICEBLUE;"
        + "-fx-faint-focus-color: lightblue;";

    // data
    private SampleOfBoards sample;
    private Deck deck;
    private Map<Direction, GeneralHandFilter> filters;

    // filter tab controls
    private CheckBox cbHCP;
    private CheckBox cbLosers;
    private CheckBox[] cbSuitsAndOtherFilters;
    private CheckBox[] cbDirections;
    private ComboBox[] cbPattern;
    private GridPane filterRanges;
    private BorderPane filtersTabPane;
    private Map<Direction, List<RangeSelect>> rangeSelects;

    // generator tab controls
    private Spinner<Integer> boardNumberSpinner;
    private TextField sampleSizeTextField;
    private TextField[][] directionTextFields;
    private Label[] metricsLabels;
    private GridPane[] directionGridPanes;
    private TextArea commentTextArea;
    private BorderPane generatorPane;
    private GridPane boardNrGridPane;

    private Stage stage;

    private void showFixedCards() {
        Map<Direction, List<Card>> fixed = deck.getHandsFixed();
        for (Direction direction : Direction.values()) {
            if (deck.isFixed(direction)) {
                for (Suit suit : Suit.values()) {
                    List<Card> fixedHand = fixed.get(direction);
                    Hand hand = new Hand(fixedHand.toArray(new Card[fixedHand.size()]));
                    directionTextFields[direction.ordinal()][suit.ordinal()].
                        setText(hand.suit(suit));
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        sample = new SampleOfBoards();
        deck = new Deck();
        filters = new HashMap<>();

        this.stage = stage;
        TabPane tabPane = (TabPane) createTabPane();
        Scene scene = new Scene(tabPane);
        //scene.getStylesheets().add("/rangeselect.css");
        //scene.getStylesheets().add("path/stylesheet.css");

        stage.setScene(scene);

        stage.setTitle(TITLE);
        stage.show();
        //   tabPane.getSelectionModel().selectLast();
    }

    private Parent createTabPane() {

        Tab[] tabs;
        tabs = new Tab[3];
        tabs[0] = new Tab("Filters");
        tabs[1] = new Tab("Generator");
        tabs[2] = new Tab("About");
        for (Tab tab : tabs) {
            tab.setStyle(TAB_STYLE);
        }

        TabPane tabPane = new TabPane(tabs);
        tabPane.setStyle("-fx-background: steelblue");
        tabPane.setSide(Side.LEFT);

        Pane filtersPane = createFiltersPane();
        tabs[0].setContent(filtersPane);
        tabs[0].setClosable(false);

        createGeneratorPane();
        tabs[1].setContent(generatorPane);
        tabs[1].setClosable(false);

        Label aboutLabel = new Label(ABOUT_TEXT);
        aboutLabel.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 5;"
            + "-fx-border-radius: 5;"
            + "-fx-background-radius: 5;"
            + "-fx-background-insets: 5;"
            + "-fx-border-color: " + BORDER_COLOR + ";"
            + "-fx-background-color: white"
        );
        BorderPane aboutPane = new BorderPane();
        aboutPane.setStyle("-fx-background-color: ALICEBLUE");
        tabs[2].setContent(aboutPane);
        aboutPane.setCenter(aboutLabel);
        tabs[2].setClosable(false);

        return tabPane;
    }

    private Pane createFiltersPane() {
        filtersTabPane = new BorderPane();
        filtersTabPane.setStyle("-fx-padding: 10;"
            //      + "-fx-border-style: solid inside;"
            + "-fx-border-width: 0;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-background-color: ALICEBLUE;"
            + "-fx-border-color: " + BORDER_COLOR
        );

        BorderPane.setMargin(filtersTabPane, new Insets(5, 5, 5, 5));

        createUpperHalfFiltersPane();
        createLowerHalfFiltersPane();

        return filtersTabPane;
    }

    private void createUpperHalfFiltersPane() {
        filterRanges = new GridPane();
        filterRanges.setGridLinesVisible(true);
        filterRanges.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 5;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR + ";"
            + "-fx-background-color: aliceblue;"
            + "-fx-background-insets: 5");
        filtersTabPane.setTop(filterRanges);

        cbSuitsAndOtherFilters = new CheckBox[6];
        cbPattern = new ComboBox[4];

        /**
         * * cbHCP *****************************************************
         */
        cbHCP = new CheckBox("HCP");
        cbSuitsAndOtherFilters[0] = cbHCP;
        GridPane.setRowIndex(cbHCP, 0);
        GridPane.setColumnIndex(cbHCP, 1);
        filterRanges.getChildren().add(cbHCP);
        GridPane.setHalignment(cbHCP, HPos.LEFT);
        GridPane.setMargin(cbHCP, new Insets(5, 5, 5, 5));

        /**
         * * cbLosers **************************************************
         */
        cbLosers = new CheckBox("Losers");
        cbSuitsAndOtherFilters[1] = cbLosers;
        GridPane.setRowIndex(cbLosers, 0);
        GridPane.setColumnIndex(cbLosers, 2);
        filterRanges.getChildren().add(cbLosers);
        GridPane.setHalignment(cbLosers, HPos.LEFT);
        GridPane.setMargin(cbLosers, new Insets(5, 5, 5, 5));
        cbLosers.setTooltip(new Tooltip(LOSERS));

        /**
         * * checkboxes of suits **************************************
         */
        CheckBox cbSpades = new CheckBox("Spades");
        GridPane.setRowIndex(cbSpades, 0);
        GridPane.setColumnIndex(cbSpades, 3);
        filterRanges.getChildren().add(cbSpades);
        cbSuitsAndOtherFilters[2] = cbSpades;
        GridPane.setHalignment(cbSpades, HPos.LEFT);
        GridPane.setMargin(cbSuitsAndOtherFilters[2], new Insets(5, 5, 5, 5));

        CheckBox cbHearts = new CheckBox("Hearts");
        GridPane.setRowIndex(cbHearts, 0);
        GridPane.setColumnIndex(cbHearts, 4);
        filterRanges.getChildren().add(cbHearts);
        cbSuitsAndOtherFilters[3] = cbHearts;
        GridPane.setHalignment(cbHearts, HPos.LEFT);
        GridPane.setMargin(cbSuitsAndOtherFilters[3], new Insets(5, 5, 5, 5));

        CheckBox cbDiamonds = new CheckBox("Diamonds");
        GridPane.setRowIndex(cbDiamonds, 0);
        GridPane.setColumnIndex(cbDiamonds, 5);
        filterRanges.getChildren().add(cbDiamonds);
        cbSuitsAndOtherFilters[4] = cbDiamonds;
        GridPane.setHalignment(cbDiamonds, HPos.LEFT);
        GridPane.setMargin(cbSuitsAndOtherFilters[4], new Insets(5, 5, 5, 5));

        CheckBox cbClubs = new CheckBox("Clubs");
        GridPane.setRowIndex(cbClubs, 0);
        GridPane.setColumnIndex(cbClubs, 6);
        filterRanges.getChildren().add(cbClubs);
        cbSuitsAndOtherFilters[5] = cbClubs;
        GridPane.setHalignment(cbClubs, HPos.LEFT);
        GridPane.setMargin(cbSuitsAndOtherFilters[5], new Insets(5, 5, 5, 5));

        /**
         * * Label Balanced *******************************************
         */
        Label lbl = new Label("Pattern");
        lbl.setTooltip(new Tooltip("Balanced pattern includes "
            + "4333, 4432, 5332, 5422"));
        GridPane.setRowIndex(lbl, 0);
        GridPane.setColumnIndex(lbl, 7);
        GridPane.setHalignment(lbl, HPos.CENTER);
        filterRanges.getChildren().add(lbl);

        /**
         * * Controls Each Row ****************************************
         */
        rangeSelects = new HashMap<>();
        cbDirections = new CheckBox[4];
        RangeSelectConstraint hcpRcp = new RangeSelectConstraint(40);
        RangeSelectConstraint losersRcp = new RangeSelectConstraint(36);
        //RangeSelectConstraint controlsRcp = new RangeSelectConstraint(12);
        RangeSelectConstraint spadesRcp = new RangeSelectConstraint(13);
        RangeSelectConstraint heartsRcp = new RangeSelectConstraint(13);
        RangeSelectConstraint diamondsRcp = new RangeSelectConstraint(13);
        RangeSelectConstraint clubsRcp = new RangeSelectConstraint(13);

        for (int row = 1; row <= 4; row++) {
            Direction direction = Direction.values()[row - 1];
            rangeSelects.put(direction, new ArrayList<>());

            cbDirections[row - 1] = new CheckBox(" " + direction.toString() + " ");
            GridPane.setRowIndex(cbDirections[row - 1], row);
            GridPane.setColumnIndex(cbDirections[row - 1], 0);
            filterRanges.getChildren().add(cbDirections[row - 1]);
            GridPane.setHalignment(cbDirections[row - 1], HPos.LEFT);
            GridPane.setMargin(cbDirections[row - 1], new Insets(0, 5, 0, 5));

            RangeSelect rs = new RangeSelect(0, 37, 10, hcpRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );

            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 1);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            rs = new RangeSelect(0, 12, 8, losersRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );
            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 2);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            rs = new RangeSelect(0, 13, 4, spadesRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );
            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 3);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            rs = new RangeSelect(0, 13, 4, heartsRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );
            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 4);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            rs = new RangeSelect(0, 13, 4, diamondsRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );
            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 5);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            rs = new RangeSelect(0, 13, 4, clubsRcp);
//            rs.setStyle(
//                "-fx-background-color: aliceblue;"
//            );
            GridPane.setRowIndex(rs, row);
            GridPane.setColumnIndex(rs, 6);
            filterRanges.getChildren().add(rs);
            rangeSelects.get(direction).add(rs);
            GridPane.setMargin(rs, new Insets(2, 2, 2, 2));

            cbPattern[row - 1] = new ComboBox();
            cbPattern[row - 1].getItems().addAll(new ArrayList<>(java.util.Arrays.asList(Hand.PATTERNS)));
            cbPattern[row - 1].setTooltip(new Tooltip("Balanced pattern includes "
                + "4333, 4432, 5332, 5422"));
            cbPattern[row - 1].setValue("unknown");
            cbPattern[row - 1].setPrefWidth(110);
//            cbPattern[row - 1].setStyle(
//                "-fx-fill-color: aliceblue;"
//            );
//            cbPattern[row-1].setStyle(
//            "-fx-background-color: lightblue");
            GridPane.setRowIndex(cbPattern[row - 1], row);
            GridPane.setColumnIndex(cbPattern[row - 1], 7);
            filterRanges.getChildren().add(cbPattern[row - 1]);
            GridPane.setHalignment(cbPattern[row - 1], HPos.CENTER);
            GridPane.setMargin(cbPattern[row - 1], new Insets(0, 5, 0, 5));
        }

        /**
         * * Eventhandlers Checkboxes *******************************
         */
        for (int i = 0; i < cbSuitsAndOtherFilters.length; i++) {
            CheckBox cb = cbSuitsAndOtherFilters[i];
            final int j = i;
            cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                for (Direction direction : Direction.values()) {
                    rangeSelects.get(direction).get(j).setEnabled(
                        new_val && cbDirections[direction.ordinal()].selectedProperty().getValue());
                }
            });
        }

        for (Direction direction : Direction.values()) {
            cbDirections[direction.ordinal()].selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                List<RangeSelect> rangeSelectsDirection = rangeSelects.get(direction);
                for (int i = 0; i < rangeSelectsDirection.size(); i++) {
                    RangeSelect rangeSelect = rangeSelectsDirection.get(i);
                    rangeSelect.setEnabled(new_val && cbSuitsAndOtherFilters[i].selectedProperty().getValue());
                }
                cbPattern[direction.ordinal()].disableProperty().set(!new_val);
            });

        }

        initRangeControls();
    }

    private void createLowerHalfFiltersPane() {

        /**
         * * Fix Cards *****************************************
         */
        BorderPane fixCardsPane = new BorderPane();

        Label title = new Label("Assign Cards To");
        title.setAlignment(Pos.CENTER_LEFT);
        title.setPrefWidth(110);

        fixCardsPane.setTop(title);

        GridPane centerPane = new GridPane();
        centerPane.setVgap(3);
        Button fixedNorthButton = new Button("NORTH");
        fixedNorthButton.setStyle(BUTTON_STYLE);

        GridPane.setConstraints(fixedNorthButton, 0, 0);
        centerPane.getChildren().add(fixedNorthButton);
        Button fixedEastButton = new Button("EAST");
        fixedEastButton.setStyle(BUTTON_STYLE);

        GridPane.setConstraints(fixedEastButton, 0, 1);
        centerPane.getChildren().add(fixedEastButton);
        Button fixedSouthButton = new Button("SOUTH");
        fixedSouthButton.setStyle(BUTTON_STYLE);

        GridPane.setConstraints(fixedSouthButton, 0, 2);
        centerPane.getChildren().add(fixedSouthButton);
        Button fixedWestButton = new Button("WEST");
        fixedWestButton.setStyle(BUTTON_STYLE);

        GridPane.setConstraints(fixedWestButton, 0, 3);
        centerPane.getChildren().add(fixedWestButton);
        fixCardsPane.setCenter(centerPane);

        Button[] assignCardsButtons = new Button[4];
        assignCardsButtons[0] = fixedNorthButton;
        assignCardsButtons[1] = fixedEastButton;
        assignCardsButtons[2] = fixedSouthButton;
        assignCardsButtons[3] = fixedWestButton;

        Button clearButton = new Button("Clear");
        clearButton.setStyle(BUTTON_STYLE);

        fixCardsPane.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR);
        fixCardsPane.setBottom(clearButton);

        /**
         * * Eventhandlers of Assign Cards Buttons ******************
         */
        for (Direction direction : Direction.values()) {
            Button button = assignCardsButtons[direction.ordinal()];
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    HandResult result = makeDialog(direction);
                    try {
                        deck.fix(result.cards, direction);
                        Map<Direction, List<Card>> handsFixed = deck.getHandsFixed();
                        for (Direction direction : Direction.values()) {
                            if (handsFixed.get(direction).size() == 13) {
                                cbDirections[direction.ordinal()].selectedProperty().setValue(Boolean.FALSE);
                            }
                        }
                        showFixedCards();

                    } catch (HandException ex) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("An exception occurred: "
                            + ex.getMessage());
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(ex.getMessage())));
                        alert.showAndWait();
                    }

                }

                private HandResult makeDialog(Direction direction) {
                    Alert dialog = new Alert(AlertType.INFORMATION);
                    dialog.setTitle("Assign Cards Dialog");
                    dialog.setHeaderText("Enter the cards assigned "
                        + " to " + direction
                        + " in order of "
                        + Suit.SPADES.getSymbol() + ", "
                        + Suit.HEARTS.getSymbol() + ", "
                        + Suit.DIAMONDS.getSymbol() + ", "
                        + Suit.CLUBS.getSymbol()
                        + " and separated by three comma's.\n"
                        + "Only these symbols are allowed: AKQJT98765432");

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    TextField cards = new TextField();
                    cards.setText(deck.fixedHand(direction));
                    grid.add(new Label("Enter Cards:"), 0, 0);
                    grid.add(cards, 1, 0);

                    dialog.getDialogPane().setContent(grid);
                    dialog.setX(700);
                    dialog.showAndWait();

                    return new HandResult(
                        direction, cards.getText());
                }

            });

        }

        clearButton.setOnAction(
            (ActionEvent event) -> {
                deck.unfix();
                showFixedCards();
            }
        );

        /**
         * * Reset Pane **************************************
         */
        BorderPane resetPane = new BorderPane();

        resetPane.setStyle(
            "-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR + ";"
        );
        Button resetButton = new Button("Reset");

        resetButton.setStyle(BUTTON_STYLE);

        resetButton.setOnAction(
            (ActionEvent event) -> {
                Scene scene = new Scene(createTabPane());
                stage.setScene(scene);
                initRangeControls();
                deck.unfix();
                showFixedCards();
            }
        );

        resetPane.setBottom(resetButton);

        /**
         * * Image Pane ****************************************
         */
        BorderPane imagePane = new BorderPane();

        imagePane.setStyle(
            "-fx-padding: 0;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR + ";"
        );

        Image image = new Image(IMAGE_RESOURCE);
        ImageView iv = new ImageView();

        iv.setStyle(
            "-fx-padding: 0;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 0;"
            + "-fx-border-insets: 1;"
            + "-fx-border-radius: 3;"
        );
        iv.setImage(image);

        iv.setFitWidth(
            816);
        iv.setFitHeight(
            258);
        iv.setPreserveRatio(
            false);
        iv.setSmooth(
            true);
        imagePane.setPadding(
            new Insets(0, 0, 0, 0));
        imagePane.setCenter(iv);

        /**
         * * Finally ********************************************
         */
        filtersTabPane.setCenter(imagePane);

        filtersTabPane.setLeft(fixCardsPane);

        filtersTabPane.setRight(resetPane);

        BorderPane.setMargin(imagePane,
            new Insets(5, 5, 5, 5));
        BorderPane.setMargin(fixCardsPane,
            new Insets(5, 5, 5, 5));
        BorderPane.setMargin(resetPane,
            new Insets(5, 5, 5, 5));
    }

    private void createGeneratorPane() {
        generatorPane = new BorderPane();
        GridPane.setMargin(generatorPane, new Insets(5, 5, 5, 5));
        generatorPane.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 0;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR + ";"
            + "-fx-background-color: ALICEBLUE"
        );

        createBoardPane();
        createConfigPane();
    }

    private void createConfigPane() {
        BorderPane configPane = new BorderPane();
        configPane.setStyle("-fx-padding: 0;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 0;"
            + "-fx-border-insets: 5;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR);
        generatorPane.setLeft(configPane);

        /**
         * * Statistics GridPane ********************************
         */
        GridPane outputGridPane = new GridPane();
        outputGridPane.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR);
        configPane.setBottom(outputGridPane);

        Label statLabel = new Label("Estimated Propability:");
        GridPane.setMargin(statLabel, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(statLabel, 0, 1, 2, 1);
        outputGridPane.getChildren().add(statLabel);

        TextField propabilityTextField = new TextField();
        propabilityTextField.setStyle("-fx-text-inner-color: black");
        propabilityTextField.setPrefSize(CONTROL_WIDTH, 30);
        propabilityTextField.setEditable(false);
        GridPane.setMargin(propabilityTextField, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(propabilityTextField, 0, 2);
        Label lbl = new Label("Board");
        lbl.setTooltip(new Tooltip(""
            + "estimated probability of all filters\n"
            + "based on the current sample"));
        GridPane.setConstraints(lbl, 1, 2);
        outputGridPane.getChildren().addAll(propabilityTextField, lbl);
        lbl.setAlignment(Pos.CENTER);

        TextField propabilityNorthTextField = new TextField();
        propabilityNorthTextField.setStyle("-fx-text-inner-color: black");

        propabilityNorthTextField.setPrefSize(CONTROL_WIDTH, 30);
        propabilityNorthTextField.setEditable(false);
        GridPane.setMargin(propabilityNorthTextField, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(propabilityNorthTextField, 0, 3);
        lbl = new Label("North");
        lbl.setTooltip(new Tooltip(""
            + "estimated probability,\n"
            + "based on the current sample,\n"
            + "of North-filter\n"
        ));
        GridPane.setConstraints(lbl, 1, 3);
        GridPane.setMargin(propabilityNorthTextField, new Insets(5, 5, 5, 5));
        outputGridPane.getChildren().addAll(propabilityNorthTextField, lbl);

        TextField propabilitySouthTextField = new TextField();
        propabilitySouthTextField.setStyle("-fx-text-inner-color: black");

        propabilitySouthTextField.setPrefSize(CONTROL_WIDTH, 30);
        propabilitySouthTextField.setEditable(false);
        GridPane.setMargin(propabilitySouthTextField, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(propabilitySouthTextField, 0, 4);
        lbl = new Label("South");
        lbl.setTooltip(new Tooltip(""
            + "estimated probability, \n"
            + "based on current sample,\n"
            + "of South-filter\n"
            + "assuming North-filter is not violated\n"));
        GridPane.setConstraints(lbl, 1, 4);
        outputGridPane.getChildren().addAll(propabilitySouthTextField, lbl);

        /**
         * * Controls *******************************************
         */
        Label controlsLabel = new Label("Frequency Tables of Controls and Losers:");
        GridPane.setMargin(controlsLabel, new Insets(5, 5, 5, 5));
        Button controlsButton = new Button("Show");
        controlsButton.setStyle(BUTTON_STYLE);

        controlsButton.setPrefSize(CONTROL_WIDTH, 30);

        GridPane.setConstraints(controlsLabel, 0, 5, 2, 1);
        GridPane.setConstraints(controlsButton, 0, 6, 1, 1);
        GridPane.setMargin(controlsButton, new Insets(5, 5, 5, 5));

        controlsButton.setOnAction((ActionEvent event) -> {
            int sampleSize = Integer.parseInt(sampleSizeTextField.getText());
            setFilters();
            //sample = deck.getBoards(new BoardFilter(filters), sampleSize);
            String distribution = printFreqTables(sample);
            showMessage("Frequency Tables Controls/Losers (NORTH)", distribution);

        });
        outputGridPane.getChildren().addAll(controlsLabel, controlsButton);

        /**
         * * Save Button ***************************************
         */
        Button saveButton = new Button("Save");
        saveButton.setStyle(BUTTON_STYLE);

        saveButton.setPrefSize(CONTROL_WIDTH, 30);
        GridPane.setMargin(saveButton, new Insets(5, 5, 5, 5));
        saveButton.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setTitle("Save Report");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null && sample != null) {
                try {
                    try (PrintWriter out = new PrintWriter(file)) {
                        if (sample != null) {
                            StringBuilder sb = new StringBuilder();

                            sb.append("Sample Settings\n\n");
                            sb.append("Sample Size: ").append(sample.getSampleSize()).append("\n\n");
                            sb.append("Filters\n\n");
                            for (Direction direction : Direction.values()) {
                                sb.append(direction.toString()).append(":\n");
                                sb.append(filters.get(direction).toString()).append("\n");

                                if (deck.isFixed(direction)) {
                                    sb.append(direction.toString()).
                                        append(" is fixed to ").
                                        append(deck.fixedHand(direction));
                                    sb.append("\n");
                                }
                            }
                            sb.append("Estimated probabilities just based on this very sample\n");

                            sb.append(sample.estimatedPropability().toString());
                            sb.append("\n\n");
                            sb.append(ABOUT_TEXT);
                            sb.append("\n\n");

                            out.write(framed(sb.toString(), 99));
                            out.write("\n\n");
                            out.write(sample.toString());
                            out.write("\n\n");
                            out.write(sample.stringBareView());
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(BridgeBoardSimulator.class
                        .getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        GridPane.setConstraints(saveButton, 0, 0);
        outputGridPane.getChildren().add(saveButton);

        /**
         * * Config TopPane ***************************************
         */
        GridPane configTopPane = new GridPane();
        configPane.setTop(configTopPane);
        configTopPane.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 0;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR);
        /**
         * * Sample Size *****************************************
         */
        sampleSizeTextField = new TextField("20");
        sampleSizeTextField.setPrefSize(CONTROL_WIDTH, 30);
        GridPane.setConstraints(sampleSizeTextField, 0, 0);
        GridPane.setMargin(sampleSizeTextField, new Insets(5, 5, 5, 5));
        lbl = new Label(" Sample Size");
        GridPane.setConstraints(lbl, 1, 0);
        //GridPane.setMargin(lbl, new Insets(5, 5, 5, 5));

        /**
         * * Generator Button **************************************
         */
        Button generateButton = new Button("Generate");
        generateButton.setStyle(BUTTON_STYLE);

        GridPane.setConstraints(generateButton, 0, 1);
        generateButton.setPrefSize(CONTROL_WIDTH, 30);
        GridPane.setMargin(generateButton, new Insets(5, 5, 5, 5));
        configTopPane.getChildren().addAll(sampleSizeTextField, lbl, generateButton);
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //clearPropabilities();
                int sampleSize = Integer.parseInt(sampleSizeTextField.getText());
                setFilters();
                sample = deck.getBoards(new BoardFilter(filters), sampleSize);
                printFreqTables(sample);
                boardNrGridPane.getChildren().remove(boardNumberSpinner);
                boardNumberSpinner = new Spinner(1, sampleSize, sampleSize);

                GridPane.setConstraints(boardNumberSpinner, 0, 1);
                boardNrGridPane.getChildren().add(boardNumberSpinner);
                boardNumberSpinner.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) -> {
                    if (newValue <= sample.getSampleSize()) {

                        Board board = sample.getBoards().get(newValue - 1);
                        for (Direction direction : Direction.values()) {
                            for (Suit suit : Suit.values()) {
                                directionTextFields[direction.ordinal()][suit.ordinal()].
                                    setText(board.hand(direction).suit(suit));
                            }
                            metricsLabels[direction.ordinal()].
                                setText(metrics(board.hand(direction)));
                        }

                    }
                });

                boardNumberSpinner.setPrefSize(CONTROL_WIDTH, 30);
                boardNumberSpinner.getValueFactory().setValue(1);

                if (sample.getBoards().size() < sampleSize) {
                    String message;
                    if (sample.getBoards().isEmpty()) {
                        message = "The chosen conditions are that restrictive that"
                            + " it is rather difficult, or even impossible, to detect boards"
                            + " that do not violate these conditions.\n";
                    } else {
                        message = "The chosen conditions are that restrictive that"
                            + " it is rather difficult to detect enough boards"
                            + " that do not violate these conditions.\n"
                            + "Only " + sample.getBoards().size() + " boards are yielded.";
                    }
                    showMessage("Warning", message);
                }

                if (sample.getBoards().isEmpty()) {
                    propabilityTextField.setText("?");
                    propabilitySouthTextField.setText("");
                    propabilityNorthTextField.setText("");
                } else {
                    Propabilities p = sample.estimatedPropability();
                    double propability = (Math.round(10000 * p.total)) / 10000.0;
                    propabilityTextField.setText(propability + "");
                    if (filters.get(Direction.EAST).isEmpty()
                        && filters.get(Direction.WEST).isEmpty()) {
                        propability = (Math.round(10000 * (p.total / p.south))) / 10000.0;
                        propabilitySouthTextField.setText(propability + "");
                        propability = (Math.round(10000 * (p.south))) / 10000.0;
                        propabilityNorthTextField.setText(propability + "");
                    } else {
                        propabilitySouthTextField.setText("");
                        propabilityNorthTextField.setText("");
                    }
                }

            }

        });

    }

    private GridPane createBoardPane() {
        GridPane boardPane = new GridPane();
        boardPane.setStyle("-fx-padding: 10;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-width: 1;"
            + "-fx-border-insets: 5;"
            + "-fx-border-radius: 3;"
            + "-fx-border-color: " + BORDER_COLOR);
        GridPane.setMargin(boardPane, new Insets(5, 5, 5, 5));
        directionGridPanes = new GridPane[4];
        directionTextFields = new TextField[4][];
        metricsLabels = new Label[4];
        Text text;
        for (int direction = 0; direction < 4; direction++) {
            directionGridPanes[direction] = new GridPane();
            directionGridPanes[direction].setVgap(1);
            GridPane.setMargin(directionGridPanes[direction], new Insets(1));
            directionTextFields[direction] = new TextField[4];

            for (Suit suit : Suit.values()) {
                text = new Text(suit.getSymbol() + "");
                text.setFont(Font.font("Verdana", 20));
                text.setFill(suit.getColor());
                directionTextFields[direction][suit.ordinal()] = new TextField();
                directionTextFields[direction][suit.ordinal()].setEditable(false);
                directionTextFields[direction][suit.ordinal()].setPrefSize(100, 30);
                directionTextFields[direction][suit.ordinal()].setAlignment(Pos.BASELINE_LEFT);
                directionTextFields[direction][suit.ordinal()].setStyle("-fx-text-inner-color: black;");
                //+"-fx-background-color: lightgray");
                GridPane.setConstraints(text, 0, suit.ordinal());
                GridPane.setConstraints(directionTextFields[direction][suit.ordinal()], 1, suit.ordinal());
                directionGridPanes[direction].getChildren().
                    addAll(text, directionTextFields[direction][suit.ordinal()]);
            }
            metricsLabels[direction] = new Label();
            metricsLabels[direction].setPrefSize(120, 10);
            metricsLabels[direction].setAlignment(Pos.CENTER);
            metricsLabels[direction].setTooltip(new Tooltip(
                "H: HCP; L: Losers; C: Controls (A=2, K=1)\n"
                + "Losers " + LOSERS
            ));

            GridPane.setConstraints(metricsLabels[direction], 1, 4);
            directionGridPanes[direction].getChildren().
                addAll(metricsLabels[direction]);
        }
        GridPane.setConstraints(directionGridPanes[0], 2, 0);
        GridPane.setConstraints(directionGridPanes[1], 3, 1);
        GridPane.setConstraints(directionGridPanes[2], 2, 2);
        GridPane.setConstraints(directionGridPanes[3], 1, 1);
        for (int i = 0; i < 4; i++) {
            boardPane.getChildren().add(directionGridPanes[i]);
        }
        Label lbl;
        boardNrGridPane = new GridPane();
        boardNrGridPane.setVgap(3);
        lbl = new Label("Board No");
        GridPane.setConstraints(lbl, 0, 0);
        lbl.setAlignment(Pos.BASELINE_CENTER);
        lbl.setPrefSize(CONTROL_WIDTH, 30);
        boardNumberSpinner = new Spinner(1, 1, 1);
        //boardNumberSpinner.setStyle(BUTTON_STYLE);
        boardNumberSpinner.setPrefSize(CONTROL_WIDTH, 30);
        boardNumberSpinner.getValueFactory().setValue(1);
        GridPane.setConstraints(boardNumberSpinner, 0, 1);
        boardNrGridPane.setAlignment(Pos.CENTER);
        Button commentButton = new Button("Comment");
        commentButton.setStyle(BUTTON_STYLE);

        commentButton.setPrefSize(80, 30);
        GridPane.setConstraints(commentButton, 0, 2);
        boardNrGridPane.getChildren().addAll(lbl, boardNumberSpinner, commentButton);
        GridPane.setConstraints(boardNrGridPane, 2, 1);
        boardPane.getChildren().add(boardNrGridPane);
        commentButton.setOnAction((ActionEvent event) -> {
            int boardnr = boardNumberSpinner.getValue();
            Board board = sample.getBoard(boardnr);
            if (board != null) {
                Alert dialog = new Alert(AlertType.INFORMATION);
                TextArea taComment = new TextArea();
                taComment.setText(board.getComment());
                taComment.setEditable(true);
                taComment.setWrapText(true);
                taComment.setMaxWidth(Double.MAX_VALUE);
                taComment.setMaxHeight(Double.MAX_VALUE);
                dialog.getDialogPane().setContent(taComment);
                dialog.setTitle("Comment Box");
                dialog.setHeaderText("Edit your comment on board "
                    + boardnr);
                dialog.setX(1000);
                dialog.showAndWait();
                board.setComment(taComment.getText());
            }
        });

        commentButton.setPrefWidth(CONTROL_WIDTH);
        GridPane.setMargin(boardPane, new Insets(5, 5, 5, 5));
        generatorPane.setCenter(boardPane);
        return boardNrGridPane;
    }

    private void initRangeControls() {
        for (CheckBox cb : cbSuitsAndOtherFilters) {
            cb.selectedProperty().setValue(true);
        }

        for (CheckBox cb : cbDirections) {
            cb.selectedProperty().setValue(true);
        }

        cbSuitsAndOtherFilters[1].selectedProperty().setValue(false);
        cbDirections[1].selectedProperty().setValue(false);
        cbDirections[3].selectedProperty().setValue(false);

        for (Direction direction : Direction.values()) {
            RangeSelect rangeSelect = rangeSelects.get(direction).get(0);
            rangeSelect.setUpperBound(17);
        }
        rangeSelects.get(Direction.NORTH).get(0).setLowerBound(10);

        for (Direction direction : Direction.values()) {
            for (int suit = 2; suit < 6; suit++) {
                rangeSelects.get(direction).get(suit).setUpperBound(6);
            }
        }

    }

    private void clearFilters() {
        filters.put(Direction.NORTH, new GeneralHandFilter());
        filters.put(Direction.EAST, new GeneralHandFilter());
        filters.put(Direction.SOUTH, new GeneralHandFilter());
        filters.put(Direction.WEST, new GeneralHandFilter());
    }

    private void setFilters() {
        clearFilters();
        for (int row = 0; row < 4; row++) {
            if (cbDirections[row].isSelected()) {
                Direction direction = Direction.values()[row];
                RangeSelect rs;
                Range<Double> range;

                if (cbHCP.isSelected()) {
                    rs = rangeSelects.get(direction).get(0);
                    if (rs.isChanged()) {
                        range = new Range(rs.getLowerBound() * 1.0, rs.getUpperBound() * 1.0);
                        HCPFilter hcpFilter = new HCPFilter(range, false);
                        filters.get(direction).addFilter(hcpFilter);
                    }
                }

                if (cbLosers.isSelected()) {
                    rs = rangeSelects.get(direction).get(1);
                    if (rs.isChanged()) {
                        range = new Range(rs.getLowerBound() - 0.5, rs.getUpperBound() * 1.0);
                        LoserFilter loserFilter = new LoserFilter(range);
                        filters.get(direction).addFilter(loserFilter);
                    }
                }

                String pattern = cbPattern[row].getValue().toString();
                if (!pattern.equals("unknown")) {
                    if (pattern.equals("balanced")) {
                        filters.get(direction).addFilter(new BalancedFilter(true));
                    } else if (pattern.equals("unbalanced")) {
                        filters.get(direction).addFilter(new BalancedFilter(false));
                    } else {
                        filters.get(direction).addFilter(new PatternFilter(pattern));
                    }

                }

                boolean withLengthFilter = false;
                Map<Suit, Range<Integer>> lengthOfSuits = new HashMap<>();
                for (int column = 2; column < 6; column++) {
                    if (cbSuitsAndOtherFilters[column].isSelected()) {
                        rs = rangeSelects.get(direction).get(column);
                        if (rs.isChanged()) {
                            Range<Integer> lengthRange;
                            lengthRange = new Range(rs.getLowerBound(), rs.getUpperBound());
                            lengthOfSuits.put(Suit.values()[column - 2], lengthRange);
                            withLengthFilter = true;
                        }
                    }
                }
                if (withLengthFilter) {
                    LengthOfSuitsFilter lengthFilter = new LengthOfSuitsFilter(lengthOfSuits);
                    filters.get(direction).addFilter(lengthFilter);
                }

            }
        }

    }

    private static void showMessage(String title, String message) {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setTitle(title);
        dialog.setHeaderText(message);
        dialog.setX(700);
        dialog.showAndWait();
    }

    private static String printFreqTables(SampleOfBoards sample) {
        int size = sample.getSampleSize();
        if (size <= 1) {
            return "sample undefined";
        }
        int[] freqTable = sample.freqTableOfControls(Direction.NORTH);
        StringBuilder sb = new StringBuilder();
        double sum = 0;
        double sumsquares = 0;
        sb.append("Controls:\n");
        for (int i = 0; i <= 12; i++) {
            sb.append(i).append(": \t");
            int freq = freqTable[i];
            sb.append(freq).append("\t\t").append(Math.round(100.0 * freq / size)).append(" %\n");
            sum += freq * i;
            sumsquares += freq * i * i;
        }
        double average = sum / size;
        double sd = Math.sqrt((sumsquares - average * average * size) / (size - 1));
        average = Math.round(100 * average) / 100.0;
        sd = Math.round(100 * sd) / 100.0;
        sb.append("average: " + average + "; standard deviation: " + sd + "\n");

        freqTable = sample.freqTableOfLosers(Direction.NORTH);
        sb.append("\nLosers:\n");
        sum = 0;
        sumsquares = 0;
        for (int i = 0; i < 25; i++) {
            sb.append(i / 2.0).append(":   \t");
            int freq = freqTable[i];
            sum += freq * (i / 2.0);
            sumsquares += freq * i * i / 4.0;
            sb.append(freq).append("\t\t").append(Math.round(100.0 * freq / size)).append(" %\n");
        }

        average = sum / size;
        sd = Math.sqrt((sumsquares - average * average * size) / (size - 1));
        average = Math.round(100 * average) / 100.0;
        sd = Math.round(100 * sd) / 100.0;
        sb.append("average: " + average + "; standard deviation: " + sd + "\n");

        return sb.toString();
    }

    private static String string(Board board) {
        StringBuilder sb = new StringBuilder();
        String[] separators = {"\t\t\t\t\t", "\n", "\t\t\t\t\t\t", "\n\t\t\t\t\t"};
        int[] order = {0, 3, 1, 2};
        for (int i = 0; i < 4; i++) {
            sb.append(separators[i]);
            Direction direction = Direction.values()[order[i]];
            sb.append(board.hand(direction).toString());
        }
        return sb.toString();
    }

    private static String metrics(Hand hand) {
        return "H " + hand.hcp()
            + "; L " + hand.losers()
            + "; C " + hand.controls();
    }

    public static String framed(String text, int width) {
        StringBuilder sb = new StringBuilder();
        String STARS = "**********" + "**********" + "**********"
            + "**********" + "**********" + "*********" + "**********"
            + "**********" + "**********" + "*********";
        String SPACES = "          " + "          " + "          "
            + "          " + "          " + "          " + "          "
            + "          " + "          " + "          ";
        sb.append(STARS).append("\n");
        String[] lines = text.split("\n");
        sb.append("*").append(SPACES.substring(0, width - 2)).append("*\n");

        for (String line : lines) {
            sb.append("* ").append(line);
            if (width - line.length() - 3 > 0) {
                sb.append(SPACES.substring(0, width - line.length() - 3));
            }
            sb.append("*\n");
        }
        sb.append("*").append(SPACES.substring(0, width - 2)).append("*\n");

        sb.append(STARS).append("\n");
        return sb.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

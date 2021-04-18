/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author frankpeeters
 */
public class RangeSelect extends Control {

    public static final int HGAP = 1;
    public static final int WIDTH = 134 + 2 * HGAP + 1;
    public static final int HEIGHT = 30;

    private final Spinner<Integer> lowerBoundSpinner;
    private final Spinner<Integer> upperBoundSpinner;
    private RangeSelectConstraint rsc;
    private int minRange;
    private int maxRange;
    private boolean changed;

    public RangeSelect(int min, int max, int def, RangeSelectConstraint rsc) {

        this.minRange = min;
        this.maxRange = max;
        this.changed = false;
        lowerBoundSpinner = new Spinner<>(min, max, min);
        //lowerBoundSpinner.setStyle(BridgeBoardSimulator.BUTTON_STYLE);
        lowerBoundSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        upperBoundSpinner = new Spinner<>(min, max, max);

        //this.setStyle("-fx-skin: 'bridge.gui.RangeSelectSkin'");

        //upperBoundSpinner.setStyle(BridgeBoardSimulator.BUTTON_STYLE);
        //setStyle(BridgeBoardSimulator.BUTTON_STYLE);
        init(min, max, def, rsc);
    }

    private void init(int min, int max, int def, RangeSelectConstraint rsc) {
        Pane pane = new FlowPane();
        lowerBoundSpinner.setPrefSize(60, HEIGHT);
        upperBoundSpinner.setPrefSize(60, HEIGHT);
        pane.getChildren().add(lowerBoundSpinner);
        pane.getChildren().add(new Label(" .. "));
        pane.getChildren().add(upperBoundSpinner);
        this.getChildren().add(pane);
        pane.setPrefWidth(WIDTH);
        eventHandling();
        getStyleClass().add("custom-control");
        this.rsc = rsc;
        rsc.addRangeSelect(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return RangeSelect.class.getResource("/rangeselect.css").toExternalForm();
    }

    private void eventHandling() {
        // When spinner value changes: check on minValue <= maxValue.
        lowerBoundSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue.compareTo(getUpperBound()) > 0) {
                    upperBoundSpinner.getValueFactory().setValue(newValue);
                }
                changed = true;
                rsc.adjust(RangeSelect.this);
            }
        });
        upperBoundSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue.compareTo(getLowerBound()) < 0) {
                    lowerBoundSpinner.getValueFactory().setValue(newValue);
                }
                changed = true;
            }
        });
    }

    public int getLowerBound() {
        return lowerBoundSpinner.getValue();
    }

    public int getUpperBound() {
        return upperBoundSpinner.getValue();
    }

    public void setLowerBound(Integer lower) {
        changed = true;
        lowerBoundSpinner.getValueFactory().setValue(lower);
    }

    public void setUpperBound(Integer upper) {
        changed = true;
        upperBoundSpinner.getValueFactory().setValue(upper);
    }

    public void setEnabled(boolean enabled) {
        lowerBoundSpinner.disableProperty().setValue(!enabled);
        upperBoundSpinner.disableProperty().setValue(!enabled);
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public boolean isChanged() {
        return changed;
    }

}

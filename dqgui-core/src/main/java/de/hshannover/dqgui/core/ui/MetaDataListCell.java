package de.hshannover.dqgui.core.ui;

import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MetaDataListCell extends ListCell<Iqm4hdMetaData> {
    private static final Image ENV = new Image(MetaDataListCell.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"database_.png")), 
                               REPORTS = new Image(MetaDataListCell.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"portfolio_.png")),
                               CLOCK = new Image(MetaDataListCell.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"clock_.png")),
                               DATE = new Image(MetaDataListCell.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"calendar_.png"));

    @Override
    protected void updateItem(Iqm4hdMetaData item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {
            setText(null);
            setGraphic(null);
            setTooltip(null);
            setContextMenu(null);
        } else {
            setTooltip(new Tooltip(item.getHumanReadable().getContextInformation()));
            setGraphic(generateGraphic(item));
        }
    }
    
    private VBox generateGraphic(Iqm4hdMetaData item) {
        VBox v = new VBox();
        HBox r1 = new HBox(IconFactory.of(item.getReturnCode()), new Label(item.getAction()));
        r1.getChildrenUnmodifiable().get(1).getStyleClass().add("font-14");
        HBox.setMargin(r1.getChildren().get(1), new Insets(4, 0, 0, 4));
        HBox r2 = new HBox(new ImageView(DATE), new Label(Utility.DATE.format(item.getFinished())), new ImageView(CLOCK), new Label(Utility.TIME.format(item.getFinished())));
        HBox r3 = new HBox(new ImageView(ENV), new Label(item.getEnvironment()), new ImageView(REPORTS), new Label(Integer.toString(item.getIssues())));
        HBox.setMargin(r3.getChildren().get(0), new Insets(0, 0, 0, 3));
        HBox.setMargin(r2.getChildren().get(0), new Insets(0, 0, 0, 3));
        VBox.setMargin(r2, new Insets(7, 0, 7, 0));
        v.getChildren().addAll(r2, r3);
        ((Label) r2.getChildren().get(1)).setMinWidth(110);
        ((Label) r3.getChildren().get(1)).setMinWidth(110);
        v.getChildren().stream()
            .map(n -> (HBox) n)
            .map(HBox::getChildren)
            .flatMap(l -> l.stream())
            .forEach(n -> {
                if(n instanceof Label) {
                    n.getStyleClass().addAll("metadatalistcell-sublabel");
                    HBox.setMargin(n, new Insets(0, 5, 0, 5));
                }
            });
        v.getChildren().add(0, r1);
        return v;
    }
}
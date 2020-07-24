package de.hshannover.dqgui.core.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import de.hshannover.dqgui.core.model.Licenses;
import de.hshannover.dqgui.core.model.Licenses.CategoryType;
import de.hshannover.dqgui.core.model.Licenses.License;
import de.hshannover.dqgui.core.model.Licenses.LicenseFormat;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.JavaFXTools;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

final class LicensesController extends AbstractWindowController {
    @FXML Accordion types;
    
    @FXML
    void initialize() throws IOException {
        Map<CategoryType, Accordion> acc = new HashMap<>();
        Map<String, String> strs = new HashMap<>();
        Map<String, Image> imgs = new HashMap<>();
        Arrays.stream(CategoryType.values()).forEach(e -> {
            acc.put(e, new Accordion());
            this.types.getPanes().add(new TitledPane(e.desc, acc.get(e)));
        });
        acc.values().forEach(a -> a.setPadding(new Insets(0, 0, 0, 20)));
        this.types.setExpandedPane(this.types.getPanes().get(0));
        for(Map.Entry<CategoryType, Map<LicenseFormat, List<License>>> e : Licenses.INSTANCE.getData().entrySet()) {
            for(Map.Entry<LicenseFormat, List<License>> ll : e.getValue().entrySet()) {
                LicenseFormat f = ll.getKey();
                for(License l : ll.getValue()) {
                    VBox content = new VBox();
                    content.getStyleClass().add("license-box");
                    content.setAlignment(Pos.CENTER);
                    Hyperlink h = new Hyperlink(l.description);
                    VBox.setMargin(h, new Insets(0, 0, 10, 0));
                    h.getStyleClass().add("license-link");
                    h.setOnAction(ev -> JavaFXTools.sendUrlToDefaultSystemBrowser(l.url));
                    Node n;
                    switch(f) {
                    case IMAGE:
                        Image img;
                        if(!imgs.containsKey(l.content)) {
                            img = new Image(getClass().getResourceAsStream("/dqgui/core/licenses/"+l.content));
                            imgs.put(l.content, img);
                        } else {
                            img = imgs.get(l.content);
                        }
                        ImageView view = new ImageView(img);
                        n = new ScrollPane(view);
                        break;
                    case TEXT:
                        String s;
                        if(!strs.containsKey(l.content)) {
                            s = CharStreams.toString(new InputStreamReader(
                                    getClass().getResourceAsStream("/dqgui/core/licenses/"+l.content), Charsets.UTF_8));
                            strs.put(l.content, s);
                        } else {
                            s = strs.get(l.content);
                        }
                        TextArea area = new TextArea();
                        area.setEditable(false);
                        area.getStyleClass().add("license-text");
                        area.setWrapText(true);
                        area.setText(s);
                        n = area;
                        break;
                    default:
                        throw new AssertionError("Unhandled"); 
                    }
                    VBox.setVgrow(n, Priority.ALWAYS);
                    content.getChildren().addAll(h, n);
                    acc.get(e.getKey()).getPanes().add(new TitledPane(l.description, content));
                }
            }
        }
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> unregister());
    }
}
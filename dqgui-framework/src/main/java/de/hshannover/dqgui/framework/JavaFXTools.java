package de.hshannover.dqgui.framework;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Place static helper functions here.
 *
 */
public final class JavaFXTools {

    private JavaFXTools() {}

    /**
     * Force the systems default browser to open an URL.
     * @param url to open.
     */
    public static void sendUrlToDefaultSystemBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw ErrorUtility.rethrow(e);
        }
    }
    
    public static <T> void tableColumnAutoResize(TableView<T> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        int factor = (int) Math.round(100 / 1.0 * table.getColumns().size());
        for(int i = 0; i < table.getColumns().size(); i++) {
            table.getColumns().get(i).setMaxWidth(1f * Integer.MAX_VALUE * factor);
        }
    }

    public static void addAccelerator(Scene scene, KeyCode code, Runnable r) {
        scene.getAccelerators().put(new KeyCodeCombination(code), r);
    }

    public static void addAcceleratorShift(Scene scene, KeyCode code, Runnable r) {
        scene.getAccelerators().put(new KeyCodeCombination(code, KeyCombination.SHIFT_DOWN), r);
    }

    public static void addAcceleratorCtrl(Scene scene, KeyCode code, Runnable r) {
        scene.getAccelerators().put(new KeyCodeCombination(code, KeyCombination.CONTROL_DOWN), r);
    }

    public static void addAcceleratorAlt(Scene scene, KeyCode code, Runnable r) {
        scene.getAccelerators().put(new KeyCodeCombination(code, KeyCombination.ALT_DOWN), r);
    }
    
    public static void tableViewResize(TableView<?> content) {
        TableColumnHeader header = (TableColumnHeader) content.lookup(".column-header");
        if (header != null) {
            content.getVisibleLeafColumns().stream().forEach(column -> doColumnAutoSize(header, column));
        }
    }

    private static void doColumnAutoSize(TableColumnHeader columnHeader, @SuppressWarnings("rawtypes") TableColumn column) {
        invokeGetMethodValue(TableColumnHeader.class, columnHeader, "doColumnAutoSize", 
                new Class[] {TableColumnBase.class, Integer.TYPE},
                new Object[] {column, -1});
    }

    private static void invokeGetMethodValue(Class<?> clazz, Object instance, String method, @SuppressWarnings("rawtypes") Class[] parameter, Object[] values) {
        try {
            Method m = clazz.getDeclaredMethod(method, parameter);
            m.invoke(instance, values);
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }
}

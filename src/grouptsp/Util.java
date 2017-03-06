/*
 * Util.java - Gemeinsame Hilfsklasse für beide Aufgaben
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
//import java.util.ArrayList;
//import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JLabel;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
//import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

import com.incors.plaf.kunststoff.KunststoffLookAndFeel;
import com.incors.plaf.kunststoff.KunststoffTheme;

public class Util {

	private Util() {
	}
	
    /*
     * Debugging
     */

    private static boolean debugEnabled =
        (System.getProperty("debug") != null);

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    /*
     * Textfunktionen
     */

    /*
    // String "splitten" (Leere Strings nicht weglassen)
    public static List splitWithEmpty(String str, char ch) {
        List list = new ArrayList();
        char[] chars = str.toCharArray();
        String element = new String();
        for(int i = 0; i < chars.length; ++i) {
            if (chars[i] == ch) {
                list.add(element);
                element = new String();
            } else
                element += chars[i];
        }
        list.add(element);
        return list;
    }
    */

    /*
     * GUI-Methoden
     */

    public static void setLookAndFeel() {
        try {
            KunststoffLookAndFeel.setCurrentTheme(new MyTheme());
            UIManager.setLookAndFeel(new KunststoffLookAndFeel());
        // Alle Exceptions abfangen (Look&Feel ist egal)
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addSizeChecker(final Component c, final Dimension d) {
        c.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (Util.isDebugEnabled())
                    System.out.println(e);
                c.setSize(Math.max(d.width, c.getWidth()),
                          Math.max(d.height, c.getHeight()));
            }
        });
    }

    /*
     * Hilfsfunktionen zum einfachen Erstellen von Swing-Komponenten
     */

    // Feld zum Anzeigen von Werten
    public static JLabel newLabel(String title, String toolTip) {
        JLabel label = new JLabel(title, JLabel.RIGHT);
        label.setToolTipText(toolTip);
        return label;
    }

    /*
    // Feld zum Eingeben von Werten
    public static JTextField newTextField(String title, String toolTip) {
        JTextField field = new JTextField(title);
        field.setToolTipText(toolTip);
        return field;
    }
    */

    // Spinner für Kommawerte
    public static JSpinner newSpinner(float value, float min, float max,
                                      float step, String format,
                                      ChangeListener listener, String tip) {
        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(new Float(value), new Float(min),
                                   new Float(max), new Float(step)));
        JSpinner.DefaultEditor editor =
            new JSpinner.NumberEditor(spinner, format);
        editor.getTextField().setToolTipText(tip);
        spinner.setEditor(editor);
        if (listener != null)
            spinner.addChangeListener(listener);
        return spinner;
    }
    
    // Spinner für ganzzahlige Werte
    public static JSpinner newSpinner(int value, int min, int max, int step,
                                      ChangeListener listener, String tip) {
        JSpinner spinner =
            new JSpinner(new SpinnerNumberModel(value, min, max, step));
        ((JSpinner.DefaultEditor)spinner.getEditor()).
            getTextField().setToolTipText(tip);
        if (listener != null)
            spinner.addChangeListener(listener);
        return spinner;
    }

    // Button
    public static JButton newButton(String title, String toolTip,
                                    Icon icon, ActionListener listener) {
        JButton button = new JButton(title, icon);
        button.setToolTipText(toolTip);
        button.addActionListener(listener);
        return button;
    }

    // Gruppe mit betiteltem Rahmen
    public static JPanel newGroup(String title) {
        JPanel group = new JPanel() {
            public void setEnabled(boolean enabled) {
                Component[] c = getComponents();
                for (int i = 0; i < c.length; ++i)
                    c[i].setEnabled(enabled);
            }
        };
        group.setBorder(BorderFactory.createTitledBorder(title));
        group.setLayout(new GridBagLayout());
        return group;
    }

    /*
    // Menüelement
    public static JMenuItem newMenuItem(String title,
                                        ActionListener listener) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(listener);
        return item;
    }
    */

    /*
    // Auswahlfeld
    public static JComboBox newComboBox(String[] values, String toolTip,
                                        ActionListener listener) {
        JComboBox comboBox = new JComboBox(values);
        comboBox.setToolTipText(toolTip);
        comboBox.addActionListener(listener);
        return comboBox;
    }
    */

    /*
     * Hilfsfunktionen zum Erstellen von GridBagConstraints
     * für den Layout-Manager GridBagLayout.
     */

    private static GridBagConstraints newGBC(int x, int y, int w, int h,
                                             int t, int l, int b, int r,
                                             double wx, double wy, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.insets = new Insets(t, l, b, r);
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.fill = fill;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    // Horizontale Skalierung
    public static GridBagConstraints newGBC_H(int x, int y, int w, int h,
                                              int t, int l, int b, int r,
                                              double wx) {
        return newGBC(x,y,w,h,t,l,b,r,wx,0,GridBagConstraints.HORIZONTAL);
    }

    // Horizontale und vertikale Skalierung
    public static GridBagConstraints newGBC_B(int x, int y, int w, int h,
                                              int t, int l, int b, int r,
                                              double wx, double wy) {
        return newGBC(x,y,w,h,t,l,b,r,wx,wy,GridBagConstraints.BOTH);
    }
}

/*
 * GUI-Thema
 */

class MyTheme extends KunststoffTheme {
    private FontUIResource defaultFont =
         new FontUIResource("Tahoma", Font.PLAIN, 12);

    private FontUIResource windowTitleFont =
        new FontUIResource("Tahoma", Font.BOLD, 12);

    private FontUIResource monospacedFont =
        new FontUIResource("Monospaced", Font.PLAIN, 12);

    public String getName() {
        return "MyTheme";
    }

    public FontUIResource getControlTextFont() {
        return defaultFont;
    }

    public FontUIResource getMenuTextFont() {
        return defaultFont;
    }

    public FontUIResource getSystemTextFont() {
        return defaultFont;
    }

    public FontUIResource getUserTextFont() {
        return defaultFont;
    }

    public FontUIResource getWindowTitleFont() {
        return windowTitleFont;
    }

    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        UIManager.getDefaults().put("PasswordField.font", monospacedFont);
        UIManager.getDefaults().put("TextArea.font",      monospacedFont);
        UIManager.getDefaults().put("TextPane.font",      monospacedFont);
        UIManager.getDefaults().put("EditorPane.font",    monospacedFont);
    }
}


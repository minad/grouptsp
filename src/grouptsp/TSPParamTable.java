/*
 * TSPParamTable.java - Swing-Tabelle, die die Parameter
 *                      der Rundreisenlösung anzeigt.
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TSPParamTable extends JPanel {

    /*
     * Private Felder
     */

    private JTable table;

    /*
     * Konstruktor
     */

    public TSPParamTable(TSProblem hrg, Color[] color) {
        table = new JTable(new Model(hrg));

        TableColumnModel columnModel = table.getColumnModel();

        // Spaltenrenderer einstellen
        columnModel.getColumn(0).setCellRenderer(new ColorRenderer(color));
        columnModel.getColumn(1).setCellRenderer(new FormatRenderer("#.##"));
        columnModel.getColumn(2).setCellRenderer(new FormatRenderer("#%"));

        // Spaltenbreiten setzen
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(80);

        // Tabellenoptionen
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionInterval(0, 0);

        // ToolTips
        table.setToolTipText("Wählen Sie einen Parameter zum Optimieren aus.");
        table.getTableHeader().setToolTipText(
            "Wählen Sie einen Parameter zum Optimieren aus.");

        setBorder(BorderFactory.createLoweredBevelBorder());

        setLayout(new BorderLayout());
        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(table, BorderLayout.CENTER);

        setPreferredSize(getMinimumSize());
    }

    /*
     * Verschiedene Öffentliche Methoden
     */

    public int getSelectedParameter() {
        return table.getSelectedRow();
    }

    public void setEnabled(boolean enabled) {
        table.setEnabled(enabled);
    }

    /*
     * Datenmodell der Tabelle
     */

    private class Model extends AbstractTableModel implements ChangeListener {

       /*
        * Private Felder
        */

        private TSProblem tsp;

        private final String[] columnName = {
            "Parameter",
            "Wert",
            "Abweichung vom Optimum",
        };

        /*
         * Konstruktor
         */

        public Model(TSProblem h) {
            tsp = h;
            tsp.addChangeListener(this);
        }

        /*
         * Überschriebene Methoden von AbstractTableModel
         */

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            return tsp.getNumParameters();
        }

        public String getColumnName(int col) {
            return columnName[col];
        }

        public Object getValueAt(int row, int col) {
            TSProblem.Parameter param = tsp.getParameter(row);
            switch (col) {
            case 0: return param.getName();
            case 1: return new Float(param.getValue());
            case 2: return new Float(param.getDifference());
            }
            return null;
        }

        /*
         * Implementation von ChangeListener
         */

        public void stateChanged(ChangeEvent e) {
            if (Util.isDebugEnabled())
                System.out.println(e);
            fireTableRowsUpdated(0, getRowCount() - 1);
        }
    } // private class Model

    /*
     * Renderer für farbige Tabellenzellen
     */

    private class ColorRenderer extends JLabel implements TableCellRenderer {

        /*
         * Private Felder
         */

        private Border noFocusBorder;
        private Color[] color;

        /*
         * Konstruktor
         */

        public ColorRenderer(Color[] c) {
            color = c;
            setOpaque(true);
            noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        }

        /*
         * Implementation von TableCellRenderer
         */

        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

            setForeground(color[row]);
            setBackground(isSelected ? table.getSelectionBackground() :
                          table.getBackground());

            setBorder(hasFocus ?
                      UIManager.getBorder("Table.focusCellHighlightBorder") :
                      noFocusBorder);

            setText((String)value);
            return this;
        }
    } // private class ColorRenderer

    /*
     * Renderer für formatierte Tabellenzellen
     */

    private class FormatRenderer extends JLabel implements TableCellRenderer {

        /*
         * Private Felder
         */

        private Border noFocusBorder;
        private DecimalFormat formatter;

        /*
         * Konstruktor
         */

        public FormatRenderer(String format) {
            setOpaque(true);
            noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
            formatter = new DecimalFormat(format);
        }

        /*
         * Implementation von TableCellRenderer
         */

        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

            setBackground(isSelected ? table.getSelectionBackground() :
                          table.getBackground());

            setBorder(hasFocus ?
                      UIManager.getBorder("Table.focusCellHighlightBorder") :
                      noFocusBorder);

            setText(formatter.format(((Number)value).doubleValue()));
            return this;
        }
    } // private class FormatRenderer
}

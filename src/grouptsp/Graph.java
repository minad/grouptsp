/*
 * Graph.java - Swing-Komponente, das einen Graphen anzeigt
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class Graph extends JComponent implements Scrollable {

    /*
     * Konstanten
     */

    private static final int HEIGHT    = 150,
                             LINE_DIST = 50,
                             UNIT_INCR = 25;

    /*
     * Private Felder
     */

    private List  colors, plots;
    private float yMax, yMin;
    private int   width;

    /*
     * Konstruktor
     */

    public Graph() {
        colors = new ArrayList();
        plots = new ArrayList();
        resetPlots();
    }

    /*
     * Plots bearbeiten
     */

    public void createPlot(int i, Color color) {
        colors.add(i, color);
        plots.add(i, new ArrayList());
    }

    public void addPlotValue(int i, float value) {
        List values = (List)plots.get(i);
        values.add(new Float(value));

        // Neue Breite?
        if (values.size() > width)
            width = values.size();

        // Neuer minimaler bzw. maximaler y-Wert?
        if (value > yMax)
            yMax = value;
        else if (value < yMin)
            yMin = value;

        resizeAndRepaint();
    }

    public void resetPlots() {
        for (Iterator i = plots.iterator(); i.hasNext(); )
            ((List)i.next()).clear();
        yMin = 0;
        yMax = 1;
        width = 0;
        resizeAndRepaint();
    }

    /*
     * Komponente zeichnen
     */

    public void paintComponent(Graphics g) {
        if (Util.isDebugEnabled())
            System.out.println(getClass().getName() + ".paintComponent()");

        Graphics2D graph = (Graphics2D)g;

        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle bounds = graph.getClipBounds();
        graph.setBackground(Color.WHITE);
        graph.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);

        graph.setColor(Color.LIGHT_GRAY);
        for (int x = (bounds.x / LINE_DIST + 1) * LINE_DIST;
             x < bounds.x + bounds.width; x += LINE_DIST)
            graph.drawLine(x, 0, x, bounds.height);

        float heightScale = bounds.height / (yMax - yMin);
        for (int i = 0; i < plots.size(); ++i)
            drawPlot(i, graph, bounds, heightScale);
    }

    /*
     * Implementation von Scrollable
     */

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        return UNIT_INCR;
    }

    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return (o == SwingConstants.HORIZONTAL ?
                r.width / 2 : r.height / 2);
    }

    public boolean getScrollableTracksViewportWidth() {
        // Größe an Viewport anpassen, wenn Graph kleiner als Viewport.
        if (getParent() instanceof JViewport)
            return (((JViewport)getParent()).getWidth() >
                    getPreferredSize().width);
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        // Größe an Viewport anpassen, wenn Graph kleiner als Viewport.
        if (getParent() instanceof JViewport)
            return (((JViewport)getParent()).getHeight() >
                    getPreferredSize().height);
        return false;
    }

    /*
     * Private Methoden
     */

    private void resizeAndRepaint() {
        // Wenn der Graph in einem ScrollPane ist,
        // wird automatisch gescrollt.
        if (getParent() instanceof JViewport) {
            JViewport viewport = (JViewport)getParent();
            Rectangle viewRect = viewport.getViewRect();
            viewRect.x = width - 1;
            viewport.scrollRectToVisible(viewRect);
        }

        setPreferredSize(new Dimension(width, HEIGHT));
        // ScrollPane aktualisieren
        revalidate();
        // Neu zeichnen
        repaint();
    }

    private void drawPlot(int i, Graphics graph,
                          Rectangle bounds,
                          float heightScale) {
        graph.setColor((Color)colors.get(i));
        List values = (List)plots.get(i);
        for (int x = bounds.x;
             x < bounds.x + bounds.width && x + 1 < values.size(); ++x) {
            float y1 = ((Float)values.get(x)).floatValue();
            float y2 = ((Float)values.get(x + 1)).floatValue();
            graph.drawLine(x,
                           bounds.height - (int)(y1 * heightScale),
                           x + 1,
                           bounds.height - (int)(y2 * heightScale));
        }
    }
}

/*
 * TSPMap.java - Swing-Komponente, das eine Karte mit Rundreisen anzeigt
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TSPMap extends JComponent implements ChangeListener {

    /*
     * Konstanten
     */

    private static final int
        POINT_RADIUS = 3,
        MAP_WIDTH    = 500,
        MAP_HEIGHT   = 500;

    private static final Color TOUR_COLOR[] = {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.ORANGE,
        Color.BLACK,
    };

    /*
     * Sonstige private Felder
     */

    private TSProblem tsp;
    private int borderWidth;

    /*
     * Konstruktor
     */

    public TSPMap(TSProblem h) {
        tsp = h;
        tsp.addChangeListener(this);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        borderWidth = getInsets().top + POINT_RADIUS;
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
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

        graph.setBackground(Color.WHITE);
        graph.clearRect(0, 0, getWidth(), getHeight());

        float xScale = (float)(getWidth() - 2 * borderWidth) / (tsp.getWidth() - 1),
              yScale = (float)(getHeight() - 2 * borderWidth) / (tsp.getHeight() - 1);

        for (int n = 0; n < tsp.getNumTours(); ++n) {
            graph.setColor(TOUR_COLOR[n]);

            /*
             * Beim Zugriff auf die Städte der Touren wird
             * kein get() verwendet, da die Städte in einer LinkedList
             * gespeichert werden.
             */

            Iterator i = tsp.getTour(n).iterator();
            if (!i.hasNext())
                continue;

            Point city = (Point)i.next();
            int firstX = (int)(city.x * xScale + borderWidth);
            int firstY = (int)(city.y * yScale + borderWidth);
            int x = firstX, y = firstY;

            while (true) {
                graph.fillOval(x - POINT_RADIUS, y - POINT_RADIUS,
                               2 * POINT_RADIUS, 2 * POINT_RADIUS);

                if (!i.hasNext())
                    break;
                
                city = (Point)i.next();
                int newX = (int)(city.x * xScale + borderWidth);
                int newY = (int)(city.y * yScale + borderWidth);
                
                graph.drawLine(x, y, newX, newY);

                x = newX;
                y = newY;
            }

            if (x != firstX || y != firstY)
                graph.drawLine(firstX, firstY, x, y);
        }
    }

    /*
     * Implementation von ChangeListener
     */

    public void stateChanged(ChangeEvent e) {
        if (Util.isDebugEnabled())
            System.out.println(e);

        // Karte neu zeichnen
        repaint();
    }
}


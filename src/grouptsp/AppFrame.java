/*
 * AppFrame.java - Hauptklasse des Programms der 2. Aufgabe
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AppFrame extends JFrame
    implements ChangeListener, ActionListener {

    /*
     * Konstanten
     */

    private static final int
        OPTIMIZE_STEPS = 1000,
        NUM_TOURS      = 3,
        NUM_CITIES     = 50,
        MAP_WIDTH      = 100,
        MAP_HEIGHT     = 100;

    // Farben für Graphen (mit Transparenz)
    private static final Color[] GRAPH_COLOR = {
        new Color(0, 0, 1, .8f),
        new Color(1, 0, 0, .8f),
        new Color(0, .5f, 0, .8f),
        new Color(1, .5f, 0, .8f),
        new Color(0, .5f, 1, .8f),
    };

    //private Color temperatureColor = Color.ORANGE;

    /*
     * Handlungsreisegruppe
     */

    private TSProblem tsp;

    /*
     * Komponenten
     */

    private TSPMap tspMap;
    private Graph  graph;

    // Gruppe "Karte erstellen"
    private JPanel   creatorGroup;
    private JSpinner toursSpinner,
                     citiesSpinner,
                     widthSpinner,
                     heightSpinner;
    private JButton  createButton,
                     randomizeButton;

    // Gruppe "Optimieren"
    private TSPParamTable paramTable;
    private JLabel   stepsLabel;
    private JSpinner temperatureSpinner,
                     coolingRateSpinner;
    private JButton  optimizeButton;

    /*
     * Sonstige Felder
     */

    private Timer optimizeTimer;

    /*
     * Konstruktor
     */

    private AppFrame(){
        super(Resources.getMessage("FRAME_CAPTION")); //$NON-NLS-1$

        setIconImage(Resources.ICON_APP.getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tsp = new TSProblem(NUM_TOURS, NUM_CITIES,
                             MAP_WIDTH, MAP_HEIGHT);
        tsp.addChangeListener(this);

        optimizeTimer = new Timer(0, this);
        optimizeTimer.setCoalesce(true);

        setContentPane(createContentPane());

        pack();
        Util.addSizeChecker(this, getSize());

        show();
    }

    /*
     * Methoden zum Erstellen der Komponenten
     */

    private JPanel createContentPane() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        // Karte
        tspMap = new TSPMap(tsp);
        panel.add(tspMap, Util.newGBC_B(0,0,1,2,5,5,5,5,1,1));

        // Erstellen
        creatorGroup = createCreatorGroup();
        panel.add(creatorGroup, Util.newGBC_H(1,0,1,1,5,0,5,5,0));

        // Optimieren
        panel.add(createOptimizeGroup(), Util.newGBC_H(1,1,1,1,5,0,5,5,0));

        return panel;
    }

    private JPanel createCreatorGroup() {
        JPanel group = Util.newGroup(Resources.getMessage("GROUP_CREATEMAP")); //$NON-NLS-1$

        // Reisende
        group.add(new JLabel(Resources.getMessage("LABEL_NUMTRAVELLERS")), //$NON-NLS-1$
        		  Util.newGBC_B(0,0,1,1,0,5,5,5,0,0)); //$NON-NLS-1$

        toursSpinner = Util.newSpinner(tsp.getNumTours(), 1, 5, 1, null,
                                       Resources.getMessage("LABEL_NUMTOURS")); //$NON-NLS-1$
        group.add(toursSpinner, Util.newGBC_B(1,0,1,1,0,0,5,5,1,0));

        // Städte
        group.add(new JLabel(Resources.getMessage("LABEL_NUMCITIES")), //$NON-NLS-1$
        		             Util.newGBC_B(0,1,1,1,0,5,5,5,0,0)); //$NON-NLS-1$

        citiesSpinner = Util.newSpinner(tsp.getNumCities(), 1, 300, 1, null,
                                        Resources.getMessage("TOOLTIP_NUMCITIES")); //$NON-NLS-1$
        group.add(citiesSpinner, Util.newGBC_B(1,1,1,1,0,0,5,5,1,0));

        // Breite
        group.add(new JLabel(Resources.getMessage("LABEL_MAPWIDTH")), //$NON-NLS-1$
        		             Util.newGBC_B(0,2,1,1,0,5,5,5,0,0)); //$NON-NLS-1$

        widthSpinner = Util.newSpinner(tsp.getWidth(), 10, 100, 1,
                                       this, Resources.getMessage("TOOLTIP_MAPWIDTH")); //$NON-NLS-1$
        group.add(widthSpinner, Util.newGBC_B(1,2,1,1,0,0,5,5,1,0));

        // Höhe
        group.add(new JLabel(Resources.getMessage("LABEL_MAPHEIGHT")), //$NON-NLS-1$
        		             Util.newGBC_B(0,3,1,1,0,5,5,5,0,0)); //$NON-NLS-1$

        heightSpinner = Util.newSpinner(tsp.getHeight(), 1, 100, 1,
        		                        this, Resources.getMessage("TOOLTIP_MAPHEIGHT")); //$NON-NLS-1$
        group.add(heightSpinner, Util.newGBC_B(1,3,1,1,0,0,5,5,1,0));

        // Button "Neu erstellen"
        createButton = Util.newButton(Resources.getMessage("BUTTON_CREATEMAP"), //$NON-NLS-1$
        		                      Resources.getMessage("TOOLTIP_CREATEMAP"), //$NON-NLS-1$ //$NON-NLS-2$
                                      Resources.ICON_NEWMAP, this);
        group.add(createButton, Util.newGBC_H(0,4,2,1,0,5,5,5,1));

        // Button "Zufällige Rundreisen"
        randomizeButton = Util.newButton(Resources.getMessage("BUTTON_RANDMAP"), //$NON-NLS-1$
                                         Resources.getMessage("TOOLTIP_RANDMAP"), //$NON-NLS-1$
                                         Resources.ICON_RANDMAP, this);
        group.add(randomizeButton, Util.newGBC_H(0,5,2,1,0,5,5,5,1));


        return group;
    }

    private JPanel createOptimizeGroup() {
        JPanel group = Util.newGroup(Resources.getMessage("GROUP_OPTIMIZE")); //$NON-NLS-1$

        //group.add(Util.newLabel("Temperatur:", temperatureColor),
        //          Util.newGBC_H(0,0,1,1,0,3,3,3,0));

        // Temperatur
        group.add(new JLabel(Resources.getMessage("LABEL_TEMPERATURE")), //$NON-NLS-1$
                  Util.newGBC_B(0,0,1,1,0,5,5,5,0,0));

        temperatureSpinner =
            Util.newSpinner(tsp.getTemperature(), 0, 10000, 10, "#.#####", //$NON-NLS-1$
                            this, Resources.getMessage("TOOLTIP_TEMPERATURE")); //$NON-NLS-1$
        group.add(temperatureSpinner, Util.newGBC_B(1,0,1,1,0,0,5,5,1,0));

        // Abkühlungsrate
        group.add(new JLabel(Resources.getMessage("LABEL_COOLINGRATE")), //$NON-NLS-1$
            Util.newGBC_B(0,1,1,1,0,5,5,5,0,0));

        coolingRateSpinner =
            Util.newSpinner(tsp.getCoolingRate(),
                            0.5f, 0.99999f, 0.0001f, "#.#####", this, //$NON-NLS-1$
                            Resources.getMessage("TOOLTIP_COOLINGRATE")); //$NON-NLS-1$
        group.add(coolingRateSpinner, Util.newGBC_B(1,1,1,1,0,0,5,5,1,0));

        // Schritte
        group.add(new JLabel(Resources.getMessage("LABEL_STEPS")), //$NON-NLS-1$
        		             Util.newGBC_B(0,2,1,1,0,5,5,5,0,0)); //$NON-NLS-1$

        stepsLabel = Util.newLabel("0", Resources.getMessage("TOOLTIP_STEPS")); //$NON-NLS-1$ //$NON-NLS-2$
        group.add(stepsLabel, Util.newGBC_B(1,2,1,1,0,0,5,5,1,0));

        // Parameter-Tabelle
        paramTable = new TSPParamTable(tsp, GRAPH_COLOR);
        group.add(paramTable, Util.newGBC_H(0,3,2,1,0,5,5,5,1));

        // Button "Optimieren"
        optimizeButton =
            Util.newButton(Resources.getMessage("BUTTON_OPTIMIZE"), //$NON-NLS-1$
                           Resources.getMessage("TOOLTIP_OPTIMIZE"), //$NON-NLS-1$
                           Resources.ICON_OPTIMIZE, this);
        group.add(optimizeButton, Util.newGBC_H(0,4,2,1,0,5,5,5,1));

        // Graph
        graph = new Graph();
        for (int i = 0; i < tsp.getNumParameters(); ++i)
            graph.createPlot(i, GRAPH_COLOR[i]);
        //graph.createPlot(2, temperatureColor); // Temperatur
        JScrollPane scrollPane =
            new JScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(graph.getPreferredSize());
        scrollPane.setMinimumSize(graph.getPreferredSize());
        
        group.add(scrollPane, Util.newGBC_H(0,5,2,1,0,5,5,5,1));

        return group;
    }

    /*
     * Implementation von ChangeListener
     */

    public void stateChanged(ChangeEvent e) {
        if (Util.isDebugEnabled())
            System.out.println(e);

        // HRGProblem aktualisieren
        if (e.getSource() == temperatureSpinner) {
            Float temperature = (Float)temperatureSpinner.getValue();
            tsp.setTemperature(temperature.floatValue());
        } else if (e.getSource() == coolingRateSpinner) {
            Float coolingRate = (Float)coolingRateSpinner.getValue();
            tsp.setCoolingRate(coolingRate.floatValue());
        } else if (e.getSource() == tsp) {
            // GUI aktualisieren
            temperatureSpinner.setValue(new Float(tsp.getTemperature()));
            coolingRateSpinner.setValue(new Float(tsp.getCoolingRate()));
            stepsLabel.setText(String.valueOf(tsp.getNumSteps()));
        } else if (e.getSource() == widthSpinner || e.getSource() == heightSpinner)  {
        	int maxValue = ((Integer)widthSpinner.getValue()).intValue() *
			               ((Integer)heightSpinner.getValue()).intValue();
        	((SpinnerNumberModel)citiesSpinner.getModel()).setMaximum(
        		new Integer(Math.min(maxValue, 300)));    
        }
        //Number of cities spinner setMaximum(width*height)
    }

    /*
     * Implementation von ActionListener
     */

    public void actionPerformed(ActionEvent e) {
        if (Util.isDebugEnabled())
            System.out.println(e);

        if (e.getSource() == createButton) {
            // Karte erstellen
            tsp.init(((Integer)toursSpinner.getValue()).intValue(),
                     ((Integer)citiesSpinner.getValue()).intValue(),
                     ((Integer)widthSpinner.getValue()).intValue(),
                     ((Integer)heightSpinner.getValue()).intValue());
            graph.resetPlots();
        } else if (e.getSource() == randomizeButton)
            // Zufällige Rundreisen setzen
            tsp.randomize();
        else if (e.getSource() == optimizeButton) {
            // Optimierung starten oder anhalten
            boolean optimizing = optimizeTimer.isRunning();
            if (optimizing) {
                optimizeTimer.stop();
                optimizeButton.setText(Resources.getMessage("BUTTON_OPTIMIZE")); //$NON-NLS-1$
                optimizeButton.setIcon(Resources.ICON_OPTIMIZE);
            } else {
                optimizeTimer.start();
                optimizeButton.setText(Resources.getMessage("BUTTON_STOP")); //$NON-NLS-1$
                optimizeButton.setIcon(Resources.ICON_STOP);
            }
            creatorGroup.setEnabled(optimizing);
            paramTable.setEnabled(optimizing);
        } else if (e.getSource() == optimizeTimer) {
            // Nächster Iterationsschritt
            tsp.optimize(paramTable.getSelectedParameter(), OPTIMIZE_STEPS);
            for (int i = 0; i < tsp.getNumParameters(); ++i)
                graph.addPlotValue(i, tsp.getParameter(i).getValue());
            //graph.addPlotValue(2, hrg.getTemperature());
        }
    }

    /*
     * Main
     */

    public static void main(String[] args) {
        // Wegen der Thread-Sicherheit
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Util.setLookAndFeel();
                new AppFrame();
            }
        });
    }
}

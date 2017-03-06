/* 
 * TSProblem.java - Klasse zum Berechnen der Rundreisenlösungen
 *                   und Parameter.
 * Geschrieben von Daniel Mendler
 */

package grouptsp;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * Klasse HRGProblem (HandlungsReiseGruppe-Problem)
 *
 * Diese Klasse ist der Kern des Programms.
 * Sie ist für alle Berechnungen zuständig.
 */
public class TSProblem {

    /*
     * Private Daten des Problems
     * (z.B. die Touren, ...)
     */

    private int   numSteps,
                  width,
                  height;
    private List  tour[], cities;
    private float temperature,
                  coolingRate,
		  minimalLengthTemp; // FIXME

    /*
     * Die Parameter-Registry. Hier können
     * Parameterklassen eingetragen werden.
     */

    private final Parameter[] parameter = {
        /*
         * Parameter "Gesamtlänge"
         */
        new Parameter() {
            public String getName() {
                return "Gesamtlänge";
            }
            public float calcValue() {
                float value = 0;
                for (int i = 0; i < tour.length; ++i)
                    value += tourLength(i);
                return value;
            }

            public float calcOptimum() {
            	return minimalLengthTemp;
            }
        },

        /*
         * Parameter "Gesamtlänge + Verteilung"
         */
        new Parameter() {
            private static final float LENGTH_FACTOR = 1,
                                       DISTRIB_FACTOR = 1;

            public String getName() {
                return "Gesamtlänge + Verteilung";
            }
            public float calcValue() {
                float totalLength = 0,
                      distribution = 0,
                      length[] = new float[tour.length];
                for (int i = 0; i < tour.length; ++i) {
                    length[i] = tourLength(i);
                    totalLength += length[i];
                }
                float average = totalLength / tour.length;
                for (int i = 0; i < tour.length; ++i)
                    distribution += Math.abs(average - length[i]);
                return (LENGTH_FACTOR * totalLength +
                        DISTRIB_FACTOR * distribution);
            }
            public float calcOptimum() {
                return (LENGTH_FACTOR * minimalLengthTemp);
            }
        },

        /*
         * Parameter "Längste Reise"
         */
        new Parameter() {
            public String getName() {
                return "Längste Reise";
            }
            public float calcValue() {
                float value = 0;
                for (int i = 0; i < tour.length; ++i) {
                    float length = tourLength(i);
                    if (length > value)
                        value = length;
                }
                return value;
            }
            public float calcOptimum() {
            	return (minimalLengthTemp / tour.length);
            }
        },
    };

    /*
     * Sonstige private Felder
     */

    private List listeners;
    private static Random random = new Random();

    /*
     * Konstruktor und Initalisierungsmethode
     */

    public TSProblem(int numTours, int numCities, int w, int h) {
        listeners = new ArrayList();
        // Parameter registrieren
        for (int i = 0; i < parameter.length; ++i)
            addChangeListener(parameter[i]);
        init(numTours, numCities, w, h);
    }

    public void init(int numTours, int numCities, int w, int h) {
        // Touren erstellen
        tour = new List[numTours];
        for (int i = 0; i < tour.length; ++i)
            // LinkedList benutzen, da andauernd entfernt
            // und hinzugefügt wird
            tour[i] = new LinkedList();

        // Werte setzen
        width = w;
        height = h;
        temperature = 10 * numCities;
        coolingRate = 0.999f;
        numSteps = 0;

        // Städte erstellen
        Set citySet = new HashSet();
        for (int i = 0; i < numCities; ++i) {
            Point position;
            do
                position = new Point(random.nextInt(width),
                                     random.nextInt(height));
            while (citySet.contains(position));
            citySet.add(position);
        }
        cities = new ArrayList(citySet);

        // Städte auf die Touren verteilen
        distributeCities();

        /*
        // Parameter initialisieren
        for (int i = 0; i < parameter.length; ++i)
            parameter[i].init();
            */
        
        minimalLengthTemp = minimalLength();
        
        fireStateChanged();
    }

    public void randomize() {
        // Städte durchmischen
        Collections.shuffle(cities, random);
        for (int i = 0; i < tour.length; ++i)
            tour[i].clear();
        // Städte auf die Touren verteilen
        distributeCities();
        
        fireStateChanged();
    }

    /*
     * Diese Optimierungsmethode optimiert einen bestimmten Parameter
     * "param". Es werden "steps" Iterationsschritte durchgeführt.
     */

    public void optimize(int param, int steps) {
        for (int i = 0; i < steps; ++i) {
            int tourA, tourB, cityA, cityB;

            // Zufällige Touren auswählen
            tourA = random.nextInt(tour.length);
            do
                tourB = random.nextInt(tour.length);
            while (tour[tourB].size() < 1 + (tourA == tourB ? 1 : 0));

            // Zufällige Städte auswählen
            cityA = random.nextInt(Math.max(1, tour[tourA].size()));
            do
                cityB = random.nextInt(tour[tourB].size());
            while (tourA == tourB && cityA == cityB);

            // Neue Lösung erstellen
            tour[tourA].add(cityA, tour[tourB].remove(cityB));

            // Wird die neue Lösung akzeptiert?
            float newValue = parameter[param].calcValue();
            if (newValue - parameter[param].value < temperature) {
                parameter[param].value = newValue;
                temperature *= coolingRate;
            }
            else
                tour[tourB].add(cityB, tour[tourA].remove(cityA));

            ++numSteps;
        }
        fireStateChanged();
    }

    /*
     * Zugriffsmethoden
     */

    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public int getNumTours() {
        return tour.length;
    }

    public int getNumCities() {
        return cities.size();
    }

    public int getNumSteps() {
        return numSteps;
    }

    public List getTour(int t) {
        return tour[t];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Parameter getParameter(int param) {
        return parameter[param];
    }

    public int getNumParameters() {
        return parameter.length;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float t) {
        temperature = t;
        fireStateChanged();
    }

    public float getCoolingRate() {
        return coolingRate;
    }

    public void setCoolingRate(float c) {
        coolingRate = c;
        fireStateChanged();
    }

    public String toString() {
        String str = getClass().getName() +
                     "[numTours="    + tour.length   +
                     ",numCities="   + cities.size() +
                     ",width="       + width         +
                     ",height="      + height        +
                     ",numSteps="    + numSteps      +
                     ",temperature=" + temperature   +
                     ",coolingRate=" + coolingRate;
        for (int i = 0; i < parameter.length; ++i)
            str += ','         + parameter[i].getName() +
                   "[value="   + parameter[i].value     +
                   ",optimum=" + parameter[i].optimum   + ']';
        return str + ']';
    }

    /*
     * Private Methoden
     */

    // Städte auf die Touren verteilen
    private void distributeCities() {
        Iterator i = cities.iterator();
        for (int n = 0; i.hasNext(); ++n)
            tour[n % tour.length].add(i.next());
    }

    // ChangeEvent "feuern"
    private void fireStateChanged() {
        for (Iterator i = listeners.iterator(); i.hasNext(); )
            ((ChangeListener)i.next()).stateChanged(new ChangeEvent(this));
    }

    /*
     * Hilfsmethoden für die Parameter
     */

    private float tourLength(int t) {
        if (tour[t].size() <= 1)
            return 0;
        float length = 0;
        // Iterator statt get(), wegen LinkedList
        Iterator i = tour[t].iterator();
        Point city, firstCity = (Point)i.next();
        city = firstCity;
        while (i.hasNext()) {
            Point nextCity = (Point)i.next();
            length += (float)city.distance(nextCity);
            city = nextCity;
        }
        return (float)(length + firstCity.distance(city));
    }

    // Untere Schranke der Gesamtlänge
    private float minimalLength() {
    	// Distanzenmatrix erzeugen
    	float[][] distance = new float[cities.size()][cities.size()];
    	for (int a = 0; a < cities.size(); ++a) {
        	for (int b = a + 1; b < cities.size(); ++b) {
        		Point pa = (Point)cities.get(a), pb = (Point)cities.get(b);
        		distance[a][b] = distance[b][a] = (float)pa.distance(pb);
        	}
        }
    	
        // Alle Längen des minimale Spannbaums in der Liste "lengthList" speichern
    	List lengthList = new LinkedList();
    	boolean[] tree = new boolean[cities.size()];
    	tree[0] = true;
        while (true) {
            float length = 1e9f;
            int node = -1;
            for (int a = 0; a < tree.length; ++a) {
                if (!tree[a]) continue;
                for (int b = 0; b < tree.length; ++b) {
                	if (tree[b]) continue;
                	if (distance[a][b] < length) {
                        length = distance[a][b];
                        node = b;
                    }               	
                }
            }
            if (node < 0)
            	break;
            tree[node] = true;
            lengthList.add(new Float(length));
        }

        /* 1. Die "NumTours - 1" längsten Längen nicht mitrechnen,
         *    da die einzelnen Rundreisen unverbunden sind.
         * 2. Die "NumTours" kleinsten Längen doppelt rechnen,
         *    da die einzelnen Rundreisen geschlossen sind.
         */

        float length = 0;
        Collections.sort(lengthList);
        for (int i = lengthList.size() - tour.length; i >= 0; --i)
            length += (i < tour.length ? 2 : 1) *
                     ((Float)lengthList.get(i)).floatValue();
        return length;
    }

    /*
     * Die (abstrakte) Basisklasse aller Parameter
     */

    public abstract class Parameter implements ChangeListener {
        private float value, optimum;

        /*
        private void init() {
            optimum = calcOptimum();
        }
        */

        public float getValue() {
            return value;
        }

        public float getDifference() {
            if (value == optimum)
                return 0;
            return ((value / optimum) - 1);
        }

        public void stateChanged(ChangeEvent e) {
            value = calcValue();
            optimum = calcOptimum();
        }

        public abstract String getName();
        public abstract float  calcValue();
        public abstract float  calcOptimum();
    } // public class Parameter
}

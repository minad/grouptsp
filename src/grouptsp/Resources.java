package grouptsp;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class Resources {
	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("grouptsp.resources.messages");
	
    public static final ImageIcon
        ICON_APP      = getIcon("app.png"), //$NON-NLS-1$
        ICON_NEWMAP   = getIcon("newMap.png"), //$NON-NLS-1$
        ICON_RANDMAP  = getIcon("randomMap.png"), //$NON-NLS-1$
        ICON_OPTIMIZE = getIcon("optimize.png"), //$NON-NLS-1$
        ICON_STOP     = getIcon("stop.png"); //$NON-NLS-1$

	private Resources() {
	}
	
	public static String getMessage(String key) {
		try {
			return BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return ('!' + key + '!');
		}
	}

    public static ImageIcon getIcon(String path) {
        return new ImageIcon(Resources.class.getResource("resources/" + path));
    }
}
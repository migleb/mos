package model_os;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by NekoChan on 2016-05-13.
 */
public abstract class Registerable {

    protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener (PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
}

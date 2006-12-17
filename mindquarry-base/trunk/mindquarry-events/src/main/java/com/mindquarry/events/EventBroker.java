/**
 * Copyright (C) 2006 Mindquarry GmbH, All Rights Reserved
 */
package com.mindquarry.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.mindquarry.events.exception.EventAlreadyRegisteredException;
import com.mindquarry.events.exception.UnknownEventException;

/**
 * Add summary documentation here.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class EventBroker {
    private HashMap<String, Collection<EventListener>> registeredListeners;

    public EventBroker() {
        registeredListeners = new HashMap<String, Collection<EventListener>>();
    }

    public void registerEvent(String id) throws EventAlreadyRegisteredException {
        if (registeredListeners.keySet().contains(id)) {
            throw new EventAlreadyRegisteredException(
                    "Event already registered."); //$NON-NLS-1$
        }
        registeredListeners.put(id, new ArrayList<EventListener>());
    }

    public void registerEventListener(EventListener listener, String id)
            throws UnknownEventException {
        if (!registeredListeners.keySet().contains(id)) {
            throw new UnknownEventException("Unknown event type."); //$NON-NLS-1$
        }
        registeredListeners.get(id).add(listener);
    }

    public void publishEvent(final Event event, boolean block)
            throws UnknownEventException {
        if (block) {
            deliverEvent(event);
        } else {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        deliverEvent(event);
                    } catch (UnknownEventException e) {
                        // nothing to do
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void deliverEvent(Event event) throws UnknownEventException {
        String id = event.getID();
        if (!registeredListeners.keySet().contains(id)) {
            throw new UnknownEventException("Unknown event type."); //$NON-NLS-1$
        }
        Collection<EventListener> listeners = registeredListeners.get(id);
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}

package agent.markers;

import agent.Agent;

public interface Marker {

    void markLocation();

    void notified(Agent notifiedAgent);

    void erase();
}

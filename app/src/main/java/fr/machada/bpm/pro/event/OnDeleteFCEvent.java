package fr.machada.bpm.pro.event;

/**
 * Created by macha on 09/03/16.
 */
public class OnDeleteFCEvent {
    public final int id;

    public OnDeleteFCEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

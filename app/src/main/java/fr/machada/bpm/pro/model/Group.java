package fr.machada.bpm.pro.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<RegisteredFC> children = new ArrayList<RegisteredFC>();

    public Group(String string) {
        this.string = string;
    }

    public String getLabel() {
        return string;
    }

}

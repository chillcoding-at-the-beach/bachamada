package fr.machada.bpm.pro.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<RegisteredBpm> children = new ArrayList<RegisteredBpm>();

    public Group(String string) {
        this.string = string;
    }

    public String getLabel() {
        return string;
    }

}

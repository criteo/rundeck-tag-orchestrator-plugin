package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;

import java.util.ArrayList;
import java.util.Collection;

public  class GroupBuilder {
    private final String name;
    private double maxPerGroup = 1;
    private Collection<INodeEntry> nodes = new ArrayList<INodeEntry>();

    public  GroupBuilder(String name) {
        this.name = name;
    }

    public GroupBuilder setMaxPerGroup(double max) {
        this.maxPerGroup = max;
        return this;
    }

    public  GroupBuilder addToDoNode(INodeEntry node) {
        this.nodes.add(node);
        return this;
    }

    public Group build() {
        return  new Group(name, maxPerGroup, nodes);
    }
}

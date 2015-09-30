package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepResult;

import java.util.*;

public class Group {
    public final String name;
    final Stack<INodeEntry> todoNodes = new Stack<INodeEntry>();
    final Map<String, INodeEntry> inProgressNodes = new HashMap<String, INodeEntry>();
    public final int size;
    private final double maxPerGroup;


    public Group(String name, double maxPerGroup, Collection<INodeEntry> todoNodes) {
        this.name = name;
        this.todoNodes.addAll(todoNodes);
        this.maxPerGroup = maxPerGroup;
        this.size = todoNodes.size();
        System.out.println(String.format("%s group contains %d nodes", name, size));
    }

    public boolean isComplete() {
        return todoNodes.isEmpty();
    }

    public boolean canProcessANode() {
        if (maxPerGroup < 1) { // it is a percentage of nodes
            return inProgressNodes.size() * 100 / size < maxPerGroup * 100;
        } else { // it is a hard number of nodes
            return inProgressNodes.size() < maxPerGroup;
        }
    }

    public INodeEntry nextNode() {
        if (!isComplete() && canProcessANode()) {
            INodeEntry node = todoNodes.pop();
            System.out.println("Process " + node.extractHostname() + " from " + name);
            inProgressNodes.put(node.extractHostname(), node);
            return node;
        } else {
            return null;
        }
    }

    public void returnNode(INodeEntry node, boolean success, NodeStepResult nodeStepResult) {
        System.out.println(String.format("Returning %s in %s (success: %b)", node.extractHostname(), name, success));
        INodeEntry e = inProgressNodes.remove(node.extractHostname());
        if (e == null) {
            System.err.println(String.format("%s was not in progress but has just been returned. It should be impossible", node.extractHostname()));
        }
    }
}


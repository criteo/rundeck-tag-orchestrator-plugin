package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.StepExecutionContext;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepResult;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Orchestrate jobs across node groups. Group for a given node is defined using a configured tag.
 */
public class TagOrchestrator implements Orchestrator {
    private static final Logger logger = Logger.getLogger(TagOrchestrator.class);

    public static final String SERVICE_PROVIDER_TYPE = "tag-orchestrator";
    private final String[] tagNames;
    private final double maxPerGroup;

    private final Map<String, Collection<INodeEntry>> toDoNodesByGroup = new HashMap<String, Collection<INodeEntry>>();
    private final Map<String, Map<String, INodeEntry>> inProgressNodesByGroup = new HashMap<String, Map<String, INodeEntry>>();
    private final Map<String, Integer> groupSize = new HashMap<String, Integer>();

    public TagOrchestrator(StepExecutionContext context, Collection<INodeEntry> nodes, String[] tagNames, double maxPerGroup) {

        this.tagNames = tagNames;
        this.maxPerGroup = maxPerGroup;

        // groups nodes by group names
        for(INodeEntry node : nodes) {
            String groupName = getNodeGroupName(node, tagNames);
            if (!toDoNodesByGroup.containsKey(groupName)) {
                toDoNodesByGroup.put(groupName, new Stack<INodeEntry>());
            }
            Collection<INodeEntry> group = toDoNodesByGroup.get(groupName);
            group.add(node);
        }

        // create in progress nodes stacks
        for(String groupName : toDoNodesByGroup.keySet()) {
            groupSize.put(groupName, toDoNodesByGroup.get(groupName).size());
            logger.info(String.format("%s group contains %d nodes", groupName, groupSize.get(groupName)));
            inProgressNodesByGroup.put(groupName, new HashMap<String, INodeEntry>());
        }
    }

    private static String getNodeGroupName(INodeEntry node, String[] tagNames) {
        Map<String, String> attributes = node.getAttributes();
        String groupName = "";
        for(String tagName : tagNames) {
            String group;
            if (attributes.containsKey(tagName)) {
                group = attributes.get(tagName);
            } else {
                group = "noValue";
            }
            groupName += String.format("|%s=%s", tagName, group);
        }
        return groupName;
    }

    @Override
    public boolean isComplete() {
        // note: doc says we don't have to wait for all nodes to be returned
        for(String groupName : toDoNodesByGroup.keySet()) {
            Collection<INodeEntry> toDoNodes = toDoNodesByGroup.get(groupName);
            if (!toDoNodes.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean groupIsAtMaximumCapacity(String groupName, Map<String, INodeEntry> inProgressNodes) {
        if (maxPerGroup < 1) { // it is a percentage of nodes
            return inProgressNodes.size() * 100 / groupSize.get(groupName) < maxPerGroup * 100;
        } else { // it is a hard number of nodes
            return inProgressNodes.size() < maxPerGroup;
        }
    }

    @Override
    public INodeEntry nextNode() {
        for(String groupName : toDoNodesByGroup.keySet()) {
            Stack<INodeEntry> toDoNodes = (Stack<INodeEntry>)toDoNodesByGroup.get(groupName);
            Map<String, INodeEntry> inProgressNodes = inProgressNodesByGroup.get(groupName);

            boolean canStartANode = toDoNodes.size() > 0 && groupIsAtMaximumCapacity(groupName, inProgressNodes);

            if (canStartANode) {
                INodeEntry toDoNode = toDoNodes.pop();
                inProgressNodes.put(toDoNode.extractHostname(), toDoNode);
                return toDoNode;
            }
        }
        logger.info("No node is available or all groups are already at full capacity");
        return null;
    }

    @Override
    public void returnNode(INodeEntry node, boolean b, NodeStepResult nodeStepResult) {
        String groupName = getNodeGroupName(node, tagNames);
        INodeEntry removedNode = inProgressNodesByGroup.get(groupName).remove(node.extractHostname());
        if (removedNode == null) {
            logger.error(String.format("%s was not in progress but has just been returned. It should be impossible", node.extractHostname()));
        }
    }
}


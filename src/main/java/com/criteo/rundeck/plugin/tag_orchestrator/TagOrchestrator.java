package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.StepExecutionContext;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepResult;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;

import java.util.*;

/**
 * Orchestrate jobs across node groups. Group for a given node is defined using a configured tag.
 */
public class TagOrchestrator implements Orchestrator {

    public static final String SERVICE_PROVIDER_TYPE = "tag-orchestrator";

    private final Map<String, Group> groups = new HashMap<String, Group>();
    private final Options options;

    public TagOrchestrator(StepExecutionContext context, Collection<INodeEntry> nodes, Options options) {

        this.options = options;

        Map<String, GroupBuilder> groupBuilders = new HashMap<String, GroupBuilder>();

        // create node groups
        for(INodeEntry node : nodes) {
            String groupName = getNodeGroupName(node, options.tagNames);
            if (!groupBuilders.containsKey(groupName)) {
                groupBuilders.put(groupName, new GroupBuilder(groupName).setMaxPerGroup(options.maxPerGroup));
            }
            groupBuilders.get((groupName)).addToDoNode(node);
        }
        for(String name : groupBuilders.keySet()) {
            groups.put(name, groupBuilders.get(name).build());
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
        for(String groupName : groups.keySet()) {
            if (!groups.get(groupName).isComplete()) {
                System.out.println(String.format("asked if job is complete: false because %s is not empty", groupName));
                return false;
            }
        }
        System.out.println("asked if job is complete: true");
        return true;
    }

    @Override
    public INodeEntry nextNode() {
        for(String groupName : groups.keySet()) {
            Group group = groups.get(groupName);

            INodeEntry next = group.nextNode();
            if (next != null) {
               return next;
            }
        }
        System.out.println("No node is available or all groups are already at full capacity");
        return null;
    }

    @Override
    public void returnNode(INodeEntry node, boolean b, NodeStepResult nodeStepResult) {
        String groupName = getNodeGroupName(node, options.tagNames);
        groups.get(groupName).returnNode(node, b, nodeStepResult);
    }
}


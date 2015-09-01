package com.criteo.rundeck.plugin.tag_orchestrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;
import org.apache.log4j.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagOrchestratorTest {

    public String ATTRIBUTE = "datacenter, somethingThatDoesntExist";
    //public String ATTRIBUTE = "datacenter";

    public  TagOrchestratorTest() {
        ConsoleAppender console = new ConsoleAppender();
        console.setName("console");
        console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        console.setThreshold(Level.ALL);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);
    }

    @Test
    public void testRespectMaximumPerGroup() {
        List<INodeEntry> nodes = new ArrayList<INodeEntry>();

        for (String dc : new String[]{"par", "ny8", "sv6"}) {
            for (int i = 0; i < 3; ++i) {
                nodes.add(createNode(dc + String.valueOf(i), dc));
            }
        }

        TagOrchestratorPlugin factory = new TagOrchestratorPlugin(ATTRIBUTE, 2);
        Orchestrator plugin = factory.createOrchestrator(null, nodes);

        Stack<INodeEntry> inProgressNodes = new Stack<INodeEntry>();

        INodeEntry node;
        for (int i = 0; i < 6; ++i) {
            assertNotNull(node = plugin.nextNode());
            inProgressNodes.push(node);
        }
        // no node is available since all groups are at maximum
        assertNull(plugin.nextNode());

        // all nodes have not been treated yet
        assertFalse(plugin.isComplete());

        INodeEntry finishedNode = inProgressNodes.pop();
        plugin.returnNode(finishedNode, true, null);

        node = plugin.nextNode();
        // a node has finished, so we should be able to get a new one
        assertNotNull(node);
        // and it should be from the same group that the one we released
        assertEquals(finishedNode.getAttributes().get(ATTRIBUTE), node.getAttributes().get(ATTRIBUTE));

        while (!inProgressNodes.isEmpty()) {
            finishedNode = inProgressNodes.pop();
            plugin.returnNode(finishedNode, true, null);
            node = plugin.nextNode();
            if (node != null) {
                inProgressNodes.push(node);
            }
        }

        // in the end there is no node left to treat
        assertNull(plugin.nextNode());
        // and the plugin knows it
        assertTrue(plugin.isComplete());
    }

    private NodeEntryImpl createNode(String nodeName, String datacenter) {
        NodeEntryImpl nodeEntry = new NodeEntryImpl(nodeName, nodeName);
        nodeEntry.setAttribute("datacenter", datacenter);
        return nodeEntry;
    }
}
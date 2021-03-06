package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TagOrchestratorPercentageTest extends TagOrchestratorTest{

    private final double maxPercentage;
    private final int concurrentProcessedNodes;

    @Parameterized.Parameters(name = "{index}: maxPercentage={0}, concurrentProcessedNodes")
    public  static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {0.10, 30},
                {0.005, 3},
        });
    }

    public TagOrchestratorPercentageTest(double maxPercentage, int concurrentProcessedNodes) {
        this.maxPercentage = maxPercentage;
        this.concurrentProcessedNodes = concurrentProcessedNodes;
    }

    //@Test
    public void TestRespectMaximumPercentagePerGroup() {
        List<INodeEntry> nodes = new ArrayList<INodeEntry>();

        for (String dc : new String[]{"par", "ny8", "sv6"}) {
            for (int i = 0; i < 100; ++i) {
                nodes.add(createNode(dc + String.valueOf(i), dc));
            }
        }
        TagOrchestratorPlugin factory = new TagOrchestratorPlugin(ATTRIBUTE, maxPercentage);
        Orchestrator plugin = factory.createOrchestrator(null, nodes);

        for (int i = 0; i < concurrentProcessedNodes; ++i) {
            assertNotNull(plugin.nextNode());
        }
        assertNull(plugin.nextNode());
    }

    @Test
    public  void TestStopProcessingGroupAfterTooManyNodesFailed() {
        List<INodeEntry> nodes = new ArrayList<INodeEntry>();
        for (String dc : new String[]{"par", "ny8", "sv6"}) {
            for (int i = 0; i < 100; ++i) {
                nodes.add(createNode(dc + String.valueOf(i), dc));
            }
        }
        TagOrchestratorPlugin factory = new TagOrchestratorPlugin(ATTRIBUTE, maxPercentage);
        factory.stopProcessingGroupAfterTooManyFailure = true; //default but we never know
        Orchestrator plugin = factory.createOrchestrator(null, nodes);


        Stack<INodeEntry> runningNodes = new Stack<INodeEntry>();
        //Asking for a few nodes
        for (int i = 0; i < concurrentProcessedNodes; ++i) {
            runningNodes.add(plugin.nextNode());
        }

        //Currently processing:
        ((TagOrchestrator)plugin).printStatus();

        assertNull(plugin.nextNode());
        //no new node can be processed

        INodeEntry n = runningNodes.pop();
        plugin.returnNode(n, false, null); // a node has failed
        //node returned
        assertNull(plugin.nextNode());
        //cannot get more node

        assertFalse(plugin.isComplete());
        //but job is not complete yet
        for(INodeEntry node : runningNodes) {
            plugin.returnNode(node, true, null);
        }
        //all nodes returned
        for(int i=0; i<100; ++i) {
            n = plugin.nextNode();
            plugin.returnNode(n, true, null);
        }

        ((TagOrchestrator)plugin).printStatus();
        n = plugin.nextNode();
        while(n != null) {
          plugin.returnNode(n, false, null);
          n = plugin.nextNode();
        }
        //many failed nodes returned
        ((TagOrchestrator)plugin).printStatus();
        // there are still nodes to treat but all groups have too many failed nodes
        assertNull(plugin.nextNode());
        assertTrue(plugin.isComplete());


    }
}

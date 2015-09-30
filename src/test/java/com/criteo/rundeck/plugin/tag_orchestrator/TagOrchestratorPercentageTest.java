package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    @Test
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
}

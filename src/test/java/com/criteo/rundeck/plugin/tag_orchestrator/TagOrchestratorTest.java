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

    NodeEntryImpl createNode(String nodeName, String datacenter) {
        NodeEntryImpl nodeEntry = new NodeEntryImpl(nodeName, nodeName);
        nodeEntry.setAttribute("datacenter", datacenter);
        return nodeEntry;
    }
}


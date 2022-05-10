package com.example.enigma.circuit;

import androidx.annotation.NonNull;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Edge;
import com.example.enigma.database.Node;
import com.example.enigma.database.NodeDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class CircuitBuilder {
    private static CircuitBuilder instance;

    private final HashMap<String, Node> graph;

    private CircuitBuilder() {
        this.graph = new HashMap<>();
    }

    @NonNull
    public static CircuitBuilder getInstance()
    {
        if(instance == null)
        {
            instance = new CircuitBuilder();
        }

        return instance;
    }

    public void importGraph(@NonNull AppDatabase databaseInstance)
    {
        NodeDao nodeDao = databaseInstance.nodeDao();
        List<Node> nodes = nodeDao.getAll();

        for(Node node: nodes)
        {
            graph.put(node.getAddress(),node);
        }

        for(Map.Entry<String, Node> set : graph.entrySet())
        {
            Node currentNode = set.getValue();
            List<Edge> edges = nodeDao.getNeighbors(currentNode.getAddress()).getEdges();

            for(Edge edge: edges)
            {
                edge.setTargetNode(graph.get(edge.getTarget()));
            }

            currentNode.setAdjacencyList(edges);
        }
    }

    public void buildShortestCircuit(String startAddress)
    {
        Node startNode = graph.get(startAddress);
        assert startNode != null;

        startNode.setDistance(0);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(startNode);

        while(!queue.isEmpty())
        {
            Node currentNode = queue.poll();

            assert currentNode != null;
            List<Edge> edges = currentNode.getAdjacencyList();

            for(Edge edge : edges)
            {
                Node v = edge.getTargetNode();

                double d = currentNode.getDistance() + edge.getWeight();

                if(d < v.getDistance())
                {
                    queue.remove(v);
                    v.setDistance(d);
                    v.setPredecessor(currentNode);
                    queue.add(v);
                }
            }
        }
    }

    public List<Node> getShortestPath(String destinationAddress)
    {
        List<Node> path = new ArrayList<>();
        Node currentNode = graph.get(destinationAddress);

        while(currentNode != null)
        {
            path.add(currentNode);
            currentNode = currentNode.getPredecessor();
        }

        Collections.reverse(path);

        return path;
    }
}

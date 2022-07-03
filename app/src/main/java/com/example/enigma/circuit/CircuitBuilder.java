/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.example.enigma.circuit;

import androidx.annotation.NonNull;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Circuit;
import com.example.enigma.database.CircuitDao;
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

    public void buildShortestCircuit(@NonNull Node startNode)
    {
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

    public void buildShortestCircuit(String startAddress)
    {
        Node startNode = graph.get(startAddress);
        assert startNode != null;

        buildShortestCircuit(startNode);
    }

    public List<Circuit> getShortestPath(String destinationAddress, @NonNull AppDatabase databaseInstance)
    {
        Node currentNode = graph.get(destinationAddress);

        List<Circuit> path = new ArrayList<>();
        CircuitDao circuitDao = databaseInstance.circuitDao();

        int i = 0;
        while(currentNode != null)
        {
            Circuit circuit = new Circuit();
            circuit.setAddress(currentNode.getAddress());
            circuit.setDestination(destinationAddress);
            circuit.setIndex(i);

            circuitDao.insertAll(circuit);

            path.add(circuit);
            currentNode = currentNode.getPredecessor();
            i++;
        }

        Collections.reverse(path);

        return path;
    }
}

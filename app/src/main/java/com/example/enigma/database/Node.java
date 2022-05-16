package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "nodes")
public class Node implements Comparable <Node> {

    @PrimaryKey
    @NonNull
    private String address = "";

    private String ip;

    private String publicKey;

    @Ignore
    private boolean visited = false;

    @Ignore
    private double distance = Double.MAX_VALUE;

    @Ignore
    private Node predecessor;

    @Ignore
    private List<Edge> adjacencyList;

    @NonNull
    public String getAddress() {
        return address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getIp()
    {
        return ip;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public double getDistance()
    {
        return distance;
    }

    public Node getPredecessor()
    {
        return predecessor;
    }

    public void setPredecessor(Node predecessor)
    {
        this.predecessor = predecessor;
    }

    public void setAdjacencyList(List<Edge> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public List<Edge> getAdjacencyList()
    {
        return this.adjacencyList;
    }

    @Override
    public int compareTo(Node otherNode) {
        return Double.compare(this.distance, otherNode.getDistance());
    }
}

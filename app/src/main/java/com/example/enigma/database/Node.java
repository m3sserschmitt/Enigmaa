package com.example.enigma.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.enigma.OnionServices;

import java.util.List;

@Entity(tableName = "nodes")
public class Node implements Comparable <Node> {

    @PrimaryKey
    @NonNull
    private String address;

    @Nullable
    private String ip;

    @Nullable
    private String publicKeyPEM;

    @Ignore
    private double distance = Double.MAX_VALUE;

    @Ignore
    private Node predecessor;

    @Ignore
    private List<Edge> adjacencyList;

    public Node(@NonNull String address, @Nullable String ip, @Nullable String publicKeyPEM)
    {
        this.address = address;
        this.ip = ip;
        this.publicKeyPEM = publicKeyPEM;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    @Nullable
    public String getPublicKeyPEM() {
        return publicKeyPEM;
    }

    @Nullable
    public String getIp()
    {
        return ip;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setIp(@Nullable String ip) {
        this.ip = ip;
    }

    public void setPublicKeyPEM(@Nullable String publicKey) {
        this.publicKeyPEM = publicKey;
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

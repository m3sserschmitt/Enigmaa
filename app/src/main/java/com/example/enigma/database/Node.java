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

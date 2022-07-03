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
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "edges", primaryKeys = {"source", "target"})
public class Edge {

    @NonNull
    private String source = "";

    @NonNull
    private String target = "";

    @Ignore
    private Node targetNode;

    @Ignore
    private int weight = 1;

    public Edge(String target, String source)
    {
        this.target = target;
        this.source = source;
    }

    @NonNull
    public String getSource() {
        return source;
    }

    @NonNull
    public String getTarget()
    {
        return target;
    }

    public void setSource(@NonNull String source) {
        this.source = source;
    }

    public void setTarget(@NonNull String target) {
        this.target = target;
    }

    public int getWeight()
    {
        return this.weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
}

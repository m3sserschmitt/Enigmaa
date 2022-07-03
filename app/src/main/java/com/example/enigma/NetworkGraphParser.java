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

package com.example.enigma;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.enigma.database.AppDatabase;
import com.example.enigma.database.Edge;
import com.example.enigma.database.EdgeDao;
import com.example.enigma.database.Node;
import com.example.enigma.database.NodeDao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.Iterator;

public class NetworkGraphParser {

    private final AppDatabase databaseInstance;

    private String guardAddress;
    private String guardPublicKey;

    public NetworkGraphParser(AppDatabase databaseInstance)
    {
        this.databaseInstance = databaseInstance;
    }

    private boolean parseGraph(JSONObject jsonObject)
    {
        try{
            JSONObject graph = jsonObject.getJSONObject("graph");

            Iterator<String> addressesIterator = graph.keys();

            EdgeDao edgeDao = databaseInstance.edgeDao();
            NodeDao nodeDao = databaseInstance.nodeDao();

            nodeDao.clear();
            edgeDao.clear();

            while(addressesIterator.hasNext())
            {
                String address = addressesIterator.next();
                JSONObject nodeInfo = graph.getJSONObject(address);
                JSONArray adjacencyList = nodeInfo.getJSONObject("data")
                        .getJSONArray("neighbors");
                String publicKey = nodeInfo.getString("publicKey");

                int numberOfNeighbors = adjacencyList.length();

                Node node = new Node(address, null, publicKey);

                nodeDao.insertAll(node);

                for(int i = 0; i < numberOfNeighbors; i ++)
                {
                    Edge edge = new Edge(address, adjacencyList.get(i).toString());
                    //edge.setSource(address);
                    //edge.setTarget(adjacencyList.get(i).toString());

                    edgeDao.insertAll(edge);
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private boolean parseGuardInfo(JSONObject jsonObject)
    {
        try {
            guardAddress = jsonObject.getString("localAddress");
            guardPublicKey = jsonObject.getString("publicKey");
        } catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean parse(@Nullable String jsonData)
    {
        if(jsonData == null)
        {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            if(!parseGraph(jsonObject) || !parseGuardInfo(jsonObject))
            {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean parseFile(Context context, String filePath)
    {
        StringBuilder data = new StringBuilder();

        try {
             FileInputStream inputStream = context.openFileInput(filePath);

             int readData = inputStream.read();

             while(readData != -1)
             {
                 data.append((char)readData);

                 readData = inputStream.read();
             }

        } catch (Exception e)
        {
            return false;
        }

        return this.parse(data.toString());
    }

    public String getGuardAddress()
    {
        return guardAddress;
    }

    public String getGuardPublicKey()
    {
        return guardPublicKey;
    }
}

package com.example.enigma;

import android.content.Context;

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

    public NetworkGraphParser(AppDatabase databaseInstance)
    {
        this.databaseInstance = databaseInstance;
    }

    public boolean parse(String jsonData)
    {
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject graph = jsonObject.getJSONObject("graph");

            Iterator<String> addressesIterator = graph.keys();

            EdgeDao edgeDao = databaseInstance.edgeDao();
            NodeDao nodeDao = databaseInstance.nodeDao();

            while(addressesIterator.hasNext())
            {
                String address = addressesIterator.next();
                JSONObject nodeInfo = graph.getJSONObject(address);
                JSONArray adjacencyList = nodeInfo.getJSONObject("data")
                        .getJSONArray("neighbors");
                String publicKey = nodeInfo.getString("publicKey");

                int numberOfNeighbors = adjacencyList.length();

                Node node = new Node();
                node.setAddress(address);
                node.setPublicKey(publicKey);

                nodeDao.insertAll(node);

                for(int i = 0; i < numberOfNeighbors; i ++)
                {
                    Edge edge = new Edge();
                    edge.setOrigin(address);
                    edge.setSecondNode(adjacencyList.get(i).toString());

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
}

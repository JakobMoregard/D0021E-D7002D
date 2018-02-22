package Sim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Sink extends Node{

    public Sink(int network, int node){
        super(network, node);
    }

    private void log_time(String time, String file_name)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_name,true));
        writer.write(time + "\n");
        writer.close();
    }

    public void recv(SimEnt src, Event ev)
    {

        if (ev instanceof Message)
        {

            try {
                log_time(Double.toString(SimEngine.getTime()), "Sink_Recieving");
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println(e);
            }

            System.out.println("SinkNode " + _id.networkId() + "." + _id.nodeId() + " receives message with seq: " + ((Message) ev).seq() + " at time " + SimEngine.getTime());
        }
    }
}

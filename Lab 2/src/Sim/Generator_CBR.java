package Sim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator_CBR extends Node {
	
	protected double _time = 0;
	protected int _number_of_packages_per_second;
	
	public Generator_CBR (int network, int node) {
		super(node, node);
		_id = new NetworkAddr(network, node);
	}

    private void log_time(String time, String file_name)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_name,true));
        writer.write(time + "\n");
        writer.close();
    }
	
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq, int number_of_packages_per_second)
	{
        _number_of_packages_per_second = number_of_packages_per_second;
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
        send(this, new TimerEvent(), 0);
        System.out.println("Sending signal to start sending...");

	}
	
	public void recv(SimEnt src, Event ev)
	{


		if (ev instanceof TimerEvent)
		{

			if (_stopSendingAfter > _sentmsg)
			{

                double temp_time = 1.0/_number_of_packages_per_second;
                int pkg_nr = 1;         //Prints the number of any package in the loop bellow.
                _time = 0;

                for (int y = 0; y < _number_of_packages_per_second; y++) {

                    try{
                        log_time(Double.toString((SimEngine.getTime() + _time)), "CBR_Generator_Sending");
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e);
                    }

                    System.out.println("Time of sending package " + pkg_nr + " is: " + (SimEngine.getTime() + _time) + "\n");

                    _sentmsg++;
                    send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq), _time);
                    System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+(SimEngine.getTime() + _time));
                    _seq++;
                    _time += temp_time;

                }
                send(this, new TimerEvent(),1);
			}
		}

		if (ev instanceof Message)
		{

			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
		}
	}
	
}

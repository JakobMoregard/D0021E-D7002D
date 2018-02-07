package Sim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator_GAUSSIAN extends Node  {
	
	protected int _time = 0;
	Random randInt = new Random();
	
	public Generator_GAUSSIAN (int network, int node) {
		super(node, node);
		_id = new NetworkAddr(network, node);
	}
	
	private void log_time(double time) 
			  throws IOException {
				BufferedWriter writer = new BufferedWriter(new FileWriter("Gaussian_times",true));
			    writer.write(Double.toString(time) + "\n");
			    writer.close();
			}
	
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq)
	{
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		
		// Prints statistics of the generator
		System.out.println("Traffic generated from CBR_Node:"+
		
		"\n	Sender: " +
		"CBR_Generator, " + this +
		
		"\n	Reciever: " +
		_toHost +
		
		"\n	To network: " +
		_toNetwork + 
		
		"\n	Time between sending: " +
		_timeBetweenSending);
		
		
		for (int i = 0; _stopSendingAfter > i; i++){
			// Generate a diviation.
			double gaussian = randInt.nextGaussian() * timeInterval;
			if (gaussian < 0) {
				// If the diviation is negative, recalculate
				gaussian = randInt.nextGaussian() * timeInterval;
			}
			
			try{
				log_time(gaussian);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
			}
			
			
			System.out.println("Time of sending package " + (i+1) + " is: " + gaussian);
			send(this, new TimerEvent(),gaussian);			 
		}
			
	}
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof TimerEvent)
		{			
			if (_stopSendingAfter > _sentmsg)
			{
				_sentmsg++;
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				send(this, new TimerEvent(),_timeBetweenSending);
				System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
				_seq++;
			}
		}
		if (ev instanceof Message)
		{
			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
			
		}
	}
	
	
	
}

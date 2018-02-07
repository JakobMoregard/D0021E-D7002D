package Sim;

public class Generator_CBR extends Node {
	
	protected int _time = 0;
	
	public Generator_CBR (int network, int node) {
		super(node, node);
		_id = new NetworkAddr(network, node);
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
			 System.out.println("Time of sending package " + (i+1) + " is: " + _time);
			 send(this, new TimerEvent(),_time);
			 _time += timeInterval;
			 _timeBetweenSending += timeInterval;
			 
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

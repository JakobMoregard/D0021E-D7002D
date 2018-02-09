package Sim;

public class Generator_CBR extends Node {
	
	protected double _time = 0;
	
	public Generator_CBR (int network, int node) {
		super(node, node);
		_id = new NetworkAddr(network, node);
	}
	
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq, int number_of_packages_per_secound)
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

		double temp_time = 1.0/number_of_packages_per_secound;
		int pkg_nr = 1;         //Prints the number of any package in the loop bellow.
		int num_packages = 0;   //This will help to stop generating packages when you reach the limit of _stopSendingAfter
		
		 for (int i = 0; _stopSendingAfter > i; i++) {
			 for (int y = 0; y < number_of_packages_per_secound; y++) {
			     if (num_packages >=_stopSendingAfter){
			         break;
                 }
				 System.out.println("Time of sending package " + pkg_nr + " is: " + _time);
				 pkg_nr++;
				 send(this, new TimerEvent(), _time);
				 _time += temp_time;
				 num_packages++;

			 }
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

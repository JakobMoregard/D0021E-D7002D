package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
	protected  NetworkAddr oldAddr;
	protected NetworkAddr _id;
	protected SimEnt _peer;
	protected int _sentmsg=0;
	protected int _seq = 0;

	
	public Node (int network, int node)
	{
		super();
		_id = new NetworkAddr(network, node);
		oldAddr = _id;
	}	
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}

	public void updateIP(int network, int node)
	{
	    // Handle node disconnect in ROUTER

	    oldAddr = _id;
		_id = new NetworkAddr(network, node);

		Link newLink = new Link();
		newLink.setConnector(newLink);

		// 1. Update the routers table.
		send(this, new UpdateNodeIP(_id, oldAddr, _id, _seq++), 0);


	}
	
	
	public NetworkAddr getAddr()
	{
		return _id;
	}
	
//**********************************************************************************	
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators
	
	protected int _stopSendingAfter = 0; //messages
	protected int _timeBetweenSending = 10; //time between messages
	protected int _toNetwork = 0;
	protected int _toHost = 0;
	
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq)
	{
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		send(this, new TimerEvent(),0);	
	}
	
//**********************************************************************************	
	
	// This method is called upon that an event destined for this node triggers.
	
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

		// Not fully implemented
		if (ev instanceof UpdateNodeIP)
		{
		    // Check the sender
		    if (((UpdateNodeIP) ev)._source == _id)
		    {
		        // This will send a package to the router
                System.out.println("\n\n\nThe node is about to send a UpdateNodeIp package!");

                // New package ....
                System.out.println("Sending a UpdateNodeIp package!\n\n\n");
                send(_peer, new UpdateNodeIP(_id, oldAddr, _id, _seq++), 0);


            }else {
				// We have received a package, update old Address
				System.out.println("\n\n\nNode got UpdateNodeIp package!\n\n\n");
				//this._id = (UpdateNodeIP)ev.
				System.out.println("\n\n\nSending UpdateNodeIp package to router!\n\n\n");

				int oldNodeID = ((UpdateNodeIP) ev)._oldAddress.nodeId();
				int oldNetworkID = ((UpdateNodeIP) ev)._oldAddress.networkId();

				if (oldNodeID == _toHost && oldNetworkID == _toNetwork) {
					_toHost = ((UpdateNodeIP) ev)._newAddr.nodeId();
					_toNetwork = ((UpdateNodeIP) ev)._newAddr.networkId();
				}
			}
		}
	}
}

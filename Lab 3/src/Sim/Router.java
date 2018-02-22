package Sim;

// This class implements a simple router

import java.util.Arrays;

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;

	// When created, number of interfaces are defined
	
	Router(int interfaces)
	{
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
		{
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress)
	{
		SimEnt routerInterface=null;
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
		if (event instanceof Message)
		{
			System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(((Message) event).destination().networkId());
			System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());		
			send (sendNext, event, _now);
	
		}
		if (event instanceof UpdateNodeIP)
		{
			// The router has been notified to update the router table


			if (Arrays.asList(_routingTable).contains(((UpdateNodeIP) event)._oldAddress))
			{
			    // If the old address is found, remove it and add the new network address
				for (RouteTableEntry en : _routingTable)
                {
                    NetworkAddr oldAddr = ((UpdateNodeIP) event).getOld();
                    NetworkAddr newAddr = ((UpdateNodeIP) event).source();
                    if (((Node)en.node())._id.equals(oldAddr))
                    {
                    	// Found an old address in the routing table, remove and replace with new address
                        en = new RouteTableEntry(en.link(), new Node(newAddr.networkId(), newAddr.nodeId()));
                        Arrays.asList(_routingTable).remove(((UpdateNodeIP) event)._oldAddress);
                        Arrays.asList(_routingTable).add(en);

                    }
                }

				//Now generate a new UpdateNodeIPNode to update all the other nodes in the network.
                for (RouteTableEntry au : _routingTable)
                {

                    // Check that the address is NOT the source to prevent loops
                    // After the check, notify all nodes of th new address
                    if (!au.node().equals(((UpdateNodeIP) event)._source.nodeId()))
                    {
                        System.out.println("\n\n\nROUTER UPDATED TABLE!!\n\n\n");

                        // This will send a package to the router
                        System.out.println("\n\n\nThe node is about to send a UpdateNodeIp package!\n\n\n");

                        // New package to all nodes EXCEPT the node who triggered the update
                        SimEnt sendNext = getInterface(((Node)au.link())._toNetwork);
                        send (sendNext, event, _now);
                    }
                }


			}




		}
	}
}

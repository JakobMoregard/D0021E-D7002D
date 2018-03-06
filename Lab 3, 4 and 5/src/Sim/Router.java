package Sim;

// This class implements a simple router

import java.util.Arrays;

public class Router extends SimEnt{

    // Rename of the tables to seperate the nodes and the routers
	private RouteTableEntry [] node_table;
	private RouteTableEntry [] router_table;
    private int node_interfaces;
    private int router_interfaces;
	private int _now=0;
	private int _RID;

	// When created, number of interfaces are defined
	
	Router(int RID, int node_interfaces, int router_interfaces)
	{
		// Set router ID
		this._RID = RID;

	    // Node table
		node_table = new RouteTableEntry[node_interfaces];
		this.node_interfaces = node_interfaces;

		// Router table, has to be modified a bit before working...
        router_table = new RouteTableEntry[router_interfaces];
        this.router_interfaces = router_interfaces;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterfaceToNode(int interfaceNumber, SimEnt link, SimEnt node)
	{
	    System.out.println("Connect Node!");
		if (interfaceNumber<node_interfaces)
		{
			node_table[interfaceNumber] = new RouteTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}

    public void connectInterfaceToRouter(int interfaceNumber, SimEnt link, SimEnt router)
    {
        System.out.println("Connect Router!");
        if (interfaceNumber<router_interfaces)
        {
            router_table[interfaceNumber] = new RouteTableEntry(link, router);
        }
        else
            System.out.println("Trying to connect to port not in router");

        ((Link) link).setConnector(this);
    }

    public void sendRIP()
	{
	    send(this, new RIP(0, router_table, node_table, this._RID), 0);
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress)
	{
		SimEnt routerInterface=null;
		for(int i=0; i<node_interfaces; i++)
			if (node_table[i] != null)
			{
				if (((Node) node_table[i].device()).getAddr().networkId() == networkAddress)
				{
				    routerInterface = node_table[i].link();
                    return routerInterface;
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

		// If we get a RIP package
		if (event instanceof RIP)
		{
		    if (((RIP) event).origin == this._RID && ((RIP) event).jumps == 0)
		    {
		        // Send a new RIP package to every router (check for link)
                System.out.println("\n\n\nSending RIP package from router " + this._RID  + "!\n\n\n");
                for (int i = 0; i < router_interfaces; i++)
                {
                	// Check if a connection (network) is connected, otherwise it will get a null pointer exception...
                    SimEnt link = router_table[i].link();
                    ((RIP) event).jumps += 1;
                    ((RIP) event).connection_cost = ((Link) router_table[i].link()).link_cost;
                    ((RIP) event).last_router_id = this._RID;
                    send(link,event,0);
                }


            } else if (((RIP) event).origin == this._RID && ((RIP) event).jumps > 0){
		        // The broadcast has somehow returned, do nothing to drop the package...
                System.out.println("\nWARNING: Got a RIP package created from this host! Dropping the package to prevent loops! \nAmmount of jumps: " + ((RIP) event).jumps + ".\nLast Link cost: " + ((RIP) event).connection_cost + ".\nSent from router: " + ((RIP) event).last_router_id + ".");

            } else {

		        // Check if the package is less than 15 otherwise do nothing to drop the package
		        if (((RIP) event).jumps < 15)
                {

                    System.out.println("\n\n\nReceiving and forwarding RIP package from router " + this._RID  + "!\n\n\n");

                    // Compare and update the table (router/nodes)
                    // for each, check if in table and if the cost is less than the table, update
                    // if not in table, add the route and cost

                    // Timeout check for router_table (RFC) and mark poison on any route not responding

                    // forward to all routers (check for link)
                    for (int i = 0; i < router_interfaces; i++)
                    {
                        // Check if a connection (network) is connected, otherwise it will get a null pointer exception...
                        SimEnt link = router_table[i].link();
                        ((RIP) event).jumps += 1;
                        ((RIP) event).connection_cost = ((Link) router_table[i].link()).link_cost;
                        ((RIP) event).last_router_id = this._RID;
                        send(link,event,0);
                    }
                }


                // Do nothing to drop package...

            }


        }


		// Not fully implemented...
		if (event instanceof UpdateNodeIP)
		{
			// The router has been notified to update the router table

            System.out.println("\n\n\nThe Router received a UpdateNodeIp package!\n\n\n");

			if (Arrays.asList(node_table).contains(((UpdateNodeIP) event)._oldAddress))
			{
			    // If the old address is found, remove it and add the new network address
				for (RouteTableEntry en : node_table)
                {
                    NetworkAddr oldAddr = ((UpdateNodeIP) event).getOld();
                    NetworkAddr newAddr = ((UpdateNodeIP) event).source();
                    if (((Node)en.device())._id.equals(oldAddr))
                    {
                    	// Found an old address in the routing table, remove and replace with new address
                        en = new RouteTableEntry(en.link(), new Node(newAddr.networkId(), newAddr.nodeId()));
                        Arrays.asList(node_table).remove(((UpdateNodeIP) event)._oldAddress);
                        Arrays.asList(node_table).add(en);

                    }
                }

				//Now generate a new UpdateNodeIPNode to update all the other nodes in the network.
                for (RouteTableEntry au : node_table)
                {

                    // Check that the address is NOT the source to prevent loops
                    // After the check, notify all nodes of th new address
                    if (!au.device().equals(((UpdateNodeIP) event)._source.nodeId()))
                    {
                        System.out.println("\n\n\nROUTER UPDATED TABLE!!\n\n\n");

                        // This will send a package to the router
                        System.out.println("\n\n\nRouter recieved UpdateIP message!\n\n\n");

                        // New package to all nodes EXCEPT the node who triggered the update
                        SimEnt sendNext = getInterface(((Node)au.link())._toNetwork);
                        send (sendNext, event, _now);
                    }
                }
			}
		}
	}
}

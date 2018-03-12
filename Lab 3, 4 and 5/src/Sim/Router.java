package Sim;

// This class implements a simple router


/**
 * TODO: Update routing tables, cost of links etc
 */
public class Router extends SimEnt {

    // Rename of the tables to seperate the nodes and the routers
    private RouteTableEntry[] node_table;
    private int node_interfaces;
    private int _now = 0;
    private int _RID;

    // When created, number of interfaces are defined

    Router(int RID, int node_interfaces) {
        // Set router ID
        this._RID = RID;

        // Node table
        //node_table = new RouteTableEntry[node_interfaces];
        node_table = new RouteTableEntry[10];
        this.node_interfaces = node_table.length;
    }

    // This method connects links to the router and also informs the
    // router of the host connects to the other end of the link

    public void connectInterfaceToNode(int interfaceNumber, SimEnt link, SimEnt node) {
        System.out.println("Connect Node!");
        if (interfaceNumber < node_interfaces) {
            node_table[interfaceNumber] = new RouteTableEntry(link, node, interfaceNumber);
        } else
            System.out.println("Trying to connect to port not in router");

        ((Link) link).setConnector(this);
    }

    public void sendRIP() {
        send(this, new RIP(0, node_table, this._RID), 0);
    }

    // This method searches for an entry in the routing table that matches
    // the network number in the destination field of a messages. The link
    // represents that network number is returned

    private SimEnt getInterface(int networkAddress) {
        SimEnt routerInterface = null;
        for (int i = 0; i < node_interfaces; i++)
            if (node_table[i] != null) {
                try {
                    if (((Node) node_table[i].device()).getAddr().networkId() == networkAddress) {
                        routerInterface = node_table[i].link();
                        return routerInterface;
                    }
                } catch (Exception e) {
                    System.out.println("Not node");
                }
            }
        return routerInterface;
    }

    public RouteTableEntry[] getNode_table() {
        return node_table;
    }

    public void printRouting(RouteTableEntry[] node_table) {
        System.out.println("\nNode table for R" + _RID);
        for (int i = 0; i < node_table.length; i++) {
            try {
                // System.out.println("Entry " + i + ": " + node_table[i] + " : " + node_table[i].interfacenr());
                System.out.println("Entry " + i + ": Node: " +
                        ((Node) node_table[i].device())._id.networkId() + "." + ((Node) node_table[i].device())._id.nodeId());
            } catch (Exception e) {
                if (node_table[i] == null) {
                    System.out.println("Entry " + i + ":-");
                } else {
                    System.out.println("Entry " + i + ": RID: " + ((Router) node_table[i].device())._RID);
                }
            }
        }
        System.out.println();
    }

    // When messages are received at the router this method is called
    public void recv(SimEnt source, Event event) {
        if (event instanceof Message) {
            System.out.println("Router " + _RID + " handles packet with seq: " + ((Message) event).seq() + " from node: " + ((Message) event).source().networkId() + "." + ((Message) event).source().nodeId());
            SimEnt sendNext = getInterface(((Message) event).destination().networkId());
            System.out.println("Router sends to node: " + ((Message) event).destination().networkId() + "." + ((Message) event).destination().nodeId());
            send(sendNext, event, _now);
        }

        // If we get a RIP package
        if (event instanceof RIP) {
            if (((RIP) event).origin == this._RID && ((RIP) event).jumps == 0) {
                // Send a new RIP package to every router (check for link)
                System.out.println("\n\nSending RIP package from router " + this._RID + "!\n\n");
                for (int i = 0; i < node_interfaces; i++) {
                    // Check if a connection (network) is connected, otherwise it will get a null pointer exception...
                    try {
                        SimEnt a = ((Router) node_table[i].device());
                        SimEnt link = node_table[i].link();
                        ((RIP) event).jumps += 1;
                        ((RIP) event).connection_cost = ((Link) node_table[i].link()).link_cost;
                        ((RIP) event).last_router_id = this._RID;
                        send(link, event, 0);
                    } catch (NullPointerException e) {
                        System.out.println("Empty routing entry: " + i);
                    } catch (ClassCastException c){

                    }
                }
            } else if (((RIP) event).origin == this._RID && ((RIP) event).jumps > 0) {
                // The broadcast has somehow returned, do nothing to drop the package...
                System.out.println("\nWARNING: Got a RIP package created from this host! Dropping the package to prevent loops! \nAmount of jumps: " + ((RIP) event).jumps + ".\nLast Link cost: " + ((RIP) event).connection_cost + ".\nSent from router: " + ((RIP) event).last_router_id + ".");

            } else {

                // Check if the package is less than 15 otherwise do nothing to drop the package
                if (((RIP) event).jumps < 15) {
                    System.out.println("\nReceiving and forwarding RIP package from router " + ((RIP) event).origin + "!\n");


                    // Compare and update the table (router/nodes)
                    // for each, check if in table and if the cost is less than the table, update
                    // if not in table, add the route and cost etc...
                    RouteTableEntry[] ripRout = ((RIP) event).get_node_table();
                    System.out.println("Table before");
                    printRouting(node_table);
                    System.out.println("Table to merge with");
                    printRouting(ripRout);
                    boolean set = false;
                    for (int i = 0; i < ripRout.length; i++) {
                        //Dont wanna add null, saves some time
                        if (ripRout[i] == null) {
                            continue;
                        }
                        //Prevent adding itself to routing table
                        else if (ripRout[i].device() instanceof Router) {
                            if (((Router) ripRout[i].device())._RID == this._RID) {
                                System.out.println("Same Router");
                                continue;
                            } else {
                                for (int l = 0; l < node_table.length; l++) {
                                    try {
                                        if (((Router) node_table[l].device())._RID == ((Router) ripRout[i].device())._RID) {
                                            System.out.println("Router exists in table " + l + i + " replacing");
                                            //Kolla så cost är mindre
                                            node_table[l] = ripRout[i];
                                            set = true;
                                            break;
                                        }else {
                                            set = false;
                                        }
                                    } catch (ClassCastException e) {
                                        continue;
                                    } catch (NullPointerException n) {
                                        continue;
                                    }
                                }
                                if (!set) {
                                    for (int k = 0; k < node_table.length; k++) {
                                        if (node_table[k] == null) {
                                            node_table[k] = ripRout[i];
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (ripRout[i].device() instanceof Node) {
                            //If the node in rip table is a node or not the same router
                            //Replace if cost is less, if it dosnt exist add it to first free entry
                            for (int j = 0; j < node_table.length; j++) {
                                try {
                                    if (((Node) node_table[j].device())._id == ((Node) ripRout[i].device())._id) {
                                        System.out.println("Node Exists in table " + j + i);
                                        //BÖR KOLLA SÅ Cost är mindre
                                        node_table[j] = ripRout[i];
                                        set = true;
                                        break;
                                    }
                                    else{
                                        set = false;
                                    }
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            if (!set) {
                                for (int k = 0; k < node_table.length; k++) {
                                    if (node_table[k] == null) {
                                        node_table[k] = ripRout[i];
                                        break;
                                    }
                                }
                            }
                        }else{
                            System.out.println("\n\n\n\n\n\n\n\n\n\n wtf \n\n\n\n\n\n\n\n");
                        }
                    }
                    System.out.println("Table after");
                    printRouting(node_table);
                    // Timeout check for router_table (RFC) and mark poison on any route not responding

                    // forward to all routers (check for link)
                    System.out.println("\nSending RIP package!\n");
                    for (int i = 0; i < node_interfaces; i++) {
                        try {
                            // Check if a connection (network) is connected, otherwise it will get a null pointer exception...
                            SimEnt link = node_table[i].link();
                            int id = ((Router) node_table[i].device())._RID;
                            System.out.println(id);
                            ((RIP) event).jumps += 1;
                            ((RIP) event).connection_cost = ((Link) node_table[i].link()).link_cost;
                            ((RIP) event).last_router_id = this._RID;
                            send(link, event, 0);
                        } catch (NullPointerException e) {
                            // System.out.println("Empty routing entry: " + i);
                        } catch (ClassCastException c) {
                           // System.out.println("Not Router");
                        }
                    }
                }
                // Do nothing to drop package...
            }
        }
/*
		// Not fully implemented...
		if (event instanceof UpdateNodeIP)
		{
			// The router has been notified to update the router table

            System.out.println("\n\nThe Router received a UpdateNodeIp package!\n\n");

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
                        System.out.println("\n\nROUTER UPDATED TABLE!!\n\n");

                        // This will send a package to the router
                        System.out.println("\n\nRouter recieved UpdateIP message!\n\n");

                        // New package to all nodes EXCEPT the node who triggered the update
                        SimEnt sendNext = getInterface(((Node)au.link())._toNetwork);
                        send (sendNext, event, _now);
                    }
                }
			}
		}
		*/
    }
}

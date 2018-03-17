package Sim;

import java.util.HashMap;

// This class implements a simple router


/**
 * TODO: Update routing tables, cost of links etc
 */
public class Router extends SimEnt {

    // Rename of the tables to seperate the nodes and the routers
    private RouteTableEntry[] node_table;
    private HashMap<NetworkAddr, NetworkAddr> bindings;
    private int node_interfaces;
    private int _now = 0;
    private int _RID;
    private final boolean printTables = true;

    // When created, number of interfaces are defined

    Router(int RID, int node_interfaces) {
        // Set router ID
        this._RID = RID;

        // Node table
        node_table = new RouteTableEntry[node_interfaces*4];
        this.node_interfaces = node_table.length;
        bindings = new HashMap<NetworkAddr, NetworkAddr>();
    }

    // This method connects links to the router and also informs the
    // router of the host connects to the other end of the link

    public void connectInterfaceToNode(int interfaceNumber, SimEnt link, SimEnt node) {
        System.out.println("Connect Node!");
        if (interfaceNumber < node_interfaces) {
            node_table[interfaceNumber] = new RouteTableEntry(link, node, 0, false);
        } else
            System.out.println("Trying to connect to port not in router");

        ((Link) link).setConnector(this);
    }

    // Take a int as time as a parameter to stop sending the RIP packages.
    // This should add the events send RIP packages until the time reaches the argument
    public void sendRIP(int maxTime) {

        // prepare checks for invalid routingpaths
        for (int checkInvalidationRIP = 0; checkInvalidationRIP < maxTime ; checkInvalidationRIP += 60){
            send(this, new InvalidFlushRIP(), checkInvalidationRIP);
        }

        // prepare for RIP broadcasts
        for (int sendRIP = 0; sendRIP < maxTime; sendRIP += 25){
            send(this, new RIP(0, node_table, this._RID), sendRIP);
        }
    }

    // This method searches for an entry in the routing table that matches
    // the network number in the destination field of a messages. The link
    // represents that network number is returned

    private SimEnt getInterface(int networkAddress) {
        SimEnt routerInterface = null;
        for (int i = 0; i < node_interfaces; i++)
            if (node_table[i] != null) {
            	SimEnt dev = node_table[i].device();
            	
            	if (dev instanceof Node) {
            		Node node = (Node)dev;
            		
            		if (node.getAddr().networkId() == networkAddress) {
                        routerInterface = node_table[i].link();
                        return routerInterface;
                    }
            	} else if (dev instanceof Router) {
            		Router router = (Router)dev;
            		
            		if (router._RID == networkAddress) {
            			routerInterface = node_table[i].link();
            			return routerInterface;
            		}
            	}
            }
        
        // for debugging purposes
        if (routerInterface == null) {
        	System.out.println(networkAddress);
        	printRouting(node_table);
        	throw new NullPointerException();
        }
        
        return null;
    }

    /// Returns a node id that's not currently being used
    private int newNodeId() {
    	int nid = 0;
    	
    	while (true) {
    		boolean taken = false;
    		for (RouteTableEntry entry: node_table) {
    			if (entry == null) {
    				continue;
    			}
    			
    			SimEnt dev = entry.device();
    			
    			if (dev instanceof Node) {
    				Node node = (Node)dev;
    				
    				if (node._id.nodeId() == nid) {
    					taken = true;
    					break; // try another id
    				}
    			}
    		}
    		
    		if (!taken) {
    			return nid;
    		}
    	}
    }
    
    /// Returns the next free slot in the routing table 
    private int nextFreeSlot() {
    	int i = 0;
    	for (RouteTableEntry entry: node_table) {
    		if (entry == null) {
    			return i;
    		}
    		
    		i++;
    	}
		
		return -1;
	}
        
    public RouteTableEntry[] getNode_table() {
        return node_table;
    }

    public void printRouting(RouteTableEntry[] node_table) {
        if(!printTables){
            return;
        }
        System.out.println("\nNode table for R" + _RID);
        for (int i = 0; i < node_table.length; i++) {
            try {
                // System.out.println("Entry " + i + ": " + node_table[i] + " : " + node_table[i]. ());
                System.out.println("Entry " + i + ": Node: " +
                        ((Node) node_table[i].device())._id.networkId() + "." + ((Node) node_table[i].device())._id.nodeId());
            } catch (Exception e) {
                if (node_table[i] == null) {
                    System.out.println("Entry " + i + ": -");
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
        	Message m = (Message)event;
        	NetworkAddr msource = m.source();
        	NetworkAddr mdestination = m.destination();
        	NetworkAddr care_of_addr = bindings.get(mdestination);
        	
        	if (care_of_addr != null) {
        		// tunnel message to the care-of address
        		System.out.println("Tunneling message from " + mdestination.toString() + " to " + care_of_addr.toString());
        		mdestination = care_of_addr;
        		m.setDestination(care_of_addr);
        	}
        	
            System.out.println("Router " + _RID + " handles packet with seq: " + m.seq() + " from node: " + msource);
            SimEnt sendNext = getInterface(mdestination.networkId());
            System.out.println("Router sends to node: " + mdestination.toString());
            send(sendNext, event, _now);
        }

        // Registration request by a mobile node
        if (event instanceof RegistrationRequest) {
        	RegistrationRequest request = (RegistrationRequest)event;
        	
        	Node mn = (Node)source;
        	Router fa = (Router)this;
        		
        	// Network id
        	// XXX is this correct?
        	int nid = fa._RID;
        		
        	// Start of the registration request
        	NetworkAddr old_address = mn.getAddr();
        	System.out.println(mn.toString() + " is migrating to network " + nid);
        		
        	// update IP address
    		mn._id = new NetworkAddr(nid, newNodeId());
    		System.out.println(mn.toString() + " migrated from " + old_address.toString());
    			
    		// Update the node's link
    		Link l = new Link(1);
    		mn.setPeer(l);
        		
    		// Add the mobile node to the routing table of the foreign agent
    		int free_spot = nextFreeSlot();
    		fa.connectInterfaceToNode(free_spot, l, mn);
    			
    		// Create a binding in the home agent routing table
    		Router ha = request.homeAgent();
    		ha.bindings.put(old_address, mn.getAddr());
        }

        // This will be used to purge the routing table (Makes shure RFC is fulfilled)
        if (event instanceof InvalidFlushRIP){
            System.out.println("CHECK FOR INVALID ROUTES!");
            for (int i = 0; i < node_table.length; i++){
                if (node_table[i] == null){
                    continue;
                } else if (node_table[i].device() instanceof Router){

                    // Start the timer...
                    node_table[i].time += 60;

                    // If there has been more time than 180 sec, mark the route poison (should not be used by the router, RFC)
                    if (node_table[i].time <= 180){
                        node_table[i].poison = true;

                    // If there has been more time than 240 sec, drop the route (RFC)
                    } else if (node_table[i].time <= 240){
                        node_table[i] = null;
                    }
                }
            }
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
                                            System.out.println("Router exists in table " + l +"," + i + " replacing");
                                            //Should have a check to see if cost is less ?
                                            node_table[l] = ripRout[i];
                                            set = true;
                                            break;
                                        }else {
                                            set = false;
                                        }
                                    } catch (ClassCastException|NullPointerException e) {
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
                            
                            // Check if a router is poisoned, do not reset the timer if that is
                            if (node_table[i].poison == true)
                            {
                                continue;
                            } else {
                                node_table[i].time = 0;
                            }

                        } else if (ripRout[i].device() instanceof Node) {
                            //If the node in rip table is a node or not the same router
                            //Replace if cost is less, if it dosnt exist add it to first free entry
                            for (int j = 0; j < node_table.length; j++) {
                                try {
                                    if (((Node) node_table[j].device())._id == ((Node) ripRout[i].device())._id) {
                                        System.out.println("Node Exists in table " + j +","+ i);
                                        //Should have a check to see if cost is less ?
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
                            System.out.println("What node is this? " + ripRout[i].device());
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
                            //System.out.println(id);
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
    }
}

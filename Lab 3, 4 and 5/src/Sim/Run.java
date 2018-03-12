package Sim;

// An example of how to build a topology and starting the simulation engine

/**
 * All routers have 5 interfaces, 2 outgoing, 3 internal
 */
public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link(1);
		Link link2 = new Link(1);
		Link R1TOR2 = new Link(1);

		Link R2TOR3 = new Link(1);
		Link R3TOR4 = new Link(1);

		Link link3 = new Link(1);
		Link link4 = new Link(1);

		//CBR
		Node host1 = new Node(1,1);
		//Generator_CBR host1 = new Generator_CBR(1,1);
        Node host2 = new Node(2,1);
        Node host3 = new Node(3, 1);
        Node host4 = new Node(4, 1);


		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		host3.setPeer(link3);
		host4.setPeer(link4);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router R1 = new Router(1,5);
		Router R2 = new Router(2,5);
		Router R3 = new Router(3,5);
		Router R4 = new Router(4,5);

		R1.connectInterfaceToNode(0, link1, host1);
		R2.connectInterfaceToNode(0, link2, host2);
		R3.connectInterfaceToNode(0, link3, host3);
		R4.connectInterfaceToNode(0, link4, host4);

		R1.connectInterfaceToNode(2, R1TOR2, R2);
		R2.connectInterfaceToNode(1, R1TOR2, R1);
		R2.connectInterfaceToNode(3, R2TOR3, R3);
		R3.connectInterfaceToNode(2, R2TOR3, R2);

		R3.connectInterfaceToNode(6, R3TOR4, R4);
		R4.connectInterfaceToNode(6, R3TOR4, R3);


		System.out.println("Connected link with first router: " + R1TOR2._connectorA.toString() +
				" and second router: " + R1TOR2._connectorB.toString());

		R1.sendRIP();
		R2.sendRIP(); //Using multiple of these to delay the startSending and allowing the table to upd
		R3.sendRIP();
		R4.sendRIP();
		R1.sendRIP();
		R2.sendRIP();
		R3.sendRIP();
		R4.sendRIP();
		R1.sendRIP();
		R2.sendRIP();
		R3.sendRIP();
		R4.sendRIP();
		host4.StartSending(1,1,2,50,0); //CTRL+F to finde the node :)

		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());

		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?" +  e.toString());
		}
		System.out.println("_____\nFinal Tables");
		R1.printRouting(R1.getNode_table());
		R2.printRouting(R2.getNode_table());
		R3.printRouting(R3.getNode_table());
		R4.printRouting(R4.getNode_table());
	}
}

package Sim;

// An example of how to build a topology and starting the simulation engine

/**
 * All routers have 5 interfaces, 2 outgoing, 3 internal
 */
public class simulateLinkFailure {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link(1);
		Link link2 = new Link(1);
		Link link3 = new Link(1);
		Link R1TOR2 = new Link(1);
        LinkSimulateFailure R2TOR3 = new LinkSimulateFailure(1,70);

		Node host1 = new Node(1,1);
        Node host2 = new Node(2,1);
        Node host3 = new Node(3, 1);


		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);
		host3.setPeer(link3);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router R1 = new Router(1,2);
		Router R2 = new Router(2,2);
		Router R3 = new Router(3,2);

		// Connect the hosts to a router
		R1.connectInterfaceToNode(0, link1, host1);
		R2.connectInterfaceToNode(0, link2, host2);
		R3.connectInterfaceToNode(0, link3, host3);

		// connects R1 to R2 and R2 to R3
		R1.connectInterfaceToNode(2, R1TOR2, R2);
		R2.connectInterfaceToNode(1, R1TOR2, R1);
		R2.connectInterfaceToNode(3, R2TOR3, R3);
		R3.connectInterfaceToNode(2, R2TOR3, R2);


		R1.sendRIP(400);
		R2.sendRIP(400);
		R3.sendRIP(400);

		host1.changeInterface(7, 2);
		//host1.send(R2, new RegistrationRequest(R1), 60);
		host1.StartSending(2,1,10,1,0, 0);
		host3.StartSending(1,1,10,10,50, 0);
		//host4.StartSending(1,1,5,50,0); //CTRL+F to finde the node :)

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
	}
}

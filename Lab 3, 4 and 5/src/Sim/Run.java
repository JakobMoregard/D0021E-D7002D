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

		R1.connectInterfaceToNode(3, R1TOR2, R2);
		R2.connectInterfaceToNode(3, R1TOR2, R1);
		R2.connectInterfaceToNode(4, R2TOR3, R3);
		R3.connectInterfaceToNode(4, R2TOR3, R2);

		R3.connectInterfaceToNode(6, R3TOR4, R4);
	R4.connectInterfaceToNode(6, R3TOR4, R3);

		//Temporary fix to check if you can send between networks
		//Remove these when merging routing tables work
		//R1.setRemote(2, R1TOR2, host2);
		//R1.setRemote2(2, R1TOR2, host3);

		//R1.setRemote3(8, R1TOR2, host4);
		//R2.setRemote3(8, R3TOR4, host4);
		//R2.setRemote2(4, R2TOR3, host3);
		//R3.setRemote2(6, R3TOR4, host4);

		System.out.println("Connected link with first router: " + R1TOR2._connectorA.toString() +
				" and second router: " + R1TOR2._connectorB.toString());


		R1.sendRIP();
		R2.sendRIP();
		R3.sendRIP();
		R4.sendRIP();
		//host1.StartSending(2,1,4,4);
		host4.StartSending(1,1,1,1,0);

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
	}
}

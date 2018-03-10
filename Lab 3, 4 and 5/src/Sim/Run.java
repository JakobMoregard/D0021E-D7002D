package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link(1);
		Link link2 = new Link(1);
		Link R1TOR2 = new Link(1);

		Link R2TOR3 = new Link(1);
		Link link3 = new Link(1);

		//CBR
		Generator_CBR host1 = new Generator_CBR(1,1);
        Node host2 = new Node(2,1);
        Node host3 = new Node( 3, 1);


		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);
		host3.setPeer(link3);


		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router R1 = new Router(1,2, 5);
		Router R2 = new Router(2,2,5);
		Router R3 = new Router(3, 2, 5);

		R1.connectInterfaceToNode(0, link1, host1);
		R2.connectInterfaceToNode(0, link2, host2);
		R3.connectInterfaceToNode(0, link3, host3);

		R1.connectInterfaceToRouter(0, R1TOR2, R2);
		R2.connectInterfaceToRouter(0, R1TOR2, R1);

		R3.connectInterfaceToRouter(3, R2TOR3, R2);
		R2.connectInterfaceToRouter(3, R2TOR3, R3);

		System.out.println("Connected link with first router: " + R1TOR2._connectorA.toString() +
				" and second router: " + R1TOR2._connectorB.toString());


		R1.sendRIP();

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

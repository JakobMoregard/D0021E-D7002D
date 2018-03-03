package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
		Link link1 = new Link(1);
		Link link2 = new Link(1);
		Link R1TOR2 = new Link(1);
		
		// Create two end hosts that will be
		// communicating via the router
		//Node host1 = new Node(1,1);
		//Node host2 = new Node(2,1);

		//CBR
		Generator_CBR host1 = new Generator_CBR(1,1);
        Node host2 = new Node(2,1);

		//NORMAL
		//Generator_NORMAL host1 = new Generator_NORMAL(1,1);
		//Sink host2 = new Sink(2,1);

		//POISSON
		//Generator_POISSON host1 = new Generator_POISSON(1,1);
		//Sink host2 = new Sink(2,1);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router R1 = new Router(1,3, 2);
		Router R2 = new Router(2,2,2);

		R2.connectInterfaceToRouter(1, R1TOR2, R1);

		R1.printTable();
		//routeNode.connectInterface(0, link1, host1);
		//routeNode.connectInterface(1, link2, host2);
		
		// Generate some traffic
		// host1 will send 3 messages with time interval 5 to network 2, node 1. Sequence starts with number 1

        //CBR
		//host1.StartSending(2, 2, 10,1);

		//Normal
        //host1.StartSendingNormal(2,2,2,10,10000);

        //POISSON
        //host1.StartSending(2,2,5,10);

		//Trigger the host to change
        // host2.updateIP(3,1);

		// host2 will send 2 messages with time interval 10 to network 1, node 1. Sequence starts with number 10
		// host2.StartSending(1, 1, 2, 10, 10); 
		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}

package Sim;

public class Mobility {
	public static void main (String [] args)
	{
		// Links
		Link a = new Link(1);
		Link b = new Link(1);
		Link r = new Link(1);
		Link c = new Link(1);
		
		// Hosts
		Node A = new Node(1, 1);
        Node B = new Node(1, 2);
        Node C = new Node(2, 3);

		//Connect hosts to links
		A.setPeer(a);
		B.setPeer(b);
		C.setPeer(c);

		// Routers
		Router R1 = new Router(1, 4);
		Router R2 = new Router(2, 4);

		// Wire up routers
		R1.connectInterfaceToNode(0, a, A);
		R1.connectInterfaceToNode(1, b, B);
		R1.connectInterfaceToNode(2, r, R2);
		
		R2.connectInterfaceToNode(0, c, C);
		R2.connectInterfaceToNode(0, r, R1);

		// A and C send two packets to B; B send two packets to A
		// The first packets will be sent *before* B migrates
		// The seconds packets will be sent *after* B migrates
		A.StartSending(B.getAddr().networkId(), B.getAddr().nodeId(), 2, 40, 0, 0);
		C.StartSending(B.getAddr().networkId(), B.getAddr().nodeId(), 2, 40, 2, 10);
		B.StartSending(A.getAddr().networkId(), A.getAddr().nodeId(), 2, 40, 4, 20);
		
		// migrate B to network 2
		B.send(R2, new RegistrationRequest(R1), 30);

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

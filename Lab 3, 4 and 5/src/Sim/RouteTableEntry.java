package Sim;

// This class represent a routing table entry by including
// the link connecting to an interface as well as the node 
// connected to the other side of the link

public class RouteTableEntry extends TableEntry{

    protected double time;
    protected boolean poison;

	RouteTableEntry(SimEnt link, SimEnt device, double time, boolean poison)
	{
		super(link, device);
		this.time = time;
	    this.poison = poison;
	}

	public SimEnt link()
	{
		return super.link();
	}

	public SimEnt device() { return super.device(); }
	
}

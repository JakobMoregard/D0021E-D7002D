package Sim;

// This class represent a routing table entry by including
// the link connecting to an interface as well as the node 
// connected to the other side of the link

public class RouteTableEntry extends TableEntry{
	private int _interfacenr;
	RouteTableEntry(SimEnt link, SimEnt device, int interfacenr)
	{
		super(link, device);
		_interfacenr = interfacenr;
	}

	// Needs a getInterface (used in router)
	public int interfacenr(){ return this._interfacenr; }

	public SimEnt link()
	{
		return super.link();
	}

	public SimEnt device() { return super.device(); }
	
}

package Sim;

// Just a class that works like a table entry hosting
// a link connecting and the node at the other end

public class TableEntry {

	private SimEnt _link;
	private SimEnt _device;
	
	TableEntry(SimEnt link, SimEnt device)
	{
		_link=link;
		_device=device;
	}
	
	protected SimEnt link()
	{
		return _link;
	}

	protected SimEnt device()
	{
		return _device;
	}
	
}

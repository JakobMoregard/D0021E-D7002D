package Sim;

// This class implements a link without any loss, jitter or delay

public class Link extends SimEnt{
	protected SimEnt _connectorA=null;
	protected SimEnt _connectorB=null;
	protected int _now=0;
	protected int link_cost;
	
	public Link(int link_cost)
	{
		super();
		this.link_cost = link_cost;
	}
	
	// Connects the link to some simulation entity like
	// a node, switch, router etc.
	
	public void setConnector(SimEnt connectTo)
	{
		if (_connectorA == null) 
			_connectorA=connectTo;
		else
			_connectorB=connectTo;
	}

	// Called when a message enters the link
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			System.out.println("Link recv msg, passes it through");
			if (src == _connectorA)
			{
				send(_connectorB, ev, _now);
			}
			else
			{
				send(_connectorA, ev, _now);
			}
		}

        // Allow RIP packages
        if (ev instanceof RIP)
        {
            System.out.println("Link recv RIP broadcast, passes it through");
            if (src == _connectorA)
            {
                send(_connectorB, ev, _now);
            }
            else
            {
                send(_connectorA, ev, _now);
            }
        }
	}	
}
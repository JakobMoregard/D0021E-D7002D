package Sim;

public class LinkSimulateFailure extends Link{
    protected SimEnt _connectorA=null;
    protected SimEnt _connectorB=null;
    protected int _now=0;
    protected int link_cost;
    protected int failure;

    public LinkSimulateFailure(int link_cost, int linkFailure)
    {
        super(link_cost);
        this.link_cost = link_cost;
        failure = linkFailure;
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

        if (SimEngine.getTime() > failure){
            System.out.println("Link simulates failure at time: " + SimEngine.getTime()+ "...");

        } else {

            if (ev instanceof Message || ev instanceof ChangeInterface)
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
            if (ev instanceof RIP) {
                System.out.println("Link recv RIP broadcast, passes it through");
                if (src == _connectorA) {
                    send(_connectorB, ev, _now);
                } else {
                    send(_connectorA, ev, _now);
                }
            }
        }
    }
}
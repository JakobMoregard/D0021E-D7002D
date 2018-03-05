package Sim;

public class RIP implements Event{

    // The amount of jumps of the package
    protected int jumps = 0;
    // The cost of using the link
    private int connection_cost;
    // The router table
    private RouteTableEntry [] router_table;
    // The node table
    private RouteTableEntry [] node_table;
    // A string (name) of the original sender
    protected int origin;
    // The link used to transmit the package
    private Link link;

    RIP(int connection_cost, RouteTableEntry [] router_table, RouteTableEntry [] node_table, int origin) {
        super();
        this.connection_cost = connection_cost;
        this.router_table = router_table;
        this.node_table = node_table;
        this.link = link;
        this.origin = origin;
    }

    public RouteTableEntry [] get_router_table(){
        return router_table;
    }

    public RouteTableEntry [] get_node_table() {
        return node_table;
    }

    public void entering(SimEnt locale){}
}

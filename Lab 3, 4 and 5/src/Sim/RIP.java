package Sim;

public class RIP implements Event{

    // The amount of jumps of the package
    protected int jumps = 0;
    // The cost of using the link
    protected int connection_cost;
    // The node table
    private RouteTableEntry [] node_table;
    // A string (name) of the original sender
    protected int origin;
    // The link used to transmit the package

    protected int last_router_id;

    RIP(int connection_cost, RouteTableEntry [] node_table, int origin) {
        super();
        this.connection_cost = connection_cost;
        this.node_table = node_table;
        this.origin = origin;
    }

    public RouteTableEntry [] get_node_table() {
        return node_table;
    }

    public void entering(SimEnt locale){}
}

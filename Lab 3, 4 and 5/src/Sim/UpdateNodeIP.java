package Sim;

public class UpdateNodeIP implements Event {

    protected NetworkAddr _source;
    protected NetworkAddr _newAddr;
    protected NetworkAddr _destination;
    protected NetworkAddr _oldAddress;
    protected int _seq=0;

    public UpdateNodeIP(NetworkAddr from, NetworkAddr old, NetworkAddr newAddr, int seq)
    {
        _newAddr = newAddr;
        _oldAddress = old;
        _source = from;
        _seq=seq;
    }

    // Shadowing the constuctor
    public UpdateNodeIP(NetworkAddr from, NetworkAddr to , NetworkAddr old, NetworkAddr newAddr, int seq)
    {
        _destination = to;
        _newAddr = newAddr;
        _oldAddress = old;
        _source = from;
        _seq=seq;
    }

    public NetworkAddr source()
    {
        return _source;
    }

    public NetworkAddr destination()
    {
        return _destination;
    }

    public NetworkAddr getOld() { return _oldAddress; }

    public int seq()
    {
        return _seq;
    }

    public void entering(SimEnt locale)
    {
    }
}

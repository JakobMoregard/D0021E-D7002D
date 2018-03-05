package Sim;

public class ChangeInterface implements Event{
    private NetworkAddr _oldInterface;
    private int _newInterfaceNumber;

    ChangeInterface (NetworkAddr source, int newInterfaceNumber)
    {
        _oldInterface = source;
        _newInterfaceNumber = newInterfaceNumber;
    }

    public NetworkAddr oldInterface()
    {
        return _oldInterface;
    }

    public int newInterfaceNumber()
    {
        return _newInterfaceNumber;
    }

    public void entering(SimEnt locale)
    {
    }
}
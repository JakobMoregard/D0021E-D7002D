package Sim;

import java.util.Random;

// This class implements a link without any loss, jitter or delay

public class LossyLink extends Link{
	private SimEnt _connectorA=null;
	private SimEnt _connectorB=null;
	private double _delay = 0;
	private int _delayMin = 0;
	private int _delayMax = 0;
	private int _dropProb = 0;
    private double _jitter = 0;
    private double prevDelay = 0;

	//dropProg: 0-100
	public LossyLink(int  delayMin, int  delayMax, int  dropProb)
	{
		super();
		_delayMin = delayMin;
		_delayMax = delayMax;
		_dropProb = dropProb;
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
	    Random rand = new Random();
		if (ev instanceof Message)
		{
		    if( rand.nextInt(100) < _dropProb) {
                System.out.println("! Lossy link dropped packet nr " + ((Message) ev).seq());
            }else{
		        _delay = getDelay(_delayMin, _delayMax);

		        _jitter = calcJitter(_delay);

                System.out.println("LossyLink recv msg " + ((Message) ev).seq() + " passes it through ");
                System.out.println(".This delay: " + _delay + ". Avg jitter: " + _jitter);

                if (src == _connectorA) {
                    send(_connectorB, ev, _delay );
                } else {
                    send(_connectorA, ev, _delay );
                }
            }
		}
	}
	private double getDelay(int dMin, int dMax){
		if(dMin > dMax){
			throw new IllegalArgumentException("Min greater than max");
		}
	    Random rand = new Random();
	    return rand.nextInt((dMax - dMin) + 1) + dMin;
    }
    private double calcJitter(double delay){

        double diff = Math.abs(delay - prevDelay);
        prevDelay = delay;

        return _jitter + (1./16.) * (diff - _jitter);
    }
}
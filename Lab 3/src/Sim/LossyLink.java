package Sim;

import java.util.Random;

public class LossyLink extends Link {
	private int delay = 0;
	private double jitter = 0;
	private int drop = 0;
	
	Random randInt = new Random();
	
	public LossyLink(int delay, double jitter, int drop){
		super();
		this.delay = delay;
		this.jitter = jitter;
		this.drop = drop;
	}
	
	
	public void recv(SimEnt src, Event ev)
	{	
		if (ev instanceof Message)
		{
			
			// Generate a diviation and multiply the jitter.
			double delay2 = randInt.nextGaussian() * jitter;
			if (delay2 < 0) {
				// If the diviation is negative, recalculate
				delay2 = randInt.nextGaussian() * jitter;
			}
			
			// Set the delay and jitter
			double newTime = _now + delay + delay2;
			
			// Check if the package should be dropped
			int rand = 1 + randInt.nextInt(100);
			if (rand < drop){
				System.out.println("LossyLink dropped the package!");
				return;
			} else {
				
				// If the package is not dropped, send as usual
				System.out.println("LossyLink recv msg, passes it through");
				if (src == _connectorA)
				{
					send(_connectorB, ev, newTime);
				}
				else
				{
					send(_connectorA, ev, newTime);
				}
			}
		}
	}	
}

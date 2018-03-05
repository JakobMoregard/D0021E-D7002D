package Sim;
import java.io.PrintWriter;
import java.util.Random;
// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node2 extends SimEnt {
	private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	private PrintWriter writer;



	public Node2(int network, int node)
	{
		super();
		_id = new NetworkAddr(network, node);


		try {
            writer = new PrintWriter("numbers.csv", "UTF-8");
        }catch (Exception e){
		    System.out.println(e);
        }
	}


	// Sets the peer to communicate with. This node is single homed

	public void setPeer (SimEnt peer)
	{
		_peer = peer;

		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}


	public NetworkAddr getAddr()
	{
		return _id;
	}

//**********************************************************************************
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators

	private int _stopSendingAfter = 0; //messages
	private int _timeBetweenSending = 10; //time between messages
	private int _toNetwork = 0;
	private int _toHost = 0;
	private int _type = 0;  // 0 = CBR, 1 = Gaussian, 2 = Poisson
	private int _changeInterfaceAfter = -1;
	private int _newInterfaceNumber = 0;
	private int _oldInterfaceNumber = 0;

    // timeInterval is used for type 0, CBR
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq, int type)
	{
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		_type = type;
		send(this, new TimerEvent(),0);
	}

    //http://www.cs.princeton.edu/courses/archive/fall09/cos126/assignments/StdGaussian.java.html
    public static double getGaussian()
    {
        double r, x, y;
        // find a uniform random point (x, y) inside unit circle
        do {
            x = 2.0 * Math.random() - 1.0;
            y = 2.0 * Math.random() - 1.0;
            r = x*x + y*y;
        } while (r > 1 || r == 0);    // loop executed 4 / pi = 1.273.. times on average
                                        // // http://en.wikipedia.org/wiki/Box-Muller_transform

        // apply the Box-Muller formula to get standard Gaussian z
        double z = x * Math.sqrt(-2.0 * Math.log(r) / r);

        // a amplifies the values, b makes sure they stay positive
        // having b 4 to 5 times larger than a seems to make all values positive
        int a = 2;
        int b = 8;
        z = z * a + b;

        return z;
    }
    // https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
    // https://stackoverflow.com/questions/9832919/generate-poisson-arrival-in-java
    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }
	public void changeInterface(int interfaceNumber, int packetsSent)
	{
		_changeInterfaceAfter = packetsSent;
		_newInterfaceNumber = interfaceNumber;
	}

//**********************************************************************************

	// This method is called upon that an event destined for this node triggers.

	public void recv(SimEnt src, Event ev)

	{
		if (ev instanceof TimerEvent)
		{
			if (_stopSendingAfter > _sentmsg)
			{

				_sentmsg++;
				switch(_type) {
                    case 1:
                        _timeBetweenSending = (int) getGaussian();
                        break;
                    case 2:
                        _timeBetweenSending = getPoissonRandom(3);
                        break;
                    default:
                        System.out.println("Case 0 _ Default");
                }

              //  writer.println(_timeBetweenSending + ";" + _sentmsg);


				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				send(this, new TimerEvent(),_timeBetweenSending);
				System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
				_seq++;
				if(_sentmsg == _changeInterfaceAfter){
					System.out.println("Node "+_id.networkId()+"."+_id.nodeId()+" requests change interface to interface number "+_newInterfaceNumber+" at time "+SimEngine.getTime());
					//send(_peer, new ChangeInterface(_id, _newInterfaceNumber), 0);
				}
			}else{
			    System.out.println("END");
                writer.close();
            }
		}
		if (ev instanceof Message)
		{
			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());

		}
	}
}

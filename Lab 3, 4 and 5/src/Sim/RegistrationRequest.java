package Sim;

/// A registration request send to the foreign agent (Router)
public class RegistrationRequest implements Event {
	private Router _home_agent;
	
	RegistrationRequest(Router home_agent) {
		_home_agent = home_agent;
	}
	
	public Router homeAgent() {
		return _home_agent;
	}
	
	@Override
	public void entering(SimEnt locale) {}
}

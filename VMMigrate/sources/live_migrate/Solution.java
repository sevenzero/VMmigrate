package live_migrate;

import java.util.Map;


public class Solution {
	private double fitness;
	private Map<Vm, Host> vmTohost;
	
	public Solution( double fitness, Map<Vm, Host> vmTohost) {
		this.fitness = fitness;
		this.vmTohost = vmTohost;
		
	}
	public double getfitness() {
		return fitness;
	}
	public void setBanlanceDegree(double banlanceDegree) {
		this.fitness = banlanceDegree;
	}
	public Map<Vm, Host> getVmTohost() {
		return vmTohost;
	}
	public void setVmTohost(Map<Vm, Host> vmTohost) {
		this.vmTohost = vmTohost;
	}
}

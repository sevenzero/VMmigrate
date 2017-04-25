package live_migrate;

import java.util.Map;

public class Solution {
	private double fitness;
	private Map<Integer,Integer> vmTohost;
	
	public Solution( double fitness, Map<Integer,Integer> vmTohost) {
		this.fitness = fitness;
		this.vmTohost = vmTohost;
		
	}
	public double getfitness() {
		return fitness;
	}
	public void setBanlanceDegree(double banlanceDegree) {
		this.fitness = banlanceDegree;
	}
	public Map<Integer, Integer> getVmTohost() {
		return vmTohost;
	}
	public void setVmTohost(Map<Integer, Integer> vmTohost) {
		this.vmTohost = vmTohost;
	}
}

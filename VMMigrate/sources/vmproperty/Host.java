package vmproperty;

import java.util.ArrayList;
import java.util.List;

public class Host {
	/** The id. */
	private int id;

	/** The storage. */
	private long storage;

	/** The bw. */
	private long bw;

	/** The available bw. */
	private long availableBw;

	/** The ram. */
	private int ram;

	/** The available ram. */
	private int availableRam;

	/** The number of PEs . */
	private int pesNum;

	private int availablePes;

	private double load;

	private double uCPU;
	private double uNet;
	private double uMem;
//	private double thmin=0.7;
//	private double thmax=0.8;

	/** The vm list. */
	private List<? extends Vm> vmList = new ArrayList<Vm>();
	public List<Vm> vmlist = new ArrayList<Vm>();

	public Host(int id, long storage, int ram, long bw, int pesNum) {
		setId(id);
		setStorage(storage);
		setRam(ram);
		setBw(bw);
		setPesNum(pesNum);
		setAvailableBw(bw);
		setAvailableRam(ram);
		setAvailblePes(pesNum);
	}

	public void addVm(Vm vm) {
		vmlist.add(vm);
		this.vmList = vmlist;
	}

	/**
	 * 输出物理主机上搭载的vm编号
	 */
	public void display() {
		System.out.print(this.getId() + "号主机上的虚拟机有：");
		for(int i=0;i<this.vmList.size();i++) {
			System.out.print(vmList.get(i).getId() + " ");
		}
		System.out.println();
	}

	/**
	 * 判断负载程度，
	 * @return 1 表示CPU超负载
	 * @return 2 表示带宽超负载
	 * @return 3 表示内存超负载
	 * @return 0 表示处于最佳负载
	 */
//	public int loadLevel(){
//		uCPU=getUsedPes()/pesNum;
//		uNet=getUsedBw()/bw;
//		uMem=getUsedRam()/ram;
//		if(uCPU>thmin&&uCPU<thmax&&uNet>thmin&&uNet<thmax&&uMem>thmin&&uMem<thmax)
//			return 0;
//		else return 4;
//	}
	
	/**
	 * 获取资源利用率
	 * @return
	 */
	public double getLoad(){
		uCPU=(double)getUsedPes()/pesNum;
		uNet=(double)getUsedBw()/bw;
		uMem=(double)getUsedRam()/ram;
		//load=(1/(1-uCPU))*((1/(1-uNet)))*(1/(1-uMem));
		load=Math.sqrt(uCPU*uCPU+uNet*uNet+uMem*uMem);
		return load;
	}
	
	protected void setId(int id) {
		this.id = id;
	}

	protected void setStorage(long storage) {
		this.storage = storage;
	}

	public int getId() {
		return id;
	}

	public long getStorage() {
		return storage;
	}

	/**
	 * Returns a VM object.
	 * 
	 * @param vmId
	 *            the vm id
	 * @param userId
	 *            ID of VM's owner
	 * @return the virtual machine object, $null if not found
	 * @pre $none
	 * @post $none
	 */
	public Vm getVm(int vmId, int userId) {
		for (Vm vm : getVmList()) {
			if (vm.getId() == vmId && vm.getUserId() == userId) {
				return vm;
			}
		}
		return null;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Gets the ram.
	 * 
	 * @return the ram
	 */
	public int getRam() {
		return ram;
	}

	/**
	 * Sets the ram.
	 * 
	 * @param ram
	 *            the ram to set
	 */
	protected void setRam(int ram) {
		this.ram = ram;
	}

	/**
	 * Gets the amount of used RAM in the host.
	 * 
	 * @return used ram
	 * 
	 * @pre $none
	 * @post $none
	 */
	public int getUsedRam() {
		return ram - availableRam;
	}

	/**
	 * Gets the available RAM in the host.
	 * 
	 * @return available ram
	 * 
	 * @pre $none
	 * @post $none
	 */
	public int getAvailableRam() {
		return availableRam;
	}

	/**
	 * Sets the available ram.
	 * 
	 * @param availableRam
	 *            the availableRam to set
	 */
	public void setAvailableRam(int availableRam) {
		this.availableRam = availableRam;
	}

	/**
	 * Gets the bw.
	 * 
	 * @return the bw
	 */
	public long getBw() {
		return bw;
	}

	/**
	 * Sets the bw.
	 * 
	 * @param bw
	 *            the new bw
	 */
	protected void setBw(long bw) {
		this.bw = bw;
	}

	/**
	 * Gets the available BW in the host.
	 * 
	 * @return available bw
	 * 
	 * @pre $none
	 * @post $none
	 */
	public long getAvailableBw() {
		return availableBw;
	}

	/**
	 * Gets the amount of used BW in the host.
	 * 
	 * @return used bw
	 * 
	 * @pre $none
	 * @post $none
	 */
	public long getUsedBw() {
		return bw - availableBw;
	}

	/**
	 * Sets the available bw.
	 * 
	 * @param availableBw
	 *            the new available bw
	 */
	public void setAvailableBw(long availableBw) {
		this.availableBw = availableBw;
	}

	public int getPesNum() {
		return pesNum;
	}

	protected void setPesNum(int pesNum) {
		this.pesNum = pesNum;
	}

	public int getUsedPes(){
		return pesNum-availablePes;
	}
	public int getAvailblePes() {
		return availablePes;
	}

	public void setAvailblePes(int availablePes) {
		this.availablePes = availablePes;
	}

	// public void setUcpu(double U){
	// this.uCPU=load;
	// }
	//
	// public double getLoad(){
	// return load;
	// }
}

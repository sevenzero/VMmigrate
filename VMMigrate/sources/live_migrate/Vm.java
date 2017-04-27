package live_migrate;

import java.util.List;



public class Vm {
	/** The id. */
	private int id;

	/** The user id. */
	private int userId;

	/** The uid. */
	private String uid;

	/** The size. */
	private long size;
	
	/** The number of PEs. */
	private int numberOfPes;

	/** The ram. */
	private int ram;

	/** The bw. */
	private long bw;

	/** The vmm. */
	private String vmm;

	/** The host. */
	private Host host;

	/** In migration flag. */
	private boolean inMigration;

	/** The current allocated size. */
	private long currentAllocatedSize;

	/** The current allocated ram. */
	private int currentAllocatedRam;

	/** The current allocated bw. */
	private long currentAllocatedBw;

	/** The current allocated mips. */
	private List<Double> currentAllocatedMips;

	/** The VM is being instantiated. */
	private boolean beingInstantiated;
	
	public Vm(
			int id,
			int userId,
			int numberOfPes,
			int ram,
			long bw,
			long size,
			String vmm) {
		setId(id);
		setUserId(userId);
		setUid(getUid(userId, id));
		setNumberOfPes(numberOfPes);
		setRam(ram);
		setBw(bw);
		setSize(size);
		setVmm(vmm);

		setInMigration(false);
		setBeingInstantiated(true);

		setCurrentAllocatedBw(0);
		setCurrentAllocatedMips(null);
		setCurrentAllocatedRam(0);
		setCurrentAllocatedSize(0);
	}


	/**
	 * Sets the uid.
	 * 
	 * @param uid the new uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get unique string identificator of the VM.
	 * 
	 * @return string uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Generate unique string identificator of the VM.
	 * 
	 * @param userId the user id
	 * @param vmId the vm id
	 * @return string uid
	 */
	public static String getUid(int userId, int vmId) {
		return userId + "-" + vmId;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userId the new user id
	 */
	protected void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Gets the ID of the owner of the VM.
	 * 
	 * @return VM's owner ID
	 * @pre $none
	 * @post $none
	 */
	public int getUserId() {
		return userId;
	}


	/**
	 * Gets the number of pes.
	 * 
	 * @return the number of pes
	 */
	public int getNumberOfPes() {
		return numberOfPes;
	}

	/**
	 * Sets the number of pes.
	 * 
	 * @param numberOfPes the new number of pes
	 */
	protected void setNumberOfPes(int numberOfPes) {
		this.numberOfPes = numberOfPes;
	}

	/**
	 * Gets the amount of ram.
	 * 
	 * @return amount of ram
	 * @pre $none
	 * @post $none
	 */
	public int getRam() {
		return ram;
	}

	/**
	 * Sets the amount of ram.
	 * 
	 * @param ram new amount of ram
	 * @pre ram > 0
	 * @post $none
	 */
	public void setRam(int ram) {
		this.ram = ram;
	}

	/**
	 * Gets the amount of bandwidth.
	 * 
	 * @return amount of bandwidth
	 * @pre $none
	 * @post $none
	 */
	public long getBw() {
		return bw;
	}

	/**
	 * Sets the amount of bandwidth.
	 * 
	 * @param bw new amount of bandwidth
	 * @pre bw > 0
	 * @post $none
	 */
	public void setBw(long bw) {
		this.bw = bw;
	}

	/**
	 * Gets the amount of storage.
	 * 
	 * @return amount of storage
	 * @pre $none
	 * @post $none
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the amount of storage.
	 * 
	 * @param size new amount of storage
	 * @pre size > 0
	 * @post $none
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Gets the VMM.
	 * 
	 * @return VMM
	 * @pre $none
	 * @post $none
	 */
	public String getVmm() {
		return vmm;
	}

	/**
	 * Sets the VMM.
	 * 
	 * @param vmm the new VMM
	 */
	protected void setVmm(String vmm) {
		this.vmm = vmm;
	}

	/**
	 * Sets the host that runs this VM.
	 * 
	 * @param host Host running the VM
	 * @pre host != $null
	 * @post $none
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public Host getHost() {
		return host;
	}


	/**
	 * Checks if is in migration.
	 * 
	 * @return true, if is in migration
	 */
	public boolean isInMigration() {
		return inMigration;
	}

	/**
	 * Sets the in migration.
	 * 
	 * @param inMigration the new in migration
	 */
	public void setInMigration(boolean inMigration) {
		this.inMigration = inMigration;
	}

	/**
	 * Gets the current allocated size.
	 * 
	 * @return the current allocated size
	 */
	public long getCurrentAllocatedSize() {
		return currentAllocatedSize;
	}

	/**
	 * Sets the current allocated size.
	 * 
	 * @param currentAllocatedSize the new current allocated size
	 */
	protected void setCurrentAllocatedSize(long currentAllocatedSize) {
		this.currentAllocatedSize = currentAllocatedSize;
	}

	/**
	 * Gets the current allocated ram.
	 * 
	 * @return the current allocated ram
	 */
	public int getCurrentAllocatedRam() {
		return currentAllocatedRam;
	}

	/**
	 * Sets the current allocated ram.
	 * 
	 * @param currentAllocatedRam the new current allocated ram
	 */
	public void setCurrentAllocatedRam(int currentAllocatedRam) {
		this.currentAllocatedRam = currentAllocatedRam;
	}

	/**
	 * Gets the current allocated bw.
	 * 
	 * @return the current allocated bw
	 */
	public long getCurrentAllocatedBw() {
		return currentAllocatedBw;
	}

	/**
	 * Sets the current allocated bw.
	 * 
	 * @param currentAllocatedBw the new current allocated bw
	 */
	public void setCurrentAllocatedBw(long currentAllocatedBw) {
		this.currentAllocatedBw = currentAllocatedBw;
	}

	/**
	 * Gets the current allocated mips.
	 * 
	 * @return the current allocated mips
	 * @TODO replace returning the field by a call to getCloudletScheduler().getCurrentMipsShare()
	 */
	public List<Double> getCurrentAllocatedMips() {
		return currentAllocatedMips;
	}

	/**
	 * Sets the current allocated mips.
	 * 
	 * @param currentAllocatedMips the new current allocated mips
	 */
	public void setCurrentAllocatedMips(List<Double> currentAllocatedMips) {
		this.currentAllocatedMips = currentAllocatedMips;
	}

	/**
	 * Checks if is being instantiated.
	 * 
	 * @return true, if is being instantiated
	 */
	public boolean isBeingInstantiated() {
		return beingInstantiated;
	}

	/**
	 * Sets the being instantiated.
	 * 
	 * @param beingInstantiated the new being instantiated
	 */
	public void setBeingInstantiated(boolean beingInstantiated) {
		this.beingInstantiated = beingInstantiated;
	}

}

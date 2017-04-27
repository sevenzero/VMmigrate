package live_migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

public class VMPlacement {
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	private static int hostid;
	private static int vmid;
	private static double fitness;
	private static Map<Vm, Host> vmTohost; //虚拟机与主机的映射方案
	private static Map<Host, ArrayList<Vm>> vmInhost; //主机与搭载在其上的虚拟机映射

	public static void main(String[] args) {
		hostid = vmid = 0;
		hostlist = new ArrayList<Host>();
		vmlist = new ArrayList<Vm>();
		specHost1(50);
		specHost2(30);
		specVm1(20);
		specVm2(20);
		PSOSel();
//		RandomSel();
//		Particle.resetHost();
//		Greedy();
	}

	/**
	 * 8G+4M+4核
	 * 
	 * @param n
	 */
	private static void specHost1(int n) {
		long storage = 102400;
		int hostram = 8192;
		long hostbw = 4000;
		int pesNumOfhost = 4;
		for (int i = 0; i < n; i++) {
			Host host = new Host(hostid, storage, hostram, hostbw, pesNumOfhost);
			hostlist.add(host);
			hostid++;
		}
	}

	/**
	 * 4G+4M+4核
	 * 
	 * @param n
	 */
	private static void specHost2(int n) {
		long storage = 102400;
		int hostram = 4096;
		long hostbw = 4000;
		int pesNumOfhost = 4;
		for (int i = 0; i < n; i++) { // 初始化10个物理主机
			Host host = new Host(hostid, storage, hostram, hostbw, pesNumOfhost);
			hostlist.add(host);
			hostid++;
		}
	}

	/**
	 * 4G+1M+2核
	 * 
	 * @param n
	 */
	private static void specVm1(int n) {
		int userid = 1;
		int ram = 4096; // 内存
		long bw = 1000;// 带宽
		int pesNumber = 2;
		long size = 10000;
		String vmm = "Xen";
		for (int i = 0; i < n; i++) {
			Vm vm = new Vm(vmid, userid, pesNumber, ram, bw, size, vmm);
			vmlist.add(vm);
			vmid++;
		}
	}

	/**
	 * 2G+1M+1核
	 * 
	 * @param n
	 */
	private static void specVm2(int n) {
		int userid = 1;
		int ram = 2048; // 内存
		long bw = 1000;// 带宽
		int pesNumber = 1;
		long size = 10000;
		String vmm = "Xen";
		for (int i = 0; i < n; i++) {
			Vm vm = new Vm(vmid, userid, pesNumber, ram, bw, size, vmm);
			vmlist.add(vm);
			vmid++;
		}
	}

	/**
	 * 判断物理机能否满足条件放置虚拟机
	 * 
	 * @param vm
	 * @param host
	 * @return
	 */
	public static boolean selFitHost(Vm vm, Host host) {
		int usedCpu = 0;
		double usedMem = 0;
		double usedNet = 0;
		for (int i = 0; i < host.vmlist.size(); i++) {
			usedCpu += host.vmlist.get(i).getNumberOfPes();
			usedMem += host.vmlist.get(i).getRam();
			usedNet += host.vmlist.get(i).getBw();
		}
		if (vm.getBw() > host.getAvailableBw() - usedNet) {
			return false;
		}
		if (vm.getNumberOfPes() > host.getAvailblePes() - usedCpu) {
			return false;
		}
		if (vm.getRam() > host.getAvailableRam() - usedMem) {
			return false;
		}
		return true;
	}

	/**
	 * 更新主机资源
	 * 
	 * @param host
	 * @param vm
	 */
	public static void updateHost(Host host) {
		for (int i = 0; i < host.vmlist.size(); i++) {
			host.setAvailableBw(host.getAvailableBw()
					- host.vmlist.get(i).getBw());
			host.setAvailableRam(host.getAvailableRam()
					- host.vmlist.get(i).getRam());
			host.setAvailblePes(host.getAvailblePes()
					- host.vmlist.get(i).getNumberOfPes());
		}
	}

	public static  double calcuLoadDgree(List<Vm> vmList,List<Host> hostList){
		double[] x = new double[hostList.size()];
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (int i = 0; i < hostList.size(); i++) {
			x[i] = hostList.get(i).getLoad();
		}
		fitness = StandardDiviation(x);
		return fitness;
//		System.out.println("算法放置结果的负载均衡度为："+fitness);
//		int j=0;
//		System.out.println("虚拟机依次放置为：");
//		for(int i=0;i<vmList.size();i++){
//			Vm vm=vmList.get(i);
//			System.out.print(vm.getHost().getId()+" ");
//			j++;
//			if (j == 10) {
//				System.out.println();
//				j = 0;
//			}
//		}
//		System.out.println();
	}
	
	/**
	 * 求标准差
	 * 
	 * @param x
	 * @return
	 */
	public static double StandardDiviation(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {// 求和
			sum += x[i];
		}
		double dAve = sum / m;// 求平均值
		double dVar = 0;
		for (int i = 0; i < m; i++) {// 求方差
			dVar += (x[i] - dAve) * (x[i] - dAve);
		}
		return Math.sqrt(dVar / m);
	}
	/**
	 * 标准PSO算法
	 */
	private static void PSOSel() {
		// for (int i = 0; i < 15; i++) {
		PSO pso = new PSO(100, 250, vmlist, hostlist);
		pso.run();
		// 此时运行结束之后只是获得了一个放置方案，但是实际上虚拟机并没有部署到主机上，此时主机和虚拟机的一切状态仍是初始状态
		// 所以需要根据该方案部署虚拟机
		pso.showresult();
		vmTohost = pso.getSolution().getVmTohost();
		vmInhost = new HashMap<Host, ArrayList<Vm>>();
		Iterator<Entry<Vm, Host>> entries = vmTohost.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Vm, Host> entry = entries.next();
			Host host=entry.getValue();
			Vm vm=entry.getKey();
			if (!vmInhost.containsKey(host.getId())) {
				ArrayList<Vm> list = new ArrayList<Vm>();
				list.add(vm);
				vmInhost.put(host, list);
			} else {
				vmInhost.get(host).add(vm);
			}
			host.addVm(vm);
		}
		for(Host host:hostlist){
			System.out.print(host.getId()+" 号主机 ：");
			for(Vm vm:host.getVmList()){
				System.out.println(vm==null);
				System.out.print(vm.getId()+" ");
			}
			System.out.println();
		}
		
		// }
	}
	   
	private static void RandomSel(){
		RandomSel random=new RandomSel(vmlist,hostlist);
		random.vmToHost();
		random.showResult();
	}
	
	private static void Greedy(){
		Greedy greedysel=new Greedy(vmlist,hostlist);
		greedysel.GreedySel();
		greedysel.showResult();
	}
}

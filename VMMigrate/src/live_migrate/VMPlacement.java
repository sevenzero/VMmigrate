package live_migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.Set;

import vmproperty.Host;
import vmproperty.Vm;

public class VMPlacement {
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	private static HashMap<Vm, ArrayList<Host>> hashmap = new HashMap<Vm, ArrayList<Host>>();

	private static int hostid;
	private static int vmid;

	public static void main(String[] args) {
		hostid = vmid = 0;
		hostlist = new ArrayList<Host>();
		vmlist = new ArrayList<Vm>();
		specHost1(50);
		//specHost2(30);
		specVm1(20);
		specVm2(20);
		PSOSel();
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
	 * 选择一个主机放置vm
	 * 
	 * @param vm
	 */
	private static void vmToHost(Vm vm) {
		// 匹配可以放置该vm的物理机
		ArrayList<Host> fithostlist = new ArrayList<Host>();
		for (int i = 0; i < hostlist.size(); i++) {
			if (selFitHost(vm, hostlist.get(i))) {
				fithostlist.add(hostlist.get(i));// 将符合条件的物理主机放入数组中
			}
		}
		if (fithostlist.size() == 0)
			System.out.println(vm.getId() + "号虚拟机无合适物理机可以放置");
		else {
			hashmap.put(vm, fithostlist); // 将虚拟机与满足条件的主机进行映射
			// 输出vm和host的映射关系
			Set<Entry<Vm, ArrayList<Host>>> sets = hashmap.entrySet();
			for (Entry<Vm, ArrayList<Host>> entry : sets) {
				System.out.print(entry.getKey().getId() + "\t");
				for (Host i : entry.getValue()) {
					System.out.print(i.getId() + " ");
				}
				System.out.println();
			}
			randomSel(vm);

			System.out.println(vm.getId() + "\t" + vm.getHost().getId());

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
	 * 从候选物理机列表中随机选择一个
	 * 
	 * @param vm
	 * @return 被选中的主机
	 */
	private static void randomSel(Vm vm) {
		Host value = null;
		// 从满足条件的主机中随机获取一个物理机编号
		int index = (int) (Math.random() * hashmap.get(vm).size());
		value = hashmap.get(vm).get(index);
		value.addVm(vm);
		vm.setHost(value);
		updateHost(value);
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

	/**
	 * 标准PSO算法
	 */
	private static void PSOSel() {
		//for (int i = 0; i < 15; i++) {
			PSO pso = new PSO(vmlist, hostlist);
			pso.init(100);
			pso.run(100);
			pso.showresult();
			System.out.println();
		//}
	}
}

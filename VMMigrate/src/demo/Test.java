package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.Set;

import live_migrate.PSO;

import vmproperty.Host;
import vmproperty.Vm;

public class Test {
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	private static HashMap<Vm, ArrayList<Host>> hashmap = new HashMap<Vm, ArrayList<Host>>();

	public static void main(String[] args) {
		int hostid = 0;
		long storage = 102400;
		int hostram = 2048;
		long hostbw = 2000;
		int pesNumOfhost = 2;
		hostlist = new ArrayList<Host>();
		for (int i = 0; i < 10; i++) { // 初始化10个物理主机
			Host host = new Host(hostid, storage, hostram, hostbw, pesNumOfhost);
			hostlist.add(host);
			hostid++;
		}
		int vmid = 0;
		int userid = 1;
		int ram = 512; // 内存
		long bw = 1000;// 带宽
		int pesNumber = 1;
		long size = 10000;
		String vmm = "Xen";
		vmlist = new ArrayList<Vm>();
		for (int i = 0; i < 5; i++) {
			Vm vm = new Vm(vmid, userid, pesNumber, ram, bw, size, vmm);
			vmlist.add(vm);
			vmToHost(vm);
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
			Set<Entry<Vm, ArrayList<Host>>> sets = hashmap.entrySet();
			for (Entry<Vm, ArrayList<Host>> entry : sets) {
				System.out.print(entry.getKey().getId() + "\t");
				for (Host i : entry.getValue()) {
					System.out.print(i.getId() + " ");
				}
				System.out.println();
			}
			vm.setHost(randomSel(vm));
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
	private static boolean selFitHost(Vm vm, Host host) {
		if (vm.getBw() > host.getAvailableBw()) {
			return false;
		}
		if (vm.getNumberOfPes() > host.getAvailblePes()) {
			return false;
		}
		if (vm.getRam() > host.getAvailableRam()) {
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
	private static Host randomSel(Vm vm) {
		Host value = null;
		// 从满足条件的主机中随机获取一个物理机编号
		int index = (int) (Math.random() * hashmap.get(vm).size());
		value = hashmap.get(vm).get(index);
		value.setAvailableBw(value.getAvailableBw() - vm.getBw());
		value.setAvailableRam(value.getAvailableRam() - vm.getRam());
		value.setAvailblePes(value.getAvailblePes() - vm.getNumberOfPes());
		return value;
	}
	

}

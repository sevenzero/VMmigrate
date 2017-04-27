package live_migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RandomSel {
	private static HashMap<Vm, ArrayList<Host>> hashmap = new HashMap<Vm, ArrayList<Host>>();
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	private static Map<Vm, Host> vmTohost;
	private Solution solution;
	public RandomSel(List<Vm> vmList, List<Host> hostList) {
		RandomSel.vmlist = vmList;
		RandomSel.hostlist = hostList;
		vmTohost=new HashMap<Vm,Host>();
	}

	/**
	 * 遍历虚拟机列表，随机选择一个主机放置
	 * 
	 * @param vm
	 */
	public void vmToHost() {
		// 匹配可以放置该vm的物理机
		for (int i = 0; i < vmlist.size(); i++) {
			Vm vm = vmlist.get(i);
			ArrayList<Host> fithostlist = new ArrayList<Host>();
			for (int j = 0; j < hostlist.size(); j++) {
				if (VMPlacement.selFitHost(vm, hostlist.get(j))) {
					fithostlist.add(hostlist.get(j));// 将符合条件的物理主机放入数组中
				}
			}
			if (fithostlist.size() == 0)
				System.out.println(vm.getId() + "号虚拟机无合适物理机可以放置");
			else {
				hashmap.put(vm, fithostlist); // 将虚拟机与满足条件的主机进行映射
				// 输出vm和host的映射关系
//				Set<Entry<Vm, ArrayList<Host>>> sets = hashmap.entrySet();
//				for (Entry<Vm, ArrayList<Host>> entry : sets) {
//					System.out.print(entry.getKey().getId() + "\t");
//					for (Host j : entry.getValue()) {
//						System.out.print(j.getId() + " ");
//					}
//					System.out.println();
//				}
				randomSet(vm);
			}
		}
	}

	/**
	 * 从候选物理机列表中随机选择一个
	 * 
	 * @param vm
	 * @return 被选中的主机
	 */
	private static void randomSet(Vm vm) {
		Host value = null;
		// 从满足条件的主机中随机获取一个物理机编号
		int index = (int) (Math.random() * hashmap.get(vm).size());
		value = hashmap.get(vm).get(index);
		value.addVm(vm);
		vm.setHost(value);
		vmTohost.put(vm, value);
		VMPlacement.updateHost(value);
	}
	
	public  void showResult(){
		solution=new Solution(VMPlacement.calcuLoadDgree(vmlist, hostlist),vmTohost);
	}
}

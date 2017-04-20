package live_migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vmproperty.Host;
import vmproperty.Vm;

public class RandomSel {
	private static HashMap<Vm, ArrayList<Host>> hashmap = new HashMap<Vm, ArrayList<Host>>();
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	private static double fitness;
	public RandomSel(List<Vm> vmList, List<Host> hostList) {
		RandomSel.vmlist = vmList;
		RandomSel.hostlist = hostList;
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
		VMPlacement.updateHost(value);
	}
	
	public  void showResult(){
		double[] x = new double[hostlist.size()];
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (int i = 0; i < hostlist.size(); i++) {
			VMPlacement.updateHost(hostlist.get(i));// 根据主机中vmlist编号更新主机资源
			x[i] = hostlist.get(i).getLoad();
		}
		fitness = Particle.StandardDiviation(x);
		System.out.println("随机放置结果的负载均衡度为："+fitness);
		int j=0;
		System.out.println("虚拟机依次放置为：");
		for(int i=0;i<vmlist.size();i++){
			Vm vm=vmlist.get(i);
			System.out.print(vm.getHost().getId()+" ");
			j++;
			if (j == 10) {
				System.out.println();
				j = 0;
			}
		}
		System.out.println();
	}
}

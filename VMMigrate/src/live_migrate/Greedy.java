package live_migrate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import vmproperty.Host;
import vmproperty.Vm;

public class Greedy {
	private static List<Vm> vmlist;
	private static List<Host> hostlist;
	public Greedy(List<Vm> vmList, List<Host> hostList) {
		Greedy.vmlist = vmList;
		Greedy.hostlist = hostList;
	}

	public void GreedySel() {
		Comparator<Host> comparator = new Comparator<Host>() {
			public int compare(Host host1, Host host2) {
				//按照负载从小到大排序
				return host1.getLoad()<host2.getLoad()?-1:1;
			}
		};
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
				Collections.sort(fithostlist, comparator);
				//选择当前负载最小的主机放置该虚拟机
//				for(Host host:fithostlist)
//					System.out.print(host.getId()+" ");
				fithostlist.get(0).addVm(vm);
				vm.setHost(fithostlist.get(0));//将主机和虚拟机建立对应关系
				VMPlacement.updateHost(fithostlist.get(0));//更新主机资源
			}
			//System.out.println();
		}
	}
	
	public  void showResult(){
		VMPlacement.calcuLoadDgree(vmlist, hostlist);
	}
}

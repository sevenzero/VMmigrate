package live_migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vmproperty.Host;
import vmproperty.Vm;

/**
 * 粒子类
 * 
 * @author seven
 * 
 */
public class Particle {

	public int[] pos;// 粒子的位置，数组的维度表示虚拟机的个数
	public int[] v;
	private double fitness;
	private int[] pbest; // 粒子的历史最好的位置
	public static int[] gbest; // 所有粒子找到的最好位置
	private double pbest_fitness;// 粒子的历史最优适应值
	private int dims;
	private static double w;
	private static double c1;
	private static double c2;
	private static Random rnd;
	private Map<Integer, Integer> vmTohost;// 每个粒子每次迭代产生的放置方案

	int size;// 单个虚拟机可以放置的主机数量
	private List<Host> fitList;//
	private static List<Host> hostlist;
	private List<Vm> vmlist;
	public int count;// 最差适应度值次数
	private double utilAvg[];// 单个物理机平均利用率向量
	public static int runtimes;
	private int cnt;

	@SuppressWarnings("static-access")
	public Particle(List<Vm> vmList, List<Host> hostList) {
		this.vmlist = vmList;
		this.hostlist = hostList;
		this.dims = vmlist.size();
		cnt = 0;
		pos = new int[dims];
		v = new int[dims];
		pbest = new int[dims];
		gbest = new int[dims];
		fitness = 1;
		pbest_fitness = Double.MAX_VALUE;
		vmTohost = new HashMap<Integer, Integer>();
		utilAvg = new double[hostList.size()];
	}

	public void init() {
		fitList = hostlist;
		rnd = new Random();
		for (Vm vm : vmlist) {
			updateVmList(vm);
			int size = fitList.size();
			if (size != 0) {
				int idx = rnd.nextInt(size);
				Host host = fitList.get(idx);
				pos[vm.getId()] = idx;
				// 对于每个粒子，在计算位置和速度过程中，只把vm加入host的属性列表中，而不更新主机资源
				// 在对粒子进行适应度值计算时在更新资源
				fitList.get(pos[vm.getId()]).addVm(vm);
				vmTohost.put(vm.getId(), host.getId());
				pbest[vm.getId()] = pos[vm.getId()];
				v[vm.getId()] = rnd.nextInt(fitList.size()) - pos[vm.getId()];
			}
		}
		evaluate();
	}

	/**
	 * 返回low—uper之间的数
	 * 
	 * @param low
	 *            下限
	 * @param uper
	 *            上限
	 * @return 返回low—uper之间的数
	 */
	int rand(int low, int uper) {
		rnd = new Random();
		return rnd.nextInt() * (uper - low + 1) + low;
	}

	public void run() {
		// System.out.println("run");
		// resetHost();
		updatev();
		evaluate();
	}

	/**
	 * 判断负载均衡度，并记录历史最优解 这里定义负载均衡度为物理主机的资源利用率的标准差
	 */
	private void evaluate() {
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (Host host : hostlist) {
			VMPlacement.updateHost(host);// 根据主机中vmlist编号更新主机资源
			utilAvg[host.getId()] = host.getLoad();
		}
		fitness = VMPlacement.StandardDiviation(utilAvg);
		if (fitness < pbest_fitness) {
			for (Vm vm : vmlist) {
				pbest[vm.getId()] = pos[vm.getId()];
			}
			pbest_fitness = fitness;
		}
		resetHost();// 每个粒子评估结束之后还原主机资源，以确保下一个粒子能正确计算负载
	}

	/**
	 * 还原所有物理机至初始状态
	 */
	private void resetHost() {
		for (Host host : hostlist) {
			host.getVmList().clear();
			host.setAvailableBw(host.getBw());
			host.setAvailableRam(host.getRam());
			host.setAvailblePes(host.getPesNum());
		}
		
	}

	/**
	 * 更新速度和位置
	 */
	private void updatev() {
//		double k;
		int δ = 2;
		vmTohost.clear();
		// 线性减少w，正态函数动态调整c1，c2
		w = 0.9 - 0.5 / 100 * cnt;
		c1 = 0.5 + (4.5 - 0.5) / (Math.sqrt(2 * Math.PI) * δ)
				* Math.exp(-(cnt / runtimes) * (cnt / runtimes) / (2 * δ * δ));
		c2 = 2.5 + (0.5 - 2.5) / (Math.sqrt(2 * Math.PI) * δ)
				* Math.exp(-(cnt / runtimes) * (cnt / runtimes) / (2 * δ * δ));
//		k = (0.1 - 1) * (runtimes - cnt) / runtimes + 1;
		for (Vm vm : vmlist) {
			updateVmList(vm);
			size = fitList.size();
			v[vm.getId()] = (int) (w * v[vm.getId()] + c1 * rnd.nextDouble()
					* (pbest[vm.getId()] - pos[vm.getId()]) + c2
					* rnd.nextDouble() * (gbest[vm.getId()] - pos[vm.getId()]));
			// 限制速度和位置
			if (v[vm.getId()] > size - pos[vm.getId()] - 1) {
				v[vm.getId()] = size - pos[vm.getId()] - 1;
			}
			if (v[vm.getId()] < -pos[vm.getId()]) {
				v[vm.getId()] = -pos[vm.getId()];
			}
			pos[vm.getId()] = pos[vm.getId()] + v[vm.getId()];
			fitList.get(pos[vm.getId()]).vmlist.add(vm);// 第i个vm放入第pos[i]个host
			vmTohost.put(vm.getId(), fitList.get(pos[vm.getId()]).getId());
		}
		cnt++;
	}

	/**
	 * 更新每个虚拟机可以匹配的主机列表
	 */
	private void updateVmList(Vm vm) {
		fitList = new ArrayList<Host>();
		for (Host host : hostlist) {
			if (VMPlacement.selFitHost(vm, host)) {
				fitList.add(host);// 将符合条件的物理主机放入数组中
			}
		}
	}

	/**
	 * 对粒子进行进化，位置初始化为全部粒子的重心
	 */
	private void updateParticle(Particle a) {
		fitness = 1;
		pbest_fitness = 1;
		count = 0;
		fitList = hostlist;
		for (int i = 0; i < dims; i++) {
			int size = fitList.size();
			pos[i] = a.pos[i];
			// 对于每个粒子，在计算位置和速度过程中，只把vm加入host的属性列表中，而不更新主机资源
			// 在对粒子进行适应度值计算时在更新资源
			pbest[i] = a.pos[i];
			v[i] = rand(-pos[i], size - pos[i] - 1);
		}
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int[] getPos() {
		return pos;
	}

	public void setPos(int[] pos) {
		this.pos = pos;
	}

	public Map<Integer, Integer> getVmTohost() {
		return vmTohost;
	}

	public void setVmTohost(Map<Integer, Integer> vmTohost) {
		this.vmTohost = vmTohost;
	}
}

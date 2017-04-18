package live_migrate;

import java.util.ArrayList;
import java.util.List;
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
	public double fitness;
	public int[] pbest; // 粒子的历史最好的位置
	public static int[] gbest; // 所有粒子找到的最好位置
	public static int dims;
	public static double w;
	public static double c1;
	public static double c2;
	public static Random rnd;
	double pbest_fitness;// 粒子的历史最优适应值
	int size;// 单个虚拟机可以放置的主机数量
	private List<Host> fitList;//
	public static List<Host> hostlist;
	public static List<Vm> vmlist;
	public  int count;//

	public void init() {
		pos = new int[dims];
		v = new int[dims];
		pbest = new int[dims];
		fitness = 1;
		pbest_fitness = 1;
		count=0;
		fitList = hostlist;
		rnd = new Random();
		for (int i = 0; i < dims; i++) {
			int size = fitList.size();
			pos[i] = rnd.nextInt(size);
			// 对于每个粒子，在计算位置和速度过程中，只把vm加入host的属性列表中，而不更新主机资源
			// 在对粒子进行适应度值计算时在更新资源
			fitList.get(pos[i]).addVm(vmlist.get(i));
			pbest[i] = pos[i];
			v[i] = rand(-pos[i], size - pos[i] - 1);
			updateVmList(vmlist.get(i));
		}
	}

	/**
	 * 随机获取列表中的对象
	 * 
	 * @param list
	 * @return
	 */
	public Host getrnd(List<Host> list) {
		int index = (int) (Math.random() * list.size());
		return list.get(index);
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

	/**
	 * 判断负载均衡度，并记录历史最优解 这里定义负载均衡度为物理主机的资源利用率的标准差
	 */
	public void evaluate() {
		double[] x = new double[hostlist.size()];
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (int i = 0; i < hostlist.size(); i++) {
			VMPlacement.updateHost(hostlist.get(i));// 根据主机中vmlist编号更新主机资源
			x[i] = hostlist.get(i).getLoad();
		}
		fitness = StandardDiviation(x);
		if (fitness < pbest_fitness) {
			for (int i = 0; i < dims; i++) {
				pbest[i] = pos[i];
			}
			pbest_fitness = fitness;
		}
		resetHost();// 每个粒子评估结束之后还原主机资源，以确保下一个粒子能正确计算负载
	}

	/**
	 * 还原所有物理机至初始状态
	 */
	private void resetHost() {
		for (int i = 0; i < hostlist.size(); i++) {
			hostlist.get(i).vmlist = new ArrayList<Vm>();
			hostlist.get(i).setAvailableBw(hostlist.get(i).getBw());
			hostlist.get(i).setAvailableRam(hostlist.get(i).getRam());
			hostlist.get(i).setAvailblePes(hostlist.get(i).getPesNum());
		}
	}

	/**
	 * 求标准差
	 * 
	 * @param x
	 * @return
	 */
	private static double StandardDiviation(double[] x) {
		int m = hostlist.size();
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
	 * 更新速度和位置
	 */
	public void updatev(int cnt, int runtimes) {
		//double k;
		int δ = 2;
		//线性减少w，正态函数动态调整c1，c2
		w = 0.9 - 0.5 / 100 * cnt;
		c1 = 0.5
				+ (4.5 - 0.5)
				/ (Math.sqrt(2 * Math.PI) * δ)
				* Math.exp(-(cnt / runtimes) * (cnt / runtimes)
						/ (2 * δ * δ));
		c2 = 2.5
				+ (0.5 - 2.5)
				/ (Math.sqrt(2 * Math.PI) * δ)
				* Math.exp(-(cnt / runtimes) * (cnt / runtimes)
						/ (2 * δ * δ));
		//k = (0.1 - 1) * (runtimes - cnt) / runtimes + 1;
		for (int i = 0; i < dims; i++) {
			updateVmList(vmlist.get(i));
			size = fitList.size();
			v[i] = (int) (w * v[i] + c1 * rnd.nextDouble()
					* (pbest[i] - pos[i]) + c2 * rnd.nextDouble()
					* (gbest[i] - pos[i]));
			// 限制速度和位置
			if (v[i] > size - pos[i] - 1) {
				v[i] = size - pos[i] - 1;
			}
			if (v[i] < -pos[i]) {
				v[i] = -pos[i];
			}
			pos[i] = pos[i] + v[i];
			fitList.get(pos[i]).vmlist.add(vmlist.get(i));// 第i个vm放入第pos[i]个host
		}
		
	}

	/**
	 * 更新每个虚拟机可以匹配的主机列表
	 */
	private void updateVmList(Vm vm) {
		fitList = new ArrayList<Host>();
		for (int i = 0; i < hostlist.size(); i++) {
			if (VMPlacement.selFitHost(vm, hostlist.get(i))) {
				fitList.add(hostlist.get(i));// 将符合条件的物理主机放入数组中
			}
		}
	}
	
	/**
	 * 对粒子进行进化，位置初始化为全部粒子的重心
	 */
	public void updateParticle(Particle a) {
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
}

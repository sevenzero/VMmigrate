package demo;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import live_migrate.Particle;
import vmproperty.Host;
import vmproperty.Vm;

/**
 * 粒子群类
 * 
 * @author seven
 * 
 */
public class Test extends RecursiveAction {
	Particle[] pars;
	double global_best;// 全局最优适应度值
	double global_worst;
	int pcount_all;
	int pcount;// 粒子数目
	private static int dim;// 维度
	private static int Imax;// 最差纪录次数阈值

	private static final int THRESHOLD = 20;
	private int start;
	private int end;

	// private static int ms;//主机的数量，这里是为了限制迭代过程中的位置

	public Test(List<Vm> vmList, List<Host> hostList,int start, int end) {
		Particle.vmlist = vmList;
		dim = vmList.size();
		Particle.hostlist = hostList;
		this.start = start;
		this.end = end;
		this.pcount=end-start;
	}

//	public Test(int start, int end) {
//		this.start = start;
//		this.end = end;
//	}

	@Override
	protected void compute() {
		boolean canCompute = start - end <= THRESHOLD;
		if (canCompute) {
			for (int i = start; i < end; i++) {
				pars[i] = new Particle();
				pars[i].init();
				pars[i].evaluate();
				// if (global_best > pars[i].fitness) {
				// global_best = pars[i].fitness;
				// //index = i;
				// }
			}
		} else {
			int mid = (start + end) / 2;
			Test leftTask = new Test(Particle.vmlist,Particle.hostlist,start, mid);
			Test rightTask = new Test(Particle.vmlist,Particle.hostlist,mid + 1, end);

			// 执行子任务
			leftTask.fork();
			rightTask.fork();

			// 等待子任务执行完，并得到结果
			leftTask.join();
			rightTask.join();

		}
	}

	/**
	 * 粒子群初始化
	 * 
	 * @param n
	 *            粒子的数量
	 */
	public void init(int n) {
		pcount = n;
		global_best = 1;
		int index = -1;// 拥有最好位置的粒子编号
		Imax = 3;
		pars = new Particle[pcount + 1];// 初始化多一个粒子，不参与位置速度的更新，只用作暂存中间数据
		// 类的静态成员的初始化
		Particle.c1 = 2;
		Particle.c2 = 2;
		Particle.w = 0.9;
		Particle.dims = dim;
		// Particle.m=ms;
		ForkJoinPool forkjoinpool = new ForkJoinPool();
		Test task = new Test(Particle.vmlist,Particle.hostlist,0, pcount);
		forkjoinpool.invoke(task);
		// for (int i = 0; i < pcount; i++) {
		// pars[i] = new Particle();
		// pars[i].init();
		// pars[i].evaluate();
		// if (global_best > pars[i].fitness) {
		// global_best = pars[i].fitness;
		// index = i;
		// }
		//
		// }
		pars[pcount] = new Particle();
		pars[pcount].init();
		System.out.println(global_best);
		Particle.gbest = new int[Particle.dims];
		for (int i = 0; i < dim; i++) {
			Particle.gbest[i] = pars[index].pos[i];
			System.out.print(Particle.gbest[i] + " ");
		}
		System.out.println("\n========init finished!========");
	}

	/**
	 * 粒子群的运行
	 */
	public void run(int runtimes) {
		System.out.println("=========run start========");
		int cnt = 1;
		int index;
		int idx;
		while (cnt <= runtimes) {
			index = -1;
			idx = -1;
			global_worst = 0;
			// Particle.w=0.9-0.5/runtimes*cnt;
			// 每个粒子更新位置和适应值
			for (int i = 0; i < pcount; i++) {
				pars[i].updatev(cnt, runtimes);
				pars[i].evaluate();
				if (global_best > pars[i].fitness) {
					global_best = pars[i].fitness;
					index = i;
				}
				if (global_worst < pars[i].fitness) {
					global_worst = pars[i].fitness;
					idx = i;
				}// 寻找每次迭代中适应度最差的粒子
			}
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < pcount; j++) {
					pars[pcount].pos[i] += pars[j].pos[i];
				}
				pars[pcount].pos[i] = pars[pcount].pos[i] / pcount;
			}// 计算粒子群位置的平均值存在在附加的粒子中
			if (idx != -1)
				pars[idx].count++;
			for (int i = 0; i < pcount; i++) {
				if (pars[i].count == Imax) {// 如果粒子最差纪录次数达到预设的次数，则对粒子进行进化
					// pars[i].updateParticle(pars[pcount]);
					for (int j = 0; j < dim; j++) {
						pars[i].pos[j] = pars[pcount].pos[i];
					}

				}
				pars[i].count = 0;
			}
			System.out.print(global_best + "    ");
			// 发现更好的解
			if (index != -1) {
				for (int i = 0; i < dim; i++) {
					Particle.gbest[i] = pars[index].pos[i];
					System.out.print(Particle.gbest[i] + " ");
				}
			}
			System.out.println();
			cnt++;
		}
	}

	/**
	 * 显示程序求解结果
	 */
	public void showresult() {
		System.out.println("算法求得的最优解为：" + global_best);
		System.out.println("虚拟机放置的主机编号依次是");
		int j = 0;
		for (int i = 0; i < Particle.dims; i++) {
			System.out.print(Particle.gbest[i] + " ");
			j++;
			if (j == 10) {
				System.out.println();
				j = 0;
			}
		}
	}

}

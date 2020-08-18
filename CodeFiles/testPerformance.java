//Authors: team BG03
//last update: 2/12/2019
// Description : java file that contains the mainn method where the user runs the whole project
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class testPerformance {

	// Declaring the queues to store PCB
	static ArrayQueue<PCB> readyQueue;
	static ArrayQueue<PCB> waitQueue;
	// static ArrayQueue<PCB> CPU = new ArrayQueue<PCB>(1);
	static PCB CPU;
	static PCB IO;

	static PriorityQueue<PCB> RRQ; // First Level of MLFQ
	static PriorityQueue<PCB> SJF; // Second Level of MLFQ
	static PriorityQueue<PCB> FIFO; // Last Level of MLFQ
	static PriorityQueue<PriorityQueue<PCB>> MLFQ;

	static PoisonDistGenerator newJobs; // to generate random type and processes

	// Assumptions for how of some operation take of time unit
	static final int contextSwitchingTime = 1; // for each 10 time units
	static final int scvStartIO_Interupt = 2;
	static final int IOcompletionInterupt = 3;
	static final int jobSchedulingOverhead = 1;
	static final int supervisorActivities = 1;

	static int currentTime;
	static int waitTime;
	static int numProcesses;

	private static int numT1c;
	private static int numT1t;
	private static int numT2c;
	private static int numT2t;
	private static int numT3c;
	private static int numT3t;
	private static int numT4c;

	// FCFS, SJF, RR and MLFQ
	static int timeSteps = 10000;
	static int queueType = 1;
	static int N = 5;// max number of type-4
	static double v = 0.5; // Expected Number of new arrived Jobs
	static int Q = 1; // Quantum Size
	// Lottery Inputs
	static int t = 5; // the minimum number of tickets
	static int T = 100; // the maximum number of tickets
	static int per = 0; // the period of ticket update

	private static int maxRQueue;
	private static int avrRQueue;
	private static int maxWQueue;
	private static int avrWQueue;

	private static int ResponseT4;
	private static int minResponseT4;
	private static int maxResponseT4;
	private static int avrResponseT4;

	private static int minTurnaroundT1;
	private static int avrTurnaroundT1;
	private static int maxTurnaroundT1;
	private static int maxTurnaroundT2;
	private static int minTurnaroundT2;
	private static int avrTurnaroundT2;
	private static int minTurnaroundT3;
	private static int avrTurnaroundT3;
	private static int maxTurnaroundT3;

	private static int minOverhead;
	private static int maxOverhead;
	private static int avrOverhead;

	private static int cpuIdleTime;
	private static int cpuBusy;

	private static int throughputT1;
	private static int throughputT2;
	private static int throughputT3;

	private static int TurnaroundT1;
	private static int TurnaroundT2;
	private static int TurnaroundT3;

	private static int TurnT1C;
	private static int TurnT2C;
	private static int TurnT3C;

	static int T4 = 0;
	static int T4complete = 0;
	static int cpuN = 0;
	static int wCount = 0;

	static ArrayQueue<PCB> thinkQueue = new ArrayQueue<PCB>();

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PCB p = null;

		try {
			start(args);
			System.out.println();
			System.out.println();

			int Q1 = Q;

			for (currentTime = 1; currentTime <= timeSteps; currentTime++) {
				generateJobs(currentTime);

				if (queueType == 1)
					FCFS(p);

				if (queueType == 2)
					SJF(p);

				if (queueType == 3)
					RR(p, Q1);

				if (queueType == 4)
					MLFQ(p, Q1);

				if (queueType == 5)
					Lottery(p);

				TimeUnit.MILLISECONDS.sleep(0);

				minTurnaroundT1 = Math.min(minTurnaroundT1, TurnaroundT1);
				minTurnaroundT2 = Math.min(minTurnaroundT2, TurnaroundT2);
				minTurnaroundT3 = Math.min(minTurnaroundT3, TurnaroundT3);

				maxTurnaroundT1 = Math.max(maxTurnaroundT1, TurnaroundT1);
				maxTurnaroundT2 = Math.max(maxTurnaroundT2, TurnaroundT2);
				maxTurnaroundT3 = Math.max(maxTurnaroundT3, TurnaroundT3);

				minResponseT4 = Math.min(minResponseT4, ResponseT4);
				maxResponseT4 = Math.max(maxResponseT4, ResponseT4);

			}

			if (wCount != 0)
				avrWQueue = avrWQueue / wCount;

			if (T4complete != 0)
				avrResponseT4 = avrResponseT4 / T4complete;

			statistics();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Maybe you are trying to enter Wrong arguments. See the Help:");
			help();
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			System.err.println("Your memorry is not enough for this time steps. Reduse it");
		}

	}

	public static void printT(PCB p) {
		System.out.printf("PID: %d. Type %d. from class %s\n", p.getJob_ID(), p.getProcess().getType(), p);
	}

	public static void start(String[] args) {
		// System.out.println(args.length);
		if (args.length == 4) {

			if (Integer.parseInt(args[1]) == 1) {
				System.out.println("CPU scheduling type is: FCFS");
				queueType = Integer.parseInt(args[1]);
			} else if (Integer.parseInt(args[1]) == 2) {
				System.out.println("CPU scheduling type is: SJF");
				queueType = Integer.parseInt(args[1]);
			} else {
				System.out.println("Only FCFS or SJF for this satuation, We will use the default: FCFS");
			}

			if (Integer.parseInt(args[0]) > 1000) {
				System.out.println("Time steps is: " + args[0]);
				timeSteps = Integer.parseInt(args[0]);
			} else {
				System.out.println("Time steps should be greater than 1000, We will use the default: 10000");
				timeSteps = 10000;
			}

			if (Integer.parseInt(args[2]) >= 0 && Integer.parseInt(args[2]) <= 100) {
				System.out.println("Max number of Type-4 jobs is: " + args[2]);
				N = Integer.parseInt(args[2]);
			} else {
				System.out.println("Max Number of type-4 should be from 0 to 100, We will use the default: 5");
			}

			if (Double.parseDouble(args[3]) >= 0 && Double.parseDouble(args[3]) <= 1) {
				System.out.println("Expected Number of new arrived Jobs is: " + args[3]);
				v = Double.parseDouble(args[3]);
			} else {
				System.out.println("Expected Number of new Jobs should be from 0 to 1, We will use the default: 0.5");
			}

		} else if (args.length == 5) {

			if (Integer.parseInt(args[1]) == 3) {
				System.out.println("CPU scheduling type is: RR");
				queueType = Integer.parseInt(args[1]);
			} else if (Integer.parseInt(args[1]) == 4) {
				System.out.println("CPU scheduling type is: MLFQ");
				queueType = Integer.parseInt(args[1]);
			} else {
				System.err.println("Only RR or MLFQ for this satuation, Run program again. See the following help:");
				help();
				System.exit(-1);
			}

			if (Integer.parseInt(args[0]) > 1000) {
				System.out.println("Time steps is: " + args[0]);
				timeSteps = Integer.parseInt(args[0]);
			} else {
				System.out.println("Time steps should be greater than 1000, We will use the default: 10000");
				timeSteps = 10000;
			}

			if (Integer.parseInt(args[2]) >= 0 && Integer.parseInt(args[2]) <= 100) {
				System.out.println("Quantume size is: " + args[2]);
				Q = Integer.parseInt(args[2]);
			} else {
				System.out.println("Quantume size should be greater than 0, We will use the default: 1");
			}

			if (Integer.parseInt(args[3]) >= 0 && Integer.parseInt(args[3]) <= 100) {
				System.out.println("Max number of Type-4 jobs is: " + args[3]);
				N = Integer.parseInt(args[3]);
			} else {
				System.out.println("Max Number of type-4 should be from 0 to 100, We will use the default: 5");
			}

			if (Double.parseDouble(args[4]) >= 0 && Double.parseDouble(args[4]) <= 1) {
				System.out.println("Expected Number of new arrived Jobs is: " + args[4]);
				v = Double.parseDouble(args[4]);
			} else {
				System.out.println("Expected Number of new Jobs should be from 0 to 1, We will use the default: 0.5");
			}

		} else if (args.length == 8) {

			if (Integer.parseInt(args[1]) == 5) {
				System.out.println("CPU scheduling type is: Lottery");
				queueType = Integer.parseInt(args[1]);
			} else {
				System.err.println("Only Lottery for this satuation, Run program again. See the following help:");
				help();
				System.exit(-1);
			}

			if (Integer.parseInt(args[0]) > 1000) {
				System.out.println("Time steps is: " + args[0]);
				timeSteps = Integer.parseInt(args[0]);
			} else {
				System.out.println("Time steps should be greater than 1000, We will use the default: 10000");
				timeSteps = 10000;
			}

			if (Integer.parseInt(args[2]) >= 0 && Integer.parseInt(args[2]) <= 100) {
				System.out.println("Quantume size is: " + args[2]);
				Q = Integer.parseInt(args[2]);
			} else {
				System.out.println("Quantume size should be greater than 0, We will use the default: 1");
			}

			if (Integer.parseInt(args[3]) >= 0 && Integer.parseInt(args[3]) <= 100) {
				System.out.println("Max number of Type-4 jobs is: " + args[3]);
				N = Integer.parseInt(args[3]);
			} else {
				System.out.println("Max Number of type-4 should be from 0 to 100, We will use the default: 5");
			}

			if (Double.parseDouble(args[4]) >= 0 && Double.parseDouble(args[4]) <= 1) {
				System.out.println("Expected Number of new arrived Jobs is: " + args[4]);
				v = Double.parseDouble(args[4]);
			} else {
				System.out.println("Expected Number of new Jobs should be from 0 to 1, We will use the default: 0.5");
			}

			if (Integer.parseInt(args[5]) > 0) {
				System.out.println("The minimum number of tickets is: " + args[5]);
				t = Integer.parseInt(args[5]);
			} else {
				System.out.println("The min number of tickets should be greater than 0, We will use the default: 5");
			}

			if (Integer.parseInt(args[6]) > t) {
				System.out.println("The maximum number of tickets is: " + args[6]);
				T = Integer.parseInt(args[6]);
			} else {
				System.out.println(
						"The max. number of tickets should be greater than the min. [t], We will sue the default: 100");
			}

			if (Integer.parseInt(args[7]) > 0) {
				System.out.println("The period of ticket is: " + args[7]);
				per = Integer.parseInt(args[7]);
			} else {
				System.out.println("The period of ticket should be greater than 0, We will use the default: 0");
			}

		} else {
			System.err.println("We will use the default values.");
			System.err.println("You Should Enter approprate arguments. See the following:");
			help();
		}

		newJobs = new PoisonDistGenerator(v);

		readyQueue = new ArrayQueue<PCB>();
		waitQueue = new ArrayQueue<PCB>();

		FIFO = new PriorityQueue<PCB>(new Comparator<PCB>() {

			@Override
			public int compare(PCB o1, PCB o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		SJF = new PriorityQueue<PCB>(new Comparator<PCB>() {

			@Override
			public int compare(PCB o1, PCB o2) {
				// TODO Auto-generated method stub
				if (1.0 / o1.getProcess().getNextCPUtime() < 1.0 / o2.getProcess().getNextCPUtime())
					return 1;
				else if (1.0 / o1.getProcess().getNextCPUtime() > 1.0 / o2.getProcess().getNextCPUtime())
					return -1;
				else
					return 0;
			}
		});

		RRQ = new PriorityQueue<PCB>(new Comparator<PCB>() {

			@Override
			public int compare(PCB o1, PCB o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		MLFQ = new PriorityQueue<PriorityQueue<PCB>>(3, new Comparator<PriorityQueue<PCB>>() {

			@Override
			public int compare(PriorityQueue<PCB> o1, PriorityQueue<PCB> o2) {
				// TODO Auto-generated method stub
				if (o1.hashCode() < o2.hashCode())
					return 1;
				else if (o1.hashCode() > o2.hashCode())
					return -1;
				else
					return 0;

			}
		});

		MLFQ.offer(FIFO);
		MLFQ.offer(SJF);
		MLFQ.offer(RRQ);

		// System.out.println(RRQ.hashCode());

		// System.out.println(MLFQ.peek().hashCode());

	}

	public static int generateJobs(int arriveTime) {

		int j = newJobs.getNumber();
		creator(j, arriveTime);
		return j;

	}

	public static void creator(int j, int arriveTime) {
		// TODO Auto-generated method stub
		PCB p;
		Random rand = new Random();
		// System.out.println(j);
		int Rt;// to store random type
		int k = 1;
		for (int i = 1; i < j; i++) {

			Rt = rand.nextInt(4) + 1;
			// System.out.println(Rt);
			switch (Rt) {
			case 1:
				p = new PCB(arriveTime);
				p.setPState(PCB.READY);
				p.setIOstate(PCB.STATE_INITIAL);
				p.getProcess().setType(Rt);
				readyQueue.enqueue(p);
				numT1c++;
				break;
			case 2:
				p = new PCB(arriveTime);
				p.setPState(PCB.READY);
				p.setIOstate(PCB.STATE_INITIAL);
				p.getProcess().setType(Rt);
				readyQueue.enqueue(p);
				numT2c++;
				break;
			case 3:
				p = new PCB(arriveTime);
				p.setPState(PCB.READY);
				p.setIOstate(PCB.STATE_INITIAL);
				p.getProcess().setType(Rt);
				readyQueue.enqueue(p);
				numT3c++;
				break;
			case 4:
				if (numT4c < N) {
					p = new PCB(arriveTime);
					p.setPState(PCB.READY);
					p.setIOstate(PCB.STATE_INITIAL);
					p.getProcess().setType(Rt);
					readyQueue.enqueue(p);
					numT4c++;
				}
				break;
			}
			// if (!readyQueue.isFull())

			// printT(p);
			maxRQueue = Math.max(maxRQueue, readyQueue.size());
			avrRQueue = avrRQueue + readyQueue.size();
			k++;
		}
		avrRQueue = avrRQueue / k;
	}

	public static void terminator() {
		// TODO Auto-generated method stub
		// System.out.printf("%s In Terminator. at %d \n", CPU, currentTime);

		switch (CPU.getProcess().getType()) {
		case 1:
			numT1t++;

			TurnaroundT1 = CPU.turnaroundTime();
			if (TurnT1C == 0) {
				minTurnaroundT1 = TurnaroundT1;
				TurnT1C = 1;
			}
			avrTurnaroundT1 = avrTurnaroundT1 + TurnaroundT1;
			CPU = null;
			break;
		case 2:
			numT2t++;
			TurnaroundT2 = CPU.turnaroundTime();
			if (TurnT2C == 0) {
				minTurnaroundT2 = TurnaroundT2;
				TurnT2C = 1;
			}
			avrTurnaroundT2 = avrTurnaroundT2 + TurnaroundT2;
			CPU = null;
			break;
		case 3:
			numT3t++;
			TurnaroundT3 = CPU.turnaroundTime();
			if (TurnT3C == 0) {
				minTurnaroundT3 = TurnaroundT3;
				TurnT3C = 1;
			}
			avrTurnaroundT3 = avrTurnaroundT3 + TurnaroundT3;
			CPU = null;
			break;
		case 4:
			// numT4t++;
			break;
		}

	}

	public static boolean monitorIO() {
		// TODO Auto-generated method stub
		boolean k = false;

		k = IO.getEnd_time() == currentTime;
		if (IO != null) {
			switch (IO.getProcess().getType()) {
			case 1:
				readyQueue.enqueue(IO);
				break;
			case 2:
				readyQueue.enqueue(IO);
				break;
			case 3:
				readyQueue.enqueue(IO);
				break;
			case 4:
				if (IO.getIOstate() == PCB.STATE_USING_IO2)
					readyQueue.enqueue(IO);
				else if (IO.getIOstate() == PCB.STATE_USING_IO) {
					IO.setIOstate(PCB.STATE_USING_THINK);
					IO.setThinkArrivalTime(currentTime);
					thinkQueue.enqueue(IO);
				}
				break;
			}

			IO = null;

		}

		return k;

	}

	public static void dispatcher() {
		// TODO Auto-generated method stub
		// System.out.println("In dispatcher.");
		CPU.setPState(PCB.WAITING);
		CPU.setIOstate(PCB.STATE_USING_IO);
		waitQueue.enqueue(CPU);

		CPU = null;

	}

	public static PCB FCFS(PCB p) {
		// TODO Auto-generated method stub

		if (CPU != null)
			if (CPU.isEndCPU() && CPU.getEnd_time() == currentTime)
				if (CPU.getProcess().getType() != 4)
					terminator();

		if (!readyQueue.isEmpty()) {

			if (CPU == null) {
				p = readyQueue.serve();
				p.setPState(PCB.RUNNING);
				p.setIOstate(PCB.STATE_USING_CPU);

				switch (p.getProcess().getType()) {
				case 1:
					if (p.getProcess().iCPU1 == 0)
						p.setStart_time(currentTime);
					break;
				case 2:
					if (p.getProcess().iCPU2 == 0)
						p.setStart_time(currentTime);
					break;
				case 3:
					if (p.getProcess().iCPU3 == 0)
						p.setStart_time(currentTime);
					break;
				case 4:
					if (p.getProcess().iCPU4 == 0)
						p.setStart_time(currentTime);
					break;
				}

				p.setEnd_time(currentTime + p.getProcess().getCPUBurst());

				if (p.getProcess().getType() == 4) {
					p.getProcess().iIO4_1 = 0;
					p.getProcess().iIO4_2 = 0;
				}

				CPU = p;

				// System.out.printf("%d : %s : %d : %d : %d : %d : In CPU\n", currentTime, p,
				// p.getJob_ID(),
				// p.getProcess().getType(), p.getEnd_time(), ++cpuN);

			} else {
				if (IO == null) {
					p = readyQueue.serve();
					waitQueue.enqueue(p);

					maxWQueue = Math.max(maxWQueue, waitQueue.size());
					avrWQueue = avrWQueue + waitQueue.size();
					wCount++;
				}
			}

		}

		if (CPU == null) {
			cpuIdleTime++;
		} else {
			cpuBusy++;
		}

		if (!waitQueue.isEmpty()) {
			if (IO == null) {
				IO = waitQueue.serve();
				IO.setPState(PCB.WAITING);

				switch (IO.getProcess().getType()) {
				case 1:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 2:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 3:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 4:
					if (IO.getIOstate() == PCB.STATE_USING_IO)
						IO.setIOstate(PCB.STATE_USING_IO);
					else if (IO.getIOstate() == PCB.STATE_USING_THINK)
						IO.setIOstate(PCB.STATE_USING_IO2);
					break;
				}

				IO.setEnd_time(currentTime + IO.getProcess().getIOBurst());
				// System.out.printf("%d : %s : %d : %d : %d : In IO\n", currentTime, IO,
				// IO.getJob_ID(),
				// IO.getProcess().getType(), IO.getEnd_time());
			}
		}

		if (CPU != null) {

			// System.out.printf("Process: %s : End Time %d : Current Time %d\n", CPU,
			// CPU.getEnd_time(), currentTime);
			if (CPU.getEnd_time() == currentTime) {

				if (!CPU.isEndCPU()) {

					dispatcher();

				} else {
					if (CPU.getProcess().getType() == 4) {
						if (T4 == 0) {
							minResponseT4 = currentTime;
							T4++;
						}
						ResponseT4 = currentTime - CPU.getStart_time();
						avrResponseT4 = avrResponseT4 + ResponseT4;
						T4complete++;
					}
					dispatcher();
				}

			}

		}

		if (IO != null) {

			if (IO.getEnd_time() == currentTime) {
				monitorIO();

			}

		}

		if (!thinkQueue.isEmpty()) {
			if (thinkQueue.peek().isEndThink(currentTime)) {
				waitQueue.enqueue(thinkQueue.poll());

				maxWQueue = Math.max(maxWQueue, waitQueue.size());
				avrWQueue = avrWQueue + waitQueue.size();
				wCount++;
			}
		}
		return p;
	}

	public static PCB SJF(PCB p) {
		// TODO Auto-generated method stub
		if (CPU != null)
			if (CPU.isEndCPU() && CPU.getEnd_time() == currentTime)
				if (CPU.getProcess().getType() != 4)
					terminator();

		if (!readyQueue.isEmpty()) {
			for (int i = 0; i < readyQueue.size(); i++) {
				SJF.offer(readyQueue.serve());
			}

		}

		if (!SJF.isEmpty()) {

			if (CPU == null) {

				p = SJF.poll();
				p.setPState(PCB.RUNNING);
				p.setIOstate(PCB.STATE_USING_CPU);

				switch (p.getProcess().getType()) {
				case 1:
					if (p.getProcess().iCPU1 == 0)
						p.setStart_time(currentTime);
					break;
				case 2:
					if (p.getProcess().iCPU2 == 0)
						p.setStart_time(currentTime);
					break;
				case 3:
					if (p.getProcess().iCPU3 == 0)
						p.setStart_time(currentTime);
					break;
				case 4:
					if (p.getProcess().iCPU4 == 0)
						p.setStart_time(currentTime);
					break;
				}
				p.getProcess().getCPUBurst();
				p.setEnd_time(currentTime + p.getProcess().remainingCPUburst());

				if (p.getProcess().getType() == 4) {
					p.getProcess().iIO4_1 = 0;
					p.getProcess().iIO4_2 = 0;
				}

				CPU = p;

				// System.out.printf("%d : %s : %d : %d : %d : %d : In CPU\n", currentTime, p,
				// p.getJob_ID(),
				// p.getProcess().getType(), p.getEnd_time(), ++cpuN);

			} else {

				if (IO == null) {
					p = SJF.poll();
					if (CPU.getEnd_time() < currentTime) {

						if (p.getProcess().getNextCPUtime() > CPU.getProcess().remainingCPUburst()) {
							CPU.getProcess().setRemainingCPUburst(CPU.getEnd_time() - currentTime);
							dispatcher();
							CPU = p;

						}
					} else {
						waitQueue.enqueue(p);

						maxWQueue = Math.max(maxWQueue, waitQueue.size());
						avrWQueue = avrWQueue + waitQueue.size();
						wCount++;
					}
				}

			}

		}

		if (CPU == null) {
			cpuIdleTime++;
		} else {
			cpuBusy++;
		}

		if (!waitQueue.isEmpty()) {
			if (IO == null) {
				IO = waitQueue.serve();
				IO.setPState(PCB.WAITING);

				switch (IO.getProcess().getType()) {
				case 1:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 2:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 3:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 4:
					if (IO.getIOstate() == PCB.STATE_USING_IO)
						IO.setIOstate(PCB.STATE_USING_IO);
					else if (IO.getIOstate() == PCB.STATE_USING_THINK)
						IO.setIOstate(PCB.STATE_USING_IO2);
					break;
				}

				IO.setEnd_time(currentTime + IO.getProcess().getIOBurst());
				// System.out.printf("%d : %s : %d : %d : %d : In IO\n", currentTime, IO,
				// IO.getJob_ID(),
				// IO.getProcess().getType(), IO.getEnd_time());
			}
		}

		if (CPU != null) {

			if (CPU.getEnd_time() == currentTime) {

				if (!CPU.isEndCPU()) {

					dispatcher();

				} else {
					if (CPU.getProcess().getType() == 4) {
						if (T4 == 0) {
							minResponseT4 = currentTime;
							T4++;
						}
						ResponseT4 = currentTime - CPU.getStart_time();
						avrResponseT4 = avrResponseT4 + ResponseT4;
						T4complete++;
					}
					dispatcher();
				}

			}

		}

		if (IO != null) {

			if (IO.getEnd_time() == currentTime) {
				monitorIO();

			}

		}

		if (!thinkQueue.isEmpty()) {
			if (thinkQueue.peek().isEndThink(currentTime)) {
				waitQueue.enqueue(thinkQueue.poll());

				maxWQueue = Math.max(maxWQueue, waitQueue.size());
				avrWQueue = avrWQueue + waitQueue.size();
				wCount++;
			}
		}
		return p;

	}

	public static PCB RR(PCB p, int Q1) {
		// TODO Auto-generated method stub
		if (CPU != null)
			if (CPU.isEndCPU() && CPU.getEnd_time() == currentTime)
				if (CPU.getProcess().getType() != 4)
					terminator();

		if (!readyQueue.isEmpty()) {

			if (CPU == null) {

				int s = 0;
				p = readyQueue.serve();
				p.setPState(PCB.RUNNING);
				p.setIOstate(PCB.STATE_USING_CPU);

				switch (p.getProcess().getType()) {
				case 1:
					if (p.getProcess().iCPU1 == 0)
						p.setStart_time(currentTime);
					break;
				case 2:
					if (p.getProcess().iCPU2 == 0)
						p.setStart_time(currentTime);
					break;
				case 3:
					if (p.getProcess().iCPU3 == 0)
						p.setStart_time(currentTime);
					break;
				case 4:
					if (p.getProcess().iCPU4 == 0)
						p.setStart_time(currentTime);
					break;
				}
				p.setStartCPUtime(currentTime);
				s = p.getProcess().getCPUBurst();

				if (Q1 < s)
					p.setEnd_time(currentTime + Q1);
				else {
					p.setEnd_time(currentTime + p.getProcess().remainingCPUburst());
				}

				if (p.getProcess().getType() == 4) {
					p.getProcess().iIO4_1 = 0;
					p.getProcess().iIO4_2 = 0;
				}

				CPU = p;

				// System.out.printf("%d : %s : %d : %d : %d : %d : In CPU\n", currentTime, p,
				// p.getJob_ID(),
				// p.getProcess().getType(), p.getEnd_time(), ++cpuN);

			} else {
				if (IO == null) {
					p = readyQueue.serve();
					waitQueue.enqueue(p);

					maxWQueue = Math.max(maxWQueue, waitQueue.size());
					avrWQueue = avrWQueue + waitQueue.size();
					wCount++;
				}
			}

		}

		if (CPU == null) {
			cpuIdleTime++;
		} else {
			cpuBusy++;
		}

		if (!waitQueue.isEmpty()) {
			if (IO == null) {
				IO = waitQueue.serve();
				IO.setPState(PCB.WAITING);

				switch (IO.getProcess().getType()) {
				case 1:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 2:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 3:
					IO.setIOstate(PCB.STATE_USING_IO);
					break;
				case 4:
					if (IO.getIOstate() == PCB.STATE_USING_IO)
						IO.setIOstate(PCB.STATE_USING_IO);
					else if (IO.getIOstate() == PCB.STATE_USING_THINK)
						IO.setIOstate(PCB.STATE_USING_IO2);
					break;
				}

				IO.setEnd_time(currentTime + IO.getProcess().getIOBurst());
				// System.out.printf("%d : %s : %d : %d : %d : In IO\n", currentTime, IO,
				// IO.getJob_ID(),
				// IO.getProcess().getType(), IO.getEnd_time());
			}
		}

		if (CPU != null) {

			// System.out.printf("Process: %s : End Time %d : Current Time %d\n", CPU,
			// CPU.getEnd_time(), currentTime);
			if (CPU.getEnd_time() == currentTime) {

				if (!CPU.isEndCPU()) {

					dispatcher();

				} else {
					if (CPU.getProcess().getType() == 4) {
						if (T4 == 0) {
							minResponseT4 = currentTime;
							T4++;
						}
						ResponseT4 = currentTime - CPU.getStart_time();
						avrResponseT4 = avrResponseT4 + ResponseT4;
						T4complete++;
						dispatcher();
					}

				}

			}

		}

		if (IO != null) {

			if (IO.getEnd_time() == currentTime) {
				monitorIO();

			}

		}

		if (!thinkQueue.isEmpty()) {
			if (thinkQueue.peek().isEndThink(currentTime)) {
				waitQueue.enqueue(thinkQueue.poll());

				maxWQueue = Math.max(maxWQueue, waitQueue.size());
				avrWQueue = avrWQueue + waitQueue.size();
				wCount++;
			}
		}
		return p;
	}

	public static void MLFQ(PCB p, int Q1) {
		// TODO Auto-generated method stub

		p = FCFS(p);
		p = SJF(p);
		p = RR(p, Q1);

	}

	public static void Lottery(PCB p) {
		// TODO Auto-generated method stub

	}

	public static void statistics() {
		throughputT1 = numT1t;
		throughputT2 = numT2t;
		throughputT3 = numT3t;
		try {
			avrTurnaroundT1 = avrTurnaroundT1 / numT1t;
			avrTurnaroundT2 = avrTurnaroundT2 / numT2t;
			avrTurnaroundT3 = avrTurnaroundT3 / numT3t;

		} catch (ArithmeticException e) {
			// TODO: handle exception

		}

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("This is the conclusion statistics:");
		System.out.printf("Total Number of time units: %d\n", timeSteps);
		System.out.printf("Total Number of Jobs created: %d\n", numT1c + numT2c + numT3c + numT4c);
		System.out.println("------------------------------------------------------");
		System.out.printf("The total number of Type-1 created: %d\n", numT1c);
		System.out.printf("-The total number of Type-1 terminated: %d\n", numT1t);
		System.out.printf("The total number of Type-2 created: %d\n", numT2c);
		System.out.printf("-The total number of Type-2 terminated: %d\n", numT2t);
		System.out.printf("The total number of Type-3 created: %d\n", numT3c);
		System.out.printf("-The total number of Type-3 terminated: %d\n", numT3t);
		System.out.printf("The total number of Type-4 created: %d\n", numT4c);
		// System.out.printf("-The total number of Type-4 terminated: %d\n", numT4t);
		System.out.println("------------------------------------------------------");
		System.out.printf("The max length of ready queue: %d\n", maxRQueue);
		System.out.printf("The avr length of ready queue: %d\n", avrRQueue);
		System.out.println("------------------------------------------------------");
		System.out.printf("The max length of wait queue: %d\n", maxWQueue);
		System.out.printf("The avr length of wait queue: %d\n", avrWQueue);
		System.out.println("------------------------------------------------------");
		System.out.printf("Min response time of Type-4: %d\n", minResponseT4);
		System.out.printf("Max response time of Type-4: %d\n", maxResponseT4);
		System.out.printf("avr response time of Type-4: %d\n", avrResponseT4);
		System.out.println("------------------------------------------------------");
		System.out.printf("Min turnaround-times of Type-1: %d\n", minTurnaroundT1);
		System.out.printf("Max turnaround-times of Type-1: %d\n", maxTurnaroundT1);
		System.out.printf("avr turnaround-times of Type-1: %d\n", avrTurnaroundT1);
		System.out.println("------------------------------------------------------");
		System.out.printf("Min turnaround-times of Type-2: %d\n", minTurnaroundT2);
		System.out.printf("Max turnaround-times of Type-2: %d\n", maxTurnaroundT2);
		System.out.printf("avr turnaround-times of Type-2: %d\n", avrTurnaroundT2);
		System.out.println("------------------------------------------------------");
		System.out.printf("Min turnaround-times of Type-3: %d\n", minTurnaroundT3);
		System.out.printf("Max turnaround-times of Type-3: %d\n", maxTurnaroundT3);
		System.out.printf("avr turnaround-times of Type-3: %d\n", avrTurnaroundT3);
		System.out.println("------------------------------------------------------");
		System.out.printf("Min turnaround-times for all other than Type-4: %d\n",
				Math.min(minTurnaroundT1, Math.min(minTurnaroundT2, minTurnaroundT3)));
		System.out.printf("Max turnaround-times for all other than Type-4: %d\n",
				Math.max(maxTurnaroundT1, Math.max(maxTurnaroundT2, maxTurnaroundT3)));
		System.out.printf("avr turnaround-times for all other than Type-4: %d\n",
				(avrTurnaroundT1 + avrTurnaroundT2 + avrTurnaroundT3) / 3);
		System.out.println("------------------------------------------------------");
		System.out.printf("The total system throughput for Type-1: %d per %d\n", throughputT1, timeSteps);
		System.out.printf("The total system throughput for Type-2: %d per %d\n", throughputT2, timeSteps);
		System.out.printf("The total system throughput for Type-3: %d per %d\n", throughputT3, timeSteps);
		System.out.println("------------------------------------------------------");
		// System.out.printf("Min CPU-overhead-time: %d\n", minOverhead);
		// System.out.printf("Max CPU-overhead-time: %d\n", maxOverhead);
		// System.out.printf("avr CPU-overhead-time: %d\n", avrOverhead);
		System.out.println("------------------------------------------------------");
		if (queueType == 4) {
			System.out.printf("Percentage of CPU-idle-time: %.6f %%\n", (cpuIdleTime / (double) timeSteps) * 100 / 3);
			System.out.printf("Percentage of CPU-utilization: %.6f %%\n ", (cpuBusy / (double) timeSteps) * 100 / 3);
		} else {
			System.out.printf("Percentage of CPU-idle-time: %.6f %%\n", (cpuIdleTime / (double) timeSteps) * 100);
			System.out.printf("Percentage of CPU-utilization: %.6f %%\n", (cpuBusy / (double) timeSteps) * 100);
		}
		System.out.println("------------------------------------------------------");
	}

	public static void help() {
		System.err.println("The approprate arguments is:");
		System.err.println("For FCFS and SJF.");
		System.err.println("	[S] [queueType] [N] [v]");
		System.err.println("For RR and MLFQ.");
		System.err.println("	[S] [queueType] [Q] [N] [v]");
		System.err.println("For Lottery.");
		System.err.println("	[S] [queueType] [Q] [N] [v] [t] [T] [p]");
		System.err.println("-----------------------------------------------------------------");
		System.err.println("	[S] time Steps is integer value only greter than 1000");
		System.err.println("	[queueType] is integer value only, 1:FCFS, 2:SJF, 3:RR, 4:MLFQ, 5:lottery");
		System.err.println("	[Q] Quantum size is integer only greter than 0");
		System.err.println("	[N] Type-4 max Number is integer from 0 to 100");
		System.err.println("	[v] Expected Number For New Jobs Arraiving is double value from 0 to 1");
		System.err.println("	[t] min number of Tickets for lottery only, it is integer only greter than 0");
		System.err.println("	[T] max number of Tickets for lottery only, it is integer only greter than t");
		System.err.println("	[p] peroid of ticket update for lottery only, it is integer only greter than 0");
	}

}

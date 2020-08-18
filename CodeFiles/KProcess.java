//Author: Team BG03
//last update:2019/12/2
public class KProcess {

	public static final int TYPE1 = 1;
	public static final int TYPE2 = 2;
	public static final int TYPE3 = 3;
	public static final int TYPE4 = 4;

	int iCPU1 = 0;
	int iIO1 = 0;
	int iCPU2 = 0;
	int iIO2 = 0;
	int iCPU3 = 0;
	int iIO3 = 0;

	int iCPU4 = 0;
	int iIO4_1 = 0;
	int iIO4_2 = 0;

	int currentCPUburst;

	// Type-1 Process: Burst Time = 15 + 49 = 64
	int[] cpuBurstsT1 = { 1, 2, 1, 1, 1, 3, 1, 2, 2, 1 };
	int[] io_BurstsT1 = { 6, 4, 10, 3, 5, 3, 2, 10, 6 };
	final int burstTimeT1 = 64;
	private int cpuTimeT1;

	// Type-2 Process: Burst Time = 750 + 2100 = 2850
	int[] cpuBurstsT2 = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
	int[] io_BurstsT2 = { 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150 };
	final int burstTimeT2 = 2850;

	// Type-3 Process: Burst Time = 12000 + 55 = 12055
	int[] cpuBurstsT3 = { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 };
	int[] io_BurstsT3 = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
	final int burstTimeT3 = 12055;

	// Type-4 Process: Burst Time = 76 per one loop
	int cpuBurstsT4 = 3;
	int io1BurstsT4 = 3;
	int thinkTimeT4 = 60;
	int io2BurstsT4 = 10;
	final int burstTimeT4 = 76;

	private static int numProcesses = 0;
	private int pid;
	private int tCreate; // creation time

	private int type = 1;// to store type of process created

	private static int total_burst;// total burst of the process

	public KProcess() {
		// TODO Auto-generated constructor stub
		numProcesses = 0;
		pid = 0;
		tCreate = 0;

	}

	public KProcess(int arrivalTime) {
		// TODO Auto-generated constructor stub
		pid = ++numProcesses;
		tCreate = arrivalTime;

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (type > 0 && type < 5)
			this.type = type;
		else
			System.err.println("Type Error");

	}

	public static int getNumProcesses() {
		return numProcesses;
	}

	public int getId() {
		return pid;
	}

	public void setId(int id) {
		this.pid = id;
	}

	public int gettCreate() {
		return tCreate;
	}

	public int inCPU() {
		int inCPUtime;

		inCPUtime = tCreate;
		return inCPUtime;
	}

	public void setRemainingCPUburst(int RemainingCPUburst) {
		switch (type) {
		case 1:
			if (iCPU1 > 0) {
				iCPU1--;
			}
			break;
		case 2:
			if (iCPU2 > 0) {
				iCPU2--;
			}
			break;
		case 3:
			if (iCPU3 > 0) {
				iCPU3--;
			}
			break;
		case 4:
			iCPU4 = 1;
			break;
		}
		this.currentCPUburst = RemainingCPUburst;

	}

	public int remainingCPUburst() {

		return currentCPUburst;
	}

	public int getCPUBurst() {
		int k = 0;
		switch (type) {
		case 1:
			if (iCPU1 < cpuBurstsT1.length) {
				k = cpuBurstsT1[iCPU1];
				iCPU1++;
			}
			break;
		case 2:
			if (iCPU2 < cpuBurstsT2.length) {
				k = cpuBurstsT2[iCPU2];
				iCPU2++;
			}
			break;
		case 3:
			if (iCPU3 < cpuBurstsT3.length) {
				k = cpuBurstsT3[iCPU3];
				iCPU3++;
			}
			break;
		case 4:
			k = cpuBurstsT4;
			iCPU4 = 1;
			break;
		}
		currentCPUburst = k;
		return k;
	}

	public int getIOBurst() {
		int k = 0;
		switch (type) {
		case 1:
			if (iIO1 < io_BurstsT1.length) {
				k = io_BurstsT1[iIO1];
				iIO1++;
			}
			break;
		case 2:
			if (iIO2 < io_BurstsT2.length) {
				k = io_BurstsT2[iIO2];
				iIO2++;
			}
			break;
		case 3:
			if (iIO3 < io_BurstsT3.length) {
				k = io_BurstsT3[iIO3];
				iIO3++;
			}
			break;
		case 4:
			if (iIO4_1 == 0) {
				k = io1BurstsT4;
				iIO4_1 = 1;
			} else if (iIO4_2 == 0) {
				k = io2BurstsT4;
				iIO4_2 = 1;
			}

			break;

		}
		return k;
	}

	public int getNextCPUtime() {
		int k = 1;
		switch (type) {
		case 1:
			if (cpuTimeT1 < cpuBurstsT1.length) {
				k = cpuBurstsT1[cpuTimeT1];
				cpuTimeT1++;
			}
			break;
		case 2:
			k = 50;
			break;
		case 3:
			k = 1000;
			break;
		case 4:
			k = 3;
			break;

		}
		return k;
	}

	public int getTotal_burst() {
		switch (type) {
		case 1:
			total_burst = burstTimeT1;
			break;
		case 2:
			total_burst = burstTimeT2;
			break;
		case 3:
			total_burst = burstTimeT3;
			break;
		case 4:
			total_burst = burstTimeT4;
			break;

		}

		return total_burst;
	}

}

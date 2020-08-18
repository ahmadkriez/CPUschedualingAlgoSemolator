//Author :Team BG03
// last update :2019/12/2
//Description : java file that makes PCB for each process that contains all the information for each process

public class PCB {

	private int Job_ID;
	private int Pstate;
	private int IOstate;
	private int arrivalTime;
	private int thinkArrivalTime;
	private int start_time;
	private int end_time;
	private int queLevel = 0;
	private KProcess p;
	private int cpuTime;

	// CPU-IO State
	final static int STATE_INITIAL = 01; // to indicate that the job is in a new state
	final static int STATE_USING_CPU = 02;// to indicate that the Job is in a using CPU state
	final static int STATE_USING_IO = 03;// to indicate that the Job is in a using IO state
	final static int STATE_USING_IO2 = 04;// to indicate that the Job is in a using IO2 state
	final static int STATE_USING_THINK = 05;// to indicate that the Job is in a using Think state
	final static int STATE_FINAL = 06;// to indicate that the Job is in a completed state

	// Process State
	final static int NEW = 10;
	final static int RUNNING = 11;
	final static int WAITING = 12;
	final static int READY = 13;
	final static int TERMINATED = 14;

	public PCB() {
		Job_ID = 0;
		Pstate = NEW;
		arrivalTime = 0;
		thinkArrivalTime = 0;

	}

	// Creates a new Job, the p, Job_ID and arrival time
	public PCB(int arrivalTime) {
		p = new KProcess(arrivalTime);
		Pstate = NEW;
		IOstate = STATE_INITIAL;
		this.Job_ID = p.getId();
		this.arrivalTime = arrivalTime;
		this.Pstate = NEW;
	}

	// to get the state of the job
	public int getPState() {
		return this.Pstate;
	}

	public int getStartCPUtime() {
		return cpuTime;
	}

	public void setStartCPUtime(int cpuTime) {
		this.cpuTime = cpuTime;
	}

	// Sets the state of the job
	public void setPState(int Pstate) {
		this.Pstate = Pstate;
	}

	public KProcess getProcess() {
		return this.p;
	}

	public void setProcess(KProcess p) {
		this.p = p;
	}

	public int getJob_ID() {
		return this.Job_ID;
	}

	// to get the queLevel of this job
	public void setLevelOfQue(int queLevel) {
		this.queLevel = queLevel;
	}

	public int getArrivalTime() {
		return this.arrivalTime;
	}

	public long getTAT() {
		return this.arrivalTime;
	}

	public int getLevelOfQue() {
		return this.queLevel;
	}

	public int getThinkArrivalTime() {
		return thinkArrivalTime;
	}

	public void setThinkArrivalTime(int thinkArrivalTime) {
		this.thinkArrivalTime = thinkArrivalTime;
	}

	public Boolean isEndThink(int currentTime) {
		boolean k = false;
		k = currentTime - this.thinkArrivalTime == 60;
		return k;

	}

	public int getStart_time() {
		return start_time;
	}

	public void setStart_time(int start_time) {
		this.start_time = start_time;
	}

	public int getEnd_time() {

		return end_time;
	}

	public void setEnd_time(int end_time) {
		this.end_time = end_time;
	}

	public int turnaroundTime() {
		return getEnd_time() - getStart_time();
	}

	public int getIOstate() {
		return IOstate;
	}

	public void setIOstate(int iOstate) {
		this.IOstate = iOstate;
	}

	public boolean isEndCPU() {
		boolean k = false;
		switch (p.getType()) {
		case 1:
			k = p.cpuBurstsT1.length == p.iCPU1;
			setPState(TERMINATED);
			break;
		case 2:
			k = p.cpuBurstsT2.length == p.iCPU2;
			setPState(TERMINATED);
			break;
		case 3:
			k = p.cpuBurstsT3.length == p.iCPU3;
			setPState(TERMINATED);
			break;
		case 4:
			k = p.iCPU4 == 1;
			break;
		}

		return k;
	}

	public boolean isEndIO() {
		boolean k = false;
		switch (p.getType()) {
		case 1:
			k = p.io_BurstsT1.length == p.iIO1;
			setPState(READY);
			break;
		case 2:
			k = p.io_BurstsT2.length == p.iIO2;
			setPState(READY);
			break;
		case 3:
			k = p.io_BurstsT3.length == p.iIO3;
			setPState(READY);
			break;
		case 4:
			if (getIOstate() == STATE_USING_IO) {
				k = p.iIO4_1 == 1;
				setPState(WAITING);
				setIOstate(STATE_USING_THINK);
			}
			if (getIOstate() == STATE_USING_IO2) {
				k = p.iIO4_2 == 1;
				setPState(READY);
			}
			break;
		}

		return k;
	}

}

//Author : Team BG03
// last update: 2019/12/2
// Description : java file that create jobs to using poison generator
public class JobGenerator {
	private PoisonDistGenerator pd;
	private double e;

	public JobGenerator() {
		// TODO Auto-generated constructor stub
		setPd(new PoisonDistGenerator());

	}

	public JobGenerator(double e) {
		// TODO Auto-generated constructor stub
		this.setE(e);
		setPd(new PoisonDistGenerator(e));

	}

	public double getE() {
		return e;
	}

	public void setE(double e) {
		this.e = e;
	}

	public PoisonDistGenerator getPd() {
		return pd;
	}

	public void setPd(PoisonDistGenerator pd) {
		this.pd = pd;
	}

}

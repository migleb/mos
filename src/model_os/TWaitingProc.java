package model_os;

public class TWaitingProc {
	TProcess receiver;
	int amount;
	int target;
	
	public TWaitingProc(TProcess receiver, int amount, int target) {
		this.receiver = receiver;
		this.target = target;
		this.amount = amount;
	}

	public TProcess getReceiver() {
		return receiver;
	}

	public int getAmount() {
		return amount;
	}

	public int getTarget() {
		return target;
	}
}

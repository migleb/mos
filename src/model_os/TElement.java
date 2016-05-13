package model_os;

public class TElement {
	
	final TProcess proc;
	final TProcess creator;
	final String info;
	int target;
	TResource resource;
	
	public TElement (TProcess proc, TProcess creator, String info) {
		this.proc = proc;
		this.creator = creator;
		this.info = info;
	}

	public TResource getResource() {
		return resource;
	}
	
	public TProcess getProc() {
		return proc;
	}

	public TProcess getCreator() {
		return creator;
	}

	public String getInfo() {
		return info;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public void assignToResource(TResource resource) {
			this.resource = resource;
		}
}

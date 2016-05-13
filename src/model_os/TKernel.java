package model_os;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

import model_os.TResource.ResourceClass;

public class TKernel implements Runnable {

	final PriorityQueue<TProcess> OSProcesses;
	final List<TResource> OSResources;
	final PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;
	
	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	boolean stepRun;
	JTextArea printer;
	String savedLine;
	
	final int pageSize = 10;
	final String[] generalMemory = new String[12 * pageSize];
	final OperativeMemory ram = new OperativeMemory(88, pageSize);
	final Processor processor = new Processor(ram);
	final HardDrive hdd = new HardDrive(100, pageSize);
	
	Runnable runnable;
	
	public TKernel (boolean stepRun) {
		OSProcesses = new PriorityQueue<TProcess>();
		OSResources = new LinkedList<TResource>();
		OSReadyProc = new PriorityQueue<TProcess>();
		
		Arrays.fill(generalMemory, "");
		
		this.stepRun = stepRun;
	}
	
	public Lock getLock() {
		return lock;
	}
		
	public Condition getCond() {
		return cond;
	}
	
	public TProcess[] getOSProcesses() {
		return OSProcesses.toArray(new TProcess[OSProcesses.size()]);
	}
	
	public void print (String text) {
		if (this.printer != null) {
			this.printer.setText(text + "\n" + this.printer.getText());
		}
	}
	
	public void setPrinter(JTextArea printer) {
		this.printer = printer;
	}
	
	public Processor getProcessor() {
		return processor;
	}
	
	public OperativeMemory getRam() {
		return ram;
	}
	
	public HardDrive getHdd() {
		return hdd;
	}
	
	public String[] getGeneralMemory() {
		return generalMemory;
	}
	
	public void setStepRun(boolean stepRun) {
		this.stepRun = stepRun;
	}
	
	public void setInputedLine(String savedLine) {
		this.savedLine = savedLine;
	}
	
	@Override
	public void run() {
		//TODO
		
	}
	
	public void onUpdate(Runnable runnable) {
		this.runnable = runnable;
	}
	
	private void updated() {
		boolean step = stepRun;
		if (step) {
			lock.lock();
		}
		try {
			if (this.runnable != null) {
				this.runnable.run();
			}
			if (step) {
				cond.await();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (step) {
			lock.unlock();
		}
	}
	
	private void executeDistributor(TResource resource) {
		updated();
		
		if (resource.getrWaitProcList().size() > 0 && resource.getrAccElem().size() > 0) {
			List<TWaitingProc> servedProcesses = new LinkedList<TWaitingProc>();
			for (TWaitingProc waitingProc : resource.getrWaitProcList()) {
				int neededAmount = waitingProc.getAmount();
				TProcess receiver = waitingProc.getReceiver();
				
				if (neededAmount > 0 && neededAmount <= availableElements(resource,receiver)) {
					List<TElement> usedElements = new LinkedList<TElement>();
					
					for (TElement el : resource.getrAccElem()) {
						if (el.getProc() == null || el.getProc() == receiver) {
							el.setTarget(waitingProc.getTarget());
							receiver.getpORElements().add(el);
							usedElements.add(el);
							neededAmount--;
						}
						if (neededAmount <= 0) {
							break;
						}
					}
					
					servedProcesses.add(waitingProc);
					
					resource.getrAccElem().removeAll(usedElements);
				}
			}
			
			for (TWaitingProc process : servedProcesses) {
				activateProcess(process.getReceiver());
			}
			
			resource.getrWaitProcList().removeAll(servedProcesses);
		}
		executePlanner();
	}
	
	private TProcess OSProcessWithId (int id) {
		for (TProcess proc : OSProcesses) {
			if (proc.getpID() == id) {
				return proc;
			}
		}
		return null;
	}
	
	private int availableElements(TResource res, TProcess p) {
		int available = 0;
		for (TElement el : res.getrAccElem()) {
			if (el.getProc() == null || el.getProc() == p) {
				available++;
			}
		}
		return available;
	}
	
	public int availableResourceElementsFor (TProcess process, ResourceClass resourceClass) throws Exception {
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				return availableElements(res, process);
			}
		}
		throw new Exception ("Resource class " + resourceClass + " does not exist in operating system");
	}
	
	int i = 1;
	private boolean checkInput() {
		if (savedLine != null) {
			String line = savedLine;
			savedLine = null;
			
			int spaceIdx = line.indexOf(" ");
			if (spaceIdx > 0) {
				String procIdString = line.substring(0, spaceIdx);
				try {
					int procId = Integer.valueOf(procIdString);
					String msg = line.substring(spaceIdx + 1, line.length());
					TProcess proc = OSProcessWithId(procId);
					if (proc != null) {
						print(msg);
						freeResource(ResourceClass.SAVEDLINE, new TElement(proc, null, msg));
					} else {
						freeResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Process with id=" + procId + "does not exist"));
					}
				} catch (NumberFormatException e) {
					freeResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Process id should be numeric"));
				}
			} else {
				freeResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Invalid line format"));
			}
			
			return true;
		}
		return false;
	}
	
	private void executePlanner() {
		updated();
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.READY) {
			this.OSCurrentProc.setpState(TPState.READY);
		}
		if (!checkInput()) {
			if (this.OSReadyProc.size() > 0) {
				TProcess headProcess = this.OSReadyProc.poll();
				headProcess.toggleLastUsing();
				OSReadyProc.add(headProcess);
				this.startProcess(headProcess);
			} else {
				freeResource(ResourceClass.IDLE, new TElement(null, null, null));
			}
		}
	}
	
	public void createProcess(TProcess process) {
		this.OSProcesses.add(process);
		if (process.getpParent() != null) {
			process.getpParent().addChild(process);
		}
		System.out.println("Created process " + process.getExternalName());
		process.setpCPUState(getProcessor().getTProcessCPU());
		this.OSReadyProc.add(process);
		this.executePlanner();
	}
	
	public void activateProcess(TProcess process) {
		System.out.println("Activated process " + process.getExternalName());
		process.setpState(TPState.READY);
		if (!OSReadyProc.contains(process)) {
			this.OSReadyProc.add(process);
		}
	}
	
	private void startProcess (TProcess process) {
		System.out.println("Started process " + process.getExternalName());
		process.setpState(TPState.RUNNING);
		this.OSCurrentProc = process;
		
		updated();
	}
	
	public void stopProcess(TProcess process) {
		System.out.println("Stop process " + process.getExternalName());
		process.setpState(TPState.WAITING);
		this.OSReadyProc.remove(process);
	}
	
	public void destroyProcess (TProcess process) {
		System.out.println("Destroy process" + process.getExternalName());
		process.setpState(TPState.FINISHED);
		this.OSResources.removeAll(process.getpChResources());
		while (process.getpChProcesses().size() > 0) {
			destroyProcess(process.getpChProcesses().element());
		}
		if (process.getpParent() != null) {
			process.getpParent().getpChProcesses().remove(process);
		}
		this.OSProcesses.remove(process);
		this.OSReadyProc.remove(process);
	}
	
	/* Resource primitives */
	
	public void createResource(TProcess process, TResource.ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		TResource resourceDesc = new TResource(process, resourceClass, reusable, availableElements);
		process.getpChResources().add(resourceDesc);
		OSResources.add(resourceDesc);
		System.out.println("Created resource descriptor " + resourceDesc.getrID());
	}
	
	public void askForResource(TProcess process, ResourceClass resourceClass, int target) {
		askForResource(process, resourceClass, target, 1);
	}
	
	public void askForResource(TProcess process, ResourceClass resourceClass, int target, int amount) {
		TResource askedResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				askedResDesc = res;
				break;
			}
		}
		askedResDesc.getrWaitProcList().add(new TWaitingProc(process, amount, target));
		System.out.println("Asked resource descriptor " + askedResDesc.getResourceClass().toString());
		stopProcess(process);
		executeDistributor(askedResDesc);
	}
	
	public void freeResource (ResourceClass resourceClass, TElement[] elements) {
		TResource freeResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				freeResDesc = res;
				break;
			}
		}
		for (TElement el : elements) {
			el.assignToResource(freeResDesc);
			freeResDesc.getrAccElem().add(el);
		}
		System.out.println("Free resource " + freeResDesc.getResourceClass().toString());
		executeDistributor(freeResDesc);
	}
	
	public void freeResource(ResourceClass resourceClass, TElement element) {
		freeResource(resourceClass, new TElement[] {element});
	}

}

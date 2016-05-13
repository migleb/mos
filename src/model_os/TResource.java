package model_os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TResource {
	
	public enum ResourceClass {
		IDLE,
		SHUTDOWN,
		SAVEDLINE,
		LOADPROGRAM,
		LOADEDPROGRAM,
		GENERALMEMORY,
		CHANNELDEVICE,
		VALIDPROGRAM,
		LINETOPRINT,
		INTERRUPTINFO,
		INTERRUPT,
		PAGES
	}
	
	final ResourceClass resourceClass;
	final int rID;
	final TProcess rCreator;
	final boolean rReusable;
	final List<TElement> rAccElem;
	final List<TWaitingProc> rWaitProcList;

	static int autoID = 0;
	
	public TResource (TProcess process,ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		rID = autoID++;
		this.resourceClass = resourceClass;
		rCreator = process;
		rReusable = reusable;
		rAccElem = availableElements != null ? new ArrayList<TElement>(Arrays.asList(availableElements)): new ArrayList<TElement>();
		for (TElement element : rAccElem) {
			element.assignToResource(this);
		}
		rWaitProcList = new ArrayList<TWaitingProc>();
	}

	public ResourceClass getResourceClass() {
		return resourceClass;
	}

	public int getrID() {
		return rID;
	}
	
	public TProcess getrCreator() {
		return rCreator;
	}

	public boolean isrReusable() {
		return rReusable;
	}

	public ArrayList<TElement> getrAccElem() {
		return (ArrayList<TElement>) rAccElem;
	}
	
	public int getrAmount() {
		return rAccElem.size();
	}

	public List<TWaitingProc> getrWaitProcList() {
		return rWaitProcList;
	}
}

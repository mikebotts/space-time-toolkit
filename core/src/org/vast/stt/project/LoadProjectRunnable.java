package org.vast.stt.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class LoadProjectRunnable implements  IRunnableWithProgress
{
	ProjectReader reader;
	String projectUrl;
	boolean isUpdating = false;
	
	public LoadProjectRunnable(ProjectReader pr, String url)
	{
		this.reader = pr;
		this.projectUrl = url;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		String msg = "Attempting to open project: " + projectUrl + "...";

		try 
		{
			monitor.beginTask(
					msg, IProgressMonitor.UNKNOWN);
			monitor.subTask("Reading Project...");
			reader.readProject(projectUrl);
			while(isUpdating){
				try {
					Thread.sleep(40);
					if (monitor.isCanceled()) {
						isUpdating =  false;
					}
					if(reader.getProject() != null)
						isUpdating = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//						System.err.println("LPR.run.IntrptdEx");
					e.printStackTrace();
				} finally {
					isUpdating = false;
				}
			}
			if(monitor.isCanceled()) {
				reader.cancelRead();
				throw new InterruptedException("Project Loading Cancelled");
			}
		} finally {
			if(monitor.isCanceled()) {
				throw new InterruptedException("Project Loading Cancelled");
			}				
			monitor.done();
		}		  
	}
}

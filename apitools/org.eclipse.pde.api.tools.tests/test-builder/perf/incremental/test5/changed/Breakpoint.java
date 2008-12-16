/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.core.model;

 
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.internal.core.DebugCoreMessages;
import org.eclipse.debug.internal.core.DebugOptions;

/**
 * Abstract implementation of a breakpoint. This class is
 * intended to be subclassed by implementations
 * of breakpoints.
 * 
 * @see IBreakpoint
 * @since 2.0
 */

public abstract class Breakpoint extends PlatformObject implements IBreakpoint {
	
	static {
		// making sure that the BreakpointManager is correctly initialized
		// before any breakpoint marker related operation (see bug 54993)
		DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();
	}
				
	/**
	 * Underlying marker.
	 */
	private IMarker fMarker= null;
	
	/**
	 * @see IBreakpoint#setMarker(IMarker)
	 */
	public void setMarker(IMarker marker) throws CoreException {
		fMarker= marker;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object item) {
		if (item instanceof IBreakpoint) {
			return getMarker().equals(((IBreakpoint)item).getMarker());
		}
		return false;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return getMarker().hashCode();
	}
		
	/**
	 * @see IBreakpoint#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) throws CoreException {
		if (enabled != isEnabled()) {
			setAttribute(ENABLED, enabled);
		}
	}
	
	/**
	 * @see IBreakpoint#isEnabled()
	 */
	public boolean isEnabled() throws CoreException {
		return getMarker().getAttribute(ENABLED, false);
	}
	
	/**
	 * @see IBreakpoint#isRegistered()
	 */
	public boolean isRegistered() throws CoreException {
			IMarker marker= getMarker();
			return marker.exists() && marker.getAttribute(REGISTERED, true);
	}	
	
	/**
	 * @since 3.4
	 * @return test
	 */
	public DebugOptions getOptions() {
		return null;
	}
	
	/**
	 * @see IBreakpoint#setRegistered(boolean)
	 */
	public void setRegistered(boolean registered) throws CoreException {
		if (isRegistered() != registered) {
			setAttribute(REGISTERED, registered);
			IBreakpointManager mgr = DebugPlugin.getDefault().getBreakpointManager();
			if (registered) {
				mgr.addBreakpoint(this);
			} else {
				mgr.removeBreakpoint(this, false);
			}
		}
	}	

	/**
	 * @see IBreakpoint#delete()
	 */
	public void delete() throws CoreException {
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(this, false);
		getMarker().delete();
	}

	/**
	 * @see IBreakpoint#getMarker()
	 */
	public IMarker getMarker() {
		return fMarker;
	}

	/**
	 * @see IBreakpoint#isPersisted()
	 */
	public boolean isPersisted() throws CoreException {
		return getMarker().getAttribute(PERSISTED, true);
	}

	/**
	 * @see IBreakpoint#setPersisted(boolean)
	 */
	public void setPersisted(boolean persisted) throws CoreException {
		if (isPersisted() != persisted) {
			setAttributes(new String[] {PERSISTED, IMarker.TRANSIENT}, new Object[] {Boolean.valueOf(persisted), Boolean.valueOf(!persisted)});
		}
	}
	
	/**
	 * Convenience method to set the given boolean attribute of
	 * this breakpoint's underlying marker in a workspace
	 * runnable. Setting marker attributes in a workspace runnable
	 * prevents deadlock.
	 * 
	 * @param attributeName attribute name
	 * @param value attribute value
	 * @exception CoreException is setting the attribute fails
	 * @see IMarker#setAttribute(java.lang.String, boolean)
	 */
	protected void setAttribute(final String attributeName, final boolean value) throws CoreException {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ensureMarker().setAttribute(attributeName, value);
				}
			};
			
		workspace.run(runnable, getMarkerRule(), 0, null);
	}
	
	/**
	 * Convenience method to set the given integer attribute of
	 * this breakpoint's underlying marker in a workspace
	 * runnable. Setting marker attributes in a workspace runnable
	 * prevents deadlock.
	 * 
	 * @param attributeName attribute name
	 * @param value attribute value
	 * @exception CoreException is setting the attribute fails
	 * @see IMarker#setAttribute(java.lang.String, int)
	 */
	protected void setAttribute(final String attributeName, final int value) throws CoreException {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ensureMarker().setAttribute(attributeName, value);
				}
			};
			
		workspace.run(runnable, getMarkerRule(), 0, null);
	}

	/**
	 * Convenience method to set the given attribute of
	 * this breakpoint's underlying marker in a workspace
	 * runnable. Setting marker attributes in a workspace runnable
	 * prevents deadlock.
	 * 
	 * @param attributeName attribute name
	 * @param value attribute value
	 * @exception CoreException is setting the attribute fails
	 * @see IMarker#setAttribute(java.lang.String, java.lang.Object)
	 */
	protected void setAttribute(final String attributeName, final Object value) throws CoreException {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ensureMarker().setAttribute(attributeName, value);
				}
			};
			
		workspace.run(runnable, getMarkerRule(), 0, null);
	}

	/**
	 * Convenience method to set the given attributes of
	 * this breakpoint's underlying marker in a workspace
	 * runnable. Setting marker attributes in a workspace runnable
	 * prevents deadlock.
	 * 
	 * @param attributeNames attribute names
	 * @param values attribute values
	 * @exception CoreException is setting the attributes fails
	 * @see IMarker#setAttributes(java.lang.String[], java.lang.Object[])
	 */
	protected void setAttributes(final String[] attributeNames, final Object[] values) throws CoreException {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ensureMarker().setAttributes(attributeNames, values);
				}
			};
			
		workspace.run(runnable, getMarkerRule(), IWorkspace.AVOID_UPDATE, null);
	}

	/**
	 * Convenience method to set the attributes of
	 * this breakpoint's underlying marker in a workspace
	 * runnable. Setting marker attributes in a workspace runnable
	 * prevents deadlock.
	 * 
	 * @param attributes attribute map
	 * @exception CoreException is setting the attributes fails
	 * @see IMarker#setAttributes(java.util.Map)
	 */
	protected void setAttributes(final Map attributes) throws CoreException{
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					ensureMarker().setAttributes(attributes);
				}
			};
			
		workspace.run(runnable, getMarkerRule(), IWorkspace.AVOID_UPDATE, null);
	}

	/**
	 * Returns the marker associated with this breakpoint.
	 * 
	 * @return breakpoint marker
	 * @exception DebugException if no marker is associated with 
	 *  this breakpoint or the associated marker does not exist
	 */
	protected IMarker ensureMarker() throws DebugException {
		IMarker m = getMarker();
		if (m == null || !m.exists()) {
			throw new DebugException(new Status(IStatus.ERROR, DebugPlugin.getUniqueIdentifier(), DebugException.REQUEST_FAILED,
				DebugCoreMessages.Breakpoint_no_associated_marker, null)); 
		}
		return m;
	}
	
	/**
	 * Returns whether this breakpoint has an associated marker that exists.
	 * 
	 * @return returns whether this breakpoint has an associated marker that exists
	 * @since 2.1
	 */
	protected boolean markerExists() {
		IMarker m = getMarker();
		return (m != null && m.exists());
	}

	/**
	 * Returns a scheduling rule to use when modifying markers on the given resource,
	 * possibly <code>null</code>.
	 * 
	 * @param resource a resource on which a marker will be created, modified, or deleted
	 * @return a scheduling rule to use when modifying markers on the given resource
	 * 	possibly <code>null</code>
	 * @since 3.1
	 */
    protected ISchedulingRule getMarkerRule(IResource resource) {
        ISchedulingRule rule = null;
        if (resource != null) {
            IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
            rule = ruleFactory.markerRule(resource);
        }
        return rule;
    }
    
	/**
	 * Returns a scheduling rule to use when modifying or deleting this breakpoint's marker, 
	 * possibly <code>null</code>. This method is only valid when this breakpoint's
	 * marker has already been created. When creating a marker on a specific resource,
	 * use <code>getMarkerRule(IResource)</code> instead.
	 * 
	 * @return a scheduling rule to use when modifying or deleting this breakpoint's marker
	 * @since 3.1
	 */
    protected ISchedulingRule getMarkerRule() {
        ISchedulingRule rule = null;
        IMarker marker = getMarker();
        if (marker != null) {
	        IResource resource = marker.getResource();
	        if (resource != null) {
	            IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
	            rule = ruleFactory.markerRule(resource);
	        }
        }
        return rule;
    }
    
    /**
	 * Execute the given workspace runnable with the scheduling rule to use when running the operation.
	 * 
	 * @param rule the rule to use when running the operation
     * @param wr the runnable operation
     * @throws DebugException If a core exception occurs performing the operation
	 * @since 3.1
	 */
    protected void run(ISchedulingRule rule, IWorkspaceRunnable wr) throws DebugException {
    	try {
    		ResourcesPlugin.getWorkspace().run(wr, rule, 0, null);
    	} catch (CoreException e) {
    		throw new DebugException(e.getStatus());
    	}			
    }  
    
}

/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.ui.cls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.ClsAdapter;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.ClsListener;
import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.ServerProject;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.server.framestore.background.CacheRequestReason;
import edu.stanford.smi.protege.server.framestore.background.FrameCalculator;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

public class ClassTreeWithBrowserTextNode extends LazyTreeNode {
	private static transient Logger log = Log.getLogger(ClassTreeWithBrowserTextNode.class);

	public ClassTreeWithBrowserTextNode(LazyTreeNode parentNode, FrameWithBrowserText userObj) {
		super(parentNode, userObj, parentNode.isSorted());
		Frame frame = userObj.getFrame();
		if (frame instanceof Cls) {
			attachListener((Cls)frame);          
		}
		if (log.isLoggable(Level.FINE)) {
			log.fine(logIntro() + "Constructed");
			if (userObj.getTypes() != null) {
				for (Cls cls : userObj.getTypes()) {
					log.fine("\thasType = " + cls);
				}
			}
		}
	}
	
	/*
	 * Listener code
	 */

	 protected void attachListener(Cls cls) {
		 if (cls != null) {
			 cls.addClsListener(_clsListener);
			 cls.addFrameListener(_frameListener);
			 cls.addInstanceListener(instanceListener);
		 }
	 }

	 protected void detachListeners(Cls cls) {
		 if (cls != null) {
			 cls.removeClsListener(_clsListener);
			 cls.removeFrameListener(_frameListener);
			 cls.removeInstanceListener(instanceListener);
		 }
	 }

	 private ClsListener _clsListener = new ClsAdapter() {
		 public void directSubclassAdded(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 if (event.getSubclass().isVisible()) {
				 childAdded(createFrameWithBrowserText(event.getSubclass()));
			 }
		 }

		 public void directSubclassRemoved(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 if (event.getSubclass().isVisible()) {
				 childRemoved(createFrameWithBrowserText(event.getSubclass()));
			 }
		 }

		 public void directSubclassMoved(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 Cls subclass = event.getSubclass();
			 FrameWithBrowserText subclassFbt = createFrameWithBrowserText(subclass);
			 int index = (new ArrayList<FrameWithBrowserText>(getChildObjects())).indexOf(subclassFbt);
			 if (index != -1) {
				 childRemoved(subclassFbt);
				 childAdded(subclassFbt, index);
			 }
		 }

		 public void directInstanceAdded(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 notifyNodeChanged();
		 }

		 public void directInstanceRemoved(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 notifyNodeChanged();
		 }

		 public void templateFacetValueChanged(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 notifyNodeChanged();
		 }

		 public void directSuperclassAdded(ClsEvent event) {
			 if (event.isReplacementEvent()) return;

			 notifyNodeChanged();
		 }
	 };

	 private FrameListener _frameListener = new FrameAdapter() {
		 @Override
		 public void frameReplaced(FrameEvent event) {
			 Frame oldFrame = event.getFrame();
			 Frame newFrame = event.getNewFrame();
			 Cls cls = getCls();
			 if (cls != null && cls.equals(oldFrame)) {
				 reload(createFrameWithBrowserText(newFrame));              
			 }
		 }

		 public void browserTextChanged(FrameEvent event) {
			 if (event.isReplacementEvent()) return;
			 FrameWithBrowserText fbt = (FrameWithBrowserText) getUserObject();
			 Frame frame = fbt.getFrame();
			 if (frame != null) {
				 fbt.setBrowserText(frame.getBrowserText());
			 }			 
			 notifyNodeChanged();
		 }

		 public void ownSlotValueChanged(FrameEvent event) {
			 if (event.isReplacementEvent()) return;
			 if (event.getSlot().getName().equals(Model.Slot.DIRECT_TYPES)) {
				 // refresh the stale cls reference
				 Cls cls = getCls().getKnowledgeBase().getCls(getCls().getName());
				 reload(createFrameWithBrowserText(cls));
			 }
			 else {
				 notifyNodeChanged();
			 }
		 }

		 public void visibilityChanged(FrameEvent event) {
			 if (event.isReplacementEvent()) return;
			 notifyNodeChanged();
		 }
	 };
	 
	 private InstanceListener instanceListener = new InstanceListener() {

		@SuppressWarnings("unchecked")
		public void directTypeAdded(InstanceEvent event) {
			 if (event.isReplacementEvent()) return;
			 FrameWithBrowserText fbt = (FrameWithBrowserText) getUserObject();
			 if (fbt.getFrame() instanceof Instance) {
				 fbt.setTypes(((Instance) fbt.getFrame()).getDirectTypes());
				 if (log.isLoggable(Level.FINE)) {
					 log.fine(logIntro() + "Types changed");
					 Collection<Cls> types = ((FrameWithBrowserText) getUserObject()).getTypes();
					 if (types != null) {
						 for (Cls cls : types) {
							 log.fine("\thasType = " + cls);
						 }
					 }
				 }
			 }
			 notifyNodeChanged();
		}

		@SuppressWarnings("unchecked")
		public void directTypeRemoved(InstanceEvent event) {
			 if (event.isReplacementEvent()) return;
			 FrameWithBrowserText fbt = (FrameWithBrowserText) getUserObject();
			 if (fbt.getFrame() instanceof Instance) {
				 fbt.setTypes(((Instance) fbt.getFrame()).getDirectTypes());
				 if (log.isLoggable(Level.FINE)) {
					 log.fine(logIntro() + "Types changed");
					 Collection<Cls> types = ((FrameWithBrowserText) getUserObject()).getTypes();
					 if (types != null) {
						 for (Cls cls : types) {
							 log.fine("\thasType = " + cls);
						 }
					 }
				 }
			 }
			 notifyNodeChanged();
		}
		 
	 };

	 protected void notifyNodeChanged() {
		 notifyNodeChanged(this);
	 }

	 protected FrameWithBrowserText createFrameWithBrowserText(Frame frame) {
		 if (frame == null) { return null; }
		 FrameWithBrowserText result = FrameWithBrowserText.getFrameWithBrowserText(frame);
		 result.setIconName(ProtegeUI.getPotentialIconName(frame));
		 return result;
	 }

	 
	 /*
	  * Get node children code 
	  */

	 @Override
	 protected Collection<FrameWithBrowserText> getChildObjects() {
		 Collection<FrameWithBrowserText> children;
		 long t0 = System.currentTimeMillis();
		 try {
			 if (isCached(getCls())) {
				 children = getLocalChildren(getCls(), showHidden());
				 if (log.isLoggable(Level.FINE)) {
					 log.fine("Get children of " + getCls().getBrowserText() + " from client in " + (System.currentTimeMillis() - t0) + " ms.");
				 }				 
			 } else {
				 if (log.isLoggable(Level.FINE)) {
					 log.fine("Get children of " + getCls().getBrowserText() + " from server in " + (System.currentTimeMillis() - t0) + " ms.");
				 }
				 children = (Collection<FrameWithBrowserText>) new GetChildObjectsJob(getCls().getKnowledgeBase(), getCls(), showHidden()).execute();
			 }
		 } catch (Throwable t) {
			 Log.getLogger().log(Level.WARNING, "Could not get children of class " + getUserObject(), t);
			 return new ArrayList<FrameWithBrowserText>();
		 }     
		 return children;
	 }
	 
	 private boolean isCached(Cls cls) { //a very weak check if the subclasses are cached
		 boolean subclassesCached = RemoteClientFrameStore.isCached(cls, cls.getKnowledgeBase().getSystemFrames().getDirectSubclassesSlot(), null, false);
		 if (!subclassesCached) { return false; }
		 Collection<Cls> subclasses = cls.getDirectSubclasses();
		 for (Cls subcls : subclasses) {
			 if (!showHidden() && !subcls.isVisible()) { continue; }
			 if (!RemoteClientFrameStore.isCacheComplete(subcls)) {
				 return false;
			 }
		 }
		 return true;
	 }
	 

	 @Override
	 protected LazyTreeNode createNode(Object o) {
		 return new ClassTreeWithBrowserTextNode(this, (FrameWithBrowserText) o);
	 }

	 @Override   
	 protected int getChildObjectCount() {
		 if(showHidden()) {
			 return getCls().getDirectSubclassCount();
		 } else {
			 /*
			  * Unfortunately here we have to call the getChildObjects method
			  * to get the size of the children.
			  * This method is always called before getChildObjects, so 
			  * the latter will be called twice which is expensive.
			  * That is why we use a hack/optimization that ensures
			  * that the children are retrieved only once, not twice.
			  * For this, I had to make some methods from LazyTreeNode
			  * protected, which I really don't like.
			  * We might want to revist this in future versions.
			  */
			 Collection<FrameWithBrowserText> children = getChildObjects(); 
			 //here come the hack/optimization; don't try this yourself
			 loadChildObjects(children);
			 setIsLoaded(true);
			 return children.size();
		 }
	 }
	 
	 
	 protected Collection<FrameWithBrowserText> getLocalChildren(Cls cls, boolean showHidden) {
		 Collection<Cls> clsChildren = null;  
		 if (showHidden) {
			 clsChildren = new LinkedHashSet<Cls>(cls.getDirectSubclasses());
		 } else {             
			 clsChildren = new LinkedHashSet<Cls>(cls.getVisibleDirectSubclasses());
			 // Remove all equivalent classes that have other superclasses as well
			 if (cls instanceof OWLNamedClass) {
				 Iterator equis = ((OWLNamedClass) cls).getEquivalentClasses().iterator();
				 while (equis.hasNext()) {
					 RDFSClass equi = (RDFSClass) equis.next();
					 if (equi instanceof OWLNamedClass && equi.getSuperclassCount() > 1) {
						 clsChildren.remove(equi);
					 }
				 }
			 }
		 }
		 //TODO - add flag whether to sort?		 
		 List<Cls> clsChildrenList = new ArrayList<Cls>(clsChildren);
		 Collections.sort(clsChildrenList, new FrameComparator<Cls>());
		 List<FrameWithBrowserText> fbtList = new ArrayList<FrameWithBrowserText>();		 
		 for (Cls child : clsChildrenList) {
			 fbtList.add(new FrameWithBrowserText(child, child.getBrowserText(), child.getDirectTypes(),
					 ProtegeUI.getPotentialIconName(child)));
		 }
		 return fbtList;
	 }
 
	 @Override
	 protected Comparator getComparator() {        
		 return null;
	 }


	 public Cls getCls() {
		 FrameWithBrowserText fbt = (FrameWithBrowserText) getUserObject();        
		 return (Cls)fbt.getFrame();
	 }

	 private boolean showHidden() {
		 return getCls().getProject().getDisplayHiddenClasses();
	 }


	 protected void dispose() {  
		 if (log.isLoggable(Level.FINE)) {
			 log.fine(logIntro() + "Disposed!");
		 }
		 detachListeners(getCls());
		 super.dispose();
	 }


	 private String logIntro() {
		return "FrameWBText Node (" + ((FrameWithBrowserText) getUserObject()).getFrame()
									+ ", " + hashCode() + "): ";
	}


	/*
	  * Protege job for computing and sorting children on the server
	  */            

	 private static class GetChildObjectsJob extends ProtegeJob {        
		 private static final long serialVersionUID = -3887527946271511007L;

		 private Cls cls;
		 private boolean showHidden;        

		 public GetChildObjectsJob(KnowledgeBase kb, Cls cls, boolean showHidden) {
			 super(kb);
			 this.cls = cls;
			 this.showHidden = showHidden;
		 }

		 @Override
		 public Object run() throws ProtegeException {
			return getChildrenOnServer();
		 }      
		 
		 
		 protected Collection<FrameWithBrowserText> getChildrenOnServer() {
			 Collection<FrameWithBrowserText> children = new ArrayList<FrameWithBrowserText>();
			 RemoteSession session = ServerFrameStore.getCurrentSession();
			 ServerFrameStore.setCurrentSession(null);
			 Collection<Cls> clsChildren = new HashSet<Cls>();
			 try {				   
				 if (showHidden) {
					 clsChildren = new LinkedHashSet<Cls>(cls.getDirectSubclasses());
				 } else {             
					 clsChildren = new LinkedHashSet<Cls>(cls.getVisibleDirectSubclasses());
					 // Remove all equivalent classes that have other superclasses as well
					 if (cls instanceof OWLNamedClass) {
						 Iterator equis = ((OWLNamedClass) cls).getEquivalentClasses().iterator();
						 while (equis.hasNext()) {
							 RDFSClass equi = (RDFSClass) equis.next();
							 if (equi instanceof OWLNamedClass && equi.getSuperclassCount() > 1) {
								 clsChildren.remove(equi);
							 }
						 }
					 }
				 }				
			} finally {
				ServerFrameStore.setCurrentSession(session);
			}			
			 
			 //TODO - add flag whether to sort?			 
			 List<Cls> clsChildrenList = new ArrayList<Cls>(clsChildren);
			 Collections.sort(clsChildrenList, new FrameComparator<Cls>());			 
			 List<FrameWithBrowserText> fbtList = new ArrayList<FrameWithBrowserText>();			 
			 for (Cls child : clsChildrenList) {
				 addRequestsToFrameCalculator(child);				 
				 fbtList.add(new FrameWithBrowserText(child, child.getBrowserText(), child.getDirectTypes(), 
						 ProtegeUI.getPotentialIconName(child)));
			 }
			 return fbtList;
		 }
		 		 
		//TODO: this can be taken out, if it proves too expensive for the server or client
		 private void addRequestsToFrameCalculator(Frame frm) {
			 if (!getKnowledgeBase().getProject().isMultiUserServer()) {
				 return;
			 }
			 Server server = Server.getInstance();
			 RemoteSession session = ServerFrameStore.getCurrentSession();
			 ServerProject serverProject = server.getServerProject(getKnowledgeBase().getProject());
			 ServerFrameStore serverFrameStore = (ServerFrameStore) serverProject.getDomainKbFrameStore(session);
			 FrameCalculator fc = serverFrameStore.getFrameCalculator();

			 fc.addRequest(frm, session, CacheRequestReason.USER_REQUESTED_FRAME_VALUES);        
		 }

		 @Override
		 public void localize(KnowledgeBase kb) {
			 super.localize(kb);
			 LocalizeUtils.localize(cls, kb);
		 }

	 } 

	 /* End Protege Job */
 
	 	 
}

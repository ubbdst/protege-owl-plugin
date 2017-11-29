package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.ServerProject;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.server.framestore.background.CacheRequestReason;
import edu.stanford.smi.protege.server.framestore.background.FrameCalculator;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import java.util.Collection;

public class OWLGetOwnSlotValuesJob extends ProtegeJob {


    private static final long serialVersionUID = 5135524417428952394L;
    private static long DEFAULT_LIMIT = 1000;
    private Frame frame;
    private Slot slot;
    private boolean directValues;
    private Collection values;

    public OWLGetOwnSlotValuesJob(KnowledgeBase kb, Frame frame, Slot slot, boolean directValues) {
        super(kb);
        this.frame = frame;
        this.slot = slot;
        this.directValues = directValues;
        this.values = getValues();
    }


    protected Collection getValues() {
        if (frame instanceof RDFResource && slot instanceof RDFProperty) {
            Collection values = ((RDFResource) frame).getHasValuesOnTypes((RDFProperty) slot);
            //Collection properties =  ((RDFResource)frame).getPropertyValues((RDFProperty)slot);
            Collection properties = InstanceUtil.getPropertyValues((RDFResource) frame, (RDFProperty) slot, directValues);
            System.out.println("Get values is called for " + frame.getName() + " on " + slot.getName() + " with size " + properties.size());
            //int listSize = properties.size();

            //If maximum limit is exceeded
                /*if(listSize > DEFAULT_LIMIT) {
                    int count = 0;

					if(!SubjectPredicateStore.exists((RDFResource)frame, (RDFProperty)slot)) {
                        showMessageDialog("Too many objects to show in the slot \"" + slot.getBrowserText() + "\"" +
                                " of instance \"" + frame.getBrowserText() + "\". " + "Currently showing only " + DEFAULT_LIMIT + " out of " + listSize);
                    }
                    //Add to the store
					SubjectPredicateStore.add((RDFResource)frame, (RDFProperty)slot);

					for (Object o : properties) {
						values.add(o);
					    count ++;
					    if(count == DEFAULT_LIMIT) {
					    	return values;
						}

					}
				}*/
            values.addAll(properties);
            return values;
        }
        return getFrameValues();
    }


    private Collection getFrameValues() {
        return this.directValues ? this.frame.getDirectOwnSlotValues(this.slot) : this.frame.getOwnSlotValues(this.slot);
    }


    //Original
	/*@Override
	protected Collection getValues() {
	    if (frame instanceof RDFResource && slot instanceof RDFProperty) {
			Collection values = ((RDFResource)frame).getHasValuesOnTypes((RDFProperty)slot);
			values.addAll(super.getValues());
			return values;
		} else {
			return super.getValues();
		}
	}*/


    @Override
    public Collection execute() throws ProtegeException {
        return (Collection) super.execute();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Collection run() throws ProtegeException {
        addRequestsToFrameCalculator(this.frame);
        //Collection values = this.getValues();

            /*for (Object var4 : values) {
                if (var4 instanceof Frame) {
                    Frame var5 = (Frame) var4;
                    //var1.add(new FrameWithBrowserText(var5, var5.getBrowserText(), ((Instance) var5).getDirectTypes()));
                    var1.add(FrameWithBrowserText.getFrameWithBrowserText(var5));
                    this.addRequestsToFrameCalculator(var5);
                } /*else {
			   var1.add(new FrameWithBrowserText(null, var4.toString(), null));
		   }
            }
            Collections.sort(var1, new FrameWithBrowserTextComparator());
            */
        return values;
    }



    private void addRequestsToFrameCalculator(Frame var1) {
        if (var1 != null && this.getKnowledgeBase().getProject().isMultiUserServer()) {
            Server var2 = Server.getInstance();
            RemoteSession var3 = ServerFrameStore.getCurrentSession();
            ServerProject var4 = var2.getServerProject(this.getKnowledgeBase().getProject());
            ServerFrameStore var5 = (ServerFrameStore) var4.getDomainKbFrameStore(var3);
            FrameCalculator var6 = var5.getFrameCalculator();
            var6.addRequest(var1, var3, CacheRequestReason.USER_REQUESTED_FRAME_VALUES);
        }
    }


    @Override
    public void localize(KnowledgeBase var1) {
        super.localize(var1);
        LocalizeUtils.localize(this.frame, var1);
        LocalizeUtils.localize(this.slot, var1);
    }

    /**
     * Display popup error window with a given message
     */
    private void showMessageDialog(String msg) {
        //Display error message in a popup window
        ProtegeUI.getModalDialogFactory().showMessageDialog((OWLModel) getKnowledgeBase(), msg);
    }

    /**
     * Get objects
     */
    public Collection getObjects() {
        return values;
    }
}


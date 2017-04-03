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

package edu.stanford.smi.protegex.owlx.examples.javaDemo.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Customer;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Product;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Purchase;

import java.util.Iterator;

public class DefaultCustomer extends DefaultCustomer_
        implements Customer {

    public DefaultCustomer(OWLModel owlModel, FrameID id) {
        super(owlModel, id);
    }


    public DefaultCustomer() {
    }


    public String getBrowserText() {
        return getFirstName() + " " + getLastName();
    }


    public float getPurchasesSum() {
        float sum = 0;
        Iterator purchases = listPurchases();
        while (purchases.hasNext()) {
            Purchase purchase = (Purchase) purchases.next();
            Product product = purchase.getProduct();
            sum += product.getPrice();
        }
        return sum;
    }
}

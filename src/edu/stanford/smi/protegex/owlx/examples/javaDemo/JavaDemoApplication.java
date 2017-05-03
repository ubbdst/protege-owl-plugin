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

package edu.stanford.smi.protegex.owlx.examples.javaDemo;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Customer;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.MyFactory;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Product;
import edu.stanford.smi.protegex.owlx.examples.javaDemo.model.Purchase;

import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JavaDemoApplication {

    public static void main(String[] args) throws Exception {
        String uri = "http://www.owl-ontologies.com/javaDemo.owl";
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);

        // Create a couple of products
        OWLNamedClass productClass = owlModel.getOWLNamedClass("Product");
        Product productA = (Product) productClass.createOWLIndividual("ProdcutA").as(Product.class);
        productA.setPrice(41.99f);
        Product productB = (Product) productClass.createOWLIndividual("ProductB").as(Product.class);
        productB.setPrice(1.50f);
        Product productC = (Product) productClass.createOWLIndividual("ProductC").as(Product.class);
        productC.setPrice(2.50f);

        OWLNamedClass customerClass = owlModel.getOWLNamedClass("Customer");
        Customer customer = (Customer) customerClass.createOWLIndividual("Hans").as(Customer.class);
        customer.setFirstName("Hans");
        customer.setLastName("Aldi");
        createPurchase(customer, productA, "2005-01-01");
        createPurchase(customer, productB, "2005-01-02");
        createPurchase(customer, productC, "2005-01-03");
        createPurchase(customer, productA, "2005-02-07");

        double sum = customer.getPurchasesSum();
        System.out.println("Customer " + customer.getBrowserText() + " has spent $" + sum);
    }


    private static Purchase createPurchase(Customer customer, Product product, String date) {
        OWLModel owlModel = customer.getOWLModel();
        Purchase purchase = new MyFactory(owlModel).createPurchase(null);
        purchase.setCustomer(customer);
        purchase.setProduct(product);
        RDFSDatatype xsdDate = owlModel.getRDFSDatatypeByName("xsd:date");
        purchase.setDate(owlModel.createRDFSLiteral(date, xsdDate));
        return purchase;
    }


    // Complicating version without model classes
    private static float getPurchasesSum(RDFIndividual customer) {
        OWLModel owlModel = customer.getOWLModel();
        float sum = 0;
        RDFProperty purchasesProperty = owlModel.getRDFProperty("purchases");
        RDFProperty productProperty = owlModel.getRDFProperty("product");
        RDFProperty priceProperty = owlModel.getRDFProperty("price");
        Iterator purchases = customer.listPropertyValues(purchasesProperty);
        while (purchases.hasNext()) {
            RDFIndividual purchase = (RDFIndividual) purchases.next();
            RDFIndividual product = (RDFIndividual) purchase.getPropertyValue(productProperty);
            Float price = (Float) product.getPropertyValue(priceProperty);
            sum += price.floatValue();
        }
        return sum;
    }
}

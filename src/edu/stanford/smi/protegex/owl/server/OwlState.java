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

package edu.stanford.smi.protegex.owl.server;


import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.server.framestore.background.ServerCachedState;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;



public enum OwlState implements ServerCachedState {
  Start, SubClass, OwlExpr, RDFList, UserIndividual, End;
  
  public static boolean allowTransition(FrameStore fs, OwlState startState, Frame startFrame, OwlState endState, Frame endingFrame) {
    switch (endState) {
    case OwlExpr:
      return isOWLAnonymous(fs, endingFrame);
    case SubClass:
      return !isOWLAnonymous(fs, endingFrame);
    case UserIndividual:
        return startFrame instanceof OWLNamedClass && !isOWLAnonymous(fs, startFrame) && !startFrame.isSystem();
    default:
      return true;
    }
  }
  
  private static boolean isOWLAnonymous(FrameStore fs, Frame f) {
      synchronized (f.getKnowledgeBase()) {
          return fs.getFrameName(f).startsWith("@");
      }
  }
}

# Protege OWL Plugin 3.6
The original Protege 3.5 OWL plugin with some customizations from the University of Bergen Library. 

## Customizations include :

- Make use of UUID for the individual URI
- Make Individual URI not editable
- Create <code>UUIDWidget</code> that automatically stores instance UUID.
- Create <code>ClassHierarchyURIWidget</code> that holds hierarchical URI for an instance
- Create <code>UBBSignatureWidget</code> that stores identifier for a particular instance. This widget communicates with <code>ClassHierarchyURIWidget</code> on value change. Identifier is validated by checking the entire knowledgebase for the uniqueness.
- Modify some instance properties for the copied individual
- Add some utility classes for convenience methods
- Add external UBB plugins to the core codes so that we don't have to install them separately
- Solve some bugs in the original OWL 3.5 plugin, see https://prosjekt.uib.no/issues/7757 


## Original source codes 
For Protege Core 3.5 : http://smi-protege.stanford.edu/svn/

For Protege OWL Plugin 3.5 : http://smi-protege.stanford.edu/repos/protege/owl/trunk/

##Installation

Download a final release from https://github.com/ubbdst/protege-owl-plugin/releases and extract it to the
Protege OWL plugin directory. You find it with a name <code>"edu.stanford.smi.protegex.owl"</code> under <code>"plugins"</code> folder in your Protege installation directory.

Note that, if you are running Protege server-client mode, you need to copy the file both to the server and to all of your clients. 


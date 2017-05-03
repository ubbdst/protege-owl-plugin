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

package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.repository.util.FileInputSource;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 3, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FTPRepository extends AbstractStreamBasedRepositoryImpl {
    private static transient final Logger log = Log.getLogger(FTPRepository.class);
    private URI ftpURI;

    private URI projectDirectory;

    private File localCopy;

    private URI ontologyName;

    private static FTPRepositoryPasswordProvider passwordProvider;


    static {
        passwordProvider = new FTPRepositoryPasswordProvider() {
            public String getUserName() {
                return null;
            }


            public String getPassword() {
                JPasswordField pwf = new JPasswordField();
                LabeledComponent lc = new LabeledComponent("FTP Password", pwf);
                JOptionPane.showMessageDialog(null, lc, "Password", JOptionPane.INFORMATION_MESSAGE);
                return new String(pwf.getPassword());
            }
        };
    }


    public FTPRepository(URI ftpURI, URI projectDirectory) {
        this.ftpURI = ftpURI;
        this.projectDirectory = projectDirectory;
        setLocalFile();
    }


    public boolean contains(URI ontologyName) {
        if (this.ontologyName != null) {
            return this.ontologyName.equals(ontologyName);
        }
        else {
            return false;
        }
    }


    private void setLocalFile() {
        try {
            File f = new File(ftpURI.getPath());
            URI localURI = new URI(new URL(projectDirectory.toURL(), f.getName()).toString());
            localCopy = new File(localURI);
        }
        catch (URISyntaxException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        catch (MalformedURLException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void ftpGet() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setRemoteHost(ftpURI.getHost());
            ftpClient.connect();
            if (passwordProvider != null) {
                ftpClient.login(ftpURI.getUserInfo(), passwordProvider.getPassword());
            }
            File f = new File(ftpURI.getPath());
            ftpClient.chdir(f.getParentFile().toString());
            FileOutputStream fos = new FileOutputStream(localCopy);
            ftpClient.get(fos, f.getName());
            ftpClient.quit();
            OntologyNameExtractor extractor = new OntologyNameExtractor(new FileInputSource(localCopy));
            ontologyName = extractor.getOntologyName();
            log.info("ftp get = " + extractor.getOntologyName());
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        catch (FTPException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void ftpPut() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setRemoteHost(ftpURI.getHost());
            ftpClient.connect();
            if (passwordProvider != null) {
                ftpClient.login(ftpURI.getUserInfo(), passwordProvider.getPassword());
            }
            File f = new File(ftpURI.getPath());
            ftpClient.chdir(f.getParentFile().toString());
            FileInputStream fis = new FileInputStream(localCopy);
            ftpClient.put(fis, f.getName());
            ftpClient.quit();
            log.info("Put!");

        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        catch (FTPException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void refresh() {
        ftpGet();
    }


    public Collection<URI> getOntologies() {
        if (ontologyName != null) {
            return Collections.singleton(ontologyName);
        }
        else {
            return Collections.emptyList();
        }
    }


    @Override
    public InputStream getInputStream(URI ontologyName)
            throws OntologyLoadException {
        if (contains(ontologyName)) {
            if (isWritable(ontologyName)) {
                try {
					return new FileInputStream(localCopy);
				} catch (FileNotFoundException e) {
					throw new OntologyLoadException(e, "Could not find file: " + localCopy);
				}
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }


    public boolean isWritable(URI ontologyName) {
        if (contains(ontologyName)) {
            if (localCopy.exists() && localCopy.canWrite()) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
        if (contains(ontologyName)) {
            if (isWritable(ontologyName)) {
                return new FileOutputStream(localCopy);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }


    public boolean isSystem() {
        return false;
    }


    public String getRepositoryDescription() {
        return "FTP Repository (" + ftpURI + ")";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        if (localCopy != null) {
            return localCopy.toString();
        }
        else {
            return "";
        }
    }


    public String getRepositoryDescriptor() {
        return ftpURI.toString();
    }


    public static void setFTPRepositoryPasswordProvider(FTPRepositoryPasswordProvider provider) {
        passwordProvider = provider;
    }


    public static void main(String [] args) {
        try {
            FTPRepositoryPasswordProvider pwp = new FTPRepositoryPasswordProvider() {
                public String getUserName() {
                    return null;
                }


                public String getPassword() {
                    JPasswordField pwd = new JPasswordField();
                    JOptionPane.showMessageDialog(null, pwd, "Password", JOptionPane.INFORMATION_MESSAGE);
                    return new String(pwd.getPassword());
                }
            };
            FTPRepository.setFTPRepositoryPasswordProvider(pwp);
            String s = "ftp://horridgm@kiss.cs.man.ac.uk:21/home/M02/cc/horridgm/ontologies/TeachingFlattened.owl";
            URI uri = new URI(s);
            System.out.println("Scheme: " + uri.getScheme());
            System.out.println("Host: " + uri.getHost());
            System.out.println("Port: " + uri.getPort());
            System.out.println("UserInfo: " + uri.getUserInfo());
            System.out.println("Path: " + uri.getPath());
            File f = new File(uri.getPath());
            System.out.println("FileName: " + f.getName());
            System.out.println("FileDirectory: " + f.getParentFile());
            URI pd = new File("/Users/matthewhorridge/Desktop").toURI();
            FTPRepository rep = new FTPRepository(uri, pd);
            rep.ftpGet();
            rep.ftpPut();
        }
        catch (URISyntaxException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }
}


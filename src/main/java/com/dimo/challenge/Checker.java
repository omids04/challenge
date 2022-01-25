package com.dimo.challenge;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.helper.OCLHelper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checker {

    private static final Checker instance = new Checker();
    private Resource metaModel;
    protected EPackage ePackage;
    private int numberOfMetaModelElements;
    private Map<String, Constraint> oclConstraints;

    private Checker(){
        loadMetamodel();
        loadOclRules();
    }

    public static Checker getInstance(){
        return instance;
    }

    public boolean check(EObject root){
        OCL ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
        for (String name : oclConstraints.keySet()){
            Constraint constraint = oclConstraints.get(name);
            OCLHelper helper = ocl.createOCLHelper();
            helper.setContext(root.eClass());
            Query query = ocl.createQuery(constraint);
            if (root.eClass().equals(constraint.getConstrainedElements().get(0)) && !query.check(root)){
                System.err.println("Ops! Constraint "+ constraint.getName()
                        + " does not get satisfied by EObject: " + root);
                return false;
            }
        }
        for (EObject eObject : root.eContents()){
            return check(eObject);
        }
        return true;
    }

    private void loadMetamodel() { // loads the input metamodel
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                    .put("ecore", new EcoreResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                    .put("xmi", new XMIResourceFactoryImpl());
            Resource myMetaModel = resourceSet
                    .getResource(URI.createFileURI(this.getClass().getClassLoader().getResource("metamodel/people.ecore").getPath()),true);
            registerPackages(myMetaModel);
            EPackage univEPackage = (EPackage)
                    myMetaModel.getContents().get(0);
            this.metaModel = myMetaModel;
            this.ePackage = univEPackage;
            this.numberOfMetaModelElements = univEPackage.getEClassifiers().size();
            System.out.println("Problem.metaModel is loaded!");
        } catch (Exception ex) {
            System.out.println("Unable to load the Metamodel");
            throw ex;
        }
    }

    private void loadOclRules() {
        OCL ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
        InputStream in = null;
        try {
            in = new FileInputStream(this.getClass().getClassLoader().getResource("metamodel/people.ocl").getPath());
            Map<String, Constraint> constraintMap = new HashMap<>();
        // parse the contents as an OCL document
            OCLInput document = new OCLInput(in);
            List<Constraint> constraints = ocl.parse(document);
            for (Constraint next : constraints) {
                constraintMap.put(next.getName(), next);
            }
            in.close();
            this.oclConstraints = constraintMap;
        } catch (Exception ex) {
            System.out.printf("Error in parsing OCL file, The OCL check is ignored! The exception: ", ex.getStackTrace());
            this.oclConstraints = null;
        }
    }

    private void registerPackages(Resource resource){
        EObject eObject = resource.getContents().get(0);
        if(eObject instanceof EPackage)
        {
            EPackage p = (EPackage) eObject;
            EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
        }
    }
}

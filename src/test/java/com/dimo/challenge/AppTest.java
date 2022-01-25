package com.dimo.challenge;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {


    @Test
    void modelWithNoPeopleTest(){
        //given
        EObject modelRoot = getModelRoot("Model1");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertFalse(result);
    }

    @Test
    void personWithNoAgeSetTest(){
        //given
        EObject modelRoot = getModelRoot("Model2");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertFalse(result);
    }

    @Test
    void personWithAgeLessThan5Test(){
        //given
        EObject modelRoot = getModelRoot("Model3");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertFalse(result);
    }

    @Test
    void personWithLessThan3LuckyNumbersTest(){
        //given
        EObject modelRoot = getModelRoot("Model4");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertFalse(result);
    }

    @Test
    void personWith3LuckyNumbersAndAge15SetTest(){
        //given
        EObject modelRoot = getModelRoot("Model5");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertTrue(result);
    }

    @Test
    void personWith5LuckyNumbersAndAge3AndNameBabyTest(){
        //given
        EObject modelRoot = getModelRoot("Model6");

        //when
        boolean result = Checker.getInstance().check(modelRoot);

        //then
        assertTrue(result);
    }


    EObject getModelRoot(String modelName){
        EPackage.Registry.INSTANCE.put(Checker.getInstance().ePackage.getNsURI(), Checker.getInstance().ePackage);
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("xmi", new XMIResourceFactoryImpl());
        String pathToModel = this.getClass().getClassLoader().getResource("models/"+modelName+".xmi").getPath();
        return resourceSet
                .getResource(URI.createFileURI(pathToModel), true)
                .getContents()
                .get(0);
    }

}
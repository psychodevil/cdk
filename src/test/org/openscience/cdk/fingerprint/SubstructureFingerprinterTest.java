/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.fingerprint;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.AminoAcids;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AminoAcidManipulator;

/**
 * @cdk.module test-experimental
 */
public class SubstructureFingerprinterTest extends NewCDKTestCase {
	
	@Test public void testFunctionalGroups() throws Exception {
		BitSet bitset = null;
		IAtomContainerSet set = null;
		set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();

		IFingerprinter printer = new SubstructureFingerprinter(set);
		IMolecule pinene = MoleculeFactory.makeAlphaPinene();
		bitset = printer.getFingerprint(pinene);
		
		Assert.assertNotNull(set);
		Assert.assertNotNull(bitset);
		// none of the functional groups is found in this molecule
		for (int i=0; i<set.getAtomContainerCount(); i++) {
			Assert.assertFalse(bitset.get(i));
		}
	}
	
	public void testFunctionalGroups_matchAll() throws Exception {
		BitSet bitset = null;
		IAtomContainerSet set = null;
		set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();

		IFingerprinter printer = new SubstructureFingerprinter(set);
		IMolecule matchesAll = new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance()).parseSmiles("C(C(=O)O)C(N([H])[H])C(O[H])C(COC)C(C(=O)[H])C(S(=O)(=O)O)C(P(=O)(=O)O)");
		bitset = printer.getFingerprint(matchesAll);
		System.out.println("BitSet: " + bitset);
		
		Assert.assertNotNull(set);
		Assert.assertNotNull(bitset);
		// all funtional groups are found in this molecule
		Assert.assertEquals(set.getAtomContainerCount(), bitset.cardinality());
	}
	
	public void testAminoAcids() throws Exception {
		BitSet bitset = null;
		IAtomContainerSet set = null;
		set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();

		IAminoAcid[] aas = AminoAcids.createAAs();
		IFingerprinter printer = new SubstructureFingerprinter(set);

		Assert.assertNotNull(set);

		// test whether all molecules have an amine and carboxylic acid group
		for (int i=0; i<aas.length; i++) {
			AminoAcidManipulator.addAcidicOxygen(aas[i]);
			IMolecule aminoAcid = aas[i].getBuilder().newMolecule(aas[i]);
			addExplicitHydrogens(aminoAcid);

			Assert.assertNotNull(aminoAcid);
			bitset = printer.getFingerprint(aminoAcid);
			Assert.assertNotNull(bitset);
			System.out.println("AA: " + aas[i].getProperty(AminoAcids.RESIDUE_NAME));
			System.out.println(" -> " + bitset);
			Assert.assertTrue(bitset.get(0)); // carboxylic acid group
			if (!aas[i].getProperty(AminoAcids.RESIDUE_NAME).equals("PRO"))
				Assert.assertTrue(bitset.get(1)); // amine group
		}			
	}
}


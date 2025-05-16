package lipid;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AdductDetectionTest {


    @Before
    public void setup() {
    }

    @Test
    public void shouldDetectAdductBasedOnMzDifference() {

        Peak mH = new Peak(700.500, 100000.0); // [M+H]+
        Peak mNa = new Peak(722.482, 80000.0);  // [M+Na]+
        Lipid lipid = new Lipid(1, "PC 34:1", "C42H82NO8P", LipidType.PC, 34, 1);

        double annotationMZ = 700.49999d;
        double annotationIntensity = 80000.0;
        double annotationRT = 6.5d;
        Annotation annotation = new Annotation(lipid, annotationMZ, annotationIntensity, annotationRT,  Set.of(mH, mNa),IoniationMode.POSITIVE );


        assertNotNull("[M+H]+ should be detected", annotation.getAdduct());
        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+", annotation.getAdduct());
    }


    @Test
    public void shouldDetectLossOfWaterAdduct() {
        Peak mh = new Peak(700.500, 90000.0);        // [M+H]+
        Peak mhH2O = new Peak(682.4894, 70000.0);     // [M+H–H₂O]+, ~18.0106 Da less

        Lipid lipid = new Lipid(1, "PE 36:2", "C41H78NO8P", LipidType.PE, 36, 2);
        Annotation annotation = new Annotation(lipid, mh.getMz(), mh.getIntensity(), 7.5d,Set.of(mh, mhH2O) , IoniationMode.POSITIVE);



        assertNotNull("[M+H]+ should be detected", annotation.getAdduct());

        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+", annotation.getAdduct());
    }

    @Test
    public void shouldDetectDoublyChargedAdduct() {
        Peak singlyCharged = new Peak(700.500, 100000.0);  // [M+H]+
        Peak doublyCharged = new Peak(350.754, 85000.0);   // [M+2H]2+

        Lipid lipid = new Lipid(3, "TG 54:3", "C57H104O6", LipidType.TG, 54, 3);
        Annotation annotation = new Annotation(lipid, singlyCharged.getMz(), singlyCharged.getIntensity(), 10d, Set.of(singlyCharged, doublyCharged), IoniationMode.POSITIVE);

        assertNotNull("[M+H]+ should be detected", annotation.getAdduct());

        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+", annotation.getAdduct());
    }


    @Test
    public void shouldDetectNegativeAdductBasedOnMzDifference() {
        Peak mH = new Peak(698.4854, 100000.0); // [M–H]⁻
        Peak mCl = new Peak(734.462, 80000.0);  // [M+Cl]⁻, diferencia de ~34.9694 Da

        Lipid lipid = new Lipid(1, "PE 36:2", "C41H78NO8P", LipidType.PE, 36, 2);
        Annotation annotation = new Annotation(lipid, mH.getMz(), mH.getIntensity(), 7.5d, Set.of(mH, mCl), IoniationMode.NEGATIVE);

        System.out.println(annotation.getAdduct());

        assertNotNull("Debería detectar un aducto", annotation.getAdduct());
        assertEquals("Adducto inferido a partir del mz más bajo en el grupo", "[M-H]−", annotation.getAdduct());
    }

    @Test
    public void shouldDetectDimerizationAndBaseMonomer() {
        Peak monomer = new Peak(700.500, 100000.0);
        Peak dimer = new Peak(1399.993, 50000.0);

        Lipid lipid = new Lipid(5, "PC 34:2", "C42H80NO8P", LipidType.TG, 34, 2);

        Annotation annotation = new Annotation(lipid, monomer.getMz(), monomer.getIntensity(), 7.2, Set.of(monomer, dimer), IoniationMode.POSITIVE);

        assertNotNull("Debe detectar el aducto principal", annotation.getAdduct());
        assertEquals("Debe ser [M+H]+ como base", "[M+H]+", annotation.getAdduct());
    }

    @Test
    public void shouldDetectDimerAdductAmongNegativeOptions() {
        Peak p1 = new Peak(349.2427, 80000.0);   // [M-H]⁻
        Peak p2 = new Peak(331.2321, 60000.0);   // [M-H-H2O]⁻
        Peak p3 = new Peak(385.2190, 55000.0);   // [M+Cl]⁻
        Peak p4 = new Peak(395.2480, 58000.0);   // [M+HCOOH-H]⁻
        Peak p5 = new Peak(699.4927, 70000.0);   // [2M-H]⁻

        Lipid lipid = new Lipid(7, "FA 18:1", "C18H34O2", LipidType.FA, 18, 1);

        Annotation annotation = new Annotation(lipid, p5.getMz(), p5.getIntensity(), 10.0, Set.of(p1, p2, p3, p4, p5), IoniationMode.NEGATIVE);

        assertEquals("[2M-H]−", annotation.getAdduct());
    }

}

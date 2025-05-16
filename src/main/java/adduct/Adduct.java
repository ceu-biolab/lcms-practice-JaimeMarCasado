package adduct;

public class Adduct {

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param mz mz
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     *
     * @return the monoisotopic mass of the experimental mass mz with the adduct @param adduct
     */

    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        if (mz == null || adduct == null || adduct.isEmpty()) {
            return null;
        }

        Double adductMass = AdductList.MAPMZPOSITIVEADDUCTS.getOrDefault(adduct, AdductList.MAPMZNEGATIVEADDUCTS.get(adduct));
        if (adductMass == null) {
            return null;
        }

        int charge = 1;
        java.util.regex.Matcher chargeMatcher = java.util.regex.Pattern.compile("([2-9])([+-])").matcher(adduct);
        if (chargeMatcher.find()) {
            charge = Integer.parseInt(chargeMatcher.group(1));
        }

        int multimer = 1;
        java.util.regex.Matcher multimerMatcher = java.util.regex.Pattern.compile("\\[([2-9])M").matcher(adduct);
        if (multimerMatcher.find()) {
            multimer = Integer.parseInt(multimerMatcher.group(1));
        }

        double numerator = (mz * charge) + adductMass;

        return numerator / multimer;
    }

    public static Double getMzFromMonoisotopicMass(Double monoisotopicMass, String adductName) {
        if (monoisotopicMass == null || adductName == null) {
            return null;
        }

        Double adductMass = AdductList.MAPMZPOSITIVEADDUCTS.get(adductName);

        if (adductMass == null) {
            adductMass = AdductList.MAPMZNEGATIVEADDUCTS.get(adductName);
        }
        if (adductMass == null) {
            return null;
        }

        int multimer = 1;
        if (adductName.startsWith("[2M")) {
            multimer = 2;
        }

        int charge = 1;
        if (adductName.endsWith("2+") || adductName.endsWith("2-")) {
            charge = 2;
        }

        return ((monoisotopicMass * multimer) - adductMass) / charge;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass    Mass measured by MS
     * @param theoreticalMass Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double experimentalMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((experimentalMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param measuredMass    Mass measured by MS
     * @param ppm ppm of tolerance
     */
    public static double calculateDeltaPPM(Double experimentalMass, int ppm) {
        double deltaPPM;
        deltaPPM =  Math.round(Math.abs((experimentalMass * ppm) / 1000000));
        return deltaPPM;

    }




}

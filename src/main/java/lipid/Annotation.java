package lipid;

import java.util.*;

import adduct.Adduct;
import adduct.AdductList;

/**
 * Class to represent the annotation over a lipid
 */
public class Annotation {

    private final Lipid lipid;
    private final double mz;
    private final double intensity;
    private final double rtMin;
    private String adduct;
    private final Set<Peak> groupedSignals;
    private int score;
    private int totalScoresApplied;
    private static final double PPM_TOLERANCE = 10;
    private final IoniationMode ionMode;

    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IoniationMode ionMode) {
        this(lipid, mz, intensity, retentionTime, Collections.emptySet(), ionMode);
    }

    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param groupedSignals
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, Set<Peak> groupedSignals, IoniationMode ionMode) {
        this.lipid = lipid;
        this.mz = mz;
        this.rtMin = retentionTime;
        this.intensity = intensity;
        this.groupedSignals = new TreeSet<>(groupedSignals);
        this.ionMode = ionMode;
        detectAdduct();
        this.score = 0;
        this.totalScoresApplied = 0;
    }

    public Lipid getLipid() {
        return lipid;
    }

    public double getMz() {
        return mz;
    }

    public double getRtMin() {
        return rtMin;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public double getIntensity() {
        return intensity;
    }

    public Set<Peak> getGroupedSignals() {
        return Collections.unmodifiableSet(groupedSignals);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int delta) {
        this.score += delta;
        this.totalScoresApplied++;
    }

    public double getNormalizedScore() {
        return (double) this.score / this.totalScoresApplied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return Double.compare(that.mz, mz) == 0 &&
                Double.compare(that.rtMin, rtMin) == 0 &&
                Objects.equals(lipid, that.lipid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lipid, mz, rtMin);
    }

    @Override
    public String toString() {
        return String.format("Annotation(%s, mz=%.4f, RT=%.2f, adduct=%s, intensity=%.1f, score=%d)",
                lipid.getName(), mz, rtMin, adduct, intensity, score);
    }


    /**
     * Method to detect an adduct based on reference peak
     */
    public void detectAdduct() {
        double referenceMz = this.mz;

        if (this.ionMode == IoniationMode.POSITIVE) {
            for (String candidateAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                Double referenceMonoisotopicMass = Adduct.getMonoisotopicMassFromMZ(referenceMz, candidateAdduct);

                for (String otherAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                    if (otherAdduct.equals(candidateAdduct)) continue;

                    for (Peak peak : groupedSignals) {
                        Double otherPeakMonoIsotopicMass = Adduct.getMonoisotopicMassFromMZ(peak.getMz(), otherAdduct);
                        int error = Adduct.calculatePPMIncrement(referenceMonoisotopicMass, otherPeakMonoIsotopicMass);

                        if (error < PPM_TOLERANCE) {
                            this.adduct = candidateAdduct;
                            return;
                        }
                    }
                }
            }
        } else if (this.ionMode == IoniationMode.NEGATIVE) {
            for (String candidateAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                Double referenceMonoisotopicMass = Adduct.getMonoisotopicMassFromMZ(referenceMz, candidateAdduct);

                for (String otherAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                    if (otherAdduct.equals(candidateAdduct)) continue;

                    for (Peak peak : groupedSignals) {
                        Double otherPeakMonoIsotopicMass = Adduct.getMonoisotopicMassFromMZ(peak.getMz(), otherAdduct);

                        int error = Adduct.calculatePPMIncrement(referenceMonoisotopicMass, otherPeakMonoIsotopicMass);

                        if (error < PPM_TOLERANCE) {
                            this.adduct = candidateAdduct;
                            return;
                        }
                    }
                }
            }
        }
    }
}
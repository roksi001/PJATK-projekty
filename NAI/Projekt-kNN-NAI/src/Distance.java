public class Distance implements Comparable<Distance> {
    public Observation trainObsv;
    public Observation testObsv;

    public double distanceValue;
    public Distance(Observation trainObsv, Observation testObsv, double distanceValue) {
        this.trainObsv = trainObsv;
        this.testObsv = testObsv;
        this.distanceValue = distanceValue;
    }
    @Override
    public int compareTo(Distance distance) {
        return Double.compare(this.distanceValue, distance.distanceValue);
    }

}

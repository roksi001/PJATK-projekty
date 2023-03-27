import java.util.List;

public class Observation {
    public List<Double> attributes;
    public String type;
    public Observation(List<Double> attributes,String type) {
        this.attributes = attributes;
        this.type = type;
    }
}


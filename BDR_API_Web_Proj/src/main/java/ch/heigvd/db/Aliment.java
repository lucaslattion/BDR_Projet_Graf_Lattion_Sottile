package ch.heigvd.db;

public class Aliment {
    public String anom;
    public int kcal;
    public double proteines;
    public double glucides;
    public double lipides;
    public double fibres;
    public double sodium;

    public String groupe;

    public Aliment() {
        // Empty constructor for serialisation/deserialization
    }
}

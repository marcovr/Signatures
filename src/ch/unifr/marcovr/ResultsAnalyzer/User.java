package ch.unifr.marcovr.ResultsAnalyzer;

import java.util.List;

public class User {
    public List<Signature> signatures;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}

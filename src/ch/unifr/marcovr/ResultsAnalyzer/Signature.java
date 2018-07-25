package ch.unifr.marcovr.ResultsAnalyzer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;

public class Signature {
    public GxlRoot gxl;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}

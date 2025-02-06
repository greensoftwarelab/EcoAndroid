package leaks.results;

import leaks.analysis.IFDSRLAnalysis;
import leaks.analysis.RLAnalysis;
import leaks.analysis.VascoRLAnalysis;

public interface IResultsProcessor {
    void visit(RLAnalysis analysis);
    void visit(VascoRLAnalysis analysis);
    void visit(IFDSRLAnalysis ifdsrlAnalysis);
}

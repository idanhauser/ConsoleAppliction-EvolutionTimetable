package data.transfer.objects;

import evolution.engine.Population;
import javafx.util.Pair;
import time.table.problem.configurations.Crossover;
import time.table.problem.configurations.EvolutionEngineInfo;
import time.table.problem.configurations.Selection;
import time.table.problem.configurations.mutation.Mutation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class DTOEngineInformation<T> implements Serializable {
    private static final long serialVersionUID = 100L;

    private int m_InitialPopulation;
    private Crossover m_Crossover;
    private Selection m_Selection;
    private List<Mutation> m_Mutations;
    //private EvolutionEngineInfo m_TheEngine = null;
    private List<T> m_BestResult = null;
    private int m_BestFitness;
    private int m_BestGen;
    private Map<Integer, Pair<Integer, Population>> m_OldPopulationList;

    public DTOEngineInformation(int i_InitialPopulation, Crossover i_Crossover, Selection i_Selection, List<Mutation> i_Mutations) {
        m_InitialPopulation = i_InitialPopulation;
        m_Crossover = i_Crossover;
        m_Selection = i_Selection;
        m_Mutations = i_Mutations;

    }
    public DTOEngineInformation(int i_InitialPopulation, Crossover i_Crossover, Selection i_Selection, List<Mutation> i_Mutations, int i_BestFitness, int i_BestGen) {
        m_InitialPopulation = i_InitialPopulation;
        m_Crossover = i_Crossover;
        m_Selection = i_Selection;
        m_Mutations = i_Mutations;
        m_BestFitness = i_BestFitness;
        m_BestGen = i_BestGen;
    }

    public DTOEngineInformation(EvolutionEngineInfo i_Engine) {
        //m_TheEngine = i_Engine;
        m_Crossover = i_Engine.getCrossover();
        m_Selection = i_Engine.getSelection();
        m_Mutations = i_Engine.getMutations();
    }

    public int getInitialPopulation() {
        return m_InitialPopulation;
    }

    public Crossover getCrossOver() {
        return m_Crossover;
    }

    public Selection getSelection() {
        return m_Selection;
    }

    public List<Mutation> getMutations() {
        return m_Mutations;
    }

    public List<T> getBestResult() {
        return m_BestResult;
    }

    public void setBestResult(List<T> i_BestResult) {
        m_BestResult = i_BestResult;
    } //sync

    public Map<Integer, Pair<Integer, Population>> getOldPopulationList() {
        return m_OldPopulationList;
    }

    public int getBestFitness() {
        return m_BestFitness;
    }

    public int getBestGen() {
        return m_BestGen;
    }

    public void setBestFitness(int m_BestFitness) {
        this.m_BestFitness = m_BestFitness;
    }

    public void setBestGen(int m_BestGen) {
        this.m_BestGen = m_BestGen;
    }

    public void setOldPopulationList(Map<Integer, Pair<Integer, Population>> m_OldPopulationList) {
        this.m_OldPopulationList = m_OldPopulationList;
    }

}

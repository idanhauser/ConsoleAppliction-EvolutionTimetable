package evolution.engine;

import data.transfer.objects.DTOEngineInformation;
import data.transfer.objects.GeneticPoolInformation;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class EvolutionEngine<T> implements Serializable, Runnable {
    private static final long serialVersionUID = 100L;
    private final DTOEngineInformation<T> m_DTOEngine;
    private Population<T> m_Population;
    private EvolutionEngineInformation m_EngineInfo;
    private Map<Integer, Pair<Integer, Population>> m_OldPopulationList;
    private int m_ExitFitness;
    private int m_ExitGeneration;
    private int m_GenerationCount;
    private int m_ProcessesGenerationToShow;

    public EvolutionEngine(Factory<T> i_Factory, EvolutionEngineInformation i_EngineInfo, int i_geneListLength, DTOEngineInformation<T> i_DTOEngine) throws InstantiationException, IllegalAccessException {
        m_Population = new Population<>(i_Factory, i_EngineInfo, i_geneListLength);
        m_OldPopulationList = new TreeMap<>();
        m_EngineInfo = i_EngineInfo;
        m_GenerationCount = 0;
        m_DTOEngine = i_DTOEngine;
    }

    public EvolutionEngine(Factory<T> i_Factory, EvolutionEngineInformation i_EngineInfo, int i_geneListLength, DTOEngineInformation<T> i_DTOEngine
            , int i_ExitGeneration, int i_ProcessesGenerationToShow, int i_ExitFitness) throws InstantiationException, IllegalAccessException {
        m_Population = new Population<>(i_Factory, i_EngineInfo, i_geneListLength);
        m_OldPopulationList = new TreeMap<>();
        m_EngineInfo = i_EngineInfo;
        m_GenerationCount = 0;
        m_DTOEngine = i_DTOEngine;

        m_ProcessesGenerationToShow = i_ProcessesGenerationToShow;
        m_ExitGeneration = i_ExitGeneration;
        m_ExitFitness = i_ExitFitness;

    }

    public Map<Integer, Pair<Integer, Population>> getOldPopulationList() {
        return m_OldPopulationList;
    }

    public void run() {

        m_Population.calculateFitness();
        Individual bestSolutionInCurrentGen = m_Population.getFittestScore();
        Individual bestSolutionInAllGens = m_Population.getFittestScore();
        m_DTOEngine.setBestResult(bestSolutionInAllGens.getListOfGenes());

        int bestGen = 0;

        while (ToContinue(bestSolutionInCurrentGen)) {
            ++m_GenerationCount;
            if (m_GenerationCount % m_ProcessesGenerationToShow == 0) {
                m_OldPopulationList.put(m_GenerationCount, new Pair<Integer, Population>(m_GenerationCount, m_Population));
                m_DTOEngine.setOldPopulationList(m_OldPopulationList);
            }
            try {
                //Selection:
                m_Population = (Population<T>) m_Population.clone();
                m_Population.copyAndSetIndividualList(m_EngineInfo.getSelection().doSelection(m_Population.getIndividuals()));
                //CrossOver:
                m_Population.setIndividuals(m_EngineInfo.getCrossover().runCrossover(m_Population.getIndividuals(), m_EngineInfo.getInitialPopulation()));
                //System.out.println("Debug:#generation: " + m_GenerationCount + ", best fitness: " + bestSolutionInCurrentGen.getFitness());
                //Mutation
                m_EngineInfo.getMutations().forEach((mutation) -> mutation.doMutation(m_Population.getIndividuals()));
            } catch (Exception ex) {
                System.out.println("There was some problem in EvolutionEngine: " + ex.getMessage());
            }
            m_Population.calculateFitness();
            bestSolutionInCurrentGen = m_Population.getFittestScore();

            if (bestSolutionInCurrentGen.getFitness() > bestSolutionInAllGens.getFitness()) {
                bestGen = m_GenerationCount + 1;
                bestSolutionInAllGens = bestSolutionInCurrentGen;
                m_DTOEngine.setBestResult(bestSolutionInAllGens.getListOfGenes());
                m_DTOEngine.setBestGen(bestGen);
                m_DTOEngine.setBestFitness(bestSolutionInAllGens.getFitness());

            }
        }
    }

    private boolean ToContinue(Individual i_bestSolutionInCurrentGen) {
        return i_bestSolutionInCurrentGen.getFitness() < m_ExitFitness && m_GenerationCount < m_ExitGeneration;
    }

    public GeneticPoolInformation<T> requestGeneticsPool() throws Exception {
        GeneticPoolInformation<T> geneticPoolDto = new GeneticPoolInformation<>();
        if (m_OldPopulationList.size() != 0) {
            geneticPoolDto.setGensToShow(m_ProcessesGenerationToShow);
            geneticPoolDto.setHistoryOfGenerations(m_OldPopulationList);
        } else {
            throw new Exception("There is no data, You have to run the engine or load the data first.");
        }

        return geneticPoolDto;
    }

    public int getGenerationCount() {
        return m_GenerationCount;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}



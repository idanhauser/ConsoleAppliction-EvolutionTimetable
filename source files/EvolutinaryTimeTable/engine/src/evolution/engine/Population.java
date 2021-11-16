package evolution.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Population class
public class Population<T> implements Serializable, Cloneable {
    private static final long serialVersionUID = 100L;
    private int m_popSize;
    private List<Individual> m_individuals;
    private EvolutionEngineInformation m_EngineInfo = null;
    private int m_geneLength;
    private int fittestScore = 0;


    public Population(Factory<T> i_Factory, EvolutionEngineInformation i_EngineInfo,  int i_geneListLength) throws InstantiationException, IllegalAccessException
    {
        m_geneLength = i_geneListLength;
        m_EngineInfo = i_EngineInfo;
        m_popSize = m_EngineInfo.getInitialPopulation();
        m_individuals = new ArrayList<>();

        //Create a first population pool
        for(int i = 0; i < m_popSize; i++)
        {
           Individual ind = new Individual(i_Factory,i_EngineInfo.GetRules(),i_geneListLength);
           ind.CreateRandomGeneList();
           m_individuals.add(ind);
        }
    }

    public Individual getFittestScore()
    {
        Individual maxFit = m_individuals.get(0);

        for (Individual ind : m_individuals)
        {
            if (maxFit.getFitness() < ind.getFitness())
            {
                maxFit = ind;
            }
        }
        return maxFit;
    }

    public void calculateFitness()
    {
       for (Individual individual : m_individuals)
       {
            individual.calcFitness();
        }
    }

    public List<Individual> getIndividuals() {
        return m_individuals;
    }

    public void setIndividuals(List<Individual> i_individuals) {
        m_individuals = i_individuals;
    }

    public void copyAndSetIndividualList(List<Individual> other) {
        m_individuals = new ArrayList<>();

        other.forEach(q-> {
            try {
                m_individuals.add((Individual)q.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
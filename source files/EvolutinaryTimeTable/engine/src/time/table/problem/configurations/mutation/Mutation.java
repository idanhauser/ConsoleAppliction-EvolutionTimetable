package time.table.problem.configurations.mutation;

import evolution.engine.Individual;
import time.table.problem.jaxb.schema.generated.ETTMutation;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Mutation implements Serializable
{
    private static final long serialVersionUID = 100L;
    private MutationEType EType;
    private double m_Probability;
    private int m_MaxTupples;
    private EComponent m_Component;

    public Mutation(ETTMutation ettMutation)
    {
        EType = MutationEType.valueOf(ettMutation.getName());
        setProbability(ettMutation.getProbability());
        setConfiguration(ettMutation.getConfiguration());
    }

    public List<Individual> doMutation(List<Individual> i_individuals)
    {
        Random rn = new Random();

        for (Individual ind: i_individuals)
        {
            if(rn.nextFloat() <= m_Probability)
            {
                EType.Mutate(ind, m_Component,m_MaxTupples);
            }
        }

        return i_individuals;
    }

    private void setConfiguration(String configurationStr)
    {
        int indexEquals;
        int indexComma;
        String maxTupplesStr;
        String EnumCompStr;
        EComponent eComp;
        int maxTupplesInt;

        indexEquals = configurationStr.indexOf("=");
        indexComma = configurationStr.indexOf(',');
        maxTupplesStr = configurationStr.substring(indexEquals + 1, indexComma);
        maxTupplesInt = Integer.parseInt(maxTupplesStr);
        indexEquals = configurationStr.lastIndexOf("=");
        EnumCompStr = configurationStr.substring(indexEquals + 1);
        try
        {
            eComp = Enum.valueOf(EComponent.class, EnumCompStr);
        }
        catch (IllegalArgumentException E)
        {
            throw new IllegalArgumentException("The value of the component in mutation is Illegal." +
                    System.lineSeparator() + "The only valid values are : T, S, D, H ,C");
        }
        m_MaxTupples = maxTupplesInt;
        m_Component = eComp;
    }

    public double getProbability() {    return m_Probability;    }

    public void setProbability(double probability)
    {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("The probability must be range of 0 to 1");
        } else {
            m_Probability = probability;
        }
    }
    @Override
    public String toString() {
        return  EType + ": probability " + m_Probability +
                System.lineSeparator() +  "Configuration: " + System.lineSeparator() + "\t Max Tupples: "+this.m_MaxTupples +
                System.lineSeparator() + "\t Component: "+this.m_Component.toString();

    }

}

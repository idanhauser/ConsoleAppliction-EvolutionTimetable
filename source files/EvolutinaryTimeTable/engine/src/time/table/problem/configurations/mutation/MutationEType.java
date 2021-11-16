package time.table.problem.configurations.mutation;

import evolution.engine.Individual;
import time.table.problem.Quintet;

import java.io.Serializable;
import java.util.Random;

public enum MutationEType implements Serializable
{
    Flipping
            {
                @Override
                public void Mutate(Individual i_individual, EComponent m_Component, int i_MaxTupples)
                {
                    Random rn = new Random();
                    int max = rn.nextInt(i_MaxTupples);
                    //random max to change
                    for(int i = 0 ; i < max ; i ++)
                    {
                        int randomQuintet = rn.nextInt(i_individual.GetGeneListSize());
                        Quintet quintet = (Quintet)i_individual.getListOfGenes().get(randomQuintet);
                        m_Component.setComponent(quintet);
                    }

                }
            };
    public abstract void Mutate(Individual i_individual, EComponent m_Component, int i_MaxTupples);
    private static final long serialVersionUID = 100L;
}



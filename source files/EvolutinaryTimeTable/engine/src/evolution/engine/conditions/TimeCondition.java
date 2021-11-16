package evolution.engine.conditions;

import java.time.Duration;
import java.time.Instant;

public class TimeCondition {
    private boolean isMarked; // if this condition chose as exit condition
    private int m_ExitTime;
    Instant m_StartTime;


    public TimeCondition(int Time)
    {
        setExitTime(Time);
    }

    public void StartTimer() {
        m_StartTime = Instant.now();
    }

    public boolean isEnded()
    {
        boolean ended;
        if (isMarked == false)
            ended = false;

        else
            ended = getDuration().getSeconds() >= m_ExitTime;

        return ended;
    }


    public Duration getDuration() {
        return Duration.between(m_StartTime, Instant.now());
    }

    public void setExitTime(int exitTime)
    {
        isMarked = (exitTime != Integer.MAX_VALUE);
        m_ExitTime = exitTime;
    }
}

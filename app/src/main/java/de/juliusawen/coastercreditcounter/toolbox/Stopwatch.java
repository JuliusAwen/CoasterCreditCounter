package de.juliusawen.coastercreditcounter.toolbox;

public class Stopwatch
{
    public boolean isRunning = false;

    public long elapsedTimeInMs = 0;
    public long lapTimeInMs = 0;

    private long startTimeInMs = 0;
    private long lapStartTimeInMs = 0;

    public void start()
    {
        long currentTimeInMs = System.currentTimeMillis();

        if(this.startTimeInMs != 0)
        {
            this.reset();
        }

        this.startTimeInMs = currentTimeInMs;
        this.isRunning = true;
    }

    public void stop()
    {
        long stopTimeInMs = System.currentTimeMillis();
        this.isRunning = false;
        this.elapsedTimeInMs = stopTimeInMs - this.startTimeInMs;
    }

    public void lap()
    {
        long currentTimeInMs = System.currentTimeMillis();

        if(this.lapStartTimeInMs == 0)
        {
            this.lapTimeInMs = currentTimeInMs - startTimeInMs;
            this.lapStartTimeInMs = currentTimeInMs;
        }
        else
        {
            this.lapTimeInMs = lapStartTimeInMs - currentTimeInMs;
        }
    }

    public void reset()
    {
        this.isRunning = false;
        this.startTimeInMs = 0;
        this.elapsedTimeInMs = 0;
        this.lapStartTimeInMs = 0;
        this.lapTimeInMs = 0;
    }
}

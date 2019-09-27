package de.juliusawen.coastercreditcounter.tools;

public class Stopwatch
{
    public boolean isRunning = false;
    public long elapsedTimeInMs = 0;
    public long lapTimeInMs = 0;

    private long startTimeInMs = 0;
    private long lapStartTimeInMs = 0;

    public Stopwatch(boolean start)
    {
        if(start)
        {
            this.start();
        }
    }

    public long start()
    {
        long currentTimeInMs = System.currentTimeMillis();

        if(this.isRunning)
        {
            this.reset();
        }

        this.startTimeInMs = currentTimeInMs;
        this.isRunning = true;
        return this.startTimeInMs;
    }

    public long stop()
    {
        long stopTimeInMs = System.currentTimeMillis();
        this.isRunning = false;
        this.elapsedTimeInMs = stopTimeInMs - this.startTimeInMs;
        return this.elapsedTimeInMs;
    }

    public long lap()
    {
        long currentTimeInMs = System.currentTimeMillis();

        if(this.isRunning)
        {
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
        else
        {
            return -1;
        }


        return lapTimeInMs;
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

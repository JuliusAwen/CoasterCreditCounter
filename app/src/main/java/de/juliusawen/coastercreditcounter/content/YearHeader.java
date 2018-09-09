package de.juliusawen.coastercreditcounter.content;

public class YearHeader extends TemporaryElement
{
    private YearHeader(String name)
    {
        super(name);
    }

    public static YearHeader createYearHeader(String name)
    {
        return new YearHeader(name);
    }
}

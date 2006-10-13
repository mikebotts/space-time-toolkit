package org.vast.stt.style;


public abstract class DimensionMapper extends PropertyMapper
{
    int dimensionIndex = 0;
    
    
    public int getDimensionIndex()
    {
        return dimensionIndex;
    }
    
    
    public abstract void setDimensionSize(int size);
}

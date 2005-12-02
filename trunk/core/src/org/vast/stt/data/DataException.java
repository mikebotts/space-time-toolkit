package org.vast.stt.data;

public class DataException extends Exception
{
	static final long serialVersionUID = 0;
		
		
	public DataException(String message)
	{
		super(message);
	}
	
	public DataException(Exception e)
	{
		super(e);
	}
	
	public DataException(String message, Exception e)
	{
		super(message, e);
	}
}

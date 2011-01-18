package com.limpidfox;

public class DisplayEventArgs
{
    private String _text;
    public String getText()
    {
        return _text;
    }
    public void setText(String value)
    {
        _text = value;
    }
    public DisplayEventArgs(String text)
    {
        setText(text);
    }
}

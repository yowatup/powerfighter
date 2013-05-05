package org.pfighter.gui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class JDigitTextField extends JTextField
{
	public JDigitTextField()
	{
		this("");
	}
	
	public JDigitTextField(String initialText)
	{
		super(createDocument(), initialText, 10);
	}
	
	public int getValue()
	{
		return Integer.parseInt(getText());
	}
	
	private static Document createDocument()
	{
		PlainDocument doc = new PlainDocument();
		doc.setDocumentFilter(new DigitDocumentFilter());
		return doc;
	}
	
	private static class DigitDocumentFilter extends DocumentFilter
	{
		@Override
		public void insertString(FilterBypass fb, int off, String str,
				AttributeSet attr)
				throws BadLocationException
		{
			fb.insertString(off, str.replaceAll("\\D++", ""), attr);
		}
		
		@Override
		public void replace(FilterBypass fb, int off, int len, String str,
				AttributeSet attr)
				throws BadLocationException
		{
			fb.replace(off, len, str.replaceAll("\\D++", ""), attr);
		}
	}
	
	private static final long serialVersionUID = 612930235999237749L;
}

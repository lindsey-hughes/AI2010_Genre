package com.bjoli.genre;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;


public class MarkovModel
{
	private Matrix mat;
	private HashMap<String, Integer> words;
	private HashMap<String, Float> counts;
	private int numWords;
	private float smallest;

	public MarkovModel()
	{
		mat = new Matrix();
		words = new HashMap<String, Integer>();
		counts = new HashMap<String, Float>();
	}

	public void update(File file)
	{
		Scanner s = createScanner(file);
		String[] line;

		while (s.hasNextLine()) //For each line in the file...
		{
			line = s.nextLine().toLowerCase().trim().split("\\s+"); //Split into words.
			
			if (line.length > 0)
			{
				for (int i = 0; i < line.length; ++i)
				{
					if (!words.containsKey(line[i])) //If the word has not been added yet...
					{
						words.put(line[i], numWords++); //Add the word.
						counts.put(line[i], new Float(1)); //Set the number of occurrences to 1.
						//System.out.println("Word: " + line[i]);
					}
					else
					{
						counts.put(line[i], counts.get(line[i]) + 1); //Increment count.
					}
				}
			}
		}
	}

	public void train(File file, int label)
	{
		Scanner s = createScanner(file);
		String[] line;
		float current;

		while (s.hasNextLine()) //For each line in the file...
		{
			line = s.nextLine().toLowerCase().split("\\s+"); //Split into words.
			
			if (line.length > 0)
			{
				for (int i = 0; i < line.length - 1; ++i) //For each word in the line, except the last word...
				{
					current = mat.get(words.get(line[i]), words.get(line[i + 1])); //Get the current count.
					mat.set(words.get(line[i]), words.get(line[i + 1]), current + 1); //Increment the current count.
				}
			}
		}
	}

//	public float probability(File file)
//	{
//		Scanner s = createScanner(file);
//		String line;
//		double firstProb;
//		double sum;
//
//		while (s.hasNextLine()) //For each line in the file...
//		{
//			line = s.nextLine();
//			for (String i: line.split("[//s]+")) //For each word in the line, except the last word...
//			{
//				firstProb = firstCounts.get(words.get(i));
//
//
//			}
//		}
//
//		return 0;
//	}
	
	public double probability(String line)
	{
		String[] splitLine = line.toLowerCase().split("\\s+"); //Array of words in the line.
		double firstProb = Math.random() * 0.001;
		double sum = 0;

		Float val;
		if ((val = counts.get(splitLine[0])) != null) //If the first word is in our counts table
			firstProb = Math.log(val); //Log of the first word probability.
		
		for (int i = 0; i < splitLine.length - 1; ++i) //going through the array
		{
			if (words.get(splitLine[i]) != null && words.get(splitLine[i + 1]) != null  //both words have been seen before separately
					&& mat.get(words.get(splitLine[i]), words.get(splitLine[i + 1])) != 0) //words have been seen consecutively
				sum += Math.log(mat.get(words.get(splitLine[i]), words.get(splitLine[i + 1]))); //taking sum of the log of the bigram probability
			else
				sum += Math.random() * 0.00001;
		}
		
		return firstProb + sum; //returning the first word probability plus the sum of the bigram probability.
	}
//	
//	public double probabilityWhole(File file)
//	{
//		
//		return 0;
//	}

	public void normalize()
	{
		//Normalize first words hash map
		for (String i: counts.keySet()) //For each first word.
		{
			System.out.println(counts.get(i) + " normalized to " + counts.get(i) / counts.keySet().size());
			counts.put(i, counts.get(i) / counts.keySet().size()); //replaces count by count divided by size
		}
		
		//Normalize bigram matrix
		smallest = Integer.MAX_VALUE;
		
		for (int i = 0; i < mat.getSize(); ++i) //Iterating through matrix rows...
		{
			float count = 0;
			for (int j = 0; j < mat.getSize(); ++j) //Iterating through matrix cols...
			{
				if (mat.get(i, j) > 0) //if it exists.
					count = count + mat.get(i, j); //Adding all non zero values in the row
			}
			for (int j = 0; j < mat.getSize(); ++j) //iterating through matrix cols again...
			{
				if (mat.get(i, j) > 0) //if it is a non zero value.
				{
					float value = mat.get(i, j) / count; //dividing non zero values by the total count.
					mat.set(i, j, value);
					if (value < smallest)
					{
						smallest = value;
					}
				}
			}
		}
	}

	private Scanner createScanner(File file)
	{
		Scanner s = null;
		try
		{
			s = new Scanner(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public void print()
	{
		mat.print();
	}
}

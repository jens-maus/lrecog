/*
 * Leaves Recognition - a neuronal network based recognition of leaf images
 * Copyright (C) 2001 Jens Langner, LightSpeed Communications GbR
 *
 * LightSpeed Communications GbR
 * Lannerstrasse 1
 * 01219 Dresden
 * Germany
 * http://www.light-speed.de/
 * <Jens.Langner@light-speed.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id$
 */
 
package lrecog.nnetwork;

import java.util.*;

public class BackProp
{
  private double  inputA[];		  // activations input

  private double	hiddenA[];	  // activations hidden
  private double	hiddenN[];	  // sum of products for hidden units
  private double	hiddenD[];	  // output error
  private double	hiddenW[][];	// connection weights matrix

  private double	outputA[];		// activations output
  private double	outputN[];		// sum of products
  private double	outputD[];		// output error
  private double	oldD[];		    // old output error
  private double	outputW[][];	// connection weights matrix

  private double	biasH[];	    // bias for the hidden units
  private double	biasO[];	    // bias for the output units

  private int			numInput;		  // number of neurons on input  layer
  private int			numHidden;		// number of neurons on hidden layer
  private int			numOutput;		// number of neurons on output layer

  private int     epoch;        // number of epochs of the learn process
  private double  momentum;     // momentum
  private double  alpha;        // learnrate
  private double  absError=0.0; // the absolute error of the learning proc.

  // create random object to get individual random numbers
  private Random rand;

  /**
  * Constructor
  */
  public BackProp(int input, int hidden, int output)
  {
  	numInput  = input;	// number of neurons in the input layer
	  numHidden = hidden;	// number of neurons in the hidden layer
	  numOutput = output;	// number of neurons in the output layer

	  inputA    = new double[numInput];

	  hiddenW   = new double[numHidden][numInput];

	  hiddenA   = new double[numHidden];
	  hiddenN   = new double[numHidden];
	  hiddenD   = new double[numHidden];
	  biasH     = new double[numHidden];

	  outputW   = new double[numOutput][numHidden];

	  outputA   = new double[numOutput];
	  outputN   = new double[numOutput];
	  outputD   = new double[numOutput];
	  oldD      = new double[numOutput];
	  biasO     = new double[numOutput];

	  alpha 	  = 0.3;  // default learnrate = 0.3
	  momentum  = 1.0;  // default momentum = 1.0

    // initialize the random object
    rand = new Random();

    // now we initialize the network
    init();
  }

  /**
  * Constructor
  */
  public BackProp(int input, int hidden, int output, double alpha, double mom)
  {
    this(input, hidden, output);

    this.alpha    = alpha;
    this.momentum = mom;
  }

  /**
  * init()
  *
  * initialize the network with random numbers
  */
  private void init()
  {
	  epoch = 0;

	  // init net with small random values
	  for (int i = 0; i < numInput; i++)
    {
	    inputA[i] = frandom(-1.0, 1.0);
    }

	  for(int i = 0; i < numHidden; i++)
    {
	    hiddenA[i]  = frandom(-1.0, 1.0);
	    biasH[i]    = frandom(-1.0, 1.0);
	    for(int m=0; m < numInput; m++)
		  {
        hiddenW[i][m] = frandom(-1.0, 1.0);
      }
    }

	  for (int i=0; i < numOutput; i++)
    {
	    biasO[i] = frandom(-1.0, 1.0);
	    for (int m=0; m < numHidden; m++)
		  {
        outputW[i][m] = frandom(-1.0,1.0);
	    }
    }
  }

  /**
  * sigmoid()
  *
  * sigmoid activation function
  */
  private double sigmoid (double x)
  {
  	return (1.0 / (1.0 + Math.exp(-x) ));
  }

  private double sigmoidDeriv(double x)
  {
	  return (sigmoid(x) * (1 - sigmoid(x)));
  }

  /**
  * feedForward()
  *
  * method to do the feed forward
  */
  private void feedForward()
  {
	  double	sum2 = 0.0;

    // calculate the hidden weights
	  for(int i = 0; i < numHidden; i++)
    {
	    sum2 = biasH[i];
	    for(int j=0; j < numInput; j++)
		  {
        sum2 += hiddenW[i][j]* inputA[j];
      }
	    hiddenN[i] = sum2;
	    hiddenA[i] = sigmoid(sum2);
	  }

    // calculate the new output weights
	  for(int i = 0; i < numOutput; i++)
    {
	    sum2 = biasO[i];
	    for(int j = 0; j < numHidden; j++)
		  {
        sum2 += outputW[i][j]* hiddenA[j];
      }
	    outputN[i] = sum2;
	  }
  }

  /**
  * computeDelta()
  *
  * method to calculate the new delta
  */
  private void computeDelta(int m)
  {
	  outputD[m] = (outputA[m] - sigmoid(outputN[m])) * sigmoidDeriv(outputN[m]);

    // hidden layer calculation
	  for(int i=0; i < numHidden; i++)
	  {
      outputW[m][i] += outputD[m] * hiddenA[i] * alpha;
    }

    // output layer calculation
	  for(int i=0; i < numOutput; i++)
	  {
      biasO[i] += outputD[m] * alpha;
    }
  }

  /**
  * updateWeights()
  *
  * method to update the other weights also
  */
  private void updateWeights()
  {
	  double	sum2;

	  for(int j = 0; j < numHidden; j++)
    {
	    sum2 = 0.0;
	    for(int i = 0;i < numOutput; i++)
		  {
        sum2 += outputD[i]* outputW[i][j];
      }

	    sum2 *= sigmoidDeriv(hiddenN[j]);
	    biasH[j] += sum2 * alpha;

	    for(int i=0; i < numInput; i++)
		  {
        hiddenW[j][i] += alpha * sum2 * inputA[i];
	    }
    }
  }

  /**
  * frandom()
  *
  * special random method that return a random number
  * within min and max.
  */
  private double frandom(double min, double max)
  {
    return rand.nextDouble() * (max - min) + min;
  }

  /**
  * propagate()
  *
  * method to return a array of doubles with the recognized
  * values after a double vector has been parsed.
  */
  public double[] propagate(double[] vector)
  {
    double	sum2;

    for (int i = 0; i < numInput; i++)
	  {
      inputA[i] = vector[i];
    }

	  for(int i = 0; i < numHidden; i++)
    {
	    sum2 = biasH[i];
	    for(int j = 0; j < numInput; j++)
		  {
        sum2 += hiddenW[i][j] * inputA[j];
	      hiddenA[i] = sigmoid(sum2);
	    }
    }

	  for (int i = 0; i < numOutput; i++)
    {
	    sum2 = biasO[i];
	    for (int j = 0; j < numHidden; j++)
		  {
        sum2 += outputW[i][j] * hiddenA[j];
	    }
      outputN[i] = sum2;
	    outputA[i] = sigmoid(sum2);
	  }

    return outputA;
  }

  /**
  * learnVector()
  *
  * method to let the network learn on passed input and output
  * vectors and return the error for this learning operation.
  *
  * Please note that this error is not the absolute error of the
  * whole network! You have to add all error for a learning phase
  * to get the overall error.
  */
  public double learnVector(double[] in, double[] out)
  {
	  for (int i = 0; i < numInput; i++)
    {
	    inputA[i] = in[i];
	  }

	  for (int i = 0; i < numOutput; i++)
    {
	    outputA[i] = out[i];
	  }

	  feedForward();

	  absError	= 0.0;

    // calculate the absError
	  for (int j = 0; j < numOutput ; j++)
    {
	    computeDelta(j);
	    absError += Math.pow(outputA[j] - sigmoid(outputN[j]), 2);
	  }

	  updateWeights();

    // update the learnrate
	  alpha *= momentum;

    return absError;
  }

  public int numInput()
  {
    return numInput;
  }

  public int numHidden()
  {
    return numHidden;
  }

  public int numOutput()
  {
    return numOutput;
  }

  public double[] getBiasH()
  {
    return biasH;
  }

  public double[] getBiasO()
  {
    return biasO;
  }

  public double[][] getHiddenW()
  {
    return hiddenW;
  }

  public double[][] getOutputW()
  {
    return outputW;
  }

  public double getAlpha()
  {
    return alpha;
  }

  public double getMomentum()
  {
    return momentum;
  }

  public int getEpoch()
  {
    return epoch;
  }

  public void setAlpha(double a)
  {
    alpha = a;
  }

  public void setMomentum(double mom)
  {
    momentum = mom;
  }

  public void setHiddenW(double weight, int hidden, int input)
  {
    //System.out.println("SetHiddenW: ["+hidden+"]["+input+"]="+weight);
    hiddenW[hidden][input] = weight;
  }

  public void setBiasH(double bias, int hidden)
  {
    //System.out.println("SetBiasH: ["+hidden+"]="+bias);
    biasH[hidden] = bias;
  }

  public void setOutputW(double weight, int output, int hidden)
  {
    //System.out.println("SetOutputW: ["+output+"]["+hidden+"]="+weight);
    outputW[output][hidden] = weight;
  }

  public void setBiasO(double bias, int output)
  {
    //System.out.println("SetBiasO: ["+output+"]="+bias);
    biasO[output] = bias;
  }
}

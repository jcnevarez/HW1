package ReversePolishNotation;

//Imports needed for the class
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/*---------------------------------------------------------*/
/* Class Name: Calculator                                  */
/*                                                         */
/* Description: This class holds all functions needed to   */
/*              change an infix equation to postfix as     */
/*              well as to calculate the postfix           */
/*              equation.                                  */
/*                                                         */
/*---------------------------------------------------------*/
public class Calculator 
{
	// Global Variables
	Queue<String> infixQ   = new LinkedList<String>();
	Queue<String> postQ    = new LinkedList<String>();
	Stack<String> opStack  = new Stack<String>();

	// Error string for reporting to user. 
	private static final String ERROR_STRING = "Not a valid input, ";
	
	/*---------------------------------------------------------*/
	/* Function Name: queueInfix                               */
	/*                                                         */
	/* Description: Takes an infix problem and place it        */
	/*              properly in a queue.                       */
	/*                                                         */
	/*---------------------------------------------------------*/
	public void queueInfix(String decode) throws QueueInfixException
	{
		// Local Variables
		String digit = "";
		char   ch;
		int    i,j; 

		// Clear all global variables
		infixQ.clear();
		postQ.clear();
		opStack.clear();
		
		// Loop through the entire string
		for(i = 0; i < decode.length(); i++)
		{
			ch = decode.charAt(i);
			
			// Check if current char is a number
			if(Character.isDigit(ch) || ch == '.')
			{
				// Loop through till we find the end of the number
				for(j = i; j < decode.length(); j++)
				{
					ch = decode.charAt(j);
					if(!Character.isDigit(ch) && ch != '.')
					{
						break;
					}
					digit += Character.toString(ch);
				}
				
				// Place the number in the queue 
				infixQ.offer(digit);
				digit = "";
				i = j-1;
			}
			// Check to see if we found the POW function
			else if (ch == 'P')
			{
				digit = decode.substring(i, i+3);
				if(!digit.equals("POW"))
				{
					throw new QueueInfixException(ERROR_STRING + "Unknown Operator " + digit);
				}
				infixQ.offer(digit);
				digit = "";
				i    += 2;
			}
			// Check to make sure we are not a space and then place 
			// operators on the queue. 
			else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || 
					 ch == '%' || ch == '(' || ch == ')')
			{
				infixQ.offer(Character.toString(ch));
			} 
			// Check to filter out spaces if not then we have encountered an error throw exception. 
			else if(ch != ' ')
			{
				throw new QueueInfixException(ERROR_STRING + "Unknown Operator " + Character.toString(ch));
			}
		} /* End For loop */
	} /* End queueInfix function */
	
	/*---------------------------------------------------------*/
	/* Function Name: infixToPostfix                           */
	/*                                                         */
	/* Description: Take the infix queue and convert it to     */
	/*              post fix notation. An example is 2 + 2     */
	/*              gets converted to 2 2 +                    */
	/*                                                         */
	/*---------------------------------------------------------*/
	public void infixToPostfix() throws InfixToPostFixException
	{
		// Local Variable
		String current;

		// Loop through the infixQ 
		while(!infixQ.isEmpty())
		{
			// Get the current infix number or operator
			current = infixQ.poll();
			
			// Check to see if a number and then add to post fix queue
			if(isNumber(current))
			{
				postQ.offer(current);
			}
			// Check to see if the operator stack is empty or if we
			// are at a left parenthesis
			else if(opStack.isEmpty() || current.equals("(") || current.equals("POW"))
			{
				opStack.push(current);
			}
			// Check to see if at the right parenthesis and then
			// unload all operators to the queue till left parenthesis
			else if(current.equals(")"))
			{
				// Keep adding to the queue if the operator stack is 
				// not empty and we have not seen the left parenthesis
				while((!opStack.isEmpty())   && 
					  (!opStack.lastElement().equals("(")))
				{
					postQ.offer(opStack.pop());
				} /* End while */
				
				if (opStack.isEmpty())
				{
					throw new InfixToPostFixException(ERROR_STRING + "Missing left parenthesis '('");
				}
				//Discard left parenthesis
				opStack.pop();
			}
			// Else for operators (+ - * / %). Make sure the
			// precedence of the current is greater or equal to
			// past operators.  
			else
			{
				while((!opStack.isEmpty())   && 
					  (getPrecedence(current) <= getPrecedence(opStack.lastElement())))
				{
					postQ.offer(opStack.pop());
				} /* End while */
				opStack.push(current);
			}
		} /* End while */

		// Add all remaining operators to the queue
		while (!opStack.isEmpty())
		{
			postQ.offer(opStack.pop());
		} /* End while */
	} /* End infixToPostfix function */

	/*---------------------------------------------------------*/
	/* Function Name: isNumber                                 */
	/*                                                         */
	/* Description: Check string is a number. If checkStr is   */
	/*              2 then will return TRUE if checkStr is     */
	/*              POW will return FALSE.                     */
	/*                                                         */
	/*---------------------------------------------------------*/
	public boolean isNumber(String checkStr)
	{
		// Local Variable
		boolean returnMe = true;
		
		// Try to parse String to Integer
		try
		{
			Double.parseDouble(checkStr);
		}
		// Unable to format number change returnME to false.
		catch (NumberFormatException e)
		{
			returnMe = false;
		}
		
		return returnMe;
	} /* End isNumber Function */
	
	/*---------------------------------------------------------*/
	/* Function Name: getPrecedence                            */
	/*                                                         */
	/* Description: This will return the precedence of the     */
	/*              current operator using the table below:    */
	/*              ___________________________                */
	/*              |   Operator  | Precedence|                */                  
	/*              |-------------|-----------|                */
	/*              |Addition (+) |     1     |                */
	/*              |Subtract (-) |     1     |                */
	/*              |Multiply (*) |     2     |                */
	/*              |Division (/) |     2     |                */
	/*              |Modulo   (%) |     2     |                */
	/*              |Power   (POW)|     3     |                */
	/*              ---------------------------                */
	/*---------------------------------------------------------*/
	public Integer getPrecedence(String checkStr)
	{
		// Local Variable 
		Integer returnMe = 0;
		
		// Get the precedence for the operators. 
		switch (checkStr)
		{
			case "+":
			case "-":
			{
				returnMe = 1;
				break;
			}
			case "*":
			case "/":
			case "%":
			{
				returnMe = 2;
				break;
			}
			case "POW":
			{
				returnMe = 3;
				break;
			}
		} /* End Switch case */
		
		return returnMe;
	} /* End getPrecedence function */
	
	/*---------------------------------------------------------*/
	/* Function Name: calculateInfix                           */
	/*                                                         */
	/* Description: Calculates the infix notation and prints   */
	/*              the final value. Also prints out the       */
	/*              infix notation during calculation.         */
	/*                                                         */
	/*---------------------------------------------------------*/
	public void calculateInfix() throws CalculateInfixException
	{
		// Local Variables
		String current       = "" ;
		String postFix       = "" ;
		Double currentdouble = 0.0;
		Double topNum        = 0.0;
		Double nextNum       = 0.0;
		Double answer        = 0.0;
		Stack<Double> eval   = new Stack<Double>();
		DecimalFormat df 	 = new DecimalFormat("####0.####"); 

		// Loop through the entire postfix queue
		while (!postQ.isEmpty())
		{
			// Grab the current string and print to the user. 
			current = postQ.poll();
			postFix += (current + " ");
			
			try
			{
				// Push numbers to the stack
				currentdouble = Double.parseDouble(current);
				eval.push(currentdouble);
			}
			// If not a number then at an operator
			catch (NumberFormatException ex)
			{
				// Check to see if we have 2 operands to pop from our stack. 
				// If not then throw exception. 
				if(eval.size() < 2)
				{
					throw new CalculateInfixException(ERROR_STRING + "Unable to pop 2 operands from stack");
				}
				
				// Pop the last two numbers. 
				topNum  = eval.pop();
				nextNum = eval.pop();
				
				// Depending on the operator do the
				// operation and return value to the stack. 
				switch(current)
				{
					case "+":   // Addition
					{
						answer = nextNum + topNum;
						break;
					}
					case "-":   // Subtraction
					{
						answer = nextNum - topNum;
						break;
					}
					case "*":   // Multiplication
					{
						answer = nextNum * topNum;
						break;
					}
					case "/":   // Division
					{
						answer = nextNum / topNum;
						break;
					}
					case "%":   // Modulo
					{
						answer = nextNum % topNum;
						break;
					}
					case "POW": // Power
					{
						answer = Math.pow(nextNum, topNum);
						break;
					}
					default:    // Could not find set to zero
					{
						answer = 0.0;
						break;
					}
				} /* End Switch case */
				
				// Add the answer to the stack. 
				eval.push(answer);
			} /* End catch NumberFormatException */
		} /* End while loop */
		
		// If there is more than 1 item then there is an error in the infix notation
		if(eval.size() != 1)
		{
			throw new CalculateInfixException(ERROR_STRING + "Missing extra operator");
		}
		
		// Print postfix notation to the user if there are no errors. 
		System.out.println(postFix);
		
		// Last item on the stack is the answer to the
		// postfix equation. 
		System.out.println(df.format(eval.pop()));
	} /* End calculateInfix function */
	
	/*---------------------------------------------------------*/
	/* Function Name: main                                     */
	/*                                                         */
	/* Description: Main function that runs through each       */
	/*              infix case submitted by the user.          */
	/*                                                         */
	/*---------------------------------------------------------*/
	public static void main(String[] args) throws IOException
	{
		// Local Variable 
		BufferedReader in 	 	= new BufferedReader(new InputStreamReader(System.in));
		Calculator myCalculator = new Calculator();
		String     inString		= "";
		
		//Print greeting message and read in the first line
		System.out.println("Please enter infix equation");
		inString = in.readLine();
		// Continue to loop until we see quit
		while(!inString.equals("quit"))
		{
			try 
			{
				myCalculator.queueInfix(inString); // Take the string and put in a queue
				myCalculator.infixToPostfix();     // Change from infix to postfix
				myCalculator.calculateInfix();     // Calculate the postfix equation
			} 
			catch (QueueInfixException | InfixToPostFixException | CalculateInfixException ex) 
			{
				System.out.println(ex.getMessage());
			}

			//Print greeting message and read in next line
			System.out.println("Please enter infix equation");
			inString = in.readLine();
		} /* End while case */
		
		System.out.println("Thank you for using our RPN Calculator");
	} /* End main function */
} /* End Class Calculator */

/*---------------------------------------------------------*/
/* Class Name: QueueInfixException                         */
/*                                                         */
/* Description: Allows the ability to throw an exception   */
/*              for invalid infix notation when queuing.   */
/*                                                         */
/*---------------------------------------------------------*/
class QueueInfixException extends Exception 
{
	// Serial Version UID needed for all Exceptions
	private static final long serialVersionUID = -2187605233053881530L;

	// Base Constructor 
	public QueueInfixException()
	{
		super();
	}
	
	// Constructor with error message to report. 
	public QueueInfixException(String message) 
	{
		super(message);
	}
} /* End Class QueueInfixException */

/*---------------------------------------------------------*/
/* Function Name: InfixToPostFixException                  */
/*                                                         */
/* Description: Allows the ability to throw an exception   */
/*              for invalid infix to postfix translation.  */
/*                                                         */
/*---------------------------------------------------------*/
class InfixToPostFixException extends Exception 
{
	// Serial Version UID needed for all Exceptions
	private static final long serialVersionUID = -7409246565024385689L;

	// Base Constructor
	public InfixToPostFixException()
	{
		super();
	}
	
	// Constructor with error message to report. 
	public InfixToPostFixException(String message)
	{
		super(message);
	}
} /* End Class InfixToPostFixException */

/*---------------------------------------------------------*/
/* Function Name: CalculateInfixException                  */
/*                                                         */
/* Description: Allows the ability to throw an exception   */
/*              for invalid equation when trying to        */
/*              calculate postfix.                         */
/*                                                         */
/*---------------------------------------------------------*/
class CalculateInfixException extends Exception 
{
	// Serial Version UID needed for all Exceptions
	private static final long serialVersionUID = 1197091565551803975L;
	
	// Base Constructor 
	public CalculateInfixException()
	{
		super();
	}
	
	// Constructor with error message to report. 
	public CalculateInfixException(String message)
	{
		super(message);
	}
} /* End Class CalculateInfixException */

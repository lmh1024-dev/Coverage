/*
 * dk.brics.automaton
 * 
 * Copyright (c) 2001-2017 Anders Moeller
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dk.brics.automaton;

import java.util.*;

/**
 * Construction of basic automata.
 */
final public class BasicAutomata {
	
	private BasicAutomata() {}

	/** 
	 * Returns a new (deterministic) automaton with the empty language. 
	 */
	public static dk.brics.automaton.Automaton makeEmpty() {
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		a.initial = new dk.brics.automaton.State();
		a.deterministic = true;
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts only the empty string. 
	 */
	public static dk.brics.automaton.Automaton makeEmptyString() {
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		a.singleton = "";
		a.deterministic = true;
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts all strings. 
	 */
	public static dk.brics.automaton.Automaton makeAnyString()	{
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		a.initial = s;
		s.accept = true;
		s.transitions.add(new Transition(Character.MIN_VALUE, Character.MAX_VALUE, s));
		a.deterministic = true;
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts any single character. 
	 */
	public static dk.brics.automaton.Automaton makeAnyChar() {
		return makeCharRange(Character.MIN_VALUE, Character.MAX_VALUE);
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts a single character of the given value. 
	 */
	public static dk.brics.automaton.Automaton makeChar(char c) {
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		a.singleton = Character.toString(c);
		a.deterministic = true;
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts a single char 
	 * whose value is in the given interval (including both end points). 
	 */
	public static dk.brics.automaton.Automaton makeCharRange(char min, char max) {
		if (min == max)
			return makeChar(min);
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		dk.brics.automaton.State s1 = new dk.brics.automaton.State();
		dk.brics.automaton.State s2 = new dk.brics.automaton.State();
		a.initial = s1;
		s2.accept = true;
		if (min <= max)
			s1.transitions.add(new Transition(min, max, s2));
		a.deterministic = true;
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts a single character in the given set. 
	 */
	public static dk.brics.automaton.Automaton makeCharSet(String set) {
		if (set.length() == 1)
			return makeChar(set.charAt(0));
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		dk.brics.automaton.State s1 = new dk.brics.automaton.State();
		dk.brics.automaton.State s2 = new dk.brics.automaton.State();
		a.initial = s1;
		s2.accept = true;
		for (int i = 0; i < set.length(); i++)
			s1.transitions.add(new Transition(set.charAt(i), s2));
		a.deterministic = true;
		a.reduce();
		return a;
	}
	
	/**
	 * Constructs sub-automaton corresponding to decimal numbers of 
	 * length x.substring(n).length().
	 */
	private static dk.brics.automaton.State anyOfRightLength(String x, int n) {
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		if (x.length() == n)
			s.setAccept(true);
		else
			s.addTransition(new Transition('0', '9', anyOfRightLength(x, n + 1)));
		return s;
	}
	
	/**
	 * Constructs sub-automaton corresponding to decimal numbers of value 
	 * at least x.substring(n) and length x.substring(n).length().
	 */
	private static dk.brics.automaton.State atLeast(String x, int n, Collection<dk.brics.automaton.State> initials, boolean zeros) {
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		if (x.length() == n)
			s.setAccept(true);
		else {
			if (zeros)
				initials.add(s);
			char c = x.charAt(n);
			s.addTransition(new Transition(c, atLeast(x, n + 1, initials, zeros && c == '0')));
			if (c < '9')
				s.addTransition(new Transition((char)(c + 1), '9', anyOfRightLength(x, n + 1)));
		}
		return s;
	}
	
	/**
	 * Constructs sub-automaton corresponding to decimal numbers of value 
	 * at most x.substring(n) and length x.substring(n).length().
	 */
	private static dk.brics.automaton.State atMost(String x, int n) {
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		if (x.length() == n)
			s.setAccept(true);
		else {
			char c = x.charAt(n);
			s.addTransition(new Transition(c, atMost(x, (char)n + 1)));
			if (c > '0')
				s.addTransition(new Transition('0', (char)(c - 1), anyOfRightLength(x, n + 1)));
		}
		return s;
	}
	
	/**
	 * Constructs sub-automaton corresponding to decimal numbers of value 
	 * between x.substring(n) and y.substring(n) and of
	 * length x.substring(n).length() (which must be equal to y.substring(n).length()).
	 */
	private static dk.brics.automaton.State between(String x, String y, int n, Collection<dk.brics.automaton.State> initials, boolean zeros) {
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		if (x.length() == n)
			s.setAccept(true);
		else {
			if (zeros)
				initials.add(s);
			char cx = x.charAt(n);
			char cy = y.charAt(n);
			if (cx == cy)
				s.addTransition(new Transition(cx, between(x, y, n + 1, initials, zeros && cx == '0')));
			else { // cx<cy
				s.addTransition(new Transition(cx, atLeast(x, n + 1, initials, zeros && cx == '0')));
				s.addTransition(new Transition(cy, atMost(y, n + 1)));
				if (cx + 1 < cy)
					s.addTransition(new Transition((char)(cx + 1), (char)(cy - 1), anyOfRightLength(x, n + 1)));
			}
		}
		return s;
	}
	
	/** 
	 * Returns a new automaton that accepts strings representing 
	 * decimal non-negative integers in the given interval.
	 * @param min minimal value of interval
	 * @param max maximal value of inverval (both end points are included in the interval)
	 * @param digits if &gt;0, use fixed number of digits (strings must be prefixed
	 *               by 0's to obtain the right length) -
	 *               otherwise, the number of digits is not fixed
	 * @exception IllegalArgumentException if min&gt;max or if numbers in the interval cannot be expressed
	 *                                     with the given fixed number of digits
	 */
	public static dk.brics.automaton.Automaton makeInterval(int min, int max, int digits) throws IllegalArgumentException {
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		String x = Integer.toString(min);
		String y = Integer.toString(max);
		if (min > max || (digits > 0 && y.length() > digits))
			throw new IllegalArgumentException();
		int d;
		if (digits > 0)
			d = digits;
		else
			d = y.length();
		StringBuilder bx = new StringBuilder();
		for (int i = x.length(); i < d; i++)
			bx.append('0');
		bx.append(x);
		x = bx.toString();
		StringBuilder by = new StringBuilder();
		for (int i = y.length(); i < d; i++)
			by.append('0');
		by.append(y);
		y = by.toString();
		Collection<dk.brics.automaton.State> initials = new ArrayList<dk.brics.automaton.State>();
		a.initial = between(x, y, 0, initials, digits <= 0);
		if (digits <= 0) {
			ArrayList<StatePair> pairs = new ArrayList<StatePair>();
			for (dk.brics.automaton.State p : initials)
				if (a.initial != p)
					pairs.add(new StatePair(a.initial, p));
			a.addEpsilons(pairs);
			a.initial.addTransition(new Transition('0', a.initial));
			a.deterministic = false;
		} else
			a.deterministic = true;
		a.checkMinimizeAlways();
		return a;
	}
	
	/** 
	 * Returns a new (deterministic) automaton that accepts the single given string.
	 */
	public static dk.brics.automaton.Automaton makeString(String s) {
		dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
		a.singleton = s;
		a.deterministic = true;
		return a;
	}
	
    /**
     * Returns a new (deterministic and minimal) automaton that accepts the union of the
     * given set of strings. The input character sequences are internally sorted in-place,
     * so the input array is modified. 
     * @see dk.brics.automaton.StringUnionOperations
     */
    public static dk.brics.automaton.Automaton makeStringUnion(CharSequence... strings) {
        if (strings.length == 0)
            return makeEmpty();
        Arrays.sort(strings, dk.brics.automaton.StringUnionOperations.LEXICOGRAPHIC_ORDER);
        dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
        a.setInitialState(StringUnionOperations.build(strings));
        a.setDeterministic(true);
        a.reduce();
        a.recomputeHashCode();
        return a;
    }

	/**
	 * Constructs automaton that accept strings representing nonnegative integers
	 * that are not larger than the given value.
	 * @param n string representation of maximum value
	 */
	public static dk.brics.automaton.Automaton makeMaxInteger(String n) {
		int i = 0;
		while (i < n.length() && n.charAt(i) == '0')
			i++;
		StringBuilder b = new StringBuilder();
		b.append("0*(0|");
		if (i < n.length())
			b.append("[0-9]{1,").append(n.length() - i - 1).append("}|");
		maxInteger(n.substring(i), 0, b);
		b.append(")");
		return dk.brics.automaton.Automaton.minimize((new dk.brics.automaton.RegExp(b.toString())).toAutomaton());
	}

	private static void maxInteger(String n, int i, StringBuilder b) {
		b.append('(');
		if (i < n.length()) {
			char c = n.charAt(i);
			if (c != '0')
				b.append("[0-").append((char) (c - 1)).append("][0-9]{").append(n.length() - i - 1).append("}|");
			b.append(c);
			maxInteger(n, i + 1, b);
		}
		b.append(')');
	}

	/**
	 * Constructs automaton that accept strings representing nonnegative integers
	 * that are not less that the given value.
	 * @param n string representation of minimum value
	 */
	public static dk.brics.automaton.Automaton makeMinInteger(String n) {
		int i = 0;
		while (i + 1 < n.length() && n.charAt(i) == '0')
			i++;
		StringBuilder b = new StringBuilder();
		b.append("0*");
		minInteger(n.substring(i), 0, b);
		b.append("[0-9]*");
		return dk.brics.automaton.Automaton.minimize((new dk.brics.automaton.RegExp(b.toString())).toAutomaton());
	}
	
	private static void minInteger(String n, int i, StringBuilder b) {
		b.append('(');
		if (i < n.length()) {
			char c = n.charAt(i);
			if (c != '9')
				b.append("[").append((char) (c + 1)).append("-9][0-9]{").append(n.length() - i - 1).append("}|");
			b.append(c);
			minInteger(n, i + 1, b);
		}
		b.append(')');
	}

	/**
	 * Constructs automaton that accept strings representing decimal numbers
	 * that can be written with at most the given number of digits.
	 * Surrounding whitespace is permitted.
	 * @param i max number of necessary digits
	 */
	public static dk.brics.automaton.Automaton makeTotalDigits(int i) {
		return dk.brics.automaton.Automaton.minimize((new dk.brics.automaton.RegExp("[ \t\n\r]*[-+]?0*([0-9]{0," + i + "}|((([0-9]\\.*){0," + i + "})&@\\.@)0*)[ \t\n\r]*")).toAutomaton());
	}
	
	/**
	 * Constructs automaton that accept strings representing decimal numbers
	 * that can be written with at most the given number of digits in the fraction part.
	 * Surrounding whitespace is permitted.
	 * @param i max number of necessary fraction digits
	 */
	public static dk.brics.automaton.Automaton makeFractionDigits(int i) {
		return dk.brics.automaton.Automaton.minimize((new RegExp("[ \t\n\r]*[-+]?[0-9]+(\\.[0-9]{0," + i + "}0*)?[ \t\n\r]*")).toAutomaton());
	}
	
	/**
	 * Constructs automaton that accept strings representing the given integer.
	 * Surrounding whitespace is permitted.
	 * @param value string representation of integer
	 */
	public static dk.brics.automaton.Automaton makeIntegerValue(String value) {
		boolean minus = false;
    	int i = 0;
    	while (i < value.length()) {
    		char c = value.charAt(i);
    		if (c == '-')
    			minus = true;
    		if (c >= '1' && c <= '9')
    			break;
    		i++;
    	}
    	StringBuilder b = new StringBuilder();
		b.append(value.substring(i));
		if (b.length() == 0)
			b.append("0");
		dk.brics.automaton.Automaton s;
		if (minus)
			s = dk.brics.automaton.Automaton.makeChar('-');
		else
			s = dk.brics.automaton.Automaton.makeChar('+').optional();
		dk.brics.automaton.Automaton ws = Datatypes.getWhitespaceAutomaton();
		return dk.brics.automaton.Automaton.minimize(ws.concatenate(s.concatenate(dk.brics.automaton.Automaton.makeChar('0').repeat()).concatenate(dk.brics.automaton.Automaton.makeString(b.toString()))).concatenate(ws));
	}
	
	/**
	 * Constructs automaton that accept strings representing the given decimal number.
	 * Surrounding whitespace is permitted.
	 * @param value string representation of decimal number
	 */
	public static dk.brics.automaton.Automaton makeDecimalValue(String value) {
		boolean minus = false;
    	int i = 0;
    	while (i < value.length()) {
    		char c = value.charAt(i);
    		if (c == '-')
    			minus = true;
    		if ((c >= '1' && c <= '9') || c == '.')
    			break;
    		i++;
    	}
    	StringBuilder b1 = new StringBuilder();
    	StringBuilder b2 = new StringBuilder();
    	int p = value.indexOf('.', i);
    	if (p == -1)
    		b1.append(value.substring(i));
    	else {
    		b1.append(value.substring(i, p));
    		i = value.length() - 1;
	    	while (i > p) {
	    		char c = value.charAt(i);
	    		if (c >= '1' && c <= '9')
	    			break;
	    		i--;
	    	}
	    	b2.append(value.substring(p + 1, i + 1));
    	}
		if (b1.length() == 0)
			b1.append("0");
		dk.brics.automaton.Automaton s;
		if (minus)
			s = dk.brics.automaton.Automaton.makeChar('-');
		else
			s = dk.brics.automaton.Automaton.makeChar('+').optional();
		dk.brics.automaton.Automaton d;
		if (b2.length() == 0)
			d = dk.brics.automaton.Automaton.makeChar('.').concatenate(dk.brics.automaton.Automaton.makeChar('0').repeat(1)).optional();
		else
			d = dk.brics.automaton.Automaton.makeChar('.').concatenate(dk.brics.automaton.Automaton.makeString(b2.toString())).concatenate(dk.brics.automaton.Automaton.makeChar('0').repeat());
		dk.brics.automaton.Automaton ws = Datatypes.getWhitespaceAutomaton();
		return dk.brics.automaton.Automaton.minimize(ws.concatenate(s.concatenate(dk.brics.automaton.Automaton.makeChar('0').repeat()).concatenate(dk.brics.automaton.Automaton.makeString(b1.toString())).concatenate(d)).concatenate(ws));
	}
	
	/**
	 * Constructs deterministic automaton that matches strings that contain the given substring.
	 */
	public static dk.brics.automaton.Automaton makeStringMatcher(String s) {
		dk.brics.automaton.Automaton a = new Automaton();
		dk.brics.automaton.State[] states = new dk.brics.automaton.State[s.length() + 1];
		states[0] = a.initial;
		for (int i = 0; i < s.length(); i++)
			states[i+1] = new dk.brics.automaton.State();
		State f = states[s.length()];
		f.accept = true;
		f.transitions.add(new Transition(Character.MIN_VALUE, Character.MAX_VALUE, f));
		for (int i = 0; i < s.length(); i++) {
			Set<Character> done = new HashSet<Character>();
			char c = s.charAt(i);
			states[i].transitions.add(new Transition(c, states[i+1]));
			done.add(c);
			for (int j = i; j >= 1; j--) {
				char d = s.charAt(j-1);
				if (!done.contains(d) && s.substring(0, j-1).equals(s.substring(i-j+1, i))) {
					states[i].transitions.add(new Transition(d, states[j]));
					done.add(d);
				}
			}
			char[] da = new char[done.size()];
			int h = 0;
			for (char w : done)
				da[h++] = w;
			Arrays.sort(da);
			int from = Character.MIN_VALUE;
			int k = 0;
			while (from <= Character.MAX_VALUE) {
				while (k < da.length && da[k] == from) {
					k++;
					from++;
				}
				if (from <= Character.MAX_VALUE) {
					int to = Character.MAX_VALUE;
					if (k < da.length) {
						to = da[k]-1;
						k++;
					}
					states[i].transitions.add(new Transition((char)from, (char)to, states[0]));
					from = to+2;
				}
			}
		}
		a.deterministic = true;
		return a;
	}
}

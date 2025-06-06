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
 * Special automata operations.
 */
final public class SpecialOperations {
	
	private SpecialOperations() {}

	/**
	 * Reverses the language of the given (non-singleton) automaton while returning
	 * the set of new initial states.
	 */
	public static Set<dk.brics.automaton.State> reverse(dk.brics.automaton.Automaton a) {
		// reverse all edges
		HashMap<dk.brics.automaton.State, HashSet<Transition>> m = new HashMap<dk.brics.automaton.State, HashSet<Transition>>();
		Set<dk.brics.automaton.State> states = a.getStates();
		Set<dk.brics.automaton.State> accept = a.getAcceptStates();
		for (dk.brics.automaton.State r : states) {
			m.put(r, new HashSet<Transition>());
			r.accept = false;
		}
		for (dk.brics.automaton.State r : states)
			for (Transition t : r.getTransitions())
				m.get(t.to).add(new Transition(t.min, t.max, r));
		for (dk.brics.automaton.State r : states)
			r.transitions = m.get(r);
		// make new initial+final states
		a.initial.accept = true;
		a.initial = new dk.brics.automaton.State();
		for (dk.brics.automaton.State r : accept)
			a.initial.addEpsilon(r); // ensures that all initial states are reachable
		a.deterministic = false;
		return accept;
	}

	/**
	 * Returns an automaton that accepts the overlap of strings that in more than one way can be split into
	 * a left part being accepted by <code>a1</code> and a right part being accepted by
	 * <code>a2</code>.
	 */
	public static dk.brics.automaton.Automaton overlap(dk.brics.automaton.Automaton a1, dk.brics.automaton.Automaton a2) {
		dk.brics.automaton.Automaton b1 = a1.cloneExpanded();
		b1.determinize();
		acceptToAccept(b1);
		dk.brics.automaton.Automaton b2 = a2.cloneExpanded();
		reverse(b2);
		b2.determinize();
		acceptToAccept(b2);
		reverse(b2);
		b2.determinize();
		return b1.intersection(b2).minus(dk.brics.automaton.BasicAutomata.makeEmptyString());
	}
	
	private static void acceptToAccept(dk.brics.automaton.Automaton a) {
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		for (dk.brics.automaton.State r : a.getAcceptStates())
			s.addEpsilon(r);
		a.initial = s;
		a.deterministic = false;
	}
	
	/** 
	 * Returns an automaton that accepts the single chars that occur 
	 * in strings that are accepted by the given automaton. 
	 * Never modifies the input automaton.
	 */
	public static dk.brics.automaton.Automaton singleChars(dk.brics.automaton.Automaton a) {
		dk.brics.automaton.Automaton b = new dk.brics.automaton.Automaton();
		dk.brics.automaton.State s = new dk.brics.automaton.State();
		b.initial = s;
		dk.brics.automaton.State q = new dk.brics.automaton.State();
		q.accept = true;
		if (a.isSingleton()) 
			for (int i = 0; i < a.singleton.length(); i++)
				s.transitions.add(new Transition(a.singleton.charAt(i), q));
		else
			for (dk.brics.automaton.State p : a.getStates())
				for (Transition t : p.transitions)
					s.transitions.add(new Transition(t.min, t.max, q));
		b.deterministic = true;
		b.removeDeadTransitions();
		return b;
	}
	
	/**
	 * Returns an automaton that accepts the trimmed language of the given
	 * automaton. The resulting automaton is constructed as follows: 1) Whenever
	 * a <code>c</code> character is allowed in the original automaton, one or
	 * more <code>set</code> characters are allowed in the new automaton. 2)
	 * The automaton is prefixed and postfixed with any number of
	 * <code>set</code> characters.
	 * @param set set of characters to be trimmed
	 * @param c canonical trim character (assumed to be in <code>set</code>)
	 */
	public static dk.brics.automaton.Automaton trim(dk.brics.automaton.Automaton a, String set, char c) {
		a = a.cloneExpandedIfRequired();
		dk.brics.automaton.State f = new dk.brics.automaton.State();
		addSetTransitions(f, set, f);
		f.accept = true;
		for (dk.brics.automaton.State s : a.getStates()) {
			dk.brics.automaton.State r = s.step(c);
			if (r != null) {
				// add inner
				dk.brics.automaton.State q = new dk.brics.automaton.State();
				addSetTransitions(q, set, q);
				addSetTransitions(s, set, q);
				q.addEpsilon(r);
			}
			// add postfix
			if (s.accept)
				s.addEpsilon(f);
		}
		// add prefix
		dk.brics.automaton.State p = new dk.brics.automaton.State();
		addSetTransitions(p, set, p);
		p.addEpsilon(a.initial);
		a.initial = p;
		a.deterministic = false;
		a.removeDeadTransitions();
		a.checkMinimizeAlways();
		return a;
	}
	
	private static void addSetTransitions(dk.brics.automaton.State s, String set, dk.brics.automaton.State p) {
		for (int n = 0; n < set.length(); n++)
			s.transitions.add(new Transition(set.charAt(n), p));
	}
	
	/**
	 * Returns an automaton that accepts the compressed language of the given
	 * automaton. Whenever a <code>c</code> character is allowed in the
	 * original automaton, one or more <code>set</code> characters are allowed
	 * in the new automaton.
	 * @param set set of characters to be compressed
	 * @param c canonical compress character (assumed to be in <code>set</code>)
	 */
	public static dk.brics.automaton.Automaton compress(dk.brics.automaton.Automaton a, String set, char c) {
		a = a.cloneExpandedIfRequired();
		for (dk.brics.automaton.State s : a.getStates()) {
			dk.brics.automaton.State r = s.step(c);
			if (r != null) {
				// add inner
				dk.brics.automaton.State q = new dk.brics.automaton.State();
				addSetTransitions(q, set, q);
				addSetTransitions(s, set, q);
				q.addEpsilon(r);
			}
		}
		// add prefix
		a.deterministic = false;
		a.removeDeadTransitions();
		a.checkMinimizeAlways();
		return a;
	}
	
	/**
	 * Returns an automaton where all transition labels have been substituted.
	 * <p>
	 * Each transition labeled <code>c</code> is changed to a set of
	 * transitions, one for each character in <code>map(c)</code>. If
	 * <code>map(c)</code> is null, then the transition is unchanged.
	 * @param map map from characters to sets of characters (where characters 
	 *            are <code>Character</code> objects)
	 */
	public static dk.brics.automaton.Automaton subst(dk.brics.automaton.Automaton a, Map<Character, Set<Character>> map) {
		if (map.isEmpty())
			return a.cloneIfRequired();
		Set<Character> ckeys = new TreeSet<Character>(map.keySet());
		char[] keys = new char[ckeys.size()];
		int j = 0;
		for (Character c : ckeys)
			keys[j++] = c;
		a = a.cloneExpandedIfRequired();
		for (dk.brics.automaton.State s : a.getStates()) {
			Set<Transition> st = s.transitions;
			s.resetTransitions();
			for (Transition t : st) {
				int index = findIndex(t.min, keys);
				while (t.min <= t.max) {
					if (keys[index] > t.min) {
						char m = (char)(keys[index] - 1);
						if (t.max < m)
							m = t.max;
						s.transitions.add(new Transition(t.min, m, t.to));
						if (m + 1 > Character.MAX_VALUE)
							break;
						t.min = (char)(m + 1);
					} else if (keys[index] < t.min) {
						char m;
						if (index + 1 < keys.length)
							m = (char)(keys[++index] - 1);
						else
							m = Character.MAX_VALUE;
						if (t.max < m)
							m = t.max;
						s.transitions.add(new Transition(t.min, m, t.to));
						if (m + 1 > Character.MAX_VALUE)
							break;
						t.min = (char)(m + 1);
					} else { // found t.min in substitution map
						for (Character c : map.get(t.min))
							s.transitions.add(new Transition(c, t.to));
						if (t.min + 1 > Character.MAX_VALUE)
							break;
						t.min++;
						if (index + 1 < keys.length && keys[index + 1] == t.min)
							index++;
					}
				}
			}
		}
		a.deterministic = false;
		a.removeDeadTransitions();
		a.checkMinimizeAlways();
		return a;
	}

	/** 
	 * Finds the largest entry whose value is less than or equal to c, 
	 * or 0 if there is no such entry. 
	 */
	static int findIndex(char c, char[] points) {
		int a = 0;
		int b = points.length;
		while (b - a > 1) {
			int d = (a + b) >>> 1;
			if (points[d] > c)
				b = d;
			else if (points[d] < c)
				a = d;
			else
				return d;
		}
		return a;
	}
	
	/**
	 * Returns an automaton where all transitions of the given char are replaced by a string.
	 * @param c char
	 * @param s string
	 * @return new automaton
	 */
	public static dk.brics.automaton.Automaton subst(dk.brics.automaton.Automaton a, char c, String s) {
		a = a.cloneExpandedIfRequired();
		Set<StatePair> epsilons = new HashSet<StatePair>();
		for (dk.brics.automaton.State p : a.getStates()) {
			Set<Transition> st = p.transitions;
			p.resetTransitions();
			for (Transition t : st)
				if (t.max < c || t.min > c)
					p.transitions.add(t);
				else {
					if (t.min < c)
						p.transitions.add(new Transition(t.min, (char)(c - 1), t.to));
					if (t.max > c)
						p.transitions.add(new Transition((char)(c + 1), t.max, t.to));
					if (s.length() == 0)
						epsilons.add(new StatePair(p, t.to));
					else {
						dk.brics.automaton.State q = p;
						for (int i = 0; i < s.length(); i++) {
							dk.brics.automaton.State r;
							if (i + 1 == s.length())
								r = t.to;
							else
								r = new dk.brics.automaton.State();
							q.transitions.add(new Transition(s.charAt(i), r));
							q = r;
						}
					}
				}
		}
		a.addEpsilons(epsilons);
		a.deterministic = false;
		a.removeDeadTransitions();
		a.checkMinimizeAlways();
		return a;
	}
	
	/**
	 * Returns an automaton accepting the homomorphic image of the given automaton
	 * using the given function.
	 * <p>
	 * This method maps each transition label to a new value.
	 * <code>source</code> and <code>dest</code> are assumed to be arrays of
	 * same length, and <code>source</code> must be sorted in increasing order
	 * and contain no duplicates. <code>source</code> defines the starting
	 * points of char intervals, and the corresponding entries in
	 * <code>dest</code> define the starting points of corresponding new
	 * intervals.
	 */
	public static dk.brics.automaton.Automaton homomorph(dk.brics.automaton.Automaton a, char[] source, char[] dest) {
		a = a.cloneExpandedIfRequired();
		for (dk.brics.automaton.State s : a.getStates()) {
			Set<Transition> st = s.transitions;
			s.resetTransitions();
			for (Transition t : st) {
				int min = t.min;
				while (min <= t.max) {
					int n = findIndex((char)min, source);
					char nmin = (char)(dest[n] + min - source[n]);
					int end = (n + 1 == source.length) ? Character.MAX_VALUE : source[n + 1] - 1;
					int length;
					if (end < t.max)
						length = end + 1 - min;
					else
						length = t.max + 1 - min;
					s.transitions.add(new Transition(nmin, (char)(nmin + length - 1), t.to));
					min += length;
				}
			}
		}
		a.deterministic = false;
		a.removeDeadTransitions();
		a.checkMinimizeAlways();
		return a;
	}
	
	/**
	 * Returns an automaton with projected alphabet. The new automaton accepts
	 * all strings that are projections of strings accepted by the given automaton
	 * onto the given characters (represented by <code>Character</code>). If
	 * <code>null</code> is in the set, it abbreviates the intervals
	 * u0000-uDFFF and uF900-uFFFF (i.e., the non-private code points). It is
	 * assumed that all other characters from <code>chars</code> are in the
	 * interval uE000-uF8FF.
	 */
	public static dk.brics.automaton.Automaton projectChars(dk.brics.automaton.Automaton a, Set<Character> chars) {
		Character[] c = chars.toArray(new Character[chars.size()]);
		char[] cc = new char[c.length];
		boolean normalchars = false;
		for (int i = 0; i < c.length; i++)
			if (c[i] == null)
				normalchars = true;
			else
				cc[i] = c[i];
		Arrays.sort(cc);
		if (a.isSingleton()) {
			for (int i = 0; i < a.singleton.length(); i++) {
				char sc = a.singleton.charAt(i);
				if (!(normalchars && (sc <= '\udfff' || sc >= '\uf900') || Arrays.binarySearch(cc, sc) >= 0))
					return BasicAutomata.makeEmpty();
			}
			return a.cloneIfRequired();
		} else {
			HashSet<StatePair> epsilons = new HashSet<StatePair>();
			a = a.cloneExpandedIfRequired();
			for (dk.brics.automaton.State s : a.getStates()) {
				HashSet<Transition> new_transitions = new HashSet<Transition>();
				for (Transition t : s.transitions) {
					boolean addepsilon = false;
					if (t.min < '\uf900' && t.max > '\udfff') {
						int w1 = Arrays.binarySearch(cc, t.min > '\ue000' ? t.min : '\ue000');
						if (w1 < 0) {
							w1 = -w1 - 1;
							addepsilon = true;
						}
						int w2 = Arrays.binarySearch(cc, t.max < '\uf8ff' ? t.max : '\uf8ff');
						if (w2 < 0) {
							w2 = -w2 - 2;
							addepsilon = true;
						}
						for (int w = w1; w <= w2; w++) {
							new_transitions.add(new Transition(cc[w], t.to));
							if (w > w1 && cc[w - 1] + 1 != cc[w])
								addepsilon = true;
						}
					}
					if (normalchars) {
						if (t.min <= '\udfff')
							new_transitions.add(new Transition(t.min, t.max < '\udfff' ? t.max : '\udfff', t.to));
						if (t.max >= '\uf900')
							new_transitions.add(new Transition(t.min > '\uf900' ? t.min : '\uf900', t.max, t.to));
					} else if (t.min <= '\udfff' || t.max >= '\uf900')
						addepsilon = true;
					if (addepsilon)
						epsilons.add(new StatePair(s, t.to));
				}
				s.transitions = new_transitions;
			}
			a.reduce();
			a.addEpsilons(epsilons);
			a.removeDeadTransitions();
			a.checkMinimizeAlways();
			return a;
		}
	}
	
	/**
	 * Returns true if the language of this automaton is finite.
	 */
	public static boolean isFinite(dk.brics.automaton.Automaton a) {
		if (a.isSingleton())
			return true;
		return isFinite(a.initial, new HashSet<dk.brics.automaton.State>(), new HashSet<dk.brics.automaton.State>());
	}
	
	/** 
	 * Checks whether there is a loop containing s. (This is sufficient since 
	 * there are never transitions to dead states.) 
	 */
	private static boolean isFinite(dk.brics.automaton.State s, HashSet<dk.brics.automaton.State> path, HashSet<dk.brics.automaton.State> visited) {
		path.add(s);
		for (Transition t : s.transitions)
			if (path.contains(t.to) || (!visited.contains(t.to) && !isFinite(t.to, path, visited)))
				return false;
		path.remove(s);
		visited.add(s);
		return true;
	}
	
	/**
	 * Returns the set of accepted strings of the given length.
	 */
	public static Set<String> getStrings(dk.brics.automaton.Automaton a, int length) {
		HashSet<String> strings = new HashSet<String>();
		if (a.isSingleton() && a.singleton.length() == length)
			strings.add(a.singleton);
		else if (length >= 0)
			getStrings(a.initial, strings, new StringBuilder(), length);
		return strings;
	}
	
	private static void getStrings(dk.brics.automaton.State s, Set<String> strings, StringBuilder path, int length) {
		if (length == 0) {
			if (s.accept)
				strings.add(path.toString());
		} else 
			for (Transition t : s.transitions)
				for (int n = t.min; n <= t.max; n++) {
					path.append((char)n);
					getStrings(t.to, strings, path, length - 1);
					path.deleteCharAt(path.length() - 1);
				}
	}
	
	/**
	 * Returns the set of accepted strings, assuming this automaton has a finite
	 * language. If the language is not finite, null is returned.
	 */
	public static Set<String> getFiniteStrings(dk.brics.automaton.Automaton a) {
		HashSet<String> strings = new HashSet<String>();
		if (a.isSingleton())
			strings.add(a.singleton);
		else if (!getFiniteStrings(a.initial, new HashSet<dk.brics.automaton.State>(), strings, new StringBuilder(), -1))
			return null;
		return strings;
	}
	
	/**
	 * Returns the set of accepted strings, assuming that at most <code>limit</code>
	 * strings are accepted. If more than <code>limit</code> strings are
	 * accepted, null is returned. If <code>limit</code>&lt;0, then this
	 * methods works like {@link #getFiniteStrings(dk.brics.automaton.Automaton)}.
	 */
	public static Set<String> getFiniteStrings(dk.brics.automaton.Automaton a, int limit) {
		HashSet<String> strings = new HashSet<String>();
		if (a.isSingleton()) {
			if (limit > 0)
				strings.add(a.singleton);
			else
				return null;
		} else if (!getFiniteStrings(a.initial, new HashSet<dk.brics.automaton.State>(), strings, new StringBuilder(), limit))
			return null;
		return strings;
	}

	/** 
	 * Returns the strings that can be produced from the given state, or false if more than 
	 * <code>limit</code> strings are found. <code>limit</code>&lt;0 means "infinite". 
	 * */
	private static boolean getFiniteStrings(dk.brics.automaton.State s, HashSet<dk.brics.automaton.State> pathstates, HashSet<String> strings, StringBuilder path, int limit) {
		pathstates.add(s);
		for (Transition t : s.transitions) {
			if (pathstates.contains(t.to))
				return false;
			for (int n = t.min; n <= t.max; n++) {
				path.append((char)n);
				if (t.to.accept) {
					strings.add(path.toString());
					if (limit >= 0 && strings.size() > limit)
						return false;
				}
				if (!getFiniteStrings(t.to, pathstates, strings, path, limit))
					return false;
				path.deleteCharAt(path.length() - 1);
			}
		}
		pathstates.remove(s);
		return true;
	}
	
	/**
	 * Returns the longest string that is a prefix of all accepted strings and
	 * visits each state at most once.
	 * @return common prefix
	 */
	public static String getCommonPrefix(dk.brics.automaton.Automaton a) {
		if (a.isSingleton())
			return a.singleton;
		StringBuilder b = new StringBuilder();
		HashSet<dk.brics.automaton.State> visited = new HashSet<dk.brics.automaton.State>();
		dk.brics.automaton.State s = a.initial;
		boolean done;
		do {
			done = true;
			visited.add(s);
			if (!s.accept && s.transitions.size() == 1) {
				Transition t = s.transitions.iterator().next();
				if (t.min == t.max && !visited.contains(t.to)) {
					b.append(t.min);
					s = t.to;
					done = false;
				}
			}
		} while (!done);
		return b.toString();
	}
	
	/**
	 * Prefix closes the given automaton.
	 */
	public static void prefixClose(dk.brics.automaton.Automaton a) {
		for (State s : a.getStates())
			s.setAccept(true);
		a.clearHashCode();
		a.checkMinimizeAlways();
	}
	
	/**
	 * Constructs automaton that accepts the same strings as the given automaton
	 * but ignores upper/lower case of A-F.
	 * @param a automaton
	 * @return automaton
	 */
	public static dk.brics.automaton.Automaton hexCases(dk.brics.automaton.Automaton a) {
		Map<Character,Set<Character>> map = new HashMap<Character,Set<Character>>();
		for (char c1 = 'a', c2 = 'A'; c1 <= 'f'; c1++, c2++) {
			Set<Character> ws = new HashSet<Character>();
			ws.add(c1);
			ws.add(c2);
			map.put(c1, ws);
			map.put(c2, ws);
		}
		dk.brics.automaton.Automaton ws = Datatypes.getWhitespaceAutomaton();
		return ws.concatenate(a.subst(map)).concatenate(ws);		
	}
	
	/**
	 * Constructs automaton that accepts 0x20, 0x9, 0xa, and 0xd in place of each 0x20 transition
	 * in the given automaton.
	 * @param a automaton
	 * @return automaton
	 */
	public static dk.brics.automaton.Automaton replaceWhitespace(Automaton a) {
		Map<Character,Set<Character>> map = new HashMap<Character,Set<Character>>();
		Set<Character> ws = new HashSet<Character>();
		ws.add(' ');
		ws.add('\t');
		ws.add('\n');
		ws.add('\r');
		map.put(' ', ws);
		return a.subst(map);
	}
}

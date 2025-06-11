
package com.lmh.coverage.entity;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.Transition;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static dk.brics.automaton.Transition.appendCharString;


public class transitionDefine implements Serializable, Cloneable {

	static final long serialVersionUID = 40001;


	char min;
	char max;

	public transitionDefine() {
	}

	public transitionDefine(char c)	{
		min = max = c;

	}

	public transitionDefine(char min, char max)	{
		if (max < min) {
			char t = max;
			max = min;
			min = t;
		}
		this.min = min;
		this.max = max;
	}

	public transitionDefine(Transition transition)	{
		this.min = transition.getMin();
		this.max = transition.getMax();
	}

	public char getMin() {
		return min;
	}
	
	public char getMax() {
		return max;
	}

	public boolean including(transitionDefine define){
		if(this.max >= define.max && this.min <= define.min)
			return true;
		else
			return false;
	}

	public static Set<transitionDefine> getUnionDefine(Set<transitionDefine> set){
		Set<transitionDefine> defines = new HashSet<>();
		Iterator<transitionDefine> iterator = set.iterator();

		while (iterator.hasNext()) {
			transitionDefine transitionDefine= iterator.next();
			if(transitionDefine.getMax() == transitionDefine.getMin()){
				transitionDefine newDefine = new transitionDefine(transitionDefine.min);
				defines.add(newDefine);
				iterator.remove();
			}
		}
		if (!set.isEmpty()){
			transitionDefine minDefine = getMinDefine(set);
			char maxx = minDefine.getMax();

			iterator = set.iterator();
			while (iterator.hasNext()){
				transitionDefine transitionDefine= iterator.next();

				if(transitionDefine.getMax() > maxx){
					maxx = transitionDefine.getMax();
				}
			}

			while (maxx >= minDefine.max){
				boolean flag = false;
				Iterator<transitionDefine> setIterator = set.iterator();
				while (setIterator.hasNext()){
					transitionDefine transitionDefine= setIterator.next();
					if(minDefine.getMax()+1 >= transitionDefine.getMin() && minDefine.getMax()+1 <= transitionDefine.getMax() && minDefine.getMin() >= transitionDefine.getMin()){
						minDefine.max = (char)(minDefine.getMax()+1);
						flag = true;
						break;
					}else if (minDefine.getMax()+1 > transitionDefine.getMax()){
						setIterator.remove();
					}
				}
				if(!flag){
					if (minDefine.max != minDefine.min){
						defines.add(minDefine);
						minDefine = new transitionDefine((char)(minDefine.getMax()+1));
					}else {
						minDefine = getMinDefine(set);
						if(minDefine == null) break;
					}
				}
			}
		}

		Iterator<transitionDefine> transitionDefineIterator = defines.iterator();
		while (transitionDefineIterator.hasNext()){
			transitionDefine transitionDefine = transitionDefineIterator.next();
			if(transitionDefine.getMin()==transitionDefine.getMax()){
				for (transitionDefine tr:defines){
					if(transitionDefine != tr && transitionDefine.getMax() <= tr.getMax() && transitionDefine.getMax() >= tr.getMin() ){
						transitionDefineIterator.remove();
						break;
					}
				}
			}
		}
		return defines;
	}

	static transitionDefine getMinDefine(Set<transitionDefine> set){
		if (set.isEmpty()) return null;
		Iterator<transitionDefine> iterator = set.iterator();

		transitionDefine first = iterator.next();
		transitionDefine minDefine = new transitionDefine(first.min,first.max);
		iterator = set.iterator();
		while (iterator.hasNext()){
			transitionDefine transitionDefine= iterator.next();
			if(transitionDefine.getMin() <= minDefine.getMin()){
				minDefine = new transitionDefine(transitionDefine.getMin(),transitionDefine.getMax());
			}
		}
		return minDefine;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof transitionDefine) {
			transitionDefine t = (transitionDefine)obj;
			return t.min == min && t.max == max;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return min * 2 + max * 3;
	}

	@Override
	public transitionDefine clone() {
		try {
			return (transitionDefine)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		appendCharString(min, b);
		if (min != max) {
			b.append("-");
			appendCharString(max, b);
		}
		return b.toString();
	}
}

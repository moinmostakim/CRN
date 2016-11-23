/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queues;

/**
 *
 * @author asus
 */
public class PriorityQueue extends Queue
{
    /**
     *	Note that Enqueue is overloaded!
     */
    public void Enqueue(Object a, int v)
    {
	Node t = new Node(a, v);
	if(hd == null) {
	    hd = tl = t;
	    t.prev = t.next = null;
	} else {
	    Node ptr;
	    for(ptr = tl; ptr != null && ptr.val > v; ptr = ptr.next)
		;

	    if(ptr == null) {
		hd.next = t;
		t.prev = hd;
		t.next = null;
		hd = t;
	    } else 
	    if(ptr == tl) {
		tl.prev = t;
		t.next = tl;
		t.prev = null;
		tl = t;
	    } else {
		t.next = ptr;
		t.prev = ptr.prev;
		ptr.prev.next = t;
		ptr.prev = t;
	    }
	}
    }	// Enqueue

    public int FrontKey()
    {
	if (hd == null) {
	    System.err.println("Attempting to examine the front key " +
			       "of an empty queue");
	    System.exit(-1);
	}
	return hd.val;
    }

    public int RearKey()
    {
	if (tl == null) {
	    System.err.println("Attempting to examine the rear key " +
			       "of an empty queue");
	    System.exit(-1);
	}
	return tl.val;
    }

    public void Print()
    {
	for(Node ptr = hd; ptr != null; ptr = ptr.prev)
	    System.out.println("Item " + ptr.item + 
		" and val is " + ptr.val);
    }
};


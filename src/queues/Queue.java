package queues;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author asus
 */
class Node 
{
    Node  	prev;
    Node  	next;
    Object      item;
    int	 	val;

    public Node(Object o) 
    {
	this(o, 0);
    }

    public Node(Object o, int v) 
    {
	prev = next = null;
	item = o;
	val = v;
    }
}

public class Queue
{
    Node	hd, tl;

    public Queue()	
    {
	hd = tl = null;
    }

    public void Enqueue(Object a)
    {
	Node t = new Node(a);
	t.next = tl;
	t.prev = null;
	if (tl != null) 
	    tl.prev = t;
	tl = t;
	if (hd == null)
	    hd = tl;
    }	// Enqueue

    public Object Dequeue()
    {
	if (hd == null)
	{
	    System.err.println("Attempting to dequeue the front " +
				"of an empty queue");
	    System.exit(-1);
	}
        Object temp = hd.item;
	hd = hd.prev;
	if (hd == null)
	    tl = null;
	else
	    hd.next = null;
	return temp;
    }	// Dequeue

    public boolean Find(Object a)
    {
	Node ptr;
        for(ptr = hd; ptr != null; ptr = ptr.next)
	   if (ptr.item.equals(a))
		return true;
	return false;
    }

    public boolean Remove(Object a)
    {
	Node ptr;
        for(ptr = hd; ptr != null; ptr = ptr.next)
	    if (ptr.item.equals(a)) {
		if (hd == ptr) {
		    hd = ptr.next;
		    if (ptr.next != null)
			ptr.next.prev = null;
		} else 
		if (tl == ptr) {
		    ptr.prev.next = null;
		    tl = ptr.prev;
		} else {
		    ptr.prev.next = ptr.next;
		    ptr.next.prev = ptr.prev;
		}
		return true;
	    }
	return false;
    }

    public Object Front()
    {
	if (hd == null) {
	    System.err.println("Attempting to examine the front " +
			       "of an empty queue");
	    System.exit(-1);
	}
	return hd.item;
    }

    public Object Rear()
    {
	if (tl == null) {
	    System.err.println("Attempting to examine the front " +
			       "of an empty queue");
	    System.exit(-1);
	}
	return tl.item;
    }

    public int  Length()
    {
	int  l = 0;
	Node ptr;
	for(ptr = hd; ptr != null; ptr = ptr.prev, l++);
	    return l;
    }

    public boolean IsEmpty()
    {
	return hd == null;
    }

    public void Print()
    {
	for(Node ptr = hd; ptr != null; ptr = ptr.prev)
	    System.out.println("Item " + ptr.item);
    }
}

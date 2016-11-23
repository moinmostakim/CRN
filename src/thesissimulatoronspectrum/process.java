/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thesissimulatoronspectrum;

/**
 *
 * @author asus
 */
import queues.*;

/**
 *	abstract process class
 *	provides simple Simula-like scheduling functions <p>
 *	void body() needs to be overwritten
**/
abstract public class process implements Runnable {

    /** name of this process as given in constructor */
    public String name;	

    /* state of this process: IDLE/RUNNING/TERMINATED */
    int	   state;		

    /**
     * a process serves as its own event notice;
     * ev_time holds its scheduled event time;
     */
    int ev_time;

    /** thread this process is running in */
    Thread	mythread;

    /** 
     * process constructor 
     * @param name name of the process
     */
    public process(String s)		
    {
	name = s;
	ev_time = -1;  		/* unschedule */
	state = scheduler.IDLE;
	scheduler.live_threads++;
	mythread = new Thread(this);
	mythread.start();
    }

    abstract void body();

    /**
     *	block until made runnable & notified
     */
    void block()
    {
	synchronized(this) {
	    try{ 
		while (state != scheduler.RUNNING && 
		       state != scheduler.TERMINATED)
		    wait();}
	    catch(InterruptedException e) {}
	}
    }

    /**
     *	main body for thread
     */
    @Override
    public synchronized void run() 
    {
	block();
	body();
    }

    /** test if process is active, i.e. running or scheduled */
    public boolean active()  		
    {
  	return state == scheduler.RUNNING || ev_time >= 0;
    }

    /** test for scheduled (has an event notice; false if running) */
    public boolean scheduled()  	
    {
	return ev_time >= 0;
    }

    /** test if this process is terminated */
    public boolean terminated()	
    {
	return (state == scheduler.TERMINATED); 
    }

    /** make this process idle (unscheduled and not running)  */
    public void cancel()  		
    {
	if (scheduled())
	    scheduler.unschedule(this);
    }

    /** 
     * make process unrunnable (no longer activatable); <p>
     * note that there is no terminate_and_delete() as Java
     * doesn't need to delete objects; similarly, you cannot rely
     * on clean-up being done in the destructor
     */
    public void terminate()  		
    {
	cancel();
	state = scheduler.TERMINATED;
	ev_time = -1;
	if (scheduler.current == this) 
	    scheduler.next_event(true);
    }

    /** 
     * enqueue reference to this process in queue q, and passivate <p>
     * note that there is no enqueue_proc/dequeue_proc anymore; <p>
     * use Queue Enqueue/Dequeue instead
     *
     * @see Queue
     * @param q queue in which to insert process
     */
    public void waitq(Queue q)  	
    {
	q.Enqueue(this);
	scheduler.passivate();
    }

    /**
     *	print name, state, and scheduled event time for process
     */
    public void print() 
    {
	System.out.println(name + ": " +
		(state == scheduler.IDLE ? "IDLE" : 
		 state == scheduler.RUNNING ? "RUNNING" : "TERMINATED") + 
		" ev_time = " + ev_time);
    }
}


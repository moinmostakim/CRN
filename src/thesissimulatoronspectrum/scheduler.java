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
/*
 * 	Simula-like Package (scheduler.java)
 *
 *	G. Back	(gback@cs.utah.edu)
 *	University of Utah
 *	December 1995
 *
 *	C++ version by G. Lindstrom, Jan 1995
 *
 *      Simula terminology used throughout	
 */

/* use import sim.* to include this package */
import queues.*;

/**
 * scheduler class
 * <p>
 * provides basic scheduling functions
 *
 * Note: all methods and data in this class are static
 */
public class scheduler {

    /**
     * global simulated time
     */
    public static int clock = 0;

    /* list of scheduled events */
    static PriorityQueue event_list = new PriorityQueue();

    /**
     * reference to current process
     */
    public static process current = null;

    /* that's the way you do enums in Java */
    static final int IDLE = 1;
    static final int RUNNING = 2;
    static final int TERMINATED = 3;

    /**
     * signal a fatal error and die
     */
    public static void fatal_error(String s) {
        System.err.println("FATAL: " + s);
        System.exit(-1);
    }

    /**
     * schedule for activation after 'delay' time units, and passivate
     *
     * @param delay time for which to hold
     */
    public static void hold(int delay) {
        reactivate(current, delay);
        next_event(false);
    }

    /**
     * make current process non-active
     */
    public static void passivate() {
        if (current.scheduled()) {
            unschedule(current);
        }
        if (current.terminated()) {
            current.print();
            fatal_error("Passivate of terminated process.");
        }
        next_event(false);
    }

    /**
     * like activate(p, 0);
     */
    public static void activate(process p) {
        activate(p, 0);
    }

    /**
     * if p is not running or scheduled, schedule it with delay time; otherwise,
     * do nothing
     *
     * @param p process to be activated
     * @param time time after which to schedule p
     */
    public static void activate(process p, int time) {
        if (p.active()) {
            return;
        }
        if (p.state == TERMINATED) {
            p.print();
            fatal_error("Activate of terminated process.");
        }

        /* p must be not active */
        schedule(p, clock + time);
    }

    /**
     * like reactivate(p, 0);
     */
    public static void reactivate(process p) {
        reactivate(p, 0);
    }

    /**
     * if p is not running or scheduled, schedule it with delay t; if p is
     * running or scheduled, reschedule it with delay t
     *
     * @param p process to be activated
     * @param t time after which to activate p
     */
    static public void reactivate(process p, int t) {
        if (p.state == TERMINATED) {
            p.print();
            fatal_error("Reactivate of terminated process.");
        }

        if (p.scheduled()) {
            unschedule(p);
        }
        schedule(p, clock + t);
    }

    /**
     * start simulation (event queue must be non-empty)
     */
    public static void run_simulation() {
        current = null;
        next_event(false);
        System.out.println("Simulation done");
    }

    // end of public interface 
    /**
     * remove event notice for process
     */
    static void unschedule(process p) {
        if (p.ev_time < 0) {
            p.print();
            fatal_error("Unschedule of unscheduled process");
        }

        if (!event_list.Remove(p)) {
            p.print();
            fatal_error("Active process not found on event list.");
        } else {
            p.state = IDLE;
            p.ev_time = -1;
        }
    }

    /**
     * insert p in event list, scheduled to execute at given time
     */
    static void schedule(process p, int time) {
        if (p.state == TERMINATED || p.ev_time >= 0) {
            p.print();
            print_ev_list();
            fatal_error("Schedule of terminated or active process.");
        }
        if (time < clock) {
            p.print();
            print_ev_list();
            System.out.println("(ev_time=" + time + ", clock=" + clock + ")");
            fatal_error("Event scheduled in past");
        }

        p.ev_time = time;
        event_list.Enqueue(p, time);
    }

    /* make next event happen */
    static void next_event(boolean die) {
        /* if run out of events, signal main thread to return */
        if (event_list.IsEmpty()) {
            live_threads = 0;
            synchronized (done) {
                done.notify();
            }
            return;
        }

        /* that shouldn't happen */
        if (clock > ((process) event_list.Front()).ev_time) {
            print_ev_list();
            fatal_error("Event list not time ordered.");
        }

        /* get next event and advance clock */
        process next = (process) event_list.Dequeue();
        clock = next.ev_time;
        next.ev_time = -1;

        /* schedule next thread */
        pass_baton(next, die);
    }

    static Object done = 0; // something to wait on
    static int live_threads = 0;	// number of processes

    /**
     * pass_baton() to support coroutines
     *
     * @param p process to be resumed
     */
    public static void pass_baton(process p) {
        pass_baton(p, false);
    }

    /**
     * pass baton to next process
     *
     * @param p process to be resumed
     * @param die caller dies if true
     */
    public static void pass_baton(process p, boolean die) {
        process caller = current;
        if (p.state == TERMINATED) {
            fatal_error("Trying to pass baton to terminated thread");
        }

        current = p;
        if (die) {
            /* if a thread terminates, signal main thread */
            caller.state = TERMINATED;
            live_threads--;
            synchronized (done) {
                done.notify();
            }
        }

        /* don't do that if passing baton to same thread */
        if (caller == null || !caller.equals(p)) {
            /* give next thread a go */
            synchronized (p) {
                p.state = RUNNING;
                p.notify();
            }

            /* called by main thread */
            if (caller == null) {
                /* wait till no more live threads */
                synchronized (done) {
                    try {
                        while (live_threads > 0) {
                            done.wait();
                        }
                    } catch (InterruptedException e) {
                    }
                    return;
                }
            }

            /* wait for someone to pass back the baton */
            if (!die) {
                caller.state = IDLE;
                caller.block();
            } else {
                caller.mythread.stop();
            }
        }
    }

    /*  event list print function */
    static void print_ev_list() {
        System.out.println("Event list at time " + clock + ":");
        event_list.Print();
        System.out.println("End of event list\n");
    }
}

package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast that is issued when a tick is iterated
 */
public class TickBroadcast implements Broadcast {

    // fields

    private final int tick;

    // constructor

    public TickBroadcast (int tick){

        this.tick = tick;
    }

    // methods

    public int getTick(){

        return tick;
    }
}

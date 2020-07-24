package de.hshannover.dqgui.core.concurrency;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Sequence for enumerating task threads.
 *
 * @author Marc Herschel
 *
 */
final class Sequence {
    static final AtomicInteger VALUE = new AtomicInteger();

    private Sequence() {}
}

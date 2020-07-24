package de.hshannover.dqgui.core.util.comparators;

import java.util.Comparator;
import de.hshannover.dqgui.execution.model.DSLComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import se.sawano.java.text.AlphanumericComparator;

public class DSLComponentComparator implements Comparator<DSLComponent> {
    private static final DSLComponentComparator INSTANCE = new DSLComponentComparator();
    private static final AlphanumericComparator NATURAL_ORDER = new AlphanumericComparator();

    private DSLComponentComparator() {}

    public static DSLComponentComparator getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
    public int compare(DSLComponent o1, DSLComponent o2) {
        return NATURAL_ORDER.compare(o1.getIdentifier().toLowerCase(), o2.getIdentifier().toLowerCase());
    }

}

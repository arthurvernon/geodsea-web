package com.geodsea.pub.domain.type;

/**
 * A simple to recognize (from a reasonable distance) classification of vessels.
 * <p>
 *     Types are either powered or not.
 * </p>
 */
public enum VesselType {

    /**
     * A boat where there is a seating section in front of the windscreen.
     */
    BOW_RIDER(true),

    /**
     * Enclosed section in the front of the boat, e.g. with bunks.
     */
    HALF_CABIN(true),

    /**
     * Enclosed centre console with the ability to walk abound the deck.
     */
    WALK_AROUND(true),

    /**
     * Basic kind of boat with a steering wheel and console suitable for fishing/diving with lots of surronding deck space.
     */
    CENTER_CONSOLE(true),

    /**
     * No superstructure, like and aluminium of riberglass dinghy.
     */
    OPEN_BOAT(true),

    /*
    * larger vessel with an elevated platform from which to skipper the vessel from.
     */
    FLY_BRIDGE(true),

    /**
     * Motorised still but limited for one or two people, e.g. a Jet ski
     */
    PERSONAL_WATER_CRAFT(true),

    /**
     * Like a racing yacht - has no cockpit or superstructure, e.g. catemarans or monohull wilth self draining deck.
     */
    OPEN_COCKPIT (false),

    /**
     * Standard sailing/cruisiung yacht with sleeping quarters
     */
    CABIN(false),

    /**
     * Non-powered small open vessell.
     */
    ROW_BOAT(false),

    /**
     * Any paddle craft.
     */
    SEA_KAYAK(false);

    private boolean powered;

    private VesselType(boolean powered)
    {
        this.powered = powered;
    }

    public boolean isPowered()
    {
        return powered;
    }
}

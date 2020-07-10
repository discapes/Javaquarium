package com.javaquarium;

public enum Event {
    LOGIN, /* Assume user is logged in, fish not set yet. */
    TICKRATECHANGE,
    CHARTSETTINGCHANGE,
    LOGOUT /* Timers should  be cancelled, aquariumService cleared */

}

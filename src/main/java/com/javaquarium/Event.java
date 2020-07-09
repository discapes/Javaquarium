package com.javaquarium;

public enum Event {
    STAGEREADY, /* (Stage primaryStage) */
    LOGIN, /* Assume user is logged in, fish not set yet. */
    LOGOUT; /* Timers should  be cancelled, aquariumService cleared */

}

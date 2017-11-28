package com.SSUAndroidProject.fairy;

import com.squareup.otto.Bus;

/**
 * Created by 박PC on 2017-11-08.
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}

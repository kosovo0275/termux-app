package com.termux.app;

class AutoRepeatDefault
{
    // Delay between initial ACTION_DOWN event and first onClickListener call.
    public static final int initialDelayInMilliseconds = 50;

    // Delay between each subsequent onClickListener call.
    public static final int repeatPeriodInMilliseconds = 300;

    private AutoRepeatDefault() {
    }
}

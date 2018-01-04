package com.devphill.music.data.annotations;

import android.support.annotation.IntDef;

@IntDef(value = {PrimaryTheme.GRAY, PrimaryTheme.RED, PrimaryTheme.ORANGE, PrimaryTheme.YELLOW,
        PrimaryTheme.GREEN, PrimaryTheme.CYAN, PrimaryTheme.PURPLE, PrimaryTheme.BLACK,
        PrimaryTheme.BLUE})
public @interface PrimaryTheme {
    int GRAY = 0;
    int RED = 1;
    int ORANGE = 2;
    int YELLOW = 3;
    int GREEN = 4;
    int CYAN = 5;
    int PURPLE = 6;
    int BLACK = 7;
    int BLUE = 8;
}

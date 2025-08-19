package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class NoXPBuff extends FlavourBuff {
    {
        type = buffType.NEGATIVE;
    }

    @Override
    public int icon() {
        return BuffIndicator.NONE;
    }

    @Override
    public boolean attachTo(Char target) {
        return super.attachTo(target);
    }
}

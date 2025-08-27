package jp.mincra.mincramagics.gui;

import lombok.Builder;

@Builder
public class BuildContext {
    private final boolean isFirstRender;

    public BuildContext(boolean isFirstRender) {
        this.isFirstRender = isFirstRender;
    }

    public boolean isFirstRender() {
        return isFirstRender;
    }
}

package InfoHUD.ClassicBar.Renderer;

import net.minecraft.util.ResourceLocation;

public class ProgressBarBuilder {

    public enum Side {
        LEFT,
        RIGHT,
        NONE
    }

    public enum NumberFormat {
        FRACTION,
        PERCENTAGE,
        CURRENT,
        MAX,
        NONE
    }

    public enum AnimationStyle {
        NONE,
        SMOOTH,
        BOUNCE,
        ELASTIC
    }

    float progress, displayProgress, maxProgress, currentProgress;
    int x, y, width, height;
    int backgroundColor, fillColor, borderColor, textColor;
    Side textSide, iconSide;
    NumberFormat numberFormat;
    ResourceLocation icon;
    int iconWidth, iconHeight;
    float iconU, iconV, iconU2, iconV2;
    boolean showBackground, showBorder, showGradient;
    int borderWidth;
    AnimationStyle animationStyle;
    float animationSpeed;
    long lastUpdateTime;
    float damageFlash;
    boolean firstRender;

    public ProgressBarBuilder(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.progress = 0.0f;
        this.displayProgress = 0.0f;
        this.maxProgress = 100;
        this.currentProgress = 0;
        this.backgroundColor = 0xFF444444;
        this.fillColor = 0xFF00FF00;
        this.borderColor = 0xFF000000;
        this.textSide = Side.RIGHT;
        this.iconSide = Side.NONE;
        this.numberFormat = NumberFormat.FRACTION;
        this.textColor = 0xFFFFFFFF;
        this.showBackground = true;
        this.showBorder = true;
        this.borderWidth = 1;
        this.animationStyle = AnimationStyle.NONE;
        this.animationSpeed = 0.15f;
        this.lastUpdateTime = System.currentTimeMillis();
        this.damageFlash = 0.0f;
        this.showGradient = true;
        this.firstRender = true;
    }

    public ProgressBarBuilder setProgress(float current, float max) {
        float newProgress = max > 0 ? current / max : 0;
        if (newProgress < this.progress && this.progress > 0) {
            this.damageFlash = 1.0f;
        }
        this.currentProgress = current;
        this.maxProgress = max;
        this.progress = Math.max(0, Math.min(1, newProgress));
        if (firstRender) {
            this.displayProgress = this.progress;
            firstRender = false;
        }
        return this;
    }

    public ProgressBarBuilder setProgress(float progress) {
        return setProgress(progress * maxProgress, maxProgress);
    }

    public ProgressBarBuilder setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public ProgressBarBuilder setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public ProgressBarBuilder setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public ProgressBarBuilder setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public ProgressBarBuilder setTextSide(Side side) {
        this.textSide = side;
        return this;
    }

    public ProgressBarBuilder setNumberFormat(NumberFormat format) {
        this.numberFormat = format;
        return this;
    }

    public ProgressBarBuilder setIcon(ResourceLocation icon, int width, int height, float u, float v, float u2,
        float v2) {
        this.icon = icon;
        this.iconWidth = width;
        this.iconHeight = height;
        this.iconU = u;
        this.iconV = v;
        this.iconU2 = u2;
        this.iconV2 = v2;
        return this;
    }

    public ProgressBarBuilder setIconSide(Side side) {
        this.iconSide = side;
        return this;
    }

    public ProgressBarBuilder setShowBackground(boolean show) {
        this.showBackground = show;
        return this;
    }

    public ProgressBarBuilder setShowBorder(boolean show) {
        this.showBorder = show;
        return this;
    }

    public ProgressBarBuilder setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    public ProgressBarBuilder setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ProgressBarBuilder setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ProgressBarBuilder setAnimationStyle(AnimationStyle style) {
        this.animationStyle = style;
        return this;
    }

    public ProgressBarBuilder setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
        return this;
    }

    public ProgressBarBuilder setShowGradient(boolean show) {
        this.showGradient = show;
        return this;
    }

    public void render() {
        ProgressBarRenderer.render(this);
    }

    void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        float speed = animationSpeed * deltaTime * 60;

        switch (animationStyle) {
            case NONE:
                displayProgress = progress;
                break;
            case SMOOTH:
                displayProgress += (progress - displayProgress) * speed;
                if (Math.abs(displayProgress - progress) < 0.001f) displayProgress = progress;
                break;
            case BOUNCE:
                float diff = progress - displayProgress;
                if (Math.abs(diff) < 0.001f) {
                    displayProgress = progress;
                } else if (diff > 0) {
                    displayProgress += diff * speed * 1.5f;
                } else {
                    displayProgress += diff * speed * (1.0f + Math.abs(diff) * 0.5f);
                    if (displayProgress < progress) {
                        displayProgress = progress + (progress - displayProgress) * 0.3f;
                    }
                }
                break;
            case ELASTIC:
                float elasticDiff = progress - displayProgress;
                if (Math.abs(elasticDiff) < 0.001f) {
                    displayProgress = progress;
                } else {
                    float elasticSpeed = speed * 2.0f;
                    if (elasticDiff > 0) {
                        displayProgress += elasticDiff * elasticSpeed;
                        if (displayProgress > progress) {
                            displayProgress = progress + (displayProgress - progress) * 0.5f;
                            if (displayProgress > progress * 1.2f) displayProgress = progress * 1.2f;
                        }
                    } else {
                        displayProgress += elasticDiff * elasticSpeed * 1.5f;
                        if (displayProgress < progress) {
                            displayProgress = progress - (progress - displayProgress) * 0.3f;
                        }
                    }
                }
                break;
        }
    }

    void updateEffects() {
        if (damageFlash > 0) {
            damageFlash -= 0.05f;
            if (damageFlash < 0) damageFlash = 0;
        }
    }
}

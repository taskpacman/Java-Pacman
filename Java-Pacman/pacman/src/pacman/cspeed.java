
package pacman;

/**
 * контроль скорочти
 */
public class cspeed {
    int steps;
    int frames;

    int frameCount;
    int stepCount;

    float frameStepRatio;

    cspeed() {
        start(1, 1);
    }

    public void start(int s, int f)
            throws Error {
        if (f < s)
            throw new Error("Cspeed.init(...): frame must >= step");

        steps = s;
        frames = f;
        frameStepRatio = (float) frames / (float) steps;

        stepCount = steps;
        frameCount = frames;
    }

    // возвращает 1 если перемещается, 0 не перемещается
    public int isMove() {
        frameCount--;

        float ratio = (float) frameCount / (float) stepCount;

        if (frameCount == 0)
            frameCount = frames;

        if (ratio < frameStepRatio) {
            stepCount--;
            if (stepCount == 0)
                stepCount = steps;
            return (1);
        }
        return (0);
    }
}

package bowling.dedios.erskine;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates the total bowling score
 * 
 * @author ededios
 *
 */
public class Bowling {

	private final int MAX_FRAMES = 10;
	
	private List<Frame> frames = new ArrayList<Frame>();
	
	public Bowling() {
		frames.add(new Frame());
	}
	
	public void roll(final int ... pinScores) {
		for (int pinsDowned : pinScores) {
			roll(pinsDowned);
		}
	}

	/**
	 * Adds pinsDowned as a frame score.
	 * Throws an exception if roll is called after maximum number of possible rolls is reached
	 * 
	 * @param pinsDowned
	 */
	public void roll(final int pinsDowned) {
		final Frame currentFrame = frames.get(frames.size() - 1);
		
		if (isRollAllowedOnOpenFrame(currentFrame) || isSecondExtraRollAllowedOnFirstExtraFrame(currentFrame)) {
			currentFrame.addScore(pinsDowned);
		} else if (isRollAllowedOnNewFrame(currentFrame) || isFirstExtraRollAllowedOnFirstExtraFrame(currentFrame) || isSecondExtraRollAllowedOnSecondExtraFrame(currentFrame)) {
			final Frame newFrame = new Frame();
			newFrame.addScore(pinsDowned);
			frames.add(newFrame);		
		} else {
			throw new IllegalArgumentException("Game is complete.  Cannot add score " + pinsDowned + ".");
		}
	}
	
	private boolean isRollAllowedOnOpenFrame(final Frame currentFrame) {
		return !currentFrame.isClosed() && frames.size() <= MAX_FRAMES;
	}
	
	private boolean isRollAllowedOnNewFrame(final Frame currentFrame) {
		return currentFrame.isClosed() && frames.size() < MAX_FRAMES;
	}
	
	private boolean isFirstExtraRollAllowedOnFirstExtraFrame(final Frame currentFrame) {
		return currentFrame.isClosed() && frames.size() == MAX_FRAMES && (currentFrame.isSpare() || currentFrame.isStrike());
	}
	
	private boolean isSecondExtraRollAllowedOnFirstExtraFrame(final Frame currentFrame) {
		return !currentFrame.isClosed() && frames.size() == MAX_FRAMES + 1 && frames.get(MAX_FRAMES - 1).isStrike();
	}
	private boolean isSecondExtraRollAllowedOnSecondExtraFrame(final Frame currentFrame) {
		return currentFrame.isClosed() && frames.size() == MAX_FRAMES + 1 && frames.get(MAX_FRAMES - 1).isStrike() && frames.get(MAX_FRAMES - 1).isStrike() && currentFrame.isStrike();
	}

	/**
	 * 
	 * @return total bowling score
	 */
	public int tallyScore() {
		int total = 0;

		for (int frameCount = 0; (frameCount < frames.size() && frameCount < 10); frameCount++) {
			final Frame currentFrame = frames.get(frameCount);
			total = total + currentFrame.getTotalScore();
			
			if (currentFrame.isSpare() && frameCount < frames.size() - 1) {
				total = total + frames.get(frameCount + 1).getFirstScore();
			} else if (currentFrame.isStrike() && frameCount < frames.size() - 1) {
				total = total + frames.get(frameCount + 1).getFirstScore();					

				if (isNextFrameSecondScoreValid(frameCount)) {
					total = total + frames.get(frameCount + 1).getSecondScore();
				} else if (isNextAfterNextFrameFirstScoreValid(frameCount)) {
					total = total + frames.get(frameCount + 2).getFirstScore();
				}
			}
		}

		return total;
	}
	
	private boolean isNextFrameSecondScoreValid(final int frameCount) {
		return frames.get(frameCount + 1).getSecondScore() != -1;
	}
	
	private boolean isNextAfterNextFrameFirstScoreValid(final int frameCount) {
		return frameCount < frames.size() - 2;
	}

	public static void main(final String[] args) {
		final Bowling bowl = new Bowling();
		for (String arg : args) {
			try {
				bowl.roll(Integer.parseInt(arg));	
			} catch (final NumberFormatException e) {
				System.out.println("Invalid score " + arg + ". Ignoring score.");
			}
		}

		final int result = bowl.tallyScore();
		System.out.println(result);
	}
}

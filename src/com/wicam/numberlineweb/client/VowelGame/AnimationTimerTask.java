package com.wicam.numberlineweb.client.VowelGame;


/**
 * A Task for the AnimationTimer with possible starting delay
 * @author patrick
 *
 */
public class AnimationTimerTask {


	private boolean deletionMark = false;
	private int delay = 0;
	protected int taskX;
	protected int taskY;

	public AnimationTimerTask(){};
	
	public void run() {
		// TODO Auto-generated method stub	
	}	

	/**
	 * return the tasks starting delay in ms
	 * @return
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * sets the tasls starting delay in ms
	 * @param delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * mark this taskText for deletion.
	 */

	public void markForDelete() {

		this.deletionMark = true;

	}

	/**
	 * unmark this taskText for deletion
	 */
	public void unmarkForDelete() {

		this.deletionMark = false;

	}

	public boolean isMarkedForDeletion() {
		return deletionMark;
	}

}

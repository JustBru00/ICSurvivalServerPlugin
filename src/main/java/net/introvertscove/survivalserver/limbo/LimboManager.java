package net.introvertscove.survivalserver.limbo;

import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;

public class LimboManager {

	public void runEveryThirtyMinutesAsync() {
		
		// Check through all members
		
		// Check if exemption has expired. If is has send an admin shout about it.
		// Not going to allow expiry on excemptions anymore.
		
		// Check if the time since last_logout is longer than "time_from_last_logout_until.nag_message"
			//if it is then see if we already sent a nag message
			// if we already nagged them then we don't worry about it.
		
		// Check if the time since last_logout is longer than "sent_to_limbo"
			// send the you have been sent to limbo message.
			// unless it has already been sent.
			// Add limbo role
		
		// Check if the time since last_logout is longer than "auto_retire_danger_message"
			// send the message if it hasn't already been sent.
			
		
		// Check if the time since last_logout is longer than "auto_retire"
			// If it is and the auto retire message was not already sent
			// Send auto retire message, send auto retire shout.
			// Add Gray retired role to discord member.
			// Remove covian role
		
		// All limbo message statuses get reset when the player successfully joins the server again.
		// this means that they need to be blocked in the preloginevent from joining the game.
		
		// limbo exceptions bypass ALL of the above stuff.
		
		for (MemberDataBean member : DatabaseManager.getAllMembers()) {
			
		}
		
	}
	
}

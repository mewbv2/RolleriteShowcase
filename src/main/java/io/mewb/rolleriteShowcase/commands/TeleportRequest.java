package io.mewb.rolleriteShowcase.commands;


import java.util.UUID;

public class TeleportRequest {
    private final UUID requester;
    private final UUID target;
    private final long expirationTime;
    private boolean accepted = false;
    private boolean handled = false; // To prevent multiple actions (accept/deny/expire messages)

    public TeleportRequest(UUID requester, UUID target, long expirationTime) {
        this.requester = requester;
        this.target = target;
        this.expirationTime = expirationTime;
    }

    public UUID getRequester() { return requester; }
    public UUID getTarget() { return target; }
    public boolean isExpired() { return System.currentTimeMillis() > expirationTime; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public boolean isHandled() { return handled; }
    public void setHandled(boolean handled) { this.handled = handled; }
}

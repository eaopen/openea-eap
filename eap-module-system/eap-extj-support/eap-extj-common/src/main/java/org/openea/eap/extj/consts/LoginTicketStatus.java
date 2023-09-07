package org.openea.eap.extj.consts;

public enum LoginTicketStatus {
    Success(1),
    UnLogin(2),
    ErrLogin(3),
    UnBind(4),
    Invalid(5),
    Multitenancy(6);

    private int status;

    public int getStatus() {
        return this.status;
    }

    private LoginTicketStatus(int status) {
        this.status = status;
    }
}
